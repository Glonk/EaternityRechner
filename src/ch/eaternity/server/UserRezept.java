package ch.eaternity.server;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import com.google.appengine.api.datastore.Key;

import ch.eaternity.shared.Rezept;

import com.google.appengine.api.users.User;

@PersistenceCapable // (identityType = IdentityType.APPLICATION)
public class UserRezept{

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;
	
	@Persistent
	private Date createDate;
	
	@Persistent
	private User user;
	
	@Persistent
	private Rezept rezept;
	  
	@Persistent
	private String rezeptKey;
	
	public void setUser(User user) {
		this.user = user;
	}
	public User getUser() {
		return user;
	}

	public UserRezept( User user){
		this.createDate = new Date();
		this.user = user;	
	}
	
	public UserRezept( Rezept rezept ,User user){
		this.rezept = rezept;
		this.createDate = new Date();
		this.user = user;	
	}

	public Date getCreateDate() {
		return this.createDate;
	}
	public void setRezept(Rezept rezept) {
		this.rezept = rezept;
	}
	public Rezept getRezept() {
		return rezept;
	}
	public void setId(Key id) {
		this.id = id;
	}
	public Key getId() {
		return id;
	}
	public void setRezeptKey(String key) {
		this.rezeptKey = key;
	}
	public String getRezeptKey() {
		return rezeptKey;
	}




}
