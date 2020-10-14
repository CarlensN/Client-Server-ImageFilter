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
	private static int _portNumber = 5000;
	private static String _ipAdr = "127.0.0.1";
	private static DataInputStream _in;
	private static DataOutputStream _out;
	private static String _fileType;
	
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
		
		while(!AskImage());
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
		if(serverMessageString.equals("Account info invalid")) {
			System.out.println("Please try again");
			return false;
		}
		return true;
	}
	//import
	public static boolean AskImage() throws IOException
	{
		while(!askFileType());
		
		System.out.println("Enter your image name (without extension)");
		_imageName = scanner.nextLine();
		BufferedImage bufferedImage;
		try {
			bufferedImage = ImageIO.read(new File(_imageName + "." + _fileType));
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(bufferedImage, _fileType, os);
			
			_out.write(ByteBuffer.allocate(4).putInt(os.size()).array());
			_out.write(os.toByteArray());
			System.out.println("Image " + _imageName +"." + _fileType + " sent");
		} catch (IOException e) {
			System.out.println("Image not found please try again");
			return false;
		}
		_out.writeUTF(_imageName + "." + _fileType);
		return true;
		
		
	}
	
	public static boolean askFileType() {
		System.out.println("Enter file type- Ex: png , jpg ");
		_fileType = scanner.nextLine();
		if(_fileType.equals("png") || _fileType.equals( "jpg")) {
			return true;
		}
		System.out.println("file type not supported");
		return false;
	}
	
	public static void ReceiveImage() throws IOException {

		byte[] size = new byte[4];
		_in.readFully(size);
		byte[] image = new byte[ByteBuffer.wrap(size).asIntBuffer().get()];
		_in.readFully(image);
		ByteArrayInputStream inputStream = new ByteArrayInputStream(image);
		BufferedImage sobelImage = ImageIO.read(inputStream);
		File outputfile = new File(_imageName+"Sobel." + _fileType );
		outputfile.createNewFile();
		ImageIO.read(outputfile);
		ImageIO.write(sobelImage, _fileType, outputfile);
		System.out.println("Image processed");
		System.out.println("Image path : " + outputfile.getAbsolutePath());
	}
	
}
