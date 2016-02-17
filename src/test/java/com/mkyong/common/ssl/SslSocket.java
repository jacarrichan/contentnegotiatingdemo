package com.mkyong.common.ssl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.TrustManagerFactory;

public class SslSocket {
	public static void main(String[] args) {
		SslSocket ss = new SslSocket();
		ss.server();
	}

	public void server() {
		String type = "TLS";// 类型
		String keyf = "/cert/srvstore";// key文件路径
		String trustf = "/cert/clitrust";// 信任证书库
		String pass = "123456";// 密码
		int port = 8888;// 端口
		try {
			// 初始化上下文
			SSLContext ctx = SSLContext.getInstance(type);

			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			TrustManagerFactory tmf = TrustManagerFactory
					.getInstance("SunX509");

			KeyStore ks = KeyStore.getInstance("JKS");
			KeyStore tks = KeyStore.getInstance("JKS");
			File keyff = new File(this.getClass().getResource("/") + keyf);
			File trustff = new File(this.getClass().getResource("/") + trustf);
			System.out.println(keyff.getAbsolutePath());
			// 载入keystore
			ks.load(new FileInputStream(keyff), pass.toCharArray());
			tks.load(new FileInputStream(trustff), pass.toCharArray());

			kmf.init(ks, pass.toCharArray());
			tmf.init(tks);

			ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(),
					new SecureRandom());

			SSLServerSocket sslServerSocket = (SSLServerSocket) ctx
					.getServerSocketFactory().createServerSocket(port);
			sslServerSocket.setNeedClientAuth(true);// 客户端认证
			while (true) {
				Socket socket = sslServerSocket.accept();
				ServerThread th = new ServerThread(socket);
				th.start();
				Thread.sleep(1000);
			}
			// 多线程处理...
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class ServerThread extends Thread {

	Socket sk = null;
	private PrintWriter wtr;
	private BufferedReader rdr;

	public ServerThread(Socket sk) {
		this.sk = sk;
	}

	public void run() {
		try {
			wtr = new PrintWriter(sk.getOutputStream());
			rdr = new BufferedReader(new InputStreamReader(sk.getInputStream()));
			String line = rdr.readLine();
			System.out.println("从客户端来的信息：" + line);
			// 特别，下面这句得加上 “\n”,
			wtr.println("你好，服务器已经收到您的信息！'" + line + "'\n");
			wtr.flush();
			System.out.println("已经返回给客户端！");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
