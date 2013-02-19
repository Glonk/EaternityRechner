package ch.eaternity.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ch.eaternity.shared.IngredientSpecification;
import ch.eaternity.shared.Recipe;

public class StaticTempBean implements Serializable {
	
	private static final long serialVersionUID = 5288234456492104L;
	
	// -------------------------- public Fields -------------------------
	
	public List<Recipe> recipes= new ArrayList<Recipe>();
	public List<IngredientSpecification> ingredients = new ArrayList<IngredientSpecification>();
	public int startIndex;
	public int stopIndex;
	
	public double personFactor;
	
	// -------------------------- public Methods -------------------------
	
	public StaticTempBean () {
		startIndex = 0;
		stopIndex = 0;
	}
	
	public void clear() {
		recipes.clear();
		ingredients.clear();
		startIndex = 0;
		stopIndex = 0;
		personFactor = 1;
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
}
