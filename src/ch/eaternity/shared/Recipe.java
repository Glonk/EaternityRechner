package ch.eaternity.shared;


import com.google.gwt.user.client.rpc.IsSerializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.condition.IfTrue;
import com.googlecode.objectify.condition.IfFalse;
import com.googlecode.objectify.annotation.*;

@Entity
public class Recipe implements IsSerializable {
 
	private static final long serialVersionUID = -5888386800366492104L;
		
	private @Id Long id;
    
	private Long ancestorId;
	
	@Index(IfFalse.class)
	private boolean deleted;

	private String symbol;
	private String subTitle;
	private String cookInstruction;
	private UploadedImage image;
	
	@Index
	private Long userId;
	@Index
	private Long kitchenId; // empty is no kitchen...
	
	private String shortUrl;
	
	private Long servings;
	
	@Embed
	private List<SavingPotential> savingPotentials = new ArrayList<SavingPotential>();

	@Embed
	private List<Ingredient> ingredients = new ArrayList<Ingredient>();
   
	
	@Index(IfTrue.class)
	private Boolean publicationRequested;
	
	@Index(IfTrue.class)
	private Boolean published;
	
	private Date createDate;
	private Date cookingDate;
	private Long hits;
	private Long popularity;
	private Boolean eaternitySelected;
	private Boolean bio;
	private Boolean regsas;

	@Ignore
	private Boolean selected;

	public Recipe() {
		symbol = "Ihr Menu";
		subTitle = "Menu Beschreibung";
		cookInstruction = "Zubereitung";
		deleted = false;
		published = false;
		publicationRequested = false;
		selected = false;
		servings = 4L;
		eaternitySelected = false;
	}
	
	// clone Constructor
	public Recipe(Recipe toClone) {
		// call standard constructor
		this();
		this.ancestorId = toClone.id;
		this.symbol = new String(toClone.symbol);
		this.subTitle = new String(toClone.subTitle);
		this.cookInstruction = new String(toClone.cookInstruction);
		//not propper yet:
		this.image = toClone.image;
		
		this.subTitle = new String(toClone.subTitle);
		this.shortUrl = new String(toClone.shortUrl);
		this.kitchenId = toClone.kitchenId;
			
		this.servings = new Long(toClone.servings);
		this.createDate = (Date) toClone.createDate.clone();
		this.cookingDate = (Date) toClone.cookingDate.clone();
		this.hits = new Long(toClone.hits);
		this.popularity = new Long(toClone.popularity);
		
		this.selected = new Boolean(toClone.selected);
		
		for (SavingPotential com : toClone.savingPotentials) {
			savingPotentials.add(new SavingPotential(com));
		}
		
		for (Ingredient com : toClone.ingredients) {
			ingredients.add(new Ingredient(com));
		}
		
		
		this.publicationRequested = new Boolean(toClone.publicationRequested);
		this.published = new Boolean(toClone.published);
		this.eaternitySelected = new Boolean(toClone.eaternitySelected);
		this.bio = new Boolean(toClone.bio);
		this.regsas = new Boolean(toClone.regsas);
	}

	public void addIngredient(Ingredient ingSpec) {
		this.ingredients.add(ingSpec);	
	}

	
	public void removeIngredientByIndex(int index) {
		this.ingredients.remove(index);
	}

	public void removeIngredient(Ingredient ingSpec) {
		int removedIndex = getIngredients().indexOf(ingSpec);
		this.ingredients.remove(removedIndex);
	}


	public double getDeviceCo2Value() {
		/*
		double sum = 0;
		if(energyMix != null && deviceSpecifications != null && deviceSpecifications.size()>0 ){
		for(DeviceSpecification device:deviceSpecifications){
			sum += device.duration * device.kWConsumption * energyMix.Co2PerKWh;
		}
		}
		return sum;
		*/
		return 0d;
	}
	
	//TODO change to quantity
	public Double getCO2Value() {
		double value = getDeviceCo2Value();
		
		for ( Ingredient zutatSpec : ingredients){
			value += zutatSpec.getCalculatedCO2Value();
		}
		if(servings != null && servings != 0)
			value = value/servings;

		return value;
	}
	

	// ------------------- getters and setters --------------------
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getAncestorId() {
		return ancestorId;
	}

	public void setAncestorId(Long directAncestorID) {
		this.ancestorId = directAncestorID;
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

	
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userID) {
		this.userId = userID;
	}


	public String getShortUrl() {
		return shortUrl;
	}

	public void setShortUrl(String shortUrl) {
		this.shortUrl = shortUrl;
	}

	public Long getKitchenId() {
		return kitchenId;
	}

	public void setKitchenId(Long kitchenId) {
		this.kitchenId = kitchenId;
	}

	public Long getPersons() {
		return servings;
	}

	public void setPersons(Long persons) {
		this.servings = persons;
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

	public List<SavingPotential> getSavingPotentials() {
		return savingPotentials;
	}

	public void setSavingPotentials(List<SavingPotential> savingPotentials) {
		this.savingPotentials = savingPotentials;
	}

	public List<Ingredient> getIngredients() {
		return ingredients;
	}

	public void setIngredients(List<Ingredient> ingredients) {
		this.ingredients = ingredients;
	}

	public Boolean getOpenRequested() {
		return publicationRequested;
	}

	public void setOpenRequested(Boolean openRequested) {
		this.publicationRequested = openRequested;
	}

	public Boolean getOpen() {
		return published;
	}

	public void setOpen(Boolean open) {
		this.published = open;
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



	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}


}