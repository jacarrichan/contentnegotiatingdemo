import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SMTPServer { // 主函数，没什么好说的
	public static void main(String[] args) {
		try {
			ServerSocket ss = new ServerSocket(25);
			while (true) {
				Socket s = ss.accept();
				new SMTPSession(s).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
