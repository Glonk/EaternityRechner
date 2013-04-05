
package ch.eaternity.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.Date;


import com.googlecode.objectify.annotation.*;

/**
 * Since this class is embedded in recipe, it must not contain Collections of any type. otherwise serialize...
 * @author aurelianjaggi
 *
 */
@Entity
public class Ingredient  implements Serializable {

	private static final long serialVersionUID = -2858311250621887438L;
	
	@Id private Long id;
	
	@Ignore
    private FoodProduct foodproduct;

	private Long productId;
	
	// optionally if speed is to slow, needs to be created when stored, not possible client-side
	//@Load private Ref<FoodProduct> productRef;
	
	@Embed
	private Quantity weight;
	private Date cookingDate;
	private double cost; 
	@Embed
	private Extraction extraction;
	// need to include home as well...?
	@Embed
	private Quantity distance;
	@Embed
	private Transportation transportation;
	@Embed
	private Production production;
	@Embed
	private Condition condition;
	
	
	// --------------------------- public methods ---------------------------
	
	public Ingredient() {
		distance = new Quantity(0.0D, Unit.KILOMETER);
		weight = new Quantity(0.0D, Unit.GRAM);
	}
	
	// Copy Constructor
	public Ingredient(Ingredient toClone) {
		this();
		productId = new Long(toClone.id);
		foodproduct = new FoodProduct(toClone.foodproduct);
		weight = toClone.weight;
		// inlcude Extraction, now just a shallow copy...
		extraction = toClone.extraction;
		cookingDate = (Date) toClone.cookingDate.clone();
		condition = new Condition(toClone.condition);
		production = new Production(toClone.production);
		transportation = new Transportation(toClone.transportation);
		distance = toClone.distance;
		cost = toClone.cost;
	}
	
	// Copy Constructor from Ingredient
	public Ingredient(FoodProduct foodproduct) {
		this();
		this.foodproduct = foodproduct;
		this.productId = foodproduct.getId();
		
		//TODO change to correct standard values
		if (foodproduct.getExtractions() != null)
			extraction = new Extraction(foodproduct.getExtractions().get(0));
		if (foodproduct.getConditions() != null)
			condition = new Condition(foodproduct.getConditions().get(0));
		if (foodproduct.getProductions() != null)
			production = new Production(foodproduct.getProductions().get(0));
		if (foodproduct.getTransportations() != null)
			transportation = new Transportation(foodproduct.getTransportations().get(0));
	}
	
	public void setExtraction(Extraction stdExtractionSymbol) {
		this.extraction = stdExtractionSymbol;
	}
	
	public FoodProduct getFoodProduct() {
		return foodproduct;
	}
	
	public void setFoodProduct(FoodProduct foodproduct) {
		this.foodproduct = foodproduct;
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

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public void setWeight(Quantity weight) {
		this.weight = weight;
	}

	public Quantity getWeight() {
		return weight;
	}

	public void setDistance(Quantity distance) {
		this.distance = distance;
	}

	public Quantity getDistance() {
		return distance;
	}
	
	public int getKmDistanceRounded() {
		int d = (int)(distance.convert(Unit.METER).getAmount()/10000);
		if (d%10 >= 5)
			d = d + 10;
		int dist = ((int)(d/10))*100;
		return dist;
	}
	
	public double calculateCo2ValueNoFactors() {
		return foodproduct.getCo2eValue()*weight.convert(Unit.GRAM).getAmount();
	}


	/**
	 * 
	 * @return co2e value in [g CO2 / g]
	 */
	public double getCalculatedCO2Value() {
		// sum up all parts
		return calculateCo2ValueNoFactors() + getConditionQuota() + getTransportationQuota() + getProductionQuota();
	}
	
	public double getConditionQuota() {
		if(condition != null && condition.getFactor() != null){
			return condition.getFactor()*weight.getAmount();
		}
		else
			return 0.0;
	}
	
	public double getTransportationQuota() {
		if(transportation != null && transportation.getFactor() != null){
			if(distance.getAmount() != null)
				return transportation.getFactor()*distance.getAmount()/1000000*weight.getAmount();
			else
				return 0.0;
		} 
		else
			return 0.0;
	}
	
	public double getProductionQuota() {
		if(production != null && production.getFactor() != null){
			return production.getFactor()*weight.getAmount();
		}
		else
			return 0.0;
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



