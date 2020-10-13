import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Scanner;
import javax.imageio.ImageIO;





public class Client {
	private static Socket socket;
	private static String _imageName;
	public static BufferedImage _imageBuff;
	static Scanner scanner = new Scanner(System.in);
	private static int _portNumber;
	private static String _ipAdr;
	private static DataInputStream _in;
	private static DataOutputStream _out;
	public static void main(String[] args) throws Exception
	{
		while(!ValidInfo());
		
		socket = new Socket(_ipAdr, _portNumber);
		
		System.out.format("The server is running on %s:%d\n", _ipAdr, _portNumber);
		
		_in = new DataInputStream(socket.getInputStream());
		_out = new DataOutputStream(socket.getOutputStream());
		
		String helloMessageFromServerString = _in.readUTF();
		System.out.println(helloMessageFromServerString);
		
		while(!Login());
		
		AskImage();
		ReceiveImage();
	
		
		scanner.close();
		socket.close();
	}
	
	public static boolean ValidInfo() 
	{
		System.out.println("Enter your ip address - Ex. 192.168.2.1");
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
	
	public static boolean Login() throws IOException 
	{
		System.out.println("Enter your username:");
		String userName = scanner.nextLine();
		_out.writeUTF(userName);
		System.out.println("Enter your password");
		String password = scanner.nextLine();
		_out.writeUTF(password);
		String serverMessageString = _in.readUTF();
		System.out.println(serverMessageString);
		return true;
	}
	
	public static void AskImage()
	{
		System.out.println("Enter your image name");
		_imageName = scanner.nextLine();
		System.out.println(_imageName+".jpg");
		BufferedImage bufferedImage;
		try {
			bufferedImage = ImageIO.read(new File(_imageName + ".jpg"));
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(bufferedImage, "JPEG", os);
			
			_out.writeUTF(_imageName);
			_out.write(ByteBuffer.allocate(4).putInt(os.size()).array());
			_out.write(os.toByteArray());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	
	public static void ReceiveImage()  {
		byte[] size = new byte[4];
		byte[] image = new byte[ByteBuffer.wrap(size).asIntBuffer().get()];
		try {
			_in.read(size);
			_in.read(image);
			ByteArrayInputStream inputStream = new ByteArrayInputStream(image);
			BufferedImage sobelImage = ImageIO.read(inputStream);
			File outputfile = new File(_imageName+"Sobel.jpg");
			outputfile.createNewFile();
			ImageIO.write(sobelImage, "JPEG", outputfile);
			System.out.println("Image recue");
			System.out.println("Chemin de l'image " + outputfile.getAbsolutePath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
}
