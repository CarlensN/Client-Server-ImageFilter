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
		Scanner userInput = new Scanner(System.in);
		System.out.println("Enter a server address");
		String serverAddress = userInput.nextLine();

		userInput = new Scanner(System.in);
		System.out.println("Enter a port number");
		int serverPort = userInput.nextInt();
		
		// Creation de la connexion pour communiquer avec les clients
		listener = new ServerSocket();
		listener.setReuseAddress(true);
		InetAddress serverIP = InetAddress.getByName(serverAddress);

		// Association de l'adresse et du port a la connexion
		listener.bind(new InetSocketAddress(serverIP, serverPort));

		System.out.println("the server is running on " + serverAddress + ':' + serverPort);

		try {

			while (true) {
				ClientHandler clientHandler = new ClientHandler(listener.accept(), clientNumber);
				clientHandler.start();
				
				DataInputStream dataInputStream = new DataInputStream(clientHandler.socket.getInputStream());
				boolean done = false;
				while(!done) 
				{
					byte messageType = dataInputStream.readByte();
					
					switch(messageType) 
					{
					case 1: //UserName - handles username
						
						break;
						
					case 2: //Password - handles password
						
						break;
						
					case 3: //Image - handles image
						break;
						
					default:
						done = true;
					}
					
				}
			}
		} finally {
			listener.close();
			// TODO: handle finally clause
		}
	}
	
	

	private static class ClientHandler extends Thread {
		private Socket socket;
		private int clientNumber;

		public ClientHandler(Socket socket, int clientNumber) {
			this.socket = socket;
			this.clientNumber = clientNumber;
			System.out.println("New connection with clients" + clientNumber + "at" + socket);
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
