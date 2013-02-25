package ch.eaternity.shared;

import java.io.Serializable;
import java.sql.Date;
import java.util.Collection;



public class CategoryQuantities implements Serializable {
	
	private static final long serialVersionUID = 52346675466492104L;

	public String categoryName;
	public CO2Value co2Value;
	public Double weight;
	public Double cost;
	public Date date;
	
	public CategoryQuantities() {
		clear();
	}
	
	public CategoryQuantities(String categoryName, CO2Value co2value, Double weight, Double cost, Date date) {
		this.categoryName = categoryName;
		this.co2Value = co2value;
		this.weight = weight;
		this.cost = cost;
		this.date = date;
	}
	
	public CategoryQuantities(String categoryName, Collection<IngredientSpecification> ingSpecs) {
		setQuantities(categoryName,ingSpecs);
	}
	
	public void setQuantities(String categoryName, Collection<IngredientSpecification> ingSpecs) {
		clear();
		for (IngredientSpecification ingSpec : ingSpecs) {
			co2Value = co2Value.add(new CO2Value(ingSpec));
			weight = weight + ingSpec.getMengeGramm();
			cost = cost + ingSpec.getCost();
		}
		this.categoryName = categoryName;
	}
	
	public CategoryQuantities add(CategoryQuantities other) {
		CategoryQuantities sum = new CategoryQuantities();
		sum.co2Value = this.co2Value.add(other.co2Value);
		sum.weight = this.weight + other.weight;
		sum.cost = this.cost + other.cost;
		return sum;
	}
	
	public CategoryQuantities mult(Double factor) {
		CategoryQuantities mult = new CategoryQuantities();
		mult.co2Value = this.co2Value.mult(factor);
		mult.weight = this.weight*factor;
		mult.cost = this.cost*factor;
		return mult;
	}
	
	public void clear() {
		this.categoryName = "";
		this.co2Value = new CO2Value();
		this.weight = 0.0;
		this.cost = 0.0;
		this.date = null; // good idea?
	}
}
