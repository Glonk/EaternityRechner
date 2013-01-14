package ch.eaternity.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public class Data implements Serializable{

	private static final long serialVersionUID = -8325524250818128692L;
	public  List<Recipe> publicRecipes = new ArrayList<Recipe>();
	public  List<Recipe> userRecipes = new ArrayList<Recipe>();
	public  List<Recipe> kitchenRecipes = new ArrayList<Recipe>();
	public  List<Ingredient> ingredients;
	public  List<SingleDistance> distances;
	
	public  List<Workgroup> kitchens;
	
	public Long lastKitchen;

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
		if(this.publicRecipes != null){
			for(Recipe recipe : this.publicRecipes){
				if(recipe != null){
					oracleIndex.add(recipe.getSymbol());
				}
			}
		}
		if(this.userRecipes != null){
			for(Recipe recipe  : this.userRecipes){
				if(recipe != null){
					oracleIndex.add(recipe.getSymbol());
				}
			}
		}

		return oracleIndex;
	}
	
	public void setPublicRecipes(List<Recipe> publicRezepte) {
		publicRecipes = publicRezepte;
	}
	public List<Recipe> getPublicRecipes() {
		return publicRecipes;
	}
	public void setUserRecipes(List<Recipe> yourRezepte) {
		userRecipes = yourRezepte;
	}
	public List<Recipe> getUserRecipes() {
		return userRecipes;
	}
	public void setKitchenRescipes(List<Recipe> KitchenRecipes) {
		this.kitchenRecipes = KitchenRecipes;
	}
	public List<Recipe> getKitchenRecipes() {
		return kitchenRecipes;
	}
	
	
	
	public Ingredient getIngredientByID(long id){
		for(Ingredient zutat : ingredients){
			if (zutat.getId() == id){
				return zutat;
			}
		}
		return null;
	}
	
	
	public Workgroup getKitchenByID(long id){
		for(Workgroup kitchen : kitchens){
			if (kitchen.id == id){
				return kitchen;
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
