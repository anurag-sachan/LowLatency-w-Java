package example.ProtobufgRPC;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import com.example.grpc.*;

import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Client for accessing the StockService.
 * Demonstrates both streaming and unary RPC calls.
 */
public class Client {
    private final ManagedChannel channel;
    private final StockServiceGrpc.StockServiceBlockingStub blockingStub;
    private final StockServiceGrpc.StockServiceStub asyncStub;

    public StockServiceClient(String host, int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()  // For development only - no TLS
                .build();
        this.blockingStub = StockServiceGrpc.newBlockingStub(channel);
        this.asyncStub = StockServiceGrpc.newStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    /**
     * Place an order using a unary RPC call
     */
    public void placeOrder(String symbol, double price, int quantity, boolean isBuy) {
        System.out.println("Placing order for " + symbol);
        
        OrderRequest request = OrderRequest.newBuilder()
                .setSymbol(symbol)
                .setPrice(price)
                .setQuantity(quantity)
                .setType(isBuy ? OrderRequest.OrderType.BUY : OrderRequest.OrderType.SELL)
                .build();
        
        OrderResponse response;
        try {
            response = blockingStub.placeOrder(request);
            System.out.println("Order placed: " + response.getOrderId());
            System.out.println("Status: " + response.getStatus());
            System.out.println("Executed price: " + response.getExecutedPrice());
        } catch (StatusRuntimeException e) {
            System.err.println("RPC failed: " + e.getStatus());
        }
    }

    /**
     * Subscribe to stock price updates using a streaming RPC call
     */
    public void getStockPriceUpdates(String symbol) throws InterruptedException {
        System.out.println("Subscribing to price updates for " + symbol);
        
        StockRequest request = StockRequest.newBuilder()
                .setSymbol(symbol)
                .build();
        
        // For synchronizing the example
        final CountDownLatch finishLatch = new CountDownLatch(1);
        
        // Use async stub for the streaming call
        asyncStub.getStockPriceStream(request, new StreamObserver<StockResponse>() {
            @Override
            public void onNext(StockResponse response) {
                System.out.printf("Received update: %s at $%.2f (timestamp: %d)%n", 
                        response.getSymbol(), response.getPrice(), response.getTimestamp());
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Error in price stream: " + t.getMessage());
                finishLatch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("Stream completed");
                finishLatch.countDown();
            }
        });
        
        // Wait for stream to complete
        finishLatch.await(15, TimeUnit.SECONDS);
    }
    
    /**
     * Alternative implementation using blocking stub for streaming
     */
    public void getStockPriceUpdatesBlocking(String symbol) {
        System.out.println("Subscribing to price updates for " + symbol + " (blocking)");
        
        StockRequest request = StockRequest.newBuilder()
                .setSymbol(symbol)
                .build();
        
        try {
            // Get the iterator over the stream
            Iterator<StockResponse> responses = blockingStub.getStockPriceStream(request);
            
            // Process each response as it arrives
            while (responses.hasNext()) {
                StockResponse response = responses.next();
                System.out.printf("Received update (blocking): %s at $%.2f (timestamp: %d)%n", 
                        response.getSymbol(), response.getPrice(), response.getTimestamp());
            }
        } catch (StatusRuntimeException e) {
            System.err.println("RPC failed: " + e.getStatus());
        }
    }

    /**
     * Main method to run the client
     */
    public static void main(String[] args) throws Exception {
        StockServiceClient client = new StockServiceClient("localhost", 50051);
        
        try {
            // Place an order
            client.placeOrder("AAPL", 150.0, 10, true);
            
            // Subscribe to price updates
            client.getStockPriceUpdates("AAPL");
            
            // Alternative: Use blocking approach
            // client.getStockPriceUpdatesBlocking("MSFT");
        } finally {
            client.shutdown();
        }
    }
}