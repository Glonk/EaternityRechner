package ch.eaternity.shared;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.view.client.ListDataProvider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import ch.eaternity.shared.Util.RecipeScope;

/**
 * No DataProviders from CellList inside here...
 * @author aurelianjaggi
 *
 */
public class ClientData implements Serializable {

	private static final long serialVersionUID = -8325524250818194857L;
	
	public List<FoodProductInfo> productInfos = new ArrayList<FoodProductInfo>();
	
	public List<RecipeInfo> recipeInfos = new ArrayList<RecipeInfo>();
	
	public List<Kitchen> kitchens = new ArrayList<Kitchen>();
	
	public CountryDistance distances;

	public UserInfo userInfo;
	
	// probably into activity (state variables) ?
	public Kitchen currentKitchen; 

	public Recipe editRecipe;
	
	public RecipeSearchRepresentation recipeSeachRepresentation = new RecipeSearchRepresentation();
	
	public RecipeScope recipeScope = RecipeScope.PUBLIC;
	
	public ClientData() {}
	
	
	public Kitchen getKitchenByID(long id){
		for(Kitchen kitchen : kitchens){
			if (kitchen.getId() == id){
				return kitchen;
			}
		}
		return null;
	}
	
	public RecipeInfo getRecipeById(long id) {
		for(RecipeInfo recipe : recipeInfos){
			if (recipe.getId() != null && id == recipe.getId()){
				return recipe;
			}
		}
		return null;
	}
	
}
