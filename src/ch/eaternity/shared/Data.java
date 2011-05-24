package ch.eaternity.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public class Data implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8325524250818128692L;
	public  List<Recipe> PublicRezepte = new ArrayList<Recipe>();
	public  List<Recipe> yourRecipes = new ArrayList<Recipe>();
	public  List<Recipe> KitchenRecipes = new ArrayList<Recipe>();
	public  List<Ingredient> ingredients;
	public  List<SingleDistance> distances;
	
	public  List<Kitchen> kitchens;
	
	public int lastKitchen;

	public Set<String> getOrcaleIndex() {

		Set<String> oracleIndex = new TreeSet<String>();

		oracleIndex.add("all");

		if(this.ingredients != null){
			for(Ingredient zutat : this.ingredients){
				if(zutat != null){
					oracleIndex.add(zutat.getSymbol());
				}
			}
		}
		if(this.PublicRezepte != null){
			for(Recipe recipe : this.PublicRezepte){
				if(recipe != null){
					oracleIndex.add(recipe.getSymbol());
				}
			}
		}
		if(this.yourRecipes != null){
			for(Recipe recipe  : this.yourRecipes){
				if(recipe != null){
					oracleIndex.add(recipe.getSymbol());
				}
			}
		}

		return oracleIndex;
	}
	
	public void setPublicRezepte(List<Recipe> publicRezepte) {
		PublicRezepte = publicRezepte;
	}
	public List<Recipe> getPublicRezepte() {
		return PublicRezepte;
	}
	public void setYourRezepte(List<Recipe> yourRezepte) {
		yourRecipes = yourRezepte;
	}
	public List<Recipe> getYourRezepte() {
		return yourRecipes;
	}
	
	public Ingredient getIngredientByID(long id){
		for(Ingredient zutat : ingredients){
			if (zutat.getId() == id){
				return zutat;
			}
		}
		return null;
	}
	
	
	public void setDistances(List<SingleDistance> distances) {
		this.distances = distances;
	}
	public List<SingleDistance> getDistances() {
		return distances;
	}
	public void setIngredients(List<Ingredient> ingredients) {
		this.ingredients = ingredients;
	}
	public List<Ingredient> getIngredients() {
		return ingredients;
	}


}
