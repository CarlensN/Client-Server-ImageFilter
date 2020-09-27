import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;




public class Client {
	private static Socket socket;
	
	public static void main(String[] args) throws Exception
	{
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter your IP address");
		String serverAddress = scanner.nextLine();
		
		System.out.println("Enter your port number");
		int port = scanner.nextInt();
		scanner.nextLine();
		
		socket = new Socket(serverAddress, port);
		
		System.out.format("The server is running on %s:%d%n", serverAddress, port);
		
		DataInputStream in = new DataInputStream(socket.getInputStream());
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		
		String helloMessageFromServerString = in.readUTF();
		System.out.println(helloMessageFromServerString);
		
		System.out.println("Enter you username:");
		String username = scanner.nextLine();
		out.writeUTF(username);
		System.out.println("Enter your password");
		String password = scanner.nextLine();
		out.writeUTF(password);
		in = new DataInputStream(socket.getInputStream());
		String serverMessageString = in.readUTF();
		System.out.println(serverMessageString);
		
		
		scanner.close();
		socket.close();
	}
}
