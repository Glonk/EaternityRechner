package ch.eaternity.shared;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


import javax.persistence.Embedded;
import javax.persistence.Id;
import javax.persistence.Transient;



import com.google.api.gwt.services.urlshortener.shared.model.Url;
import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.Serialized;


public class Recipe implements Serializable, Cloneable{
 

	/**
	 * 
	 */
	private static final long serialVersionUID = -5888386800366492104L;
		
	@Id Long id;
    
	private Long directAncestorID;
	private List<Long> descandentsIds =  new ArrayList<Long>();
	public Boolean ancestorAlreadyMarked;
	
	private String symbol;
	private String subTitle;
	private String cookInstruction;
	public UploadedImage image;
	
	@Indexed
	private String emailAddressOwner;
	
	@Embedded
	public EnergyMix energyMix;
	
	public String ShortUrl;
	public List<Long> kitchenIds =  new ArrayList<Long>(); // empty is no kitchen...
	
	private Long persons;
	
	private Date createDate;
	public Date cookingDate;
	private Long hits;
	private Long popularity;
	
	@Transient
	private Boolean selected;
	
	@Serialized
	public ArrayList<RecipeComment> comments = new ArrayList<RecipeComment>();

	@Serialized
	public ArrayList<IngredientSpecification> ingredients = new ArrayList<IngredientSpecification>();
	
	@Serialized
	public ArrayList<DeviceSpecification> deviceSpecifications = new ArrayList<DeviceSpecification>();
    
	private Double CO2Value;
	public Boolean openRequested;
	public Boolean open;
	public Boolean eaternitySelected;
	public Boolean bio;
	public Boolean regsas;

	

	public Recipe() {
		symbol = "Ihr Menu";
		subTitle = "Menu Beschreibung";
		cookInstruction = "Sie sind nicht angemeldet. Alle Änderungen am Rezept können nicht gespeichert werden.";
		open = false;
		openRequested = true;
		selected = false;
		persons = 4L;
		eaternitySelected = false;
	}
	
	// clone Constructor
	public Recipe(Recipe toClone) {
		// call standard constructor
		this();
		this.directAncestorID = toClone.id;
		this.ancestorAlreadyMarked = false;
		this.symbol = new String(toClone.symbol);
		this.subTitle = new String(toClone.subTitle);
		this.cookInstruction = new String(toClone.cookInstruction);
		//not propper yet:
		this.image = toClone.image;
		
		this.subTitle = new String(toClone.subTitle);
		this.energyMix = new EnergyMix(toClone.energyMix);
		this.ShortUrl = new String(toClone.ShortUrl);
		
		for (Long kID : toClone.kitchenIds) {
			kitchenIds.add(new Long(kID));
		}
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
	}


	public String getSymbol() {
		return this.symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public void addZutaten(List<IngredientSpecification> zutaten) {
		for(IngredientSpecification zutat : zutaten){
			this.ingredients.add(zutat);
		}
		
	}

	public ArrayList<IngredientSpecification> getZutaten() {
		return this.ingredients;
	}

	public void setZutaten(ArrayList<IngredientSpecification> zutaten) {
			this.ingredients = zutaten;

	}
	
	public void removeZutat(int index) {
		this.ingredients.remove(index);
	}


	public void setOpen(boolean open) {
		this.open = open;
	}

	public boolean isOpen() {
		return open;
	}

	public void setHits(Long hits) {
		this.hits = hits;
	}

	public Long getHits() {
		return hits;
	}

	public void setPopularity(Long popularity) {
		this.popularity = popularity;
	}

	public Long getPopularity() {
		return popularity;
	}
	public Recipe getRezept(){
		return this;
	}

	

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCO2Value() {

		
		double sum = getDeviceCo2Value();
		
		for ( IngredientSpecification zutatSpec : ingredients){
			sum += zutatSpec.getCalculatedCO2Value();
		}
		if(persons != null){
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

	public double getCO2Value() {
		return CO2Value;
	}

	public void setCookInstruction(String cookInstruction) {
		this.cookInstruction = cookInstruction;
	}

	public String getCookInstruction() {
		return cookInstruction;
	}

	public void setPersons(Long persons) {
		this.persons = persons;
	}

	public Long getPersons() {
		return persons;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
	}

	public Boolean getSelected() {
		return selected;
	}

	public void setEmailAddressOwner(String emailAddressOwner) {
		this.emailAddressOwner = emailAddressOwner;
	}

	public String getEmailAddressOwner() {
		return emailAddressOwner;
	}

	public void setDirectAncestorID(Long directAncestorID) {
		this.directAncestorID = directAncestorID;
	}

	public Long getDirectAncestorID() {
		return directAncestorID;
	}

	public void setDirectDescandentID(List<Long> directDescandentID) {
		this.descandentsIds = directDescandentID;
	}
	
	public void addDirectDescandentID(Long directDescandentID) {
		if(!this.descandentsIds.contains(directDescandentID)){
			this.descandentsIds.add(directDescandentID);
		}
	}

	public List<Long> getDirectDescandentID() {
		return descandentsIds;
	}





}