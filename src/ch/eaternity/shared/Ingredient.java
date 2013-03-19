
package ch.eaternity.shared;

import java.io.Serializable;
import java.util.Date;

import com.googlecode.objectify.annotation.*;

@Entity
public class Ingredient  implements Serializable, Cloneable  {

	private static final long serialVersionUID = -2858311250621887438L;
	
	@Id private Long id;
	
	// possibly not storing it directly here, but via a key/ref relationship in appengine
	// and loding it correctly when needed via @Load Ref<FoodProduct>
	@Embed
    private FoodProduct foodproduct;

    @Embed
	private Quantity weight;
	private Date cookingDate;
	private double cost; 
	
	@Embed
	private Extraction extraction;
	@Embed
	private Condition condition;
	@Embed
	private Production production;
	@Embed
	private Transportation transportation;
	
	@Embed
	private Quantity distance;
	
	// --------------------------- public methods ---------------------------
	
	public Ingredient() {}
	
	// Copy Constructor
	public Ingredient(Ingredient toClone) {
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
		this.foodproduct = foodproduct;
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
	
	public FoodProduct getProduct() {
		return foodproduct;
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
		return foodproduct.getCo2eValue().convert(Unit.GRAM).getAmount()*weight.getAmount();
	}


	public double getCalculatedCO2Value() {
		// sum up all parts
		return calculateCo2ValueNoFactors() + getConditionQuota() + getTransportationQuota() + getProductionQuota();
	}
	
	public double getConditionQuota() {
		if(condition != null && condition.factor != null){
			return condition.factor*weight.getAmount();
		}
		else
			return 0.0;
	}
	
	public double getTransportationQuota() {
		if(transportation != null && transportation.factor != null){
			if(distance.getAmount() != 0)
				return transportation.factor*distance.getAmount()/1000000*weight.getAmount();
			else
				return 0.0;
		} 
		else
			return 0.0;
	}
	
	public double getProductionQuota() {
		if(production != null && production.factor != null){
			return production.factor*weight.getAmount();
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



