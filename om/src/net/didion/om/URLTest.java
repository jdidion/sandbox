package net.didion.om;

import java.net.URL;

public class URLTest {
	public static void main(String[] args) {
		try {
			System.out.println(System.getProperty("java.version"));
			URL url = new URL("http://www.goober.com/test?foo=bar");
			System.out.println("protocol: " + url.getProtocol());
			System.out.println("user info: " + url.getUserInfo());
			System.out.println("host: " + url.getHost());
			System.out.println("port: " + url.getPort());
			System.out.println("authority: " + url.getAuthority());
			System.out.println("user info: " + url.getUserInfo());
			System.out.println("path: " + url.getPath());
			System.out.println("file: " + url.getFile());
			System.out.println("query: " + url.getQuery());
			System.out.println("ref: " + url.getRef());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
