package ch.eaternity.shared;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.googlecode.objectify.annotation.Serialized;

public class Ingredient implements IsSerializable{

    @Id private Long id;

    private String symbol;
    private String symbol_en;

    private Integer co2eValue;
    public Integer stdWeight; // in grams
    
    @Transient
    public Boolean noAlternative;
    private Long[] alternativeIds;
 
    // currently related to Switzerland
	public Boolean hasSeason;
	public SeasonDate startSeason;
	public SeasonDate stopSeason;
	
	public List<String> tags;
    
    // possibly to be eliminated
    public String stdExtractionSymbol;
    
    @Serialized
	public Extraction stdExtraction;
    @Serialized 
    private List<Extraction> extractions;
    
    @Embedded
	public ArrayList<Condition> conditions;
    @Embedded
	public ArrayList<Production> productions;
    @Embedded
	public ArrayList<MoTransportation> moTransportations;
    

    // ------------------- public Methods  ------------------- 
    
    // empty constructor necessary for GWT serialization
    private Ingredient() {}
    
    public Ingredient(Long id)
    {
    	this.setId(id);
    }
    

    
// getters and setters...
      
    
	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getSymbol() {
		return symbol;
	}
	
	public void setSymbol_en(String symbol_en) {
		this.symbol_en = symbol_en;
	}

	public String getSymbol_en() {
		return symbol_en;
	}

	public void setCo2eValue(Integer co2eValue) {
		this.co2eValue = co2eValue;
	}

	public Integer getCo2eValue() {
		return co2eValue;
	}

	public void setAlternatives(Long[] alternativeIds) {
		this.alternativeIds = alternativeIds;
	}

	public Long[] getAlternatives() {
		return alternativeIds;
	}

	public void setExtractions(List<Extraction> extractions) {
		this.extractions = extractions;
	}

	public List<Extraction> getExtractions() {
		return extractions;
	}



    
}
