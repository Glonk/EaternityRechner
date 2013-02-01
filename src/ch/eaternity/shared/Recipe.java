package ch.eaternity.shared;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.Serialized;


public class Recipe implements Serializable, Cloneable{
 

	/**
	 * 
	 */
	private static final long serialVersionUID = -5888386800366492104L;
		
	@Id Long id;
    
	private Long directAncestorID;
	
	private String symbol;
	private String subTitle;
	private String cookInstruction;
	private UploadedImage image;
	
	@Indexed
	private String emailAddressOwner;
	
	@Embedded
	private EnergyMix energyMix;
	
	private String ShortUrl;
	// no List, just in one Kitchen
	private Long kitchenId; // empty is no kitchen...
	
	private Long persons;
	
	private Date createDate;
	private Date cookingDate;
	private Long hits;
	private Long popularity;
	
	@Transient
	private Boolean selected;
	
	@Serialized
	private List<RecipeComment> comments = new ArrayList<RecipeComment>();

	@Serialized
	private List<IngredientSpecification> ingredients = new ArrayList<IngredientSpecification>();
	
	@Serialized
	private List<DeviceSpecification> deviceSpecifications = new ArrayList<DeviceSpecification>();
    
	private Double CO2Value;
	private Boolean openRequested;
	private Boolean open;
	private Boolean eaternitySelected;
	private Boolean bio;
	private Boolean regsas;

	

	public Recipe() {
		symbol = "Ihr Menu";
		subTitle = "Menu Beschreibung";
		cookInstruction = "Sie sind nicht angemeldet. Alle Änderungen am Rezept können nicht gespeichert werden.";
		open = false;
		openRequested = true;
		selected = false;
		persons = 4L;
		eaternitySelected = false;
		setCO2Value();
	}
	
	// clone Constructor
	public Recipe(Recipe toClone) {
		// call standard constructor
		this();
		this.directAncestorID = toClone.id;
		this.symbol = new String(toClone.symbol);
		this.subTitle = new String(toClone.subTitle);
		this.cookInstruction = new String(toClone.cookInstruction);
		//not propper yet:
		this.image = toClone.image;
		
		this.subTitle = new String(toClone.subTitle);
		this.energyMix = new EnergyMix(toClone.energyMix);
		this.ShortUrl = new String(toClone.ShortUrl);
		this.kitchenId = toClone.kitchenId;
			
		this.persons = new Long(toClone.persons);
		this.createDate = (Date) toClone.createDate.clone();
		this.cookingDate = (Date) toClone.cookingDate.clone();
		this.hits = new Long(toClone.hits);
		this.popularity = new Long(toClone.popularity);
		
		this.selected = new Boolean(toClone.selected);
		
		for (RecipeComment com : toClone.comments) {
			comments.add(new RecipeComment(com));
		}
		
		for (IngredientSpecification com : toClone.ingredients) {
			ingredients.add(new IngredientSpecification(com));
		}
		
		for (DeviceSpecification com : toClone.deviceSpecifications) {
			deviceSpecifications.add(new DeviceSpecification(com));
		}
		
		this.CO2Value = new Double(toClone.CO2Value);
		this.openRequested = new Boolean(toClone.openRequested);
		this.open = new Boolean(toClone.open);
		this.eaternitySelected = new Boolean(toClone.eaternitySelected);
		this.bio = new Boolean(toClone.bio);
		this.regsas = new Boolean(toClone.regsas);
		setCO2Value();
	}

	public void addIngredient(IngredientSpecification ingSpec) {
		this.ingredients.add(ingSpec);	
	}

	
	public void removeIngredient(int index) {
		this.ingredients.remove(index);
	}



	public void setCO2Value() {
		double sum = getDeviceCo2Value();
		
		for ( IngredientSpecification zutatSpec : ingredients){
			sum += zutatSpec.getCalculatedCO2Value();
		}
		if(persons != null && persons != 0){
			CO2Value = sum/persons;
		} else {
			CO2Value = sum;
		}
		
	}

	public double getDeviceCo2Value() {
		double sum = 0;
		if(energyMix != null && deviceSpecifications != null && deviceSpecifications.size()>0 ){
		for(DeviceSpecification device:deviceSpecifications){
			sum += device.duration * device.kWConsumption * energyMix.Co2PerKWh;
		}
		}
		return sum;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getDirectAncestorID() {
		return directAncestorID;
	}

	public void setDirectAncestorID(Long directAncestorID) {
		this.directAncestorID = directAncestorID;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public String getCookInstruction() {
		return cookInstruction;
	}

	public void setCookInstruction(String cookInstruction) {
		this.cookInstruction = cookInstruction;
	}

	public UploadedImage getImage() {
		return image;
	}

	public void setImage(UploadedImage image) {
		this.image = image;
	}

	public String getEmailAddressOwner() {
		return emailAddressOwner;
	}

	public void setEmailAddressOwner(String emailAddressOwner) {
		this.emailAddressOwner = emailAddressOwner;
	}

	public EnergyMix getEnergyMix() {
		return energyMix;
	}

	public void setEnergyMix(EnergyMix energyMix) {
		this.energyMix = energyMix;
	}

	public String getShortUrl() {
		return ShortUrl;
	}

	public void setShortUrl(String shortUrl) {
		ShortUrl = shortUrl;
	}

	public Long getKitchenId() {
		return kitchenId;
	}

	public void setKitchenId(Long kitchenId) {
		this.kitchenId = kitchenId;
	}

	public Long getPersons() {
		return persons;
	}

	public void setPersons(Long persons) {
		this.persons = persons;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getCookingDate() {
		return cookingDate;
	}

	public void setCookingDate(Date cookingDate) {
		this.cookingDate = cookingDate;
	}

	public Long getHits() {
		return hits;
	}

	public void setHits(Long hits) {
		this.hits = hits;
	}

	public Long getPopularity() {
		return popularity;
	}

	public void setPopularity(Long popularity) {
		this.popularity = popularity;
	}

	public Boolean getSelected() {
		return selected;
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
	}

	public List<RecipeComment> getComments() {
		return comments;
	}

	public void setComments(List<RecipeComment> comments) {
		this.comments = comments;
	}

	public List<IngredientSpecification> getIngredients() {
		return ingredients;
	}

	public void setIngredients(List<IngredientSpecification> ingredients) {
		this.ingredients = ingredients;
	}

	public List<DeviceSpecification> getDeviceSpecifications() {
		return deviceSpecifications;
	}

	public void setDeviceSpecifications(
			List<DeviceSpecification> deviceSpecifications) {
		this.deviceSpecifications = deviceSpecifications;
	}

	public Double getCO2Value() {
		setCO2Value();
		return CO2Value;
	}


	public Boolean getOpenRequested() {
		return openRequested;
	}

	public void setOpenRequested(Boolean openRequested) {
		this.openRequested = openRequested;
	}

	public Boolean getOpen() {
		return open;
	}

	public void setOpen(Boolean open) {
		this.open = open;
	}

	public Boolean getEaternitySelected() {
		return eaternitySelected;
	}

	public void setEaternitySelected(Boolean eaternitySelected) {
		this.eaternitySelected = eaternitySelected;
	}

	public Boolean getBio() {
		return bio;
	}

	public void setBio(Boolean bio) {
		this.bio = bio;
	}

	public Boolean getRegsas() {
		return regsas;
	}

	public void setRegsas(Boolean regsas) {
		this.regsas = regsas;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	// ------------------- getters and setters --------------------




}