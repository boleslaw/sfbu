package com.boleslaw.client;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

public class ScanFiles 
{
	static FileSystem fs ;
	private static List<String> banned= new LinkedList<String>();
	static
	{
		banned.add("/boot");
		banned.add("/opt");
		banned.add("/lib32");
		banned.add("/lib64");
		banned.add("/lib");
		banned.add("/tftpboot");
		banned.add("/dev");
		banned.add("/proc");
		banned.add("/sbin");
		banned.add("/sys");
		banned.add("/tmp");
		banned.add("/srv");
		banned.add("/usr");
		banned.add("/var");
		banned.add("/bin");
		banned.add("/etc");
		banned.add("/lost");
		banned.add("/cdrom");
		banned.add("/.rpmdb");
		banned.add("/run");
		banned.add("/init");
		banned.add("/vm");
		banned.add("/home/juice/.");
		banned.add("/home/juice/dev");
		try {
			fs =  FileSystem.getLocal(new Configuration());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private static TreeMap<String,Long> list = new TreeMap<String, Long>();
	
	public static void main(String args[]) throws Exception
	{
		long start = System.currentTimeMillis();
		
		if (args==null||args.length<1)
		{
			args = new String[]{"/","mp3"};
		}
		scan(args[0],args[1]);
		System.out.println(System.currentTimeMillis()-start+"\t"+list.size());
	}
	
	public static void scan(String in, String filter) throws Exception
	{
		//System.out.println(in);
		File fst[] = new File(in).listFiles();
		if(fst!=null)
		{
			for(File fsti : fst)
			{
				String fn = ""+fsti.getCanonicalPath();
				if(fsti.isDirectory())
				{
					List<String> files= banned.stream().filter(x -> fn.startsWith(x))
							.collect(Collectors.toList());
					if(files.size()<1)
					{
						scan(fn+"/",filter);
					}
				}
				else if(filter==null||fn.trim().toLowerCase().contains(filter))
				{
					list.put(fn.trim(), fsti.length());
				}
			}
		}
	}

	public static TreeMap<String,Long> getList() {
		return list;
	}
}
