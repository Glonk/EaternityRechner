package ch.eaternity.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.List;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.*;

@Entity
@Cache
public class UserInfo implements Serializable {

	private static final long serialVersionUID = 3457245750362835L;
	
	// the id is the same as the google user id fetched from User.getId()
	@Id private Long id;

	private Long currentKitchenId = null;
	
	// many to many relationship stored in both object -> update properly!
	@Embed
	private List<Long> kitchenIds;
	
	@Ignore
	private String loginUrl;
	@Ignore
	private String logoutUrl;
	
	private String emailAddress;
	private String nickname;
	
	// probably date??
	private int currentMonth;
	
	/**
	 * currentLocation is a valid Google Maps location (processed)
	 */
	private String currentLocation;
	
	private boolean isadmin = false;
	private boolean loggedIn = false;
	private boolean enabled = true;
	

	public UserInfo() {}
	
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

	public int getCurrentMonth() {
		return currentMonth;
	}

	public void setCurrentMonth(int currentMonth) {
		this.currentMonth = currentMonth;
	}

	public String getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(String currentLocation) {
		this.currentLocation = currentLocation;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}