package ch.eaternity.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eaticious.common.Quantity;
import org.eaticious.common.QuantityImpl;
import org.eaticious.common.Unit;

import com.googlecode.objectify.annotation.*;

@Entity
@Cache
public class FoodProduct implements Serializable {

	private static final long serialVersionUID = -3996987374367823464L;
	
    @Id private Long id;

    private String name;
    private String name_en;
    
    private List<String> synonyms = new ArrayList<String>();

    /**
     *  in kg / kg or g / g (the same ...)
    */
    private Double co2eValue;
    @Embed
    private QuantityImpl stdWeight;
    
    private List<Long> substituteIds = new ArrayList<Long>();

    @Embed
    private Season season;
    
    private boolean seasonDependant;
	
    private List<String> tags = new ArrayList<String>();
    
    @Serialize
    private List<Extraction> extractions = new ArrayList<Extraction>();
    @Serialize
    private List<Condition> conditions = new ArrayList<Condition>();
    @Serialize
    private List<Production> productions  = new ArrayList<Production>();
    @Serialize
    private List<Transportation> transportations = new ArrayList<Transportation>();
    

    // ------------------- public Methods  ------------------- 
    
    // empty constructor necessary for GWT serialization
	public FoodProduct() {
		stdWeight = new QuantityImpl(100.0, Unit.GRAM);
	}    

	// Copy Constructor
	public FoodProduct(FoodProduct toClone) {
		this();
		if (toClone.name != null) name = new String(toClone.name);
		if (toClone.name_en != null) name_en = new String(toClone.name_en);
		if (toClone.co2eValue != null) co2eValue = new Double(toClone.co2eValue);
		if (toClone.stdWeight != null) stdWeight = new QuantityImpl(toClone.stdWeight);
		for (Long id : toClone.substituteIds)
			substituteIds.add(id);
		if (toClone.season != null) season = new Season(toClone.season);
		for (String tag : toClone.getTags()) 
			tags.add(new String(tag));
		for (Extraction extr : toClone.extractions)
			extractions.add(extr);
		for (Condition cond : toClone.conditions) 
			conditions.add(cond);
		for (Production prod: toClone.productions)
			productions.add(prod);
		for (Transportation trans : toClone.transportations)
			transportations.add(trans);
	}
	
 // ------------------- getters and setters -------------------
       
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getCo2eValue() {
		return co2eValue;
	}

	public void setCo2eValue(Double co2eValue) {
		this.co2eValue = co2eValue;
	}

	public QuantityImpl getStdWeight() {
		return stdWeight;
	}

	public void setStdWeight(QuantityImpl stdWeight) {
		this.stdWeight = stdWeight;
	}
	
    public List<Long> getSubstitues() {
		return substituteIds;
	}

	public void setSubstitutes(List<Long> substituteIds) {
		this.substituteIds = substituteIds;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public List<Extraction> getExtractions() {
		return extractions;
	}

	public void setExtractions(List<Extraction> extractions) {
		this.extractions = extractions;
	}
	/**
	 * adds @param extractionString to beginning of list, doesn't create new one if already exists
	 * @param extractionString verified location
	 * @return added extraction
	 */
	public Extraction addExtraction(String extractionString) {
		Extraction extraction = null;
			
		// check weather extraction already exists in product extraction list
		for (Extraction incrExtraction : extractions) {
			if (incrExtraction.symbol.equals(extractionString) ){
	  			extraction = incrExtraction;
			}
		}	
     
		//don't add new extraction in ingredients list if already exists
    	if (extraction == null) {
    		extraction = new Extraction(extractionString);
    		extractions.add(0, extraction);
    	}
    	return extraction;
	}

	public List<Condition> getConditions() {
		return conditions;
	}

	public void setConditions(List<Condition> conditions) {
		this.conditions = conditions;
	}
	
	/**
	 * 
	 * @param symbol
	 * @return the searched condition, null if not found
	 */
	public Condition getCondition(String symbol) {
		for (Condition cond : conditions) {
			if (cond.getSymbol().equalsIgnoreCase(symbol))
				return cond;
		}
		return null;
	}

	public List<Production> getProductions() {
		return productions;
	}

	public void setProductions(List<Production> productions) {
		this.productions = productions;
	}
	
	/**
	 * 
	 * @param symbol
	 * @return the searched production, null if not found
	 */
	public Production getProduction(String symbol) {
		for (Production prod : productions) {
			if (prod.getSymbol().equalsIgnoreCase(symbol))
				return prod;
		}
		return null;
	}

	public List<Transportation> getTransportations() {
		return transportations;
	}

	public void setTransportations(List<Transportation> transportations) {
		this.transportations = transportations;
	}
	
	/**
	 * 
	 * @param symbol
	 * @return the searched transportation, null if not found
	 */
	public Transportation getTransportation(String symbol) {
		for (Transportation trans : transportations) {
			if (trans.getSymbol().equalsIgnoreCase(symbol))
				return trans;
		}
		return null;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName_En() {
		return name;
	}
	
	public void setName_En(String name) {
		this.name_en = name;
	}
	
	public Season getSeason() {
		return this.season;
	}

	public void setSeason(Season season) {
		this.season = season;
	}
	
	public void setSeason(SeasonDate start, SeasonDate stop) {
		season = new Season(start,stop,SeasonType.MAIN_SEASON);
	}


	/**
	 * not implemented yet
	 */
	public Double getDensity() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getSynonyms() {
		return synonyms;
	}

	public void setSynonyms(List<String> synonyms) {
		this.synonyms = synonyms;
	}

	public boolean isSeasonDependant() {
		return seasonDependant;
	}

	public void setSeasonDependant(boolean seasonDependant) {
		this.seasonDependant = seasonDependant;
	}





    
}
