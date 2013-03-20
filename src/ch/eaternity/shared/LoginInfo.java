package ch.eaternity.shared;

import java.io.Serializable;
import java.util.List;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.*;

@Entity
public class LoginInfo implements Serializable {

	private static final long serialVersionUID = 8516034014140362835L;
	
	@Id private Long id;
	
	private boolean loggedIn;
	
	@Ignore
	private String loginUrl;
	@Ignore
	private String logoutUrl;
	
	private String emailAddress;
	private String nickname;
	private boolean isadmin;
	
	// saving the id form the Google User here
	private Long userId;

	//Probably in future like that:
	// @Load private Ref<Kitchen> currentKitchenId;
	private Long currentKitchenId;
	private List<Long> kitchenIDs;

	public LoginInfo() {
		isadmin = false;
		currentKitchenId = null;
		loggedIn = false;
	}
	
	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	public String getLoginUrl() {
		return loginUrl;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public String getLogoutUrl() {
		return logoutUrl;
	}

	public void setLogoutUrl(String logoutUrl) {
		this.logoutUrl = logoutUrl;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public void setAdmin(boolean admin) {
		this.isadmin = admin;
	}

	public boolean isAdmin() {
		return this.isadmin;
	}

	public boolean setCurrentKitchen(Long kitchenId) {
		if (kitchenIDs.contains(kitchenId)) {
			this.currentKitchenId = kitchenId;
			return true;
		}
		else
			return false;
	}

	public Long getCurrentKitchen() {
		return currentKitchenId;
	}


	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public List<Long> getKitchenIDs() {
		return kitchenIDs;
	}

	public void setKitchenIDs(List<Long> kitchenIDs) {
		this.kitchenIDs = kitchenIDs;
	}

}