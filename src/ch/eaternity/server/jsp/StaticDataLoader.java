package ch.eaternity.server.jsp;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import ch.eaternity.server.DAO;
import ch.eaternity.server.jsp.StaticProperties.ValueType;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.IngredientSpecification;
import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.Util;

import ch.eaternity.shared.CO2Value;
import ch.eaternity.shared.comparators.RezeptDateComparator;

import java.util.Collection;
import java.util.List;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.Locale;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.logging.Logger;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;


/*
 * For outsourcing all functionality of report_lang.jsp
 */
public class StaticDataLoader implements Serializable{

	private static final long serialVersionUID = 588838682566492104L;
	
	// -------------- Class Variables --------------
	
	public String errorMessage;
	public boolean everythingFine = true;

	public List<Recipe> recipes = new ArrayList<Recipe>();
	public Long kitchenLongId = 0L;

	public boolean DoItWithPermanentIds = true;
	
	private StaticProperties properties;

	
// --------------------- public Methods --------------
	
	public StaticDataLoader() {}
	
	public void initialize(StaticProperties properties, boolean removeRecipesWithoutDate) {
		
		// -------------- Parse Request Parameters -----------------
		Logger rootLogger = Logger.getLogger("");
		DAO dao = new DAO();
		
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		
		if(properties.tempIds != null){
			recipes = dao.getRecipeByIds(properties.tempIds,true);
		} 
		else if(properties.permanentId != null){
			recipes = dao.getRecipeByIds(properties.permanentId,false);
			DoItWithPermanentIds  = false;
		} 
		else if(properties.kitchenId != null){
			kitchenLongId = Long.parseLong(properties.kitchenId);
			recipes = dao.getKitchenRecipes(kitchenLongId);
			DoItWithPermanentIds = false;
		}
		else if (user != null) {
			recipes = dao.getYourRecipe(user);
			if (userService.isUserAdmin())
				recipes.addAll(dao.adminGetRecipe(user));
		}
		else {
			rootLogger.log(Level.SEVERE, "No recipes could be loaded. Plz login or pass correct id/pid/kid. <br />");
			errorMessage = errorMessage + "No recipes could be loaded. Plz login or pass correct id/pid/kid.  <br />";
			everythingFine = false;
		}
		
		
		Calendar c = Calendar.getInstance();
		
		
		//  Load the corresponding Recipes
		for(Recipe recipe: recipes){
			recipe.setCO2Value();
			
			if (removeRecipesWithoutDate && recipe.cookingDate == null) {
				recipes.remove(recipe);
				rootLogger.log(Level.SEVERE, "Following recipe has no cooking date setted and thus been removed: " + recipe.getSymbol() + "<br />");
				errorMessage = errorMessage + "Following recipe has no cooking date setted and thus been removed: " + recipe.getSymbol() + "<br />";
			}
			else if (recipe.cookingDate != null) {
				// TODO -> remove this baaaaad hack. Dates where saved wrongly with EHL
				c.setTime(recipe.cookingDate);
				c.add(Calendar.DATE, 1);  // number of days to add
				recipe.cookingDate = c.getTime();
			}
		}
		
		
		if (recipes.size() == 0)
		{
			rootLogger.log(Level.SEVERE, "No recipes could be loaded. Plz login or pass correct id/pid/kid.<br />");
			errorMessage = errorMessage + "No recipes could be loaded. Plz login or pass correct id/pid/kid.  <br />";
			everythingFine = false;
		}
		
		Collections.sort(recipes,new RezeptDateComparator());
		
	}
	

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public boolean isEverythingFine() {
		return everythingFine;
	}

	public void setEverythingFine(boolean everythingFine) {
		this.everythingFine = everythingFine;
	}


	public List<Recipe> getRecipes() {
		return recipes;
	}

	public void setRecipes(List<Recipe> recipes) {
		this.recipes = recipes;
	}

	public Long getKitchenLongId() {
		return kitchenLongId;
	}

	public void setKitchenLongId(Long kitchenLongId) {
		this.kitchenLongId = kitchenLongId;
	}


	public boolean isDoItWithPermanentIds() {
		return DoItWithPermanentIds;
	}

	public void setDoItWithPermanentIds(boolean doItWithPermanentIds) {
		DoItWithPermanentIds = doItWithPermanentIds;
	}


}
