package ch.eaternity.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.*;

@Entity
@Cache
public class UserInfo implements Serializable {

	private static final long serialVersionUID = 3457245750362835L;
	
	// the id is the same as the google user id fetched from User.getId()
	@Id private String id;

	private Long currentKitchenId = null;
	
	// many to many relationship stored in both object -> update properly!
	// at the moment not used in fetching the kitchens...
	@Index
	private List<Long> kitchenIds = new ArrayList<Long>();
	
	@Ignore
	private String loginUrl;
	@Ignore
	private String logoutUrl;
	
	/* not really representative, (double counting, every refresh counting but for comparing ok */
	private Long loginCount = 0L;
	
	@Index
	private String emailAddress;
	private String nickname;

	private Date currentDate;
	
	/**
	 * currentLocation is a valid Google Maps location (processed)
	 */
	private String verifiedLocation;
	
	private boolean isadmin = false;
	private boolean loggedIn = false;
	private boolean enabled = false;
	

	public UserInfo() {
		this.verifiedLocation = "Zürich, Schweiz";
		this.currentDate = new Date();
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

	public Long getLoginCount() {
		return loginCount;
	}

	public void setLoginCount(Long loginCount) {
		this.loginCount = loginCount;
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


	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public List<Long> getKitchenIDs() {
		return kitchenIds;
	}

	public void setKitchenIDs(List<Long> kitchenIDs) {
		this.kitchenIds = kitchenIDs;
	}

	public Date getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}

	public String getVerifiedLocation() {
		return verifiedLocation;
	}

	public void setVerifiedLocation(String verifiedLocation) {
		this.verifiedLocation = verifiedLocation;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}