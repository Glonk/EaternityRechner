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
	
	@Id private String id;
	
	// standard values
	private boolean loggedIn;
	
	@Transient
	private String loginUrl;
	@Transient
	private String logoutUrl;
	
	private String emailAddress;
	private String nickname;
	private boolean admin;

	// eaternity-rechner stuff
	//werden die ueberhaupt gespeichert? weil ev. Google Objekt
	private boolean isInKitchen;
	private Long currentKitchen;
	private List<Long> kitchenIDs;
	// private String lastLogin;
	// private String lastLocation;
	

	public LoginInfo() {
		admin = false;
		isInKitchen = false;
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

	public void setIsInKitchen(Boolean isInKitchen) {
		this.isInKitchen = isInKitchen;
	}

	public Boolean getIsInKitchen() {
		return isInKitchen;
	}

	public void setLastKitchen(Long lastKitchen) {
		this.currentKitchen = lastKitchen;
	}

	public Long getLastKitchen() {
		return currentKitchen;
	}


	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
}