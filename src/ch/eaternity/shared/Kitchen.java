package ch.eaternity.shared;


import java.io.Serializable;
import java.util.ArrayList;
import javax.persistence.Id;


import com.googlecode.objectify.annotation.Serialized;


public class Kitchen implements Serializable, Cloneable{
 
	private static final long serialVersionUID = 8711036976355728738L;

	@Id
	public Long id;
    
	private String symbol;

	public UploadedImage image;
	
	
	public String energyMix;
	public String location;
	
	private String emailAddressOwner;

	
	@Serialized
	public ArrayList<Device> devices = new ArrayList<Device>();
	
	@Serialized
	public ArrayList<User> personal = new ArrayList<User>();
    
    
	public Boolean openRequested;
	public Boolean open;
	public Boolean approvedOpen;


	public Kitchen() {

	}

	public Kitchen(String symbol) {
		this.symbol = symbol;
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