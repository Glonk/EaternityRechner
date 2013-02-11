package ch.eaternity.shared;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Transient;


public class LoginInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8516034014140362835L;
	
	@Id private Long id;
	
	// standard values
	private boolean loggedIn;
	
	@Transient
	private String loginUrl;
	@Transient
	private String logoutUrl;
	
	private String emailAddress;
	private String nickname;
	private boolean admin;

	private Long currentKitchenId;
	private List<Long> kitchenIDs;

	public LoginInfo() {
		admin = false;
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
		this.admin = admin;
	}

	public boolean isAdmin() {
		return this.admin;
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