package com.boleslaw.client;

import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class Poster 
{
	public static void main(String args[]) throws Exception
	{
		if(args==null||args.length<1)
			throw new Exception ("please pass dir and filter");
		long start = System.currentTimeMillis();
		ScanFiles.scan(args[0],args[1]);
		LinkedBlockingQueue<Entry<String, Long>> list = new LinkedBlockingQueue<>();
		list.addAll(ScanFiles.getList().entrySet());
		int threads =1;
		ForkJoinPool pool = new ForkJoinPool(threads);
		for(int i=0;i<threads;i++)
		{
			pool.execute(new FileToHttpConsumer<>(list));
		}
		pool.shutdown();
		pool.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
		System.out.println("done "+FileToHttpConsumer.ai2.get()+"\ttime:"+(System.currentTimeMillis()-start));
	}
}
