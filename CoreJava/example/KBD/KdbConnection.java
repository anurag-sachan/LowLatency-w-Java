package example.KBD;

import kx.c;

public class KdbConnection {
    public static void main(String[] args) {
        try {
            // Connect to KDB+ server (host, port, credentials)
            c connection = new c("localhost", 5001, "username:password");
            
            // Execute a simple query
            Object result = connection.k("select from trade where sym=`AAPL");
            System.out.println("Query result: " + result);
            
            // Execute a function with parameters
            double[] prices = {100.5, 101.2, 99.8, 102.3};
            Object avgResult = connection.k("avg", prices);
            System.out.println("Average price: " + avgResult);
            
            // Insert data
            String[] symbols = {"AAPL", "MSFT", "GOOG"};
            double[] quantities = {100, 200, 150};
            double[] prices2 = {150.5, 290.3, 2100.75};
            
            Object insertResult = connection.k("{`trade insert (x;y;z)}", symbols, quantities, prices2);
            System.out.println("Insert result: " + insertResult);
            
            // Close the connection
            connection.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}