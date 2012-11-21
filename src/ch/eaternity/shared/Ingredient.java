package ch.eaternity.shared;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Serialized;

public class Ingredient implements IsSerializable{

    @Id private Long id;

    private String symbol;
    
    private String source;

    private Integer co2eValue;
    
    @Transient
    public Boolean noAlternative;
    
    private Long[] alternativeIds;
    
    Key<Ingredient> alternativeKeys;
    
    public Integer stdAmountGramm;
    
    // possibly to be eliminated
    public String stdExtractionSymbol;
	
    @Serialized
	public
    Extraction stdExtraction;
    
    @Embedded
	public ArrayList<IngredientCondition> conditions;
    @Embedded
	public ArrayList<Production> productions;
    @Embedded
	public ArrayList<MoTransportation> moTransportations;
    
//    @Serialized Map<Key<Production>,Double> productionKeys;
//    @Serialized Map<Key<MoTransportation>,Double> moTransportationKeys;
    
    // fetches all, max 5000
//    Key<Extraction>[] extractionKeys;
    
    @Serialized 
    private
    List<Extraction> extractions;

	public Boolean hasSeason;
	
	public List<String> tags;


    
//    @Serialized
//    @Transient
//    private
//    Map<Condition,Double> conditions;
//    
//    @Transient
//    @Serialized 
//    private
//    Map<Production,Double> productions;
//    
//    @Transient
//    @Serialized 
//    private
//    Map<MoTransportation,Double> moTransportations;

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

	public void setSource(String source) {
		this.source = source;
	}

	public String getSource() {
		return source;
	}

//	public void setConditions(Map<Condition,Double> conditions) {
//		this.conditions = conditions;
//	}
//
//	public Map<Condition,Double> getConditions() {
//		return conditions;
//	}
//
//	public void setProductions(Map<Production,Double> productions) {
//		this.productions = productions;
//	}
//
//	public Map<Production,Double> getProductions() {
//		return productions;
//	}
//
//	public void setMoTransportations(Map<MoTransportation,Double> moTransportations) {
//		this.moTransportations = moTransportations;
//	}
//
//	public Map<MoTransportation,Double> getMoTransportations() {
//		return moTransportations;
//	}


    
}
