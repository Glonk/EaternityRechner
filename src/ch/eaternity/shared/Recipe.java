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
	private Long hits;
	private Long popularity;
	
	@Transient
	private Boolean selected = false;
	
	@Serialized
	public ArrayList<RecipeComment> comments = new ArrayList<RecipeComment>();
	
    // @Persistent //(mappedBy = "recipe") //, defaultFetchGroup = "true")
//    @Element(dependent = "true")
	@Serialized
	public ArrayList<IngredientSpecification> Zutaten = new ArrayList<IngredientSpecification>();
	
	@Serialized
	public ArrayList<DeviceSpecification> deviceSpecifications = new ArrayList<DeviceSpecification>();

    
//    @Persistent 
//    private List<String> ZutatSpecificationKeys = new ArrayList<String>(); 
    
	private Double CO2Value;
	public Boolean openRequested;
	public Boolean open;
	public Boolean eaternitySelected;
	public Boolean bio;
	public Boolean regsas;

	

	public Recipe() {

	}

	public Recipe(String symbol) {
		this.symbol = symbol;
	}

	public Recipe(Long id, String symbol) {
		this();

		this.symbol = symbol;
	}



	public String getSymbol() {
		return this.symbol;
	}





	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public void addZutaten(List<IngredientSpecification> zutaten) {
		for(IngredientSpecification zutat : zutaten){
//			zutat.setRezept(this);
			this.Zutaten.add(zutat);
		}
		
	}

	public ArrayList<IngredientSpecification> getZutaten() {
		return this.Zutaten;
	}

	public void setZutaten(ArrayList<IngredientSpecification> zutaten) {
			this.Zutaten = zutaten;

	}
	
	public void removeZutat(int index) {
		this.Zutaten.remove(index);
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
		
		for ( IngredientSpecification zutatSpec : Zutaten){
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