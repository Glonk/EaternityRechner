package ch.eaternity.server;

import java.util.Date;

import javax.persistence.Id;

import ch.eaternity.shared.Recipe;

import com.google.appengine.api.users.User;
import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.Serialized;


public class UserRecipeWrapper{

	@Id Long id;
	
	private Date createDate;
	
	@Indexed private User user;
	@Indexed private Long kitchenId;


	// for fast access requests
	private Boolean approvedOpen;
	private Boolean requestedOpen;
	
	@Serialized private Recipe recipe;
	
	
	public UserRecipeWrapper(){}
	
	
	public void setUser(User user) {
		this.user = user;
	}
	public User getUser() {
		return user;
	}

	public UserRecipeWrapper( User user){
		this.createDate = new Date();
		this.user = user;	
	}
	
	public UserRecipeWrapper( Recipe recipe ,User user){
		this.recipe = recipe;
		this.createDate = new Date();
		this.user = user;	
	}

	public Long getKitchenId() {
		return kitchenId;
	}


	public void setKitchenId(Long kitchenId) {
		this.kitchenId = kitchenId;
	}


	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	public Date getCreateDate() {
		return this.createDate;
	}
	public void setRecipe(Recipe recipe) {
		this.recipe = recipe;
	}
	public Recipe getRecipe() {
		return recipe;
	}

	public String getUserName() {
		return user.getNickname();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Boolean getApprovedOpen() {
		return approvedOpen;
	}


	public void setApprovedOpen(Boolean approvedOpen) {
		this.approvedOpen = approvedOpen;
	}


	public Boolean getRequestedOpen() {
		return requestedOpen;
	}


	public void setRequestedOpen(Boolean requestedOpen) {
		this.requestedOpen = requestedOpen;
	}





}
