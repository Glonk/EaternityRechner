
package ch.eaternity.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.persistence.Embedded;
import javax.persistence.Id;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.googlecode.objectify.annotation.Serialized;




/**
 * A specification in more detail of the ingredient.
 */
@PersistenceCapable //(detachable = "true")
public class IngredientSpecification  implements Serializable, Cloneable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2858311250621887438L;
	
	@Id private Long id;
	
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    @Extension(vendorName="datanucleus", key="gae.encoded-pk", value="true")
    private String encodedKey;
    
	@Persistent
	private String name;
    private String name_en;
	
	@Persistent
	private String recipeKey;
	
	private Long ingredientId;

	private int weight;
	@Serialized
	private Extraction extraction;
	private Date cookingDate;
	
	@Embedded
	private Condition condition;
	@Embedded
	private Production production;
	@Embedded
	private MoTransportation transportation;
	private double distance; // in km
	private Long label;
	
	public Boolean hasSeason() {
		return ( startSeason != null && stopSeason != null );
	}

	private SeasonDate startSeason;
	private SeasonDate stopSeason;
	
	private int NormalCO2Value; // in (Kg Co2)/Kg
	// no factors included
	private double co2ValueNoFactors;
	private double cost; 
	public List<String> tags;
	
	public IngredientSpecification(Long zutat_id, String name, Date cookingDate,Condition symbol,Production symbol2, 
		 MoTransportation symbol3) {
		this.setName(name);
		this.setZutat_id(zutat_id);
		
		this.cookingDate = cookingDate;
		this.condition = symbol;
		this.production = symbol2;
		this.transportation = symbol3;	
	}
	
	// Copy Constructor
	public IngredientSpecification(IngredientSpecification toClone) {
		name = new String(toClone.name);
		recipeKey = new String(toClone.recipeKey);
		ingredientId = new Long(toClone.ingredientId);
		weight = toClone.weight;
		// inlcude Extraction, now just a shallow copy...
		extraction = toClone.extraction;
		cookingDate = (Date) toClone.cookingDate.clone();
		condition = new Condition(toClone.condition);
		production = new Production(toClone.production);
		transportation = new MoTransportation(toClone.transportation);
		distance = toClone.distance;
		label = new Long(toClone.label);
		startSeason = new SeasonDate(toClone.startSeason);
		stopSeason = new SeasonDate(toClone.startSeason);
		NormalCO2Value = toClone.NormalCO2Value;
		co2ValueNoFactors = toClone.co2ValueNoFactors;
		cost = toClone.cost;
	}
	
	// Copy Constructor from Ingredient
	public IngredientSpecification(Ingredient ing, int weight) {
		/*
		 * 		Extraction stdExtraction = null;
		for(Extraction extraction: item.getExtractions()){
			if(item.stdExtractionSymbol.equalsIgnoreCase(extraction.symbol)){
				stdExtraction = extraction;
			}
		}
		IngredientSpecification ingredientSpecification = new IngredientSpecification(item.getId(), item.getSymbol(),
				 new Date(),stdExtraction.stdCondition, stdExtraction.stdProduction, 
				 stdExtraction.stdMoTransportation);
		ingredientSpecification.setHerkunft(stdExtraction);
		if (grams == 0)
			ingredientSpecification.setMengeGramm(item.stdWeight);
		else
			ingredientSpecification.setMengeGramm(grams);
		ingredientSpecification.setSeason(item.startSeason, item.stopSeason);
		ingredientSpecification.setNormalCO2Value(item.getCo2eValue());*/
		 
	}
	
	public IngredientSpecification(){
		
	}

	public IngredientSpecification(Long zutat_id, String name) {
		this.setZutat_id(zutat_id);
		this.setName(name);
	}

	public void setExtraction(Extraction stdExtractionSymbol) {
		this.extraction = stdExtractionSymbol;
	}
	
	
	public Extraction getExtraction() {
		return extraction;
	}
	public void setCookingDate(Date cookingDate) {
		this.cookingDate = cookingDate;
	}
	public Date getCookingDate() {
		return cookingDate;
	}
	public void setCondition(Condition zustand) {
		this.condition = zustand;
	}
	public Condition getCondition() {
		return condition;
	}
	public void setProduction(Production produktion) {
		this.production = produktion;
	}
	public Production getProduction() {
		return production;
	}
	public void setTransportation(MoTransportation transportmittel) {
		this.transportation = transportmittel;
	}
	public MoTransportation getTransportation() {
		return transportation;
	}

	public void setLabel(Long label) {
		this.label = label;
	}

	public Long getLabel() {
		return label;
	}
	public void setSeason(String strStart,String strStop) {
		this.startSeason = new SeasonDate();
		this.startSeason.setDate(strStart);
		this.stopSeason = new SeasonDate();
		this.stopSeason.setDate(strStop);
	}
	
	public void setSeason(SeasonDate startSeason, SeasonDate stopSeason) {
		this.startSeason = startSeason;
		this.stopSeason = stopSeason;
	}

	public SeasonDate getStartSeason() {
		return startSeason;
	}

	public SeasonDate getStopSeason() {
		return stopSeason;
	}

	public void setZutat_id(Long zutat_id) {
		this.ingredientId = zutat_id;
	}

	public Long getZutat_id() {
		return ingredientId;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getWeight() {
		return weight;
	}

//	public void setLabels(ArrayList<Long> labels) {
//		Labels = labels;
//	}
//
//	public ArrayList<Long> getLabels() {
//		return Labels;
//	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
//	public void setId(Long id) {
//		this.id = id;
//	}
//	public Long getId() {
//		return id;
//	}



	public void setDistance(double distance) {
		this.distance = distance;
	}



	public double getDistance() {
		return distance;
	}
	
	/*
	 *  TODO this is a hack, hardcoded value. remove after all objects are updated
	 */
	public void update() {
		if(transportation.factor == null && transportation.symbol.equals("LKW")) {
			transportation.factor = 0.188D;
		}
	}
	

	public double calculateCo2ValueNoFactors() {
		co2ValueNoFactors = NormalCO2Value*weight/1000;
		return co2ValueNoFactors;
	}


	public double getCalculatedCO2Value() {
		// sum up all parts
		return calculateCo2ValueNoFactors() + getConditionQuota() + getTransportationQuota() + getProductionQuota();
	}
	
	public double getConditionQuota() {
		if(condition != null && condition.factor != null){
			return condition.factor*weight;
		}
		else
			return 0.0;
	}
	
	public double getTransportationQuota() {
		if(transportation != null && transportation.factor != null){
			if(distance != 0)
				return transportation.factor*distance/1000000*weight;
			else
				return 0.0;
		} 
		else
			return 0.0;
	}
	
	public double getProductionQuota() {
		if(production != null && production.factor != null){
			return production.factor*weight;
		}
		else
			return 0.0;
	}


	public void setNormalCO2Value(int normalCO2Value) {
		NormalCO2Value = normalCO2Value;
	}



	public int getNormalCO2Value() {
		return NormalCO2Value;
	}



	public void setEncodedKey(String encodedKey) {
		this.encodedKey = encodedKey;
	}



	public String getEncodedKey() {
		return encodedKey;
	}



	public void setRezeptKey(String key) {
		recipeKey = key;
	}



	public String getRezeptKey() {
		return recipeKey;
	}



	public void setId(Long id) {
		this.id = id;
	}



	public Long getId() {
		return id;
	}
	
	// returns -1 if price is not set yet
	public double getCost(){
		return cost;
	}

	public void setCost(double cost)
	{
		this.cost = cost;
	}
}



