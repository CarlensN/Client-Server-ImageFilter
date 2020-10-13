import java.nio.ByteBuffer;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;

public class Server {
	private static ServerSocket listener;
	private static int _portNumber = 5000;
	private static String _ipAdr = "127.0.0.1";
	
	public static void main(String[] args) throws Exception {
		int clientNumber = 0;
		while(!ValidInfo());

		// Creation de la connexion pour communiquer avec les clients
		listener = new ServerSocket();
		listener.setReuseAddress(true);
		InetAddress serverIP = InetAddress.getByName(_ipAdr); 

		// Association de l'adresse et du port a la connexion
		listener.bind(new InetSocketAddress(serverIP, _portNumber));

		System.out.println("the server is running on " + _ipAdr + ':' + _portNumber);

		try {

			while (true) {
				new ClientHandler(listener.accept(), clientNumber++).start();
			}
		} finally {
			listener.close();
			// TODO: handle finally clause
		}
	}
	
	public static boolean ValidInfo() 
	{
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter your ip address- Ex. 192.168.2.1");
		_ipAdr = scanner.nextLine();
		System.out.println("Enter a port number - Ex. 5000");
		
		//Ip address validation
		String ipDigits[] = _ipAdr.split("\\.");
		if(ipDigits.length != 4 ) return false;
		boolean ipIsValid = false;
		int digit;
		for(String digitString : ipDigits) 
		{
			try {
				digit = Integer.parseInt(digitString);
			} catch (Exception e) {
				System.out.println("Invalid address");
				return false;
			}
			ipIsValid = (digit >= 0 && digit <= 255) ? true : false;
			ipIsValid = (digitString.length() < 4) ? true : false;
		}
		
		//Port validation
		String port = scanner.nextLine();
		try {
			digit = Integer.parseInt(port);
		} catch (Exception e) {
			System.out.println("Invalid port number");
			return false;
		}
		ipIsValid = (digit >= 5000 && digit <= 5050) ? true : false;	
		_portNumber = digit;
		return ipIsValid;
	}

	private static class ClientHandler extends Thread {
		private Socket socket;
		private int clientNumber;

		public ClientHandler(Socket socket, int clientNumber) throws IOException {
			this.socket = socket;
			this.clientNumber = clientNumber;
			System.out.println("New connection with clients" + clientNumber + "at" + socket);

		}

		public void run() {
			try {
				welcomeUser();
				handleLogin();
				BufferedImage bufferedImage = receiveImage();
				sendImage(bufferedImage);

			} catch (IOException e) {
				// TODO: handle exception
			} finally {

				try {
					socket.close();
				} catch (IOException e) {
					System.out.println("Couldn't close a socket");
				}
				System.out.println("Connection with clients" + clientNumber + "closed");
			}

		}

		public void welcomeUser() throws IOException {
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			out.writeUTF("hello from server - you are client" + clientNumber);
		}

		public void handleLogin() throws IOException {
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			DataInputStream in = new DataInputStream(socket.getInputStream());
			String username = in.readUTF();
			String password = in.readUTF();
			String gotemString = "gotem";
			out.writeUTF(gotemString);
			handleAccountInfo(username, password);

		}

		public void handleAccountInfo(String username, String password) throws FileNotFoundException, IOException {
			if (authenticateUser(username, password)) {
				return;
			}
		}

		public boolean authenticateUser(String username, String password) throws IOException {
			String line;
			BufferedReader reader = new BufferedReader(new FileReader("database.txt"));
			while ((line = reader.readLine()) != null) {
				String[] accountInfo = line.split(":", 2);
				if (accountInfo[0].equals(username)) {
					reader.close();
					if (accountInfo[1].equals(password)) {
						System.out.println("Account info valid");
						return true;
					}
					System.out.println("Account info invalid");
					return false;
				}
			}
			reader.close();
			return createAccount(username, password);
		}

		public boolean createAccount(String username, String password) {
			File database = new File("database.txt");
			try {
				PrintWriter pWriter = new PrintWriter(new FileOutputStream(database, true));
				pWriter.append(username + ":" + password + "\n");
				pWriter.close();
			} catch (FileNotFoundException e) {
				return false;
			}
			System.out.println("Account created");
			return true;
		}
		public BufferedImage receiveImage() throws IOException{
			DataInputStream in = new DataInputStream(socket.getInputStream());
			String imageName = in.readUTF();
			byte[] size = new byte[4];
			in.read(size);
			byte[] image = new byte[ByteBuffer.wrap(size).asIntBuffer().get()];
			in.read(image);
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			//out.writeUTF("received image " + imageName );
			ByteArrayInputStream is = new ByteArrayInputStream(image);
			BufferedImage bufferedImage = ImageIO.read(is);
			return Sobel.process(bufferedImage);
		}
		
		public void sendImage(BufferedImage image) throws IOException {
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(image, "JPEG", os);
			out.write(ByteBuffer.allocate(4).putInt(os.size()).array());
			out.write(os.toByteArray());
			
		}
	}
}
