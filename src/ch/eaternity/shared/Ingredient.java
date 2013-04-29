
package ch.eaternity.shared;

import java.io.Serializable;
import java.util.Date;

import org.eaticious.common.Quantity;
import org.eaticious.common.QuantityImpl;
import org.eaticious.common.Unit;


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
	
	@Serialize
	private QuantityImpl weight;
	private Date cookingDate;
	private double cost; 
	
	@Embed
	private Extraction extraction;
	// need to include home as well...?
	@Serialize
	private Route route;

	@Embed
	private Transportation transportation;
	@Embed
	private Production production;
	@Embed
	private Condition condition;
	
	
	// --------------------------- public methods ---------------------------
	
	public Ingredient() {
		route = new Route();
		weight = new QuantityImpl(0.0, Unit.GRAM);
		cost = 0.0;
	}
	
	// Copy Constructor
	public Ingredient(Ingredient toClone) {
		this();
		if(toClone.productId != null) productId = new Long(toClone.productId);
		foodproduct = new FoodProduct(toClone.foodproduct);
		if (toClone.weight != null)weight = toClone.weight;
		// inlcude Extraction, now just a shallow copy...
		if (toClone.extraction != null)extraction = toClone.extraction;
		if (toClone.cookingDate != null) cookingDate = (Date) toClone.cookingDate.clone();
		if (toClone.condition != null)condition = new Condition(toClone.condition);
		if (toClone.production != null)production = new Production(toClone.production);
		if (toClone.transportation != null)transportation = new Transportation(toClone.transportation);
		route = toClone.route;
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

	public void setWeight(QuantityImpl weight) {
		this.weight = weight;
	}

	public QuantityImpl getWeight() {
		return weight;
	}

	public void setRoute(Route route) {
		this.route = route;
	}

	public Route getRoute() {
		return route;
	}
	
	public int getKmDistanceRounded() {
		int d = (int)(route.getDistanceKM().getAmount()/10);
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
			if(route.getDistanceKM() != null)
				return transportation.getFactor()*route.getDistanceKM().getAmount()*weight.convert(Unit.KILOGRAM).getAmount();
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



