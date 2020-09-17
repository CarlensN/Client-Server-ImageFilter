import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	private static Socket socket;
	
	public static void main(String[] args) throws Exception
	{
		Scanner userInput = new Scanner(System.in);
		System.out.println("Enter your IP address");
		String serverAddress = userInput.nextLine();
		
		userInput = new Scanner(System.in);
		System.out.println("Enter your port number");
		int port = userInput.nextInt();
		
		socket = new Socket(serverAddress, port);
		
		System.out.format("The server is running on %s:%d%n", serverAddress, port);
		
		DataInputStream in = new DataInputStream(socket.getInputStream());
		
		String helloMessageFromServerString = in.readUTF();
		System.out.println(helloMessageFromServerString);

		DataOutputStream output = new DataOutputStream(socket.getOutputStream());
		output.writeByte(1);
		output.writeUTF("This is the first type of message.");
		output.flush(); // 
		
		socket.close();
	}
}
