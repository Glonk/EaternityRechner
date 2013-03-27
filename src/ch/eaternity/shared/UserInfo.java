package ch.eaternity.shared;

import com.google.gwt.user.client.rpc.IsSerializable;
import java.util.List;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.*;

@Entity
public class UserInfo implements IsSerializable {

	private static final long serialVersionUID = 8516034014140362835L;
	
	// the id is the same as the google user id fetched from User.getId()
	@Id private Long id;

	private Long currentKitchenId;
	
	// many to many relationship stored in both object -> update properly!
	private List<Long> kitchenIds;
	
	@Ignore
	private String loginUrl;
	@Ignore
	private String logoutUrl;
	
	private String emailAddress;
	private String nickname;
	
	private int currentMonth;
	
	/**
	 * currentLocation is a valid Google Maps location (processed)
	 */
	private String currentLocation;
	
	private boolean isadmin;
	private boolean loggedIn;
	
	

	public UserInfo() {
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
		if (kitchenIds.contains(kitchenId)) {
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
		return kitchenIds;
	}

	public void setKitchenIDs(List<Long> kitchenIDs) {
		this.kitchenIds = kitchenIDs;
	}

}