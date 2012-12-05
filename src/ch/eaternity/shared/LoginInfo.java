package ch.eaternity.shared;

import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.Transient;

public class LoginInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8516034014140362835L;
	
	@Id private String id;
	
	// standard values
	private boolean loggedIn = false;
	
	@Transient
	private String loginUrl;
	@Transient
	private String logoutUrl;
	
	private String emailAddress;
	private String nickname;
	private boolean admin = false;

	// eaternity-rechner stuff
	//werden die ueberhaupt gespeichert? weil ev. Google Objekt
	private boolean usedLastKitchen = false;
	private Long lastKitchen;
	private String lastLogin;
	private String lastLocation;
	

	public LoginInfo() {}
	
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

	public void setInKitchen(Boolean inKitchen) {
		this.usedLastKitchen = inKitchen;
	}

	public Boolean getInKitchen() {
		return usedLastKitchen;
	}

	public void setLastKitchen(Long lastKitchen) {
		this.lastKitchen = lastKitchen;
	}

	public Long getLastKitchen() {
		return lastKitchen;
	}


	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
}