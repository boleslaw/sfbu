# sfbu #

Sequence file backup util - a server and client for migrating media libraries without losing too much.  Basically files are bad and having so many makes for slow IO every time there is a computer migration for me. Sequence files are more modular than tar and should have good copy IO.  This is mostly a sample project so there might be excessive random technologies about the code.

### Client###
Scan a given directory, optionally include a simple filter

```java
import java.util.concurrent.LinkedBlockingQueue;
import java.util.*;
import java.util.Map.Entry;

import com.boleslaw.client.FileToHttpConsumer;
import com.boleslaw.client.Poster;
...
	@org.junit.Test
	public void test1()
	{
		
		LinkedBlockingQueue<Entry<String, Long>> in = new LinkedBlockingQueue<>();
		in.add(new AbstractMap.SimpleEntry<String, Long>("/dev/shm/test.xyz",1L) );
		in.add(new AbstractMap.SimpleEntry<String, Long>("/dev/shm/test2.xyz",2L) );
		FileToHttpConsumer<String, Long> te = new FileToHttpConsumer<>(in); 
		te.run();
	}
	@org.junit.Test
	public void test2() throws Exception
	{
		Poster.main(new String[]{"/media/username/old-old-old-comp", "xyz"});
	}
```

### Server###
Take files POST server:1111/media/XXX [octetstream]
Singleton
metadata about file is in the key, the value is the original byte[]
currently ~20MB/s IO

```java
new JettyServer().start();
```

### About###
Jersey - Jetty - Gradle - hadoop stack
Currently reads/writes local, eventually support hdfs and either yarn or mesos

### Future###
* merge sequence files
* index metadata in elasticsearch or other
* group by tags
* clone sf
* update if doesn't exist based on md5
* more file formats
