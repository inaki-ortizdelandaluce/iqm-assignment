package iqm.assignment;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.*;

public class EchoHttpServer {
    
	private int port;
	private String message;
	
	public EchoHttpServer(int port, String message) {
		this.port = port;
		this.message = message;
	}
	
	public void start() {
		try {
	
			System.out.println("\nStarting HTTP Server");
			System.out.println("  Port:" + this.port);
			System.out.println("  Message:" + this.message);
			
	
			HttpServer server = HttpServer.create(new InetSocketAddress(this.port), 0);
			server.createContext("/", new EchoHttpHandler(this.message));
			server.setExecutor(null); 
			server.start();
			
			System.out.print("HTTP Server started\n");

		} catch (IOException e) {
			System.err.println("HTTP Server could not be started");
			e.printStackTrace();
		}
    }
    
    static class EchoHttpHandler implements HttpHandler {
    	
    	private String message;
    	
    	public EchoHttpHandler(String message) {
    		this.message = message;
    	}
    	
        @Override
        public void handle(HttpExchange e) throws IOException {
        	
        	System.out.println("Handling request");
        	
            // set headers
        	Headers headers = e.getResponseHeaders();
        	headers.clear();
        	headers.add("Content-Type", "text/plain; charset=utf-8");
        	
            e.sendResponseHeaders(200, message.length());
            
            // write response  
            OutputStream os = e.getResponseBody();
            os.write(message.getBytes());
            os.close();
            
        }
    }
}
