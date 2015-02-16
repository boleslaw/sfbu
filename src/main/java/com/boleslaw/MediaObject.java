package com.boleslaw;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.compress.utils.IOUtils;

public class MediaObject
{
	private String id = UUID.randomUUID().toString().replaceAll("-","");
	private String canonPath;
	private String simpleName;
	private transient byte[] file;
	private String originalDir;
	private long size;
	private List<String> tags;
	public MediaObject(InputStream in, String path) throws Exception 
	{
		tags=new LinkedList<>();
		if(path==null||in==null)
			throw new Exception("missing a parameter");
		path = path.replaceAll("\\\\", "/");
		if(path.endsWith("/"))
			path = path.substring(0,path.lastIndexOf("/"));
		int index=path.length();
		if(path.contains("/"))
		{
			index = path.lastIndexOf("/");
			this.simpleName=path.substring(index);
			this.canonPath=path.substring(0,index);
			for (String si:canonPath.split("/"))
			{
				if(si!=null&&si.trim().length()>0)
					tags.add(si);
			}
		}
		else
		{
			canonPath="tbd";
			simpleName=path;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		IOUtils.copy(in,baos);
		in.close();
		baos.close();
		this.file= baos.toByteArray();
		size=file.length;
	}
	public String getCanonPath() {
		return canonPath;
	}
	public void setCanonPath(String canonPath) {
		this.canonPath = canonPath;
	}
	public String getSimpleName() {
		return simpleName;
	}
	public void setSimpleName(String simpleName) {
		this.simpleName = simpleName;
	}
	public byte[] getFile() {
		return file;
	}
	public void setFile(byte[] file) {
		this.file = file;
	}
	public String getOriginalDir() {
		return originalDir;
	}
	public void setOriginalDir(String originalDir) {
		this.originalDir = originalDir;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
