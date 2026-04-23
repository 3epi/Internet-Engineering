package ir.ac.ut.ece.ie.dynamiccontentserver;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

public class DynamicContentServer {
	public void start() throws IOException {
		ServerSocket serverSocket = new ServerSocket(9092);
		Socket socket;
		while ((socket = serverSocket.accept()) != null) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String readLine = reader.readLine();
			if (readLine == null)
				continue;
			String pageName = getPageName(readLine);
			try {

				Class<?> c = Class.forName("ir.ac.ut.ece.ie.dynamiccontentserver." + pageName);
				Object page = c.getDeclaredConstructor().newInstance();
				Method method = c.getMethod("pageBody");
				byte[] data = (byte[]) method.invoke(page);

				String header = "HTTP1.1 200 OK \r\nContent-Type: text/html\r\nContent.Length: " 
				+ data.length
				+ "\r\n\r\n";

				socket.getOutputStream().write(header.getBytes());
				socket.getOutputStream().write(data);
			} catch (FileNotFoundException | 
					ClassNotFoundException | InstantiationException | 
					IllegalAccessException | IllegalArgumentException | 
					InvocationTargetException | NoSuchMethodException | 
					SecurityException e) {
				String header = "HTTP1.1 404 Page Not Found\r\n\r\n";
				socket.getOutputStream().write(header.getBytes());
			}

			socket.getOutputStream().flush();
			socket.close();
		}
		serverSocket.close();
	}
	
	private String getPageName(String readLine) {
		StringTokenizer tokenizer = new StringTokenizer(readLine, " ");
		tokenizer.nextToken();
		String fileName = tokenizer.nextToken().substring(1);
		return fileName;
	}
	
	public static void main(String[] args) throws IOException {
		DynamicContentServer dcs = new DynamicContentServer();
		dcs.start();
	}

}
