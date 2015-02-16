package com.boleslaw.client;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;

public class FileToHttpConsumer<K,V> implements  Runnable
{
	private ClientConfig config = new ClientConfig();
	private LinkedBlockingQueue<Entry<K,V>> list ;
	private int id=0;
	static AtomicInteger ai = new AtomicInteger();
	static AtomicInteger ai2 = new AtomicInteger();
	private JerseyClient out;
	public FileToHttpConsumer(LinkedBlockingQueue<Entry<K,V>>  in)
	{
		id=ai.incrementAndGet();
		list=in;

	}
	@Override
	public void run() 
	{
		FileOutputStream fos =null;
		try
		{
			fos = new FileOutputStream("/dev/shm/cat"+id);
			//Thread.sleep(100L);

			Entry<K, V> x=null;
			do
			{
				x = list.poll();
				if(x!=null)
				{
					ai2.incrementAndGet();
					if(list.peek()==null)
						http(x,true);
					else
						http(x,false);
					System.out.println(id+" "+x.getKey());
					fos.write(x.getKey().toString().getBytes());
					fos.write("\n".getBytes());
				}
			}while(x!=null);
			
			fos.close();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	private void http(Entry<K, V> x, boolean close) throws Exception
	{
		if(out==null)
			out = JerseyClientBuilder.createClient(config);
		String contentDisposition = "attachment; filename=\"" + x.getKey().toString() + "\"";
		   
		WebTarget target = out.target("http://localhost:1111/").path("media");
		Response ret= target.path("/mp3").queryParam("path", x.getKey().toString())
		.request()
		.header("Content-Type", MediaType.APPLICATION_OCTET_STREAM)
		.header("path", x.getKey().toString())
		.header("Content-Disposition", contentDisposition)
		.post(Entity.entity(new FileInputStream(x.getKey().toString()), MediaType.APPLICATION_OCTET_STREAM) );
		System.err.println(target.path("mp3").getUri().toString());
		System.out.println(ret.getStatus());
		System.out.println(ret.readEntity(String.class));
	}

}
