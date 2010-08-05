package ch.eaternity.server;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.User;


@PersistenceCapable // (identityType = IdentityType.APPLICATION)
public class ImageBlob{

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;
	
	@Persistent
	private Date createDate;
	
	@Persistent
	private Blob picture;
	
	@Persistent
	private User uploader;
	
	@Persistent
	private String name;
	
	@Persistent
	private String source;


	public ImageBlob(String name, Blob picture) {
		this.setPicture(picture);
		this.setName(name);
		// TODO Auto-generated constructor stub
	}

	public void setId(Key id) {
		this.id = id;
	}

	public Key getId() {
		return id;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setPicture(Blob picture) {
		this.picture = picture;
	}

	public Blob getPicture() {
		return picture;
	}

	public void setUploader(User uploader) {
		this.uploader = uploader;
	}

	public User getUploader() {
		return uploader;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getSource() {
		return source;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}



}
