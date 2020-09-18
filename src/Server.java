import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.Scanner;

public class Server {
	private static ServerSocket listener;

	public static void main(String[] args) throws Exception {
		int clientNumber = 0;
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter a server address");
		String serverAddress = scanner.nextLine();

		System.out.println("Enter a port number");
		int serverPort = scanner.nextInt();

		// Creation de la connexion pour communiquer avec les clients
		listener = new ServerSocket();
		listener.setReuseAddress(true);
		InetAddress serverIP = InetAddress.getByName(serverAddress);

		// Association de l'adresse et du port a la connexion
		listener.bind(new InetSocketAddress(serverIP, serverPort));

		System.out.println("the server is running on " + serverAddress + ':' + serverPort);

		try {

			while (true) {
				new ClientHandler(listener.accept(), clientNumber).start();
			}
		} finally {
			scanner.close();
			listener.close();
			// TODO: handle finally clause
		}
	}

	private static class ClientHandler extends Thread {
		private Socket socket;
		private int clientNumber;

		public ClientHandler(Socket socket, int clientNumber) throws IOException {
			this.socket = socket;
			this.clientNumber = clientNumber;
			System.out.println("New connection with clients" + clientNumber + "at" + socket);
			DataInputStream dIn = new DataInputStream(socket.getInputStream());
			boolean done = false;
			while(!done) {
			  byte messageType = dIn.readByte();

			  switch(messageType)
			  {
			  case 1: // Type A
			    System.out.println("Message A: " + dIn.readUTF());
			    break;
			  case 2: // Type B
			    System.out.println("Message B: " + dIn.readUTF());
			    break;
			  case 3: // Type C
			    System.out.println("Message C [1]: " + dIn.readUTF());
			    System.out.println("Message C [2]: " + dIn.readUTF());
			    break;
			  default:
			    done = true;
			  }
			}

			dIn.close();

		}

		public void run() {
			try {
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				out.writeUTF("hello from server - you are client" + clientNumber);
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
	}
}
