package ch.eaternity.shared;


import java.util.List;
import java.util.Map;

import javax.persistence.Embedded;
import javax.persistence.Id;
import javax.persistence.Transient;


import com.google.gwt.user.client.rpc.IsSerializable;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Serialized;

public class Ingredient implements IsSerializable{

    @Id private Long id;

    private String symbol;

    private Integer co2eValue;
    
    private Long[] alternativeIds;
    
    Integer stdAmountGramm;
	
    Key<Extraction> stdExtraction;
    
//    @Serialized Map<Key<Condition>,Double> conditionKeys;
//    @Serialized Map<Key<Production>,Double> productionKeys;
//    @Serialized Map<Key<MoTransportation>,Double> moTransportationKeys;
    
    // fetches all, max 5000
//    Key<Extraction>[] extractionKeys;
    
    @Serialized 
    private
    List<Extraction> extractions;
    
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
