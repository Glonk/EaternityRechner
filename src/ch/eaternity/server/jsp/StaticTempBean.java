package ch.eaternity.server.jsp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.eaternity.shared.IngredientSpecification;
import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.CO2Value;;

public class StaticTempBean implements Serializable {
	
	private static final long serialVersionUID = 5288234456492104L;
	
	// -------------------------- public Fields -------------------------
	
	public List<Recipe> recipes= new ArrayList<Recipe>();

	public List<IngredientSpecification> ingredients = new ArrayList<IngredientSpecification>();
	public int startIndex;
	public int stopIndex;
	
	public Collection<CO2Value> co2Values = new ArrayList<CO2Value>();
	public CO2Value co2Value;
	
	public double personFactor;
	public double persons;
	
	// -------------------------- public Methods -------------------------
	
	public StaticTempBean () {
		startIndex = 0;
		stopIndex = 0;
	}
	
	public void clear() {
		recipes.clear();
		ingredients.clear();
		co2Values.clear();
		startIndex = 0;
		stopIndex = 0;
		personFactor = 1;
		persons = 1;
	}
	
	// ------------------- Getters and Setters ------------------------------

	public List<Recipe> getRecipes() {
		return recipes;
	}

	public void setRecipes(List<Recipe> recipes) {
		this.recipes = recipes;
	}

	public List<IngredientSpecification> getIngredients() {
		return ingredients;
	}

	public void setIngredients(List<IngredientSpecification> ingredients) {
		this.ingredients = ingredients;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public int getStopIndex() {
		return stopIndex;
	}

	public void setStopIndex(int stopIndex) {
		this.stopIndex = stopIndex;
	}

	public double getPersonFactor() {
		return personFactor;
	}

	public void setPersonFactor(double personFactor) {
		this.personFactor = personFactor;
	}

	public Collection<CO2Value> getCo2Values() {
		return co2Values;
	}

	public void setCo2Values(Collection<CO2Value> co2Values) {
		this.co2Values = co2Values;
	}
	public CO2Value getCo2Value() {
		return co2Value;
	}

	public void setCo2Value(CO2Value co2Value) {
		this.co2Value = co2Value;
	}

	public double getPersons() {
		return persons;
	}

	public void setPersons(double persons) {
		this.persons = persons;
	}
}
