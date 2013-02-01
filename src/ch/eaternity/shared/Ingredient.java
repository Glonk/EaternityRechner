package ch.eaternity.shared;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.googlecode.objectify.annotation.Serialized;

public class Ingredient implements Serializable{

	private static final long serialVersionUID = -3996022378367823464L;
	
    @Id private Long id;

    private String symbol;
    private String symbol_en;

    private Integer co2eValue;
    private Integer stdWeight; // in grams
    
    @Transient
    private Boolean noAlternative;
    private List<Long> alternativeIds = new ArrayList<Long>();

	// currently related to Switzerland
    private Boolean hasSeason;
    @Embedded
    private SeasonDate startSeason = new SeasonDate();
    @Embedded
    private SeasonDate stopSeason = new SeasonDate();
	
    private List<String> tags = new ArrayList<String>();
    
    // stdExtraction should be setted during loading. probably not necessary at all...
    @Serialized
    private Extraction stdExtraction;
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
	private Ingredient() {}
    
    public Ingredient(Long id)
    {
    	this.setId(id);
    }

    

    
 // ------------------- getters and setters -------------------
       
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

	public String getSymbol_en() {
		return symbol_en;
	}

	public void setSymbol_en(String symbol_en) {
		this.symbol_en = symbol_en;
	}

	public Integer getCo2eValue() {
		return co2eValue;
	}

	public void setCo2eValue(Integer co2eValue) {
		this.co2eValue = co2eValue;
	}

	public Integer getStdWeight() {
		return stdWeight;
	}

	public void setStdWeight(Integer stdWeight) {
		this.stdWeight = stdWeight;
	}

	public Boolean getNoAlternative() {
		return noAlternative;
	}

	public void setNoAlternative(Boolean noAlternative) {
		this.noAlternative = noAlternative;
	}
	
    public List<Long> getAlternatives() {
		return alternativeIds;
	}

	public void setAlternatives(List<Long> alternativeIds) {
		this.alternativeIds = alternativeIds;
	}

	public Boolean getHasSeason() {
		return hasSeason;
	}

	public void setHasSeason(Boolean hasSeason) {
		this.hasSeason = hasSeason;
	}

	public SeasonDate getStartSeason() {
		return startSeason;
	}

	public void setStartSeason(SeasonDate startSeason) {
		this.startSeason = startSeason;
	}
	
	public void setStartSeason(String startSeason) {
		this.startSeason.setDate(startSeason);
	}

	public SeasonDate getStopSeason() {
		return stopSeason;
	}

	public void setStopSeason(SeasonDate stopSeason) {
		this.stopSeason = stopSeason;
	}
	
	public void setStopSeason(String stopSeason) {
		this.stopSeason.setDate(stopSeason);
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public Extraction getStdExtraction() {
		return stdExtraction;
	}

	public void setStdExtraction(Extraction stdExtraction) {
		this.stdExtraction = stdExtraction;
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




    
}
