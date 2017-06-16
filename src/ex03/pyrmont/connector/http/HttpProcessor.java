package ex03.pyrmont.connector.http;

import java.io.OutputStream;
import java.net.Socket;

public class HttpProcessor {
	
	public void process(Socket socket) {
		SocketInputStream input = null;
		OutputStream output = null;
		try {
			input = new SocketInputStream(socket.getInputStream(), 2048);
			output = socket.getOutputStream();
			
			// create HttpRequest object and parse
			HttpRequest request = new HttpRequest(input);
			
			// create HttpResponse object
		    HttpResponse response = new HttpResponse(output);
		    response.setRequest(request);
		    
		    response.setHeader("Server", "Pyrmont Servlet Container");

		    parseRequest(input, output);
		    parseHeaders(input);
		    
		    if(request.getRequestURI().startsWith("/servlet/")) {
		    	ServletProcessor processor = new ServletProcessor();
		        processor.process(request, response);
		    } else {
		        StaticResourceProcessor processor = new StaticResourceProcessor();
		        processor.process(request, response);
		    }
		    
		    socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
				
	}
		
}
