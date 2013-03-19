package ch.eaternity.shared;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.annotation.*;

@Entity
public class Kitchen implements Serializable {
 
	private static final long serialVersionUID = 8711036976355728738L;

	@Id private Long id;
    
	private String symbol;

	@Embed
	private UploadedImage image;
	
	// location is a valid Google Maps location (processLocation() returns true)
	private String processedLocation;
	
	private String emailAddressOwner;
	
	@Embed
	private List<Device> devices = new ArrayList<Device>();
	@Embed
	private EnergyMix energyMix;
	
	@Embed
	private List<LoginInfo> personal = new ArrayList<LoginInfo>();
    
	@Ignore
	private Boolean changed;
	
    /*
	public Boolean openRequested;
	public Boolean open;
	public Boolean approvedOpen;
     */
	

	public Kitchen() {

	}

	public Kitchen(String symbol) {
		this.symbol = symbol;
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

	public String getLocation() {
		return processedLocation;
	}

	public void setLocation(String location) {
		this.processedLocation = location;
	}

	public String getEmailAddressOwner() {
		return emailAddressOwner;
	}

	public void setEmailAddressOwner(String emailAddressOwner) {
		this.emailAddressOwner = emailAddressOwner;
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

	public List<LoginInfo> getPersonal() {
		return personal;
	}

	public void setPersonal(List<LoginInfo> personal) {
		this.personal = personal;
	}
	






}