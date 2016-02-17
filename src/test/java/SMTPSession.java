import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;

public class SMTPSession extends Thread {

	public static final String CMD_HELO = "EHLO";// 信号
	public static final String CMD_MAIL = "MAIL";//
	public static final String CMD_RCPT = "RCPT";
	public static final String CMD_DATA = "DATA";
	public static final String CMD_QUIT = "QUIT";
	public static final String CMD_RSET = "RSET";
	public static final String CMD_NOOP = "NOOP";
	public static final String CMD_AUTH = "AUTH";
	public static final String CMD_ = "AUTH";
	private Socket s;
	private BufferedReader br;
	private PrintStream ps;

	private String from;
	private String to;

	public SMTPSession(Socket s) {
		this.s = s;
	}
	boolean a=false;
	public void run() {
		try {
			br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			ps = new PrintStream(s.getOutputStream());
			doWelcome();
			String line = null;
			line = br.readLine();
			while (line != null) {
				System.out.println(line);
				if(a){
					break;
				}
				String command = line.substring(0, 4).trim();
				if (command.equalsIgnoreCase(CMD_HELO)) {
					doHello();
				} else if (command.equalsIgnoreCase(CMD_RSET))
					doRset();
				else if (command.equalsIgnoreCase(CMD_MAIL))
					doMail(line);
				else if (command.equalsIgnoreCase(CMD_RCPT))
					doRcpt(line);
				else if (command.equalsIgnoreCase(CMD_DATA))
					doData();
				else if (command.equalsIgnoreCase(CMD_AUTH))
					doAuth();
				else if (command.equalsIgnoreCase(CMD_NOOP))
					doNoop();
				else if (command.equalsIgnoreCase(CMD_QUIT)) {
					doQuit();
				}else{
					ps.println("334 UGFzc3dvcmQ6");//BASE64编码"Password:"
					a=true;
				}
				ps.flush();
				line = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
				ps.close();
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void doNoop() {
		ps.println("250 OK");
	}

	private void doQuit() {
		ps.println("221 GoodBye");
	}
	private void doAuth() {
		ps.println("334 VXNlcm5hbWU6");
	}

	private void doData() {
		try {
			ps.println("354  end data with <CR><LF>.</LF></CR>");
			String line = null;
			StringBuffer sb = new StringBuffer();
			while ((line = br.readLine()) != null) {
				if (line.equals("."))
					break;
				sb.append(line + "\r");
			}
			sb.deleteCharAt(sb.length() - 1);
			String sender = from.substring(0, from.indexOf("@"));
			// 将信件拷贝到发件夹
			String path = "MailBox/" + sender + "/sender/"
					+ System.currentTimeMillis() + ".txt";
			System.out.println(path);
			File file = new File(path);
			if (!file.exists()) {
				// 创建父级目录
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			PrintWriter pw = new PrintWriter(file);
			pw.println(sb);
			pw.close();
			// 将新建内容拷贝到发件夹
			String receiver = to.substring(0, to.indexOf("@"));
			String to_path = "MailBox/" + receiver + "/receiver/"
					+ System.currentTimeMillis() + ".txt";
			File to_file = new File(to_path);
			if (!to_file.exists()) {
				// 创建父级目录
				to_file.getParentFile().mkdirs();
				to_file.createNewFile();
			}
			PrintWriter to_pw = new PrintWriter(to_path);
			BufferedReader in = new BufferedReader(new FileReader(path));
			String str;
			while ((str = in.readLine()) != null) {
				to_pw.println(str);
			}
			to_pw.close();
			in.close();

			ps.println("250 OK");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void doRcpt(String command) {
		String strTo = "to:";
		int index = command.indexOf(strTo);
		if (index == -1)
			index = command.indexOf(strTo.toUpperCase());
		int start = command.indexOf("<");
		int end = command.indexOf(">");
		if (index > 4 && start > 0 && end > start) {
			to = command.substring(start + 1, end);
			ps.println("250  OK");
		}
	}

	private void doMail(String command) {
		String strFrom = "from:";
		int index = command.indexOf(strFrom);
		if (index == -1)
			index = command.indexOf(strFrom.toUpperCase());
		int start = command.indexOf("<");
		int end = command.indexOf(">");
		if (index > 4 && start > 0 && end > start) {
			from = command.substring(start + 1, end);
			ps.println("250  OK");
		} else {
			ps.println("500  ERROR");
		}
	}

	private void doRset() {
		ps.println("250 OK");
	}

	private void doHello() {
		ps.println("250-AUTH LOGIN PLAIN CRAM-MD5");
		ps.println("250-AUTH=LOGIN");
		ps.println("250 OK");
	}

	private void doWelcome() {
		ps.println("220 wrfei");
	}

}
