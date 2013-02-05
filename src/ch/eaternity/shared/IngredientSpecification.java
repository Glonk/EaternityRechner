
package ch.eaternity.shared;

import java.io.Serializable;
import java.text.DecimalFormat;
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
	
//    @PrimaryKey
//    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
//	private Long id;
//	
//    @Persistent
//    @Extension(vendorName="datanucleus", key="gae.pk-id", value="true")
//    private Long keyId;
	
	
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

	private Transportation transportation;
	private double distance; // in km

	private SeasonDate startSeason;
	private SeasonDate stopSeason;

	private int normalCO2Value;
	// no factors included
	private double co2ValueNoFactors;
	
	private double cost; 
	public List<String> tags = new ArrayList<String>();
	
	public IngredientSpecification(Long zutat_id, String name, Date cookingDate,Condition symbol,Production symbol2, 
		 Transportation symbol3) {
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
		transportation = new Transportation(toClone.transportation);
		distance = toClone.distance;
		startSeason = new SeasonDate(toClone.startSeason);
		stopSeason = new SeasonDate(toClone.startSeason);
		normalCO2Value = toClone.normalCO2Value;
		co2ValueNoFactors = toClone.co2ValueNoFactors;
		cost = toClone.cost;
	}
	
	// Copy Constructor from Ingredient
	public IngredientSpecification(Ingredient ing) {
		name = new String(ing.getSymbol());
		name_en = new String(ing.getSymbol_en());
		ingredientId = new Long(ing.getId());
		extraction = new Extraction(ing.getStdExtraction());
		if (ing.getConditions() != null)
			condition = new Condition(ing.getConditions().get(0));
		if (ing.getProductions() != null)
			production = new Production(ing.getProductions().get(0));
		if (ing.getTransportations() != null)
			transportation = new Transportation(ing.getTransportations().get(0));
		startSeason = new SeasonDate(ing.getStartSeason());
		stopSeason = new SeasonDate(ing.getStopSeason());
		normalCO2Value = new Integer(ing.getCo2eValue());
		for (String tag : ing.getTags()) {
			tags.add(new String(tag));
		}
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
	public void setTransportation(Transportation transportmittel) {
		this.transportation = transportmittel;
	}
	public Transportation getTransportation() {
		return transportation;
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
	
	public Boolean hasSeason() {
		return ( startSeason != null && stopSeason != null );
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
	
	public int getKmDistanceRounded() {
		int d = (int)(distance/10000);
		if (d%10 >= 5)
			d = d + 10;
		int dist = ((int)(d/10))*100;
		return dist;
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
		co2ValueNoFactors = normalCO2Value*weight/1000;
		return co2ValueNoFactors;
	}


	public double getCalculatedCO2Value() {
		// just in case its not setted yet
		calculateCo2ValueNoFactors();
		
		// sum up all parts
		return co2ValueNoFactors + getConditionQuota() + getTransportationQuota() + getProductionQuota();
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
		normalCO2Value = normalCO2Value;
	}



	public int getNormalCO2Value() {
		return normalCO2Value;
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



