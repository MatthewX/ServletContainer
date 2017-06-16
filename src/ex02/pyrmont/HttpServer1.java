package ex02.pyrmont;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer1 {
	
	private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";
	private boolean shutdown = false;
	
	public static void main(String[] args) {
		HttpServer1 server = new HttpServer1();
		server.await();
	}
	
	public void await() {
		ServerSocket serverSocket = null;
		int port = 8080;
		try {
			serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		// Loop waiting for a request
		while(!shutdown) {
			Socket socket = null;
			InputStream inputStream = null;
			OutputStream outputStream = null;
			try {
				socket = serverSocket.accept();
				inputStream = socket.getInputStream();
				outputStream = socket.getOutputStream();
				
				// Create request object and parse
				Request request = new Request(inputStream);
				request.parse();
				
				// Create reponse object
				Response response = new Response(outputStream);
		        response.setRequest(request);
		        
		        // Check if it's a request for a servlet or a static resource
		        if(request.getUri().startsWith("/servlet/")) {
		        	ServletProcessor1 processor = new ServletProcessor1();
		        	processor.process(request, response);
		        } else {
		        	StaticResourceProcessor processor = new StaticResourceProcessor();                               
		            processor.process(request, response);  
		        }
		        
		        // Close the socket
		        socket.close();
		        shutdown = request.getUri().equals(SHUTDOWN_COMMAND);
		        
				
			} catch(Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
	
}
