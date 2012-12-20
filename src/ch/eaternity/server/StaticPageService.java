package ch.eaternity.server;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import ch.eaternity.server.DAO;
import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.IngredientSpecification;
import ch.eaternity.shared.Converter;
import ch.eaternity.shared.RecipeComment;
import ch.eaternity.shared.comparators.RezeptDateComparator;
import ch.eaternity.shared.CatRyzer;


import java.util.List;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;
import java.util.Collection;
import java.util.Arrays;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.logging.Logger;
import java.util.logging.Level;


import java.net.MalformedURLException;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Date;

/*
 * For outsourcing all functionality of report_lg.jsp
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
	
	Logger rootLogger;
	public boolean everythingFine = true;

	public  UserService userService;
	public  User user;
	DAO dao;

	public List<Recipe> kitchenRecipes = new ArrayList<Recipe>();
	Long kitchenLongId = 0L;

	public DecimalFormat formatter = new DecimalFormat("##");
	public SimpleDateFormat dateFormatter = new SimpleDateFormat("d. MMMM yyyy",Locale.ENGLISH);

	// some precalculation of values
	public Double MaxValueRezept = 0.0;
	public Double MinValueRezept = 10000000.0;
	Double average = 0.0;
	Double median = 0.0;
	Integer counter = 0;
	
	// calculate average, median, min, max
	private ArrayList<Double> values = new ArrayList<Double>();
	
	Date date = new Date();
	long iTimeStamp = (long) (date.getTime() * .00003);

	
	public StaticPageService() {
	}
	
	public StaticPageService(String BASEURL,String tempIds,String permanentId,String kitchenId,String pdf, List<CatRyzer.CatFormula> categoryFormulas) {
	
		rootLogger = Logger.getLogger("");
		dao = new DAO();
		
		this.BASEURL = BASEURL;
		this.tempIds = tempIds;
		this.permanentId = permanentId;
		this.kitchenId = kitchenId;
		this.pdf = pdf;
		
		userService = UserServiceFactory.getUserService();
		user = userService.getCurrentUser();
		
		// -------------- Parse Adress Parameters
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
					rootLogger.log(Level.SEVERE, "No Kitchen Id or permanend Id passed. Pass it with ?kid=234234 ");
					everythingFine = false;
				}
			}
		}
		
		if (kitchenRecipes.size() == 0)
		{
			rootLogger.log(Level.SEVERE, "Kitchen with id=" + kitchenLongId + "doesn't contains any recipes or kitchen doesn't exist.");
			everythingFine = false;
		}

		for (Recipe recipe : kitchenRecipes){
			if (recipe.cookingDate == null) {
				kitchenRecipes.remove(recipe);
				rootLogger.log(Level.SEVERE, "Following recipe has no cooking date setted and thus been removed: " + recipe.getSymbol());
			}
		}
		Collections.sort(kitchenRecipes,new RezeptDateComparator());

		Calendar c = Calendar.getInstance();
		//  go over the Recipes in the Workspace
		for(Recipe recipe: kitchenRecipes){
			recipe.setCO2Value();
			Double value = recipe.getCO2Value();
			getMinMax(value);
			
			// TODO -> remove this baaaaad hack. Dates where saved wrongly with EHL
			c.setTime(recipe.cookingDate);
			c.add(Calendar.DATE, 1);  // number of days to add
			recipe.cookingDate = c.getTime();
		}
		if (values.size() > 0)
			setMedianAverage();

	}

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

	private void getMinMax(Double value) {
		
		values.add((double) value);
		average = average + value;
		counter++;
		if(value>MaxValueRezept){
			MaxValueRezept = value;
		} 
		if(value<MinValueRezept){
			MinValueRezept = value;
		}
	}
}
