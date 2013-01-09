package ch.eaternity.server;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import ch.eaternity.server.DAO;
import ch.eaternity.shared.Recipe;

import ch.eaternity.shared.comparators.RezeptDateComparator;
import ch.eaternity.shared.CatRyzer;

import java.util.Collection;
import java.util.List;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.Locale;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.logging.Logger;
import java.util.logging.Level;


/*
 * For outsourcing all functionality of report_lang.jsp
 */
public class StaticPageService {

	// -------------- Class Variables --------------
	public String BASEURL;
	public String tempIds;
	public String permanentId;
	public String kitchenId;
	public String pdf;
	public Boolean doItWithPermanentIds = true;
	
	public CatRyzer catryzer;
	
	public Logger rootLogger;
	public String errorMessage;
	public boolean everythingFine = true;

	public UserService userService;
	public User user;
	public DAO dao;

	public List<Recipe> kitchenRecipes = new ArrayList<Recipe>();
	public Long kitchenLongId = 0L;

	public SimpleDateFormat dateFormatter;

	public List<CatRyzer.DateValue> valuesByDate;
	public List<CatRyzer.CategoryValue> valuesByCategory;
	public List<CatRyzer.CategoryValue> valuesByIngredient;
	public List<CatRyzer.CategoryValuesByDates> valuesByDate_Category;
	
	// some precalculation of values
	public Double maxValKitchenRecipes = 0.0;
	public Double minValKitchenRecipes = 10000000000000.0;
	public Double maxValTemp = 0.0;
	public Double minValTemp = 10000000000000.0;
	
	public StaticPageService() {
		rootLogger = Logger.getLogger("");
		dao = new DAO();
		
		userService = UserServiceFactory.getUserService();
		user = userService.getCurrentUser();
	}
	
	public StaticPageService(String BASEURL,String tempIds,String permanentId,String kitchenId,String pdf, List<CatRyzer.CatFormula> categoryFormulas, Locale locale) {
	
		this();
		
		dateFormatter = new SimpleDateFormat("d. MMMM yyyy",locale);
		 
		this.BASEURL = BASEURL;
		this.tempIds = tempIds;
		this.permanentId = permanentId;
		this.kitchenId = kitchenId;
		this.pdf = pdf;
		
		// -------------- Parse Request Parameters
		if(tempIds != null){
			kitchenRecipes = dao.getRecipeByIds(tempIds,true);
		} 
		else {
			// rootLogger.log(Level.INFO, "Loading Data: Test if Logger works.");
			if(permanentId != null){
				kitchenRecipes = dao.getRecipeByIds(permanentId,false);

				doItWithPermanentIds = false;
			} 
			else {
				if(kitchenId != null){
					kitchenLongId = Long.parseLong(kitchenId);
					kitchenRecipes = dao.getKitchenRecipes(kitchenLongId);
					doItWithPermanentIds = false;
				}
				else {
					rootLogger.log(Level.SEVERE, "No Kitchen Id or permanend Id passed. Pass it with ?kid=234234 <br />");
					errorMessage = errorMessage + "No Kitchen Id or permanend Id passed. Pass it with ?kid=234234. <br />";
					everythingFine = false;
				}
			}
		}
		
		Calendar c = Calendar.getInstance();
		List<Double> values = new ArrayList<Double>();
		
		//  go over the Recipes in the Workspace
		for(Recipe recipe: kitchenRecipes){
			recipe.setCO2Value();
			values.add(recipe.getCO2Value());
			
			if (recipe.cookingDate == null) {
				kitchenRecipes.remove(recipe);
				rootLogger.log(Level.SEVERE, "Following recipe has no cooking date setted and thus been removed: " + recipe.getSymbol() + "<br />");
				errorMessage = errorMessage + "Following recipe has no cooking date setted and thus been removed: " + recipe.getSymbol() + "<br />";
			}
			
			// TODO -> remove this baaaaad hack. Dates where saved wrongly with EHL
			c.setTime(recipe.cookingDate);
			c.add(Calendar.DATE, 1);  // number of days to add
			recipe.cookingDate = c.getTime();
		}
		
		setMinMax(values);
		maxValKitchenRecipes = maxValTemp;
		minValKitchenRecipes = minValTemp;
		
		if (kitchenRecipes.size() == 0)
		{
			rootLogger.log(Level.SEVERE, "Kitchen with id=" + kitchenLongId + "doesn't contains any recipes or kitchen doesn't exist.<br />");
			errorMessage = errorMessage + "Kitchen with id=" + kitchenLongId + "doesn't contains any recipes or kitchen doesn't exist. <br />";
			everythingFine = false;
		}
		
		Collections.sort(kitchenRecipes,new RezeptDateComparator());
		
		// ----------- CatRyzer -----------
		catryzer = new CatRyzer(kitchenRecipes,locale);

		catryzer.setCatFormulas(categoryFormulas);
		catryzer.categoryze();

		valuesByDate = catryzer.getDateValues();
		valuesByCategory = catryzer.getCatVals();
		valuesByIngredient = catryzer.getIngVals();
		valuesByDate_Category = catryzer.getCatValsByDates(); 
	}
	
	public void setMinMax(Collection<Double> values) {
		for (Double value : values) {
			if(value>maxValTemp){
				maxValTemp = value;
			} 
			if(value<minValTemp){
				minValTemp = value;
			}
		}
	}
	
	public String getNormalisedLength(Double val) {
		DecimalFormat formatter = new DecimalFormat("##");
		return formatter.format(val/maxValTemp*200);
	}

	//Median: 50% of values over median, 50% under median
	//Not used yet - programm it when used.
	/*
	private void setMedianAverage() {
		average = (average /counter);

		Collections.sort(values);

		if (values.size() % 2 == 1)
			median = values.get((values.size()+1)/2-1)  ;
		else {
			double lower;
			if (values.size() >= 1)
				lower = values.get(values.size()/2-1);
			else
				lower = values.get(values.size()/2);
			
			double upper = (values.get(values.size()/2));				

			median = ((lower + upper) / 2.0);
		}
	}
	*/

	// 

}
