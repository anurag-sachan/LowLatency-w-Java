package example.FIX;

import quickfix.*;
import quickfix.field.*;
import quickfix.fix44.*;

import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

/**
 * This example demonstrates implementing a Financial Information eXchange (FIX) protocol application
 * using QuickFIX/J. The FIX protocol is the standard for electronic trading communication
 * in financial markets.
 */
class FixTrading {
    
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: java FixTradingExample [initiator|acceptor]");
            System.exit(1);
        }
        
        if ("initiator".equalsIgnoreCase(args[0])) {
            startInitiator();
        } else if ("acceptor".equalsIgnoreCase(args[0])) {
            startAcceptor();
        } else {
            System.out.println("Invalid argument. Use 'initiator' or 'acceptor'");
            System.exit(1);
        }
    }
    
    /**
     * Start a FIX client (initiator)
     */
    private static void startInitiator() throws Exception {
        System.out.println("Starting FIX client (initiator)...");
        
        // Create settings from config file
        SessionSettings settings = new SessionSettings(
                new FileInputStream("config/quickfix-client.properties"));
        
        // Create application, storeFactory, and logFactory
        ClientApplication application = new ClientApplication();
        MessageStoreFactory storeFactory = new FileStoreFactory(settings);
        LogFactory logFactory = new FileLogFactory(settings);
        
        // Create initiator
        SocketInitiator initiator = new SocketInitiator(
                application, storeFactory, settings, logFactory);
        
        // Start initiator
        initiator.start();
        System.out.println("FIX client started");
        
        // Wait for logon
        application.waitForLogon();
        System.out.println("FIX client logged on");
        
        // Send some sample orders
        sendSampleOrders(application);
        
        // Wait for user input to exit
        System.out.println("Press ENTER to exit");
        System.in.read();
        
        // Stop initiator
        initiator.stop();
        System.out.println("FIX client stopped");
    }
    
    /**
     * Start a FIX server (acceptor)
     */
    private static void startAcceptor() throws Exception {
        System.out.println("Starting FIX server (acceptor)...");
        
        // Create settings from config file
        SessionSettings settings = new SessionSettings(
                new FileInputStream("config/quickfix-server.properties"));
        
        // Create application, storeFactory, and logFactory
        ServerApplication application = new ServerApplication();
        MessageStoreFactory storeFactory = new FileStoreFactory(settings);
        LogFactory logFactory = new FileLogFactory(settings);
        
        // Create acceptor
        SocketAcceptor acceptor = new SocketAcceptor(
                application, storeFactory, settings, logFactory);
        
        // Start acceptor
        acceptor.start();
        System.out.println("FIX server started");
        
        // Wait for user input to exit
        System.out.println("Press ENTER to exit");
        System.in.read();
        
        // Stop acceptor
        acceptor.stop();
        System.out.println("FIX server stopped");
    }
    
    /**
     * Send some sample orders
     */
    private static void sendSampleOrders(ClientApplication application) throws Exception {
        // Send a market order
        application.sendMarketOrder("AAPL", Side.BUY, 100);
        Thread.sleep(1000);
        
        // Send a limit order
        application.sendLimitOrder("MSFT", Side.SELL, 200, 300.50);
        Thread.sleep(1000);
        
        // Send a cancel request
        application.sendCancelRequest("12345", "GOOG", Side.BUY);
        Thread.sleep(1000);
    }
    
    /**
     * Client (initiator) application implementation
     */
    static class ClientApplication implements Application {
        private final CountDownLatch logonLatch = new CountDownLatch(1);
        private SessionID sessionID;
        
        public void waitForLogon() throws InterruptedException {
            logonLatch.await();
        }
        
        /**
         * Send a market order
         */
        public void sendMarketOrder(String symbol, char side, int quantity) {
            if (sessionID == null) {
                System.out.println("No session available");
                return;
            }
            
            try {
                // Create a new order
                NewOrderSingle order = new NewOrderSingle(
                    new ClOrdID(UUID.randomUUID().toString().substring(0, 8)),
                    new Side(side),
                    new TransactTime(convertToDate(LocalDateTime.now())),
                    new OrdType(OrdType.MARKET)
                );
                
                // Set order fields
                order.set(new Symbol(symbol));
                order.set(new OrderQty(quantity));
                order.set(new HandlInst('1')); // Manual order
                
                // Send order
                Session.sendToTarget(order, sessionID);
                System.out.println("Sent market order: " + symbol + ", " + 
                    (side == Side.BUY ? "BUY" : "SELL") + ", " + quantity);
            } catch (Exception e) {
                System.err.println("Error sending market order: " + e.getMessage());
            }
        }
        
        /**
         * Send a limit order
         */
        public void sendLimitOrder(String symbol, char side, int quantity, double price) {
            if (sessionID == null) {
                System.out.println("No session available");
                return;
            }
            
            try {
                // Create a new order
                NewOrderSingle order = new NewOrderSingle(
                    new ClOrdID(UUID.randomUUID().toString().substring(0, 8)),
                    new Side(side),
                    new TransactTime(convertToDate(LocalDateTime.now())),
                    new OrdType(OrdType.LIMIT)
                );
                
                // Set order fields
                order.set(new Symbol(symbol));
                order.set(new OrderQty(quantity));
                order.set(new Price(price));
                order.set(new HandlInst('1')); // Manual order
                order.set(new TimeInForce(TimeInForce.DAY));
                
                // Send order
                Session.sendToTarget(order, sessionID);
                System.out.println("Sent limit order: " + symbol + ", " + 
                    (side == Side.BUY ? "BUY" : "SELL") + ", " + quantity + 
                    " @ " + price);
            } catch (Exception e) {
                System.err.println("Error sending limit order: " + e.getMessage());
            }
        }
        
        /**
         * Send a cancel request
         */
        public void sendCancelRequest(String origClOrdID, String symbol, char side) {
            if (sessionID == null) {
                System.out.println("No session available");
                return;
            }
            
            try {
                // Create a cancel request
                OrderCancelRequest cancel = new OrderCancelRequest(
                    new OrigClOrdID(origClOrdID),
                    new ClOrdID(UUID.randomUUID().toString().substring(0, 8)),
                    new Side(side),
                    new TransactTime(convertToDate(LocalDateTime.now()))
                );
                
                // Set cancel fields
                cancel.set(new Symbol(symbol));
                
                // Send cancel
                Session.sendToTarget(cancel, sessionID);
                System.out.println("Sent cancel request for: " + origClOrdID);
            } catch (Exception e) {
                System.err.println("Error sending cancel request: " + e.getMessage());
            }
        }
        
        // Convert LocalDateTime to Date
        private Date convertToDate(LocalDateTime dateTime) {
            return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
        }
        
        // Application interface methods
        @Override
        public void onCreate(SessionID sessionID) {
            System.out.println("onCreate: " + sessionID);
        }
        
        @Override
        public void onLogon(SessionID sessionID) {
            System.out.println("onLogon: " + sessionID);
            this.sessionID = sessionID;
            logonLatch.countDown();
        }
        
        @Override
        public void onLogout(SessionID sessionID) {
            System.out.println("onLogout: " + sessionID);
            this.sessionID = null;
        }
        
        @Override
        public void toAdmin(Message message, SessionID sessionID) {
            // You can modify admin messages here (e.g., add credentials to Logon)
            System.out.println("toAdmin: " + message);
        }
        
        @Override
        public void fromAdmin(Message message, SessionID sessionID) 
                throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
            System.out.println("fromAdmin: " + message);
        }
        
        @Override
        public void toApp(Message message, SessionID sessionID) throws DoNotSend {
            System.out.println("toApp: " + message);
        }
        
        @Override
        public void fromApp(Message message, SessionID sessionID) 
                throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
            System.out.println("fromApp: " + message);
            
            // Handle different message types
            if (message instanceof ExecutionReport) {
                handleExecutionReport((ExecutionReport) message);
            } else if (message instanceof OrderCancelReject) {
                handleOrderCancelReject((OrderCancelReject) message);
            } else if (message instanceof Reject) {
                handleReject((Reject) message);
            }
        }
        
        /**
         * Handle execution reports (order status updates)
         */
        private void handleExecutionReport(ExecutionReport execReport) throws FieldNotFound {
            String clOrdID = execReport.getClOrdID().getValue();
            String symbol = execReport.getSymbol().getValue();
            char execType = execReport.getExecType().getValue();
            char ordStatus = execReport.getOrdStatus().getValue();
            
            System.out.println("Execution Report:");
            System.out.println("  ClOrdID: " + clOrdID);
            System.out.println("  Symbol: " + symbol);
            System.out.println("  ExecType: " + getExecTypeDescription(execType));
            System.out.println("  OrdStatus: " + getOrdStatusDescription(ordStatus));
            
            // Handle filled orders
            if (execReport.isSetCumQty() && execReport.isSetAvgPx()) {
                int cumQty = execReport.getCumQty().getValue();
                double avgPx = execReport.getAvgPx().getValue();
                System.out.println("  CumQty: " + cumQty);
                System.out.println("  AvgPx: " + avgPx);
            }
        }
        
        /**
         * Handle order cancel rejections
         */
        private void handleOrderCancelReject(OrderCancelReject cancelReject) throws FieldNotFound {
            String clOrdID = cancelReject.getClOrdID().getValue();
            String origClOrdID = cancelReject.getOrigClOrdID().getValue();
            char cxlRejReason = cancelReject.getCxlRejReason().getValue();
            
            System.out.println("Order Cancel Reject:");
            System.out.println("  ClOrdID: " + clOrdID);
            System.out.println("  OrigClOrdID: " + origClOrdID);
            System.out.println("  Reason: " + getCancelRejectReason(cxlRejReason));
        }
        
        /**
         * Handle message rejections
         */
        private void handleReject(Reject reject) throws FieldNotFound {
            int refSeqNum = reject.getRefSeqNum().getValue();
            String text = reject.isSetText() ? reject.getText().getValue() : "No reason provided";
            
            System.out.println("Message Reject:");
            System.out.println("  RefSeqNum: " + refSeqNum);
            System.out.println("  Text: " + text);
        }
        
        // Helper methods for readable output
        private String getExecTypeDescription(char execType) {
            switch (execType) {
                case ExecType.NEW: return "New";
                case ExecType.PARTIAL_FILL: return "Partial Fill";
                case ExecType.FILL: return "Fill";
                case ExecType.CANCELED: return "Canceled";
                case ExecType.REJECTED: return "Rejected";
                default: return "Unknown (" + execType + ")";
            }
        }
        
        private String getOrdStatusDescription(char ordStatus) {
            switch (ordStatus) {
                case OrdStatus.NEW: return "New";
                case OrdStatus.PARTIALLY_FILLED: return "Partially Filled";
                case OrdStatus.FILLED: return "Filled";
                case OrdStatus.CANCELED: return "Canceled";
                case OrdStatus.REJECTED: return "Rejected";
                default: return "Unknown (" + ordStatus + ")";
            }
        }
        
        private String getCancelRejectReason(char reason) {
            switch (reason) {
                case CxlRejReason.TOO_LATE_TO_CANCEL: return "Too late to cancel";
                case CxlRejReason.UNKNOWN_ORDER: return "Unknown order";
                case CxlRejReason.DUPLICATE_CLORDID: return "Duplicate ClOrdID";
                default: return "Unknown (" + reason + ")";
            }
        }
    }
    
    /**
     * Server (acceptor) application implementation
     */
    static class ServerApplication implements Application {
        
        @Override
        public void onCreate(SessionID sessionID) {
            System.out.println("Server onCreate: " + sessionID);
        }
        
        @Override
        public void onLogon(SessionID sessionID) {
            System.out.println("Server onLogon: " + sessionID);
        }
        
        @Override
        public void onLogout(SessionID sessionID) {
            System.out.println("Server onLogout: " + sessionID);
        }
        
        @Override
        public void toAdmin(Message message, SessionID sessionID) {
            System.out.println("Server toAdmin: " + message);
        }
        
        @Override
        public void fromAdmin(Message message, SessionID sessionID) 
                throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
            System.out.println("Server fromAdmin: " + message);
        }
        
        @Override
        public void toApp(Message message, SessionID sessionID) throws DoNotSend {
            System.out.println("Server toApp: " + message);
        }
        
        @Override
        public void fromApp(Message message, SessionID sessionID) 
                throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
            System.out.println("Server fromApp: " + message);
            
            // Handle incoming messages from clients
            if (message instanceof NewOrderSingle) {
                handleNewOrder((NewOrderSingle) message, sessionID);
            } else if (message instanceof OrderCancelRequest) {
                handleCancelRequest((OrderCancelRequest) message, sessionID);
            }
        }
        
        /**
         * Handle incoming new orders
         */
        private void handleNewOrder(NewOrderSingle order, SessionID sessionID) throws FieldNotFound {
            String clOrdID = order.getClOrdID().getValue();
            String symbol = order.getSymbol().getValue();
            char side = order.getSide().getValue();
            int orderQty = order.getOrderQty().getValue();
            char ordType = order.getOrdType().getValue();
            
            System.out.println("Received new order:");
            System.out.println("  ClOrdID: " + clOrdID);
            System.out.println("  Symbol: " + symbol);
            System.out.println("  Side: " + (side == Side.BUY ? "BUY" : "SELL"));
            System.out.println("  Quantity: " + orderQty);
            System.out.println("  Type: " + (ordType == OrdType.MARKET ? "MARKET" : "LIMIT"));
            
            // Send acknowledgment (order accepted)
            sendExecutionReport(sessionID, clOrdID, symbol, side, orderQty, 
                ExecType.NEW, OrdStatus.NEW, 0, 0, 0);
            
            // Simulate order processing and fill
            new Thread(() -> {
                try {
                    Thread.sleep(1000); // Simulate processing time
                    
                    // Simulate a fill
                    double fillPrice = getSimulatedPrice(symbol);
                    sendExecutionReport(sessionID, clOrdID, symbol, side, orderQty,
                        ExecType.FILL, OrdStatus.FILLED, orderQty, orderQty, fillPrice);
                } catch (Exception e) {
                    System.err.println("Error processing order: " + e.getMessage());
                }
            }).start();
        }
        
        /**
         * Handle order cancel requests
         */
        private void handleCancelRequest(OrderCancelRequest cancelRequest, SessionID sessionID) throws FieldNotFound {
            String clOrdID = cancelRequest.getClOrdID().getValue();
            String origClOrdID = cancelRequest.getOrigClOrdID().getValue();
            String symbol = cancelRequest.getSymbol().getValue();
            char side = cancelRequest.getSide().getValue();
            
            System.out.println("Received cancel request:");
            System.out.println("  ClOrdID: " + clOrdID);
            System.out.println("  OrigClOrdID: " + origClOrdID);
            System.out.println("  Symbol: " + symbol);
            
            // For simplicity, reject all cancel requests (order already filled)
            sendOrderCancelReject(sessionID, clOrdID, origClOrdID, 
                CxlRejReason.TOO_LATE_TO_CANCEL, "Order already filled");
        }
        
        /**
         * Send an execution report
         */
        private void sendExecutionReport(SessionID sessionID, String clOrdID, String symbol, 
                char side, int orderQty, char execType, char ordStatus, 
                int lastQty, int cumQty, double avgPx) {
            try {
                ExecutionReport execReport = new ExecutionReport(
                    new OrderID(UUID.randomUUID().toString().substring(0, 8)),
                    new ExecID(UUID.randomUUID().toString().substring(0, 8)),
                    new ExecType(execType),
                    new OrdStatus(ordStatus),
                    new Side(side),
                    new LeavesQty(orderQty - cumQty),
                    new CumQty(cumQty),
                    new AvgPx(avgPx)
                );
                
                execReport.set(new ClOrdID(clOrdID));
                execReport.set(new Symbol(symbol));
                execReport.set(new OrderQty(orderQty));
                execReport.set(new LastQty(lastQty));
                execReport.set(new LastPx(avgPx));
                
                Session.sendToTarget(execReport, sessionID);
                System.out.println("Sent execution report: " + execType + " for " + clOrdID);
            } catch (Exception e) {
                System.err.println("Error sending execution report: " + e.getMessage());
            }
        }
        
        /**
         * Send an order cancel reject
         */
        private void sendOrderCancelReject(SessionID sessionID, String clOrdID, 
                String origClOrdID, char cxlRejReason, String text) {
            try {
                OrderCancelReject cancelReject = new OrderCancelReject(
                    new OrderID(UUID.randomUUID().toString().substring(0, 8)),
                    new ClOrdID(clOrdID),
                    new OrigClOrdID(origClOrdID),
                    new OrdStatus(OrdStatus.REJECTED),
                    new CxlRejReason(cxlRejReason)
                );
                
                cancelReject.set(new Text(text));
                
                Session.sendToTarget(cancelReject, sessionID);
                System.out.println("Sent cancel reject for: " + origClOrdID);
            } catch (Exception e) {
                System.err.println("Error sending cancel reject: " + e.getMessage());
            }
        }
        
        /**
         * Get a simulated price for a symbol
         */
        private double getSimulatedPrice(String symbol) {
            switch (symbol.toUpperCase()) {
                case "AAPL": return 150.0 + (Math.random() - 0.5) * 10;
                case "MSFT": return 300.0 + (Math.random() - 0.5) * 20;
                case "GOOG": return 2500.0 + (Math.random() - 0.5) * 100;
                default: return 100.0 + (Math.random() - 0.5) * 10;
            }
        }
    }
}

/*
 * Configuration files needed:
 * 
 * config/quickfix-client.properties:
 * [default]
 * ConnectionType=initiator
 * ReconnectInterval=60
 * FileStorePath=fixtmp
 * FileLogPath=fixlog
 * StartTime=00:00:00
 * EndTime=00:00:00
 * HeartBtInt=30
 * CheckLatency=Y
 * 
 * [session]
 * BeginString=FIX.4.4
 * SenderCompID=CLIENT
 * TargetCompID=SERVER
 * SocketConnectHost=localhost
 * SocketConnectPort=9876
 * 
 * config/quickfix-server.properties:
 * [default]
 * ConnectionType=acceptor
 * FileStorePath=fixtmp
 * FileLogPath=fixlog
 * StartTime=00:00:00
 * EndTime=00:00:00
 * HeartBtInt=30
 * CheckLatency=Y
 * 
 * [session]
 * BeginString=FIX.4.4
 * SenderCompID=SERVER
 * TargetCompID=CLIENT
 * SocketAcceptPort=9876
 * 
 * Maven dependency:
 * <dependency>
 *     <groupId>org.quickfixj</groupId>
 *     <artifactId>quickfixj-core</artifactId>
 *     <version>2.3.1</version>
 * </dependency>
 * <dependency>
 *     <groupId>org.quickfixj</groupId>
 *     <artifactId>quickfixj-messages-fix44</artifactId>
 *     <version>2.3.1</version>
 * </dependency>
 */