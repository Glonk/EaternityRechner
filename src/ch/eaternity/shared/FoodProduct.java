package ch.eaternity.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.annotation.*;

@Entity
@Cache
public class FoodProduct implements IsSerializable {

	private static final long serialVersionUID = -3996987374367823464L;
	
    @Id private Long id;

    private String name;
    private String name_en;
    
    private List<String> synonyms = new ArrayList<String>();

    // probably change to double, take standard measures?
    @Embed
    private Quantity co2eValue;
    @Embed
    private Quantity stdWeight;
    
    @Ignore
    private Boolean notASubstitute;
    private List<Long> substituteIds = new ArrayList<Long>();

    @Embed
    private Season season;
	
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
	public FoodProduct() {}    

	// Copy Constructor
	public FoodProduct(FoodProduct toClone) {
		name = new String(toClone.name);
		name_en = new String(toClone.name_en);
		co2eValue = new Quantity(toClone.co2eValue);
		stdWeight = new Quantity(toClone.stdWeight);
		notASubstitute = toClone.notASubstitute;
		for (Long id : toClone.substituteIds)
			substituteIds.add(id);
		season = new Season(toClone.season);
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

	public Quantity getCo2eValue() {
		return co2eValue;
	}

	public void setCo2eValue(Quantity co2eValue) {
		this.co2eValue = co2eValue;
	}

	public Quantity getStdWeight() {
		return stdWeight;
	}

	public void setStdWeight(Quantity stdWeight) {
		this.stdWeight = stdWeight;
	}

	public Boolean isNotASubstitute() {
		return notASubstitute;
	}

	public void setNotASubstitute(Boolean notASubstitute) {
		this.notASubstitute = notASubstitute;
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
		this.name = name;
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





    
}
