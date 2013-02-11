package ch.eaternity.shared;


import java.io.Serializable;
import java.util.ArrayList;

import javax.persistence.Embedded;
import javax.persistence.Id;


import com.googlecode.objectify.annotation.Serialized;


public class Kitchen implements Serializable, Cloneable{
 
	private static final long serialVersionUID = 8711036976355728738L;

	@Id private Long id;
    
	private String symbol;

	private UploadedImage image;
	
	private String location;
	
	private String emailAddressOwner;
	
	private Boolean changed;
	
	@Serialized
	private ArrayList<Device> devices = new ArrayList<Device>();
	@Embedded
	private EnergyMix energyMix;
	
	@Serialized
	private ArrayList<Staff> personal = new ArrayList<Staff>();
    
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
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
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

	public ArrayList<Device> getDevices() {
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

	public ArrayList<Staff> getPersonal() {
		return personal;
	}

	public void setPersonal(ArrayList<Staff> personal) {
		this.personal = personal;
	}
	






}