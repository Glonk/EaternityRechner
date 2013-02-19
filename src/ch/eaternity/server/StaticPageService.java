package ch.eaternity.server;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import ch.eaternity.server.DAO;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.IngredientSpecification;
import ch.eaternity.shared.Recipe;

import ch.eaternity.shared.CatRyzer.Co2Value;
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
public class StaticPageService implements Serializable{

	private static final long serialVersionUID = 588838682566492104L;
	
	// -------------- Class Variables --------------
	public String BASEURL;
	public String tempIds;
	public String permanentId;
	public String kitchenId;
	public String pdfStr;
	public String thresholdStr;
	public String extraStr;
	public String personsStr;

	public boolean pdf = true;
	public Integer threshold = 1500;
	public Integer extra = 0;
	public Integer persons = 1;
	
	public String errorMessage;
	public boolean everythingFine = true;

	public List<Recipe> recipes = new ArrayList<Recipe>();
	public Long kitchenLongId = 0L;
	public List<Double> values = new ArrayList<Double>();
	
	// passing Parameters when used as a bean


	public boolean DoItWithPermanentIds = true;
	
	public StaticProperties properties;

	
// --------------------- public Methods --------------
	
	public StaticPageService() {}
	
	public void initialize(HttpServletRequest request, StaticProperties properties, boolean removeRecipesWithoutDate) {
		 
		BASEURL = request.getRequestURL().toString();
		tempIds = request.getParameter("ids");
		permanentId = request.getParameter("pid");
		kitchenId = request.getParameter("kid");
		pdfStr = request.getParameter("pdf");
		thresholdStr = request.getParameter("median");
		extraStr = request.getParameter("extra");
		personsStr = request.getParameter("persons");
		
		// -------------- Parse Request Parameters -----------------
		Logger rootLogger = Logger.getLogger("");
		DAO dao = new DAO();
		
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		
		if(tempIds != null){
			recipes = dao.getRecipeByIds(tempIds,true);
		} 
		else if(permanentId != null){
			recipes = dao.getRecipeByIds(permanentId,false);
			DoItWithPermanentIds  = false;
		} 
		else if(kitchenId != null){
			kitchenLongId = Long.parseLong(kitchenId);
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
			values.add(recipe.getCO2Value());
			
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
	
	public String getCo2ValueBar(Collection<Double> allValues, Co2Value value, int totalLength) {
		String html = "";
		
		if (value.noFactorsQuota > 0.0) 
			html = html + "<img class='bar' src='gray.png' alt='gray' height='11'  width=" + totalLength/getMax(allValues)*value.noFactorsQuota + " />";
		if (value.transQuota > 0.0) 
			html = html + "<img class='bar' src='orange.png' alt='orange' height='11'  width=" + totalLength/getMax(allValues)*value.transQuota + " />";
		if (value.condQuota > 0.0) 
			html = html + "<img class='bar' src='green.png' alt='green' height='11'  width=" + totalLength/getMax(allValues)*value.condQuota + " />";
		if (value.prodQuota > 0.0) 
			html = html + "<img class='bar' src='light-gray.png' alt='light-gray' height='11'  width=" + totalLength/getMax(allValues)*value.prodQuota + " />";

		return html;
	}
	
	public String getCo2ValueBarSimple(Collection<Double> allValues, Double value, int totalLength) {
		String html = "<img class='bar' src='gray.png' alt='gray' height='11'  width=" + totalLength/getMax(allValues)*value + " />";
		return html;
	}
	
	
	// just for testing
	public double getMax(Collection<Double> values) {
		double max = 0.0;
		for (Double value : values) {
			if(value>max)
				max = value;
		}
		return max;
	}
	
	public Double getAverage() {
		Double average = 0D;
		for (Double value : values) {
			average = average + value;
		}
		average = average / values.size();
		return average;
	}
	
	public Double getMedian() {
		Collections.sort(values);
		Double median = 0D;
		
	    if (values.size() % 2 == 1)
	    	median = values.get((values.size()+1)/2-1);
	    else
	    {
	    	double lower;
			if (values.size() >= 1)
				lower = values.get(values.size()/2-1);
			else
				lower = values.get(values.size()/2);
			
			double upper = (values.get(values.size()/2));				

			median = ((lower + upper) / 2.0);
	    }
	    return median;
	}
	
	public String getNormalisedLength(Double val, Collection<Double> values) {
		
		DecimalFormat formatter = new DecimalFormat("##");
		return formatter.format(val/getMax(values)*200);
	}

	public String getBASEURL() {
		return BASEURL;
	}

	public void setBASEURL(String bASEURL) {
		BASEURL = bASEURL;
	}

	public String getTempIds() {
		return tempIds;
	}

	public void setTempIds(String tempIds) {
		this.tempIds = tempIds;
	}

	public String getPermanentId() {
		return permanentId;
	}

	public void setPermanentId(String permanentId) {
		this.permanentId = permanentId;
	}

	public String getKitchenId() {
		return kitchenId;
	}

	public void setKitchenId(String kitchenId) {
		this.kitchenId = kitchenId;
	}

	public boolean isPdf() {
		return pdf;
	}

	public void setPdf(boolean pdf) {
		this.pdf = pdf;
	}

	public Integer getThreshold() {
		return threshold;
	}

	public void setThreshold(Integer threshold) {
		this.threshold = threshold;
	}

	public Integer getExtra() {
		return extra;
	}

	public void setExtra(Integer extra) {
		this.extra = extra;
	}

	public Integer getPersons() {
		return persons;
	}

	public void setPersons(Integer persons) {
		this.persons = persons;
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

	public List<Double> getValues() {
		return values;
	}

	public void setValues(List<Double> values) {
		this.values = values;
	}

	public boolean isDoItWithPermanentIds() {
		return DoItWithPermanentIds;
	}

	public void setDoItWithPermanentIds(boolean doItWithPermanentIds) {
		DoItWithPermanentIds = doItWithPermanentIds;
	}


}
