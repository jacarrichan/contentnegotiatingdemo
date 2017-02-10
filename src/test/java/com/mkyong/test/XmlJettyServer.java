package com.mkyong.test;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * 开发调试使用的 Jetty Server
 * 
 * @author jacarri
 */
public class XmlJettyServer {

	public static void main(String[] args) throws Exception {
		printClassLoader();
		// System.setProperty("spring.profiles.active", "dev");
		Server server = buildNormalServer(8080, "/");
		// URLencoding设置(防止POST和Get请求乱码),tomcat也要设置
		server.setAttribute("org.eclipse.jetty.util.URI.charset", "UTF-8");
		server.start();
	}

	/**
	 * 创建用于正常运行调试的Jetty Server, 以src/main/webapp为Web应用目录.
	 * 
	 * @see org.eclipse.jetty.servlet.DefaultServlet
	 *      useFileMappedBuffer为false时，不开启静态文件的内存映射
	 * @throws Exception
	 */
	public static Server buildNormalServer(int port, String contextPath)
			throws Exception {
		WebAppContext webContext = new WebAppContext();
		// ----------------------------------------
		String war = "src/main/webapp";
		String path = Resource.newSystemResource(".").getFile().getParentFile()
				.getParent();
		path = path + "/" + war;
		// 手动设置BaseResource是为了解决项目在idea中不是顶级项目导致的问题
		webContext.setBaseResource(Resource.newResource(new File(path)));
		webContext.setContextPath("/");
		// ----------------------------------------

		// 解决请求的操作无法在使用用户映射区域打开的文件上执行。
		webContext.setInitParameter(
				"org.eclipse.jetty.servlet.Default.useFileMappedBuffer",
				"false");
		// ====================
		Server server = new Server(port);
		// ====================
		server.setHandler(webContext);
		server.setStopAtShutdown(true);
		return server;
	}

	private static void printClassLoader() {
		// Get the System Classloader
		ClassLoader sysClassLoader = ClassLoader.getSystemClassLoader();
		// Get the URLs
		URL[] urls = ((URLClassLoader) sysClassLoader).getURLs();
		for (int i = 0; i < urls.length; i++) {
			System.out.println(urls[i].getFile());
		}
	}
}
