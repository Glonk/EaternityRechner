package ch.eaternity.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public class ClientData implements Serializable{

	private static final long serialVersionUID = -8325524250818128692L;
	public List<Recipe> publicRecipes = new ArrayList<Recipe>();
	public List<Recipe> userRecipes = new ArrayList<Recipe>();
	public List<Recipe> kitchenRecipes = new ArrayList<Recipe>();
	
	public List<Recipe> currentKitchenRecipes = new ArrayList<Recipe>();
	
	public List<Recipe> workspaceRecipes = new ArrayList<Recipe>();
	public Recipe editRecipe;
	
	public List<Ingredient> ingredients;
	public Distance distances;
	public List<Workgroup> kitchens;

	public LoginInfo loginInfo;
	public Workgroup currentKitchen; 
	public int selectedMonth;
	public String currentLocation;
	
	
	public ClientData() {
		
	}
	
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
}
