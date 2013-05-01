package ch.eaternity.shared;


import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.googlecode.objectify.annotation.*;

@Entity
public class UploadedImage implements Serializable {

	private static final long serialVersionUID = 588767248962315221L;
	public static final String SERVING_URL = "servingUrl";
	public static final String CREATED_AT = "createdAt";
	public static final String OWNER_ID = "ownerId";

	@Id private Long id;
	
	private String key;
	private String servingUrl;
	private Date createdAt;
	private String ownerId; // Refers to the User that uploaded this

	@Serialize 
	private List<Tag> tags;
	
	public UploadedImage() {}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getUrl() {
		return servingUrl;
	}

	public void setUrl(String servingUrl) {
		this.servingUrl = servingUrl;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

}
