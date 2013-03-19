package ch.eaternity.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.googlecode.objectify.annotation.Serialized;

public class FoodProduct implements Serializable {

	private static final long serialVersionUID = -3996022378367823464L;
	
    @Id private Long id;

    private String name;
    private String name_en;

    @Embedded
    private Quantity co2eValue;
    @Embedded
    private Quantity stdWeight;
    
    @Transient
    private Boolean notASubstitute;
    private List<Long> substituteIds = new ArrayList<Long>();

    @Embedded
    private Season season;
	
    private List<String> tags = new ArrayList<String>();
    
    @Serialized 
    private List<Extraction> extractions = new ArrayList<Extraction>();
    @Embedded
    private List<Condition> conditions = new ArrayList<Condition>();
    @Embedded
    private List<Production> productions  = new ArrayList<Production>();
    @Embedded
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

	public List<Production> getProductions() {
		return productions;
	}

	public void setProductions(List<Production> productions) {
		this.productions = productions;
	}

	public List<Transportation> getTransportations() {
		return transportations;
	}

	public void setTransportations(List<Transportation> transportations) {
		this.transportations = transportations;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
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

	/**
	 * not implemented yet
	 */
	public String getSynonyms() {
		// TODO Auto-generated method stub
		return null;
	}





    
}
