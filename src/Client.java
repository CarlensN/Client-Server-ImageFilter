import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Scanner;

import javax.imageio.ImageIO;







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
		
		System.out.println("Enter your image name");
		String imageName = scanner.nextLine();
		System.out.println(imageName+".jpg");
		BufferedImage bufferedImage = ImageIO.read(new File(imageName + ".jpg"));
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, "JPEG", os);
		
		out.writeUTF(imageName);
		out.write(ByteBuffer.allocate(4).putInt(os.size()).array());
		out.write(os.toByteArray());
		
		byte[] size = new byte[4];
		in.read(size);
		byte[] image = new byte[ByteBuffer.wrap(size).asIntBuffer().get()];
		in.read(image);
		ByteArrayInputStream inputStream = new ByteArrayInputStream(image);
		BufferedImage sobelImage = ImageIO.read(inputStream);
		File outputfile = new File(imageName+"Sobel.jpg");
		outputfile.createNewFile();
		
		ImageIO.write(sobelImage, "JPEG", outputfile);
		System.out.println("Image recue");
		System.out.println("Chemin de l'image " + outputfile.getAbsolutePath());
		scanner.close();
		socket.close();
	}
}
