import java.io.DataInputStream;
import java.io.ObjectOutputStream;
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
		
		socket = new Socket(serverAddress, port);
		
		System.out.format("The server is running on %s:%d%n", serverAddress, port);
		
		DataInputStream in = new DataInputStream(socket.getInputStream());
		
		String helloMessageFromServerString = in.readUTF();
		System.out.println(helloMessageFromServerString);

		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		oos.writeByte(1);
		oos.writeUTF("This is the first type of message.");

		
		scanner.close();
		//socket.close();
	}
}
