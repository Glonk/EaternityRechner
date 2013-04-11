package ch.eaternity.shared;


import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.annotation.*;

@Entity
public class Kitchen implements Serializable {
 
	private static final long serialVersionUID = 8711036976355728738L;

	@Id private Long id;
	
	// Load that on loading the whole kitchen
	@Ignore
	private List<UserInfo> userInfos = new ArrayList<UserInfo>();
	
	/**
	 *  many to many relationship stored in both object -> update properly!
	 */
	private List<String> userIds = new ArrayList<String>();
	
	/**
	 *  here the user with not UserInfo yet are stored, converted as soon as they registrate
	 *  <Name, mailadress>
	 */
	private List<Pair<String, String>> unmatchedUsers;
	
	/**
	 * Name of the kitchen
	 */
	private String symbol;

	@Embed
	private UploadedImage image;
	
	// location is a valid Google Maps location (processLocation() returns true)
	private String processedLocation;
	
	
	@Serialize // because it has a collection inside...
	private List<Device> devices = new ArrayList<Device>();
	@Embed
	private EnergyMix energyMix = new EnergyMix();
    
	@Ignore
	private Boolean changed;
	

	public Kitchen() {
		symbol = "Neue Küche";
		energyMix = new EnergyMix("ewz.naturpower",0.01345);
		changed = false;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public UploadedImage getImage() {
		return image;
	}

	public void setImage(UploadedImage image) {
		this.image = image;
	}

	public String getProcessedLocation() {
		return processedLocation;
	}

	public void setProcessedLocation(String location) {
		this.processedLocation = location;
	}


	public Boolean getChanged() {
		return changed;
	}

	public void setChanged(Boolean changed) {
		this.changed = changed;
	}

	public List<Device> getDevices() {
		return devices;
	}

	public void setDevices(ArrayList<Device> devices) {
		this.devices = devices;
	}

	public EnergyMix getEnergyMix() {
		return energyMix;
	}

	public void setEnergyMix(EnergyMix energyMix) {
		this.energyMix = energyMix;
	}

	public List<UserInfo> getUserInfos() {
		return userInfos;
	}

	public void setUserInfos(List<UserInfo> personal) {
		this.userInfos = personal;
	}

	public List<String> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<String> userIds) {
		this.userIds = userIds;
	}

	public List<Pair<String, String>> getUnmatchedUsers() {
		return unmatchedUsers;
	}

	public void setUnmatchedUsers(List<Pair<String, String>> unmatchedUsers) {
		this.unmatchedUsers = unmatchedUsers;
	}
	






}