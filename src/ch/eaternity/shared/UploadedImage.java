package ch.eaternity.shared;


import com.google.gwt.user.client.rpc.IsSerializable;
import java.util.Date;
import java.util.List;

import com.googlecode.objectify.annotation.*;

@Entity
public class UploadedImage implements IsSerializable {

	private static final long serialVersionUID = 588767248962315221L;
	public static final String SERVING_URL = "servingUrl";
	public static final String CREATED_AT = "createdAt";
	public static final String OWNER_ID = "ownerId";

	@Id private Long id;
	
	String key;
	String servingUrl;
	Date createdAt;
	String ownerId; // Refers to the User that uploaded this

	@Embed
	List<Tag> tags;
	
	public UploadedImage() {}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getServingUrl() {
		return servingUrl;
	}

	public void setServingUrl(String servingUrl) {
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
