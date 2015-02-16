package com.boleslaw;

public class ServerTest
{
	public static void main(String args[]) throws Exception
	{
		System.setProperty("sfbu.wd", "/dev/shm/ld");
		new JettyServer().start();
	}
}
