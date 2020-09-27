import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;

abstract public class Server {
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
				new ClientHandler(listener.accept(), clientNumber++).start();
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

		}

		public void run() {
			try {
				welcomeUser();
				handleLogin();

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
	}
}
