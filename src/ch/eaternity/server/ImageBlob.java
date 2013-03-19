package ch.eaternity.server;


import com.googlecode.objectify.annotation.*;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.users.User;

@Entity
public class ImageBlob{


	@Id private Long id;
	
	
//	private Date createDate;
	
	
	private Blob picture;
	
	
	private User uploader;
	
	
	private String name;
	
	
//	private String source;

	public ImageBlob(){
		
	}

	public ImageBlob(String name, Blob picture) {
		this.setPicture(picture);
		this.setName(name);
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

//	public void setCreateDate(Date createDate) {
//		this.createDate = createDate;
//	}
//
//	public Date getCreateDate() {
//		return createDate;
//	}

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

//	public void setSource(String source) {
//		this.source = source;
//	}
//
//	public String getSource() {
//		return source;
//	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}



}
