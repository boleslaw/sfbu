package com.boleslaw;

import java.io.*;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;

@Path("/media")
@Produces(MediaType.TEXT_PLAIN)
@Singleton
public class MediaServlet 
{
	static final String wd = System.getProperty("lazy.depot.wd","/dev/shm/ld/");
	private static final Configuration conf = new Configuration();
	private SequenceFile.Writer writer;
	@PostConstruct
	public void setRD()
	{
		try {
			File f = new File(wd);
			if(!f.exists())
				f.mkdirs();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@GET
	public Response getInfo()
	{
		return Response.ok(usage()+"\n").build();
	}
	@POST
	@Path("/mp3")
	public Response push(InputStream in,@QueryParam("path") String path,
			@DefaultValue("true") @QueryParam("pretty") boolean pretty,
			@DefaultValue("false") @QueryParam("close") boolean close
			)
	{
		StringBuffer sb = new StringBuffer();
		try {
			MediaObject out = new MediaObject(in,path);
			String name = wd+out.getId();
//			FileOutputStream fos = new FileOutputStream(name);
//			fos.write(out.getFile());
//			fos.close();
			Mp3File ff;
			try {
				ff = new Mp3File(name);
				out.getTags().add("bitRate:"+ff.getBitrate());
				out.getTags().add("lengthS:"+ff.getLengthInSeconds());
				out.getTags().add("channelMode:"+ff.getChannelMode());
				out.getTags().add("sampleRate:"+ff.getSampleRate());
				ID3v1 i1 = ff.getId3v1Tag();
				if(i1!=null)
				{
					if(i1.getArtist()!=null)
						out.getTags().add(i1.getArtist());
					if(i1.getAlbum()!=null)
						out.getTags().add(i1.getAlbum());
					if(i1.getTitle()!=null)
						out.getTags().add(i1.getTitle());
				}
				ID3v2 i2 = ff.getId3v2Tag();
				if(i2!=null)
				{
					if(i2.getArtist()!=null)
						out.getTags().add(i2.getArtist());
					if(i2.getAlbum()!=null)
						out.getTags().add(i2.getAlbum());
					if(i2.getTitle()!=null)
						out.getTags().add(i2.getTitle());
				}
			} catch (Exception e)
			{
				System.err.println("Error for "+name);
			}
		
			write(new org.apache.hadoop.fs.Path(wd+"data."+System.currentTimeMillis()+".sf"),
					out.getFile(),out,close);
			out.setFile(null);
			if(!pretty)
				sb.append(new Gson().toJson(out)+"\n");
			else
				sb.append(new GsonBuilder().setPrettyPrinting().create().toJson(out)+"\n");
		} catch (Exception e) {
			e.printStackTrace();
			sb.append(e.getMessage()+"\n"+usage()+"\n");
		}
		
		return Response.ok(sb.toString()).build();
	}
	private String usage() {
		return "usage: curl -XPOST 'http://localhost:1111/media/mp3?path=fullPath' -d @filename ";
	}
	private void write(org.apache.hadoop.fs.Path path, byte[] file, MediaObject mo, boolean close)
	{
		
		try {
			if(writer == null)
			{
				writer = SequenceFile.createWriter(FileSystem.get(conf),conf , path, Text.class, BytesWritable.class);
			}
			writer.append(new Text(new Gson().toJson(mo)), new BytesWritable(file));
			if(close)
			{
				writer.close();
				writer=null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
