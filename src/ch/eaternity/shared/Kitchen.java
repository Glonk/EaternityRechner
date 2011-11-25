package ch.eaternity.shared;


import java.io.Serializable;
import java.util.ArrayList;

import javax.persistence.Embedded;
import javax.persistence.Id;


import com.googlecode.objectify.annotation.Serialized;


public class Kitchen implements Serializable, Cloneable{
 
	private static final long serialVersionUID = 8711036976355728738L;

	@Id
	public Long id;
    
	private String symbol;

	public UploadedImage image;
	
	@Embedded
	public EnergyMix energyMix;
	
	public String location;
	
	private String emailAddressOwner;

	
//	@Serialized
	
//	@Embedded
	
	@Serialized
	public ArrayList<Device> devices = new ArrayList<Device>();
	
	// here the staff is also stored! (double storage)
//	@Serialized
//	@Embedded
	
	@Serialized
	public ArrayList<Staff> personal = new ArrayList<Staff>();
    
    
	public Boolean openRequested;
	public Boolean open;
	public Boolean approvedOpen;

	public Boolean hasChanged;


	public Kitchen() {

	}

	public Kitchen(String symbol) {
		this.symbol = symbol;
	}
	
	public Kitchen(String symbol, String location) {
		this.symbol = symbol;
		this.location = location;
	}

	public Kitchen(Long id, String symbol) {
		this();

		this.symbol = symbol;
	}



	public String getSymbol() {
		return this.symbol;
	}


	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}



	public void setOpen(boolean open) {
		this.open = open;
	}

	public boolean isOpen() {
		return open;
	}


	public void setEmailAddressOwner(String emailAddressOwner) {
		this.emailAddressOwner = emailAddressOwner;
	}

	public String getEmailAddressOwner() {
		return emailAddressOwner;
	}





}