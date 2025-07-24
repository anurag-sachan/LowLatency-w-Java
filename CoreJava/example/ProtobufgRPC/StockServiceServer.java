package example.ProtobufgRPC;

// Server Implementation
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import com.example.grpc.*;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Server that implements the StockService gRPC service.
 * This shows how to build a streaming service with gRPC and Protobuf.
 */
public class StockServiceServer {
    private final int port;
    private final Server server;

    public StockServiceServer(int port) {
        this.port = port;
        this.server = ServerBuilder.forPort(port)
                .addService(new StockServiceImpl())
                .build();
    }

    public void start() throws IOException {
        server.start();
        System.out.println("Server started, listening on port " + port);
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down gRPC server");
            try {
                StockServiceServer.this.stop();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
        }));
    }

    public void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    /**
     * Implementation of the StockService defined in the proto file
     */
    static class StockServiceImpl extends StockServiceGrpc.StockServiceImplBase {
        private final Random random = new Random();
        
        /**
         * Streaming API to provide continuous stock price updates
         */
        @Override
        public void getStockPriceStream(StockRequest request, 
                                        StreamObserver<StockResponse> responseObserver) {
            String symbol = request.getSymbol();
            System.out.println("Received request for stock price stream: " + symbol);
            
            // Base price for the requested stock
            double basePrice = getInitialPrice(symbol);
            
            // Start a new thread to send periodic updates (in real apps, use a scheduled executor)
            new Thread(() -> {
                try {
                    // Send 10 updates at 1-second intervals
                    for (int i = 0; i < 10 && !Thread.currentThread().isInterrupted(); i++) {
                        // Simulate price fluctuation
                        double priceChange = (random.nextDouble() - 0.5) * 2; // -1.0 to 1.0
                        double currentPrice = basePrice + priceChange;
                        
                        // Build and send response
                        StockResponse response = StockResponse.newBuilder()
                                .setSymbol(symbol)
                                .setPrice(currentPrice)
                                .setTimestamp(System.currentTimeMillis())
                                .build();
                        
                        responseObserver.onNext(response);
                        System.out.println("Sent price update: " + symbol + " = " + currentPrice);
                        
                        // Delay between updates
                        Thread.sleep(1000);
                    }
                    
                    // Complete the stream
                    responseObserver.onCompleted();
                    System.out.println("Completed stock price stream for: " + symbol);
                } catch (InterruptedException e) {
                    responseObserver.onError(e);
                }
            }).start();
        }
        
        /**
         * Unary RPC to place an order
         */
        @Override
        public void placeOrder(OrderRequest request, 
                               StreamObserver<OrderResponse> responseObserver) {
            System.out.println("Received order: " + request.getSymbol() + 
                    " - " + request.getType() + 
                    " - Quantity: " + request.getQuantity() + 
                    " - Price: " + request.getPrice());
            
            // Process the order (in a real application, this would interact with a trading engine)
            String orderId = UUID.randomUUID().toString();
            
            // Simulate slight price improvement
            double executedPrice = request.getPrice();
            if (request.getType() == OrderRequest.OrderType.BUY) {
                executedPrice = request.getPrice() * 0.99; // 1% better than requested
            } else {
                executedPrice = request.getPrice() * 1.01; // 1% better than requested
            }
            
            // Build and send response
            OrderResponse response = OrderResponse.newBuilder()
                    .setOrderId(orderId)
                    .setStatus("EXECUTED")
                    .setExecutedPrice(executedPrice)
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
            System.out.println("Order executed: " + orderId);
        }
        
        /**
         * Helper method to get initial price for a stock symbol
         */
        private double getInitialPrice(String symbol) {
            // In a real app, this would look up real prices
            switch (symbol.toUpperCase()) {
                case "AAPL": return 150.0;
                case "MSFT": return 300.0;
                case "GOOG": return 2500.0;
                case "AMZN": return 3300.0;
                default: return 100.0;
            }
        }
    }
    
    /**
     * Main method to start the server
     */
    public static void main(String[] args) throws Exception {
        int port = 50051;
        StockServiceServer server = new StockServiceServer(port);
        server.start();
        server.blockUntilShutdown();
    }
}
