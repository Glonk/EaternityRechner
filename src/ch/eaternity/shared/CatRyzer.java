package ch.eaternity.shared;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.eaternity.server.DAO;
import ch.eaternity.shared.comparators.CategoryValuesComparator;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;



public class CatRyzer {
	
	// -------------- Inner Classes --------------
	public class CatMapping {
		public String category;
		public List<String> hastags = new ArrayList<String>();
		public List<String> hasnotthistags  = new ArrayList<String>();
		public CatMapping() {}
	}
	
	public class CatFormula {
		public String category;
		public String formula;
		public boolean isHeading;
		
		public CatFormula() {}
		
		public CatFormula(String category, String formula, boolean isHeading) {
			this.category = category;
			this.formula = formula;
			this.isHeading = isHeading;
		}
		
		public CatFormula(String category, String formula) {
			this.category = category;
			this.formula = formula;
			this.isHeading = false;
		}
	}
	
	
	public class DateValue {
		public Date date;
		public CO2Value co2value;
		
		public DateValue() {}
		
		public DateValue(Date date, CO2Value co2value) {
			this.date = date;
			this.co2value = co2value;
		}
	}
	
	public class CategoryValue {
		public String categoryName;
		public CO2Value co2value;
		public Double weight;
		public Double cost;
		
		public CategoryValue(){}
		
		public CategoryValue(String name, CO2Value co2value) {
			this.categoryName = name;
			this.co2value = co2value;
		}
		public CategoryValue(String name, CO2Value co2value, Double amountGram) {
			this(name,co2value);
			this.weight = amountGram;
		}
		public CategoryValue(String name, CO2Value co2value, Double amountGram, Double cost) {
			this(name,co2value);
			this.weight = amountGram;
			this.cost = cost;
		}
	}
	
	public class CategoryValuesByDates {
		public List<CategoryValue> categories;
		//mulltiple dates are possible, usually just one
		public List<Date> date = new ArrayList<Date>();
		public CO2Value co2value;
		
		public CategoryValuesByDates(){}
		
		public CategoryValuesByDates(List<CategoryValue> categories, List<Date> date){
			this.categories = categories;
			this.date = date;
		}
	}
	
	// -------------- Class Variables --------------
	
	private List<Recipe> recipes 					= new ArrayList<Recipe>();
	private List<IngredientSpecification> ingSpecs  = new ArrayList<IngredientSpecification>();
	private List<Ingredient> ingredients 			= new ArrayList<Ingredient>();

	private boolean initializedMapping = false;
	private boolean ingredientsLoaded = false;
	
	private Locale locale;
	public List<CatMapping> mappings 				= new ArrayList<CatMapping>();
	
	
	// -------------- Functions --------------
	// Constructors
	public CatRyzer() {
		DAO dao = new DAO();
		ingredients = dao.getAllIngredients();
	}
	
	public CatRyzer(List<Recipe> recipes, Locale locale)
	{
		this();
		this.locale = locale;
		setRecipes(recipes);
	}
	
	public CatRyzer(Collection<IngredientSpecification> ingSpecs, Locale locale) {
		this();
		this.locale = locale;
		setIngredients((List<IngredientSpecification>) ingSpecs);
	}
	
	
	
	// -------------- Public Methods --------------
	
	public void setRecipes(List<Recipe> recipes) {
		this.recipes = recipes;
		writeDatesToIngSpec();
		
		//get all ingredients from all recipes, write into single list
		for (Recipe recipe : recipes){
			ingSpecs.addAll((Collection<IngredientSpecification>)recipe.getZutaten());
		}
		ingredientsLoaded = true;
	}
	
	public void setIngredients(List<IngredientSpecification> ingSpecs) {
		this.ingSpecs = ingSpecs;
		ingredientsLoaded = true;
	}
	
	
	/***
	 * Sets the current mapping of categories to tags
	 * @param str_mappings (Category, Tag1, Tag2, -Tag3, ...)
	 * @throws IllegalArgumentException
	 */
	public void setCatFormulas(List<CatFormula> formulas) throws IllegalArgumentException
	{
		// clear old mapping
		mappings.clear();
					
		for (CatFormula formula : formulas)
		{
			String tag_ar[] = formula.formula.split(",");
			if (tag_ar.length == 0)
				throw new IllegalArgumentException("A Category with no tag is not valid.");
			
			CatMapping newmap = new CatMapping();
			newmap.category = formula.category;
			for (int i = 0; i < tag_ar.length;i++)
			{
				tag_ar[i].trim();
				if (tag_ar[i].charAt(0) == '-')
					newmap.hasnotthistags.add(tag_ar[i].substring(1));
				else
					newmap.hastags.add(tag_ar[i]);
			}
			mappings.add(newmap);
		}
		initializedMapping = true;
	}

	
	public List<CategoryValuesByDates> getCatValsByDates() {
		List<CategoryValuesByDates> categoryValuesByDatesList = new ArrayList<CategoryValuesByDates>();
		
		if (initializedMapping && ingredientsLoaded)
		{
			Map<Date,Multimap<String,IngredientSpecification>> MapOfcatMultiMap = new HashMap<Date,Multimap<String,IngredientSpecification>>();
			
			for (IngredientSpecification ingSpec : ingSpecs){
				Date date = ingSpec.getCookingDate();
				
				// check if inner Multimap already exist, if not, create one
				Multimap<String,IngredientSpecification> catMM;
				if (!MapOfcatMultiMap.containsKey(date))
					catMM = HashMultimap.create();
				else
					catMM = MapOfcatMultiMap.get(date);
				
				fillCatMultiMap(catMM, ingSpec);
				MapOfcatMultiMap.put(date, catMM);
			}
			
			//  Converting MAPS to LISTS 
			
			// sort the date set
			List<Date> dateOfKeys2 = asSortedList(MapOfcatMultiMap.keySet());
			
			for(Date date : dateOfKeys2)
			{
				List<CategoryValue> categoryValues = new ArrayList<CategoryValue>();
				
				Multimap<String,IngredientSpecification> catMM = MapOfcatMultiMap.get(date);
				
				for (CatMapping mapping : mappings) {
					categoryValues.add(new CategoryValue(mapping.category, Util.getCO2Value(catMM.get(mapping.category))));
				}
				
				CategoryValuesByDates categoryValuesByDates = new CategoryValuesByDates();
				categoryValuesByDates.date.add(date);
				categoryValuesByDates.categories = categoryValues;
				categoryValuesByDates.co2value = new CO2Value();
				for (CategoryValue catval : categoryValues){
					categoryValuesByDates.co2value = categoryValuesByDates.co2value.add(catval.co2value);
				}
				categoryValuesByDatesList.add(categoryValuesByDates);
			}
		}
		return categoryValuesByDatesList;
	}
	
	public List<CategoryValue> getCatVals() {
		Logger rootLogger = Logger.getLogger("");
		List<CategoryValue> categoryValues = new ArrayList<CategoryValue>();
		
		Multimap<String,IngredientSpecification> catMultiMap = HashMultimap.create();
		
		if (initializedMapping && ingredientsLoaded)
		{	
			// String : Category, Long: id of Ingredient
			for (IngredientSpecification ingSpec : ingSpecs){
				if (getIngredient(ingSpec) == null) {
					rootLogger.log(Level.SEVERE, "Ingredient can not be found. Id of IngredientSpecification: " + ingSpec.getId() + " Id of Ingredient: " + ingSpec.getZutat_id());
				}
				else {
					fillCatMultiMap(catMultiMap, ingSpec);
				}
			}
			
			//  Converting MAPS to LISTS 
			for (CatMapping mapping : mappings) {
				Collection<IngredientSpecification> ingSpecs = catMultiMap.get(mapping.category);
				categoryValues.add(new CategoryValue(mapping.category, Util.getCO2Value(ingSpecs), Util.getWeight(ingSpecs), Util.getCost(ingSpecs)));
			}
		}
		return categoryValues;	
	}
	
	public List<DateValue> getDateValues() {
		List<DateValue> dateValues = new ArrayList<DateValue>();
		
		if (initializedMapping && ingredientsLoaded)
		{	
			
			// ---- first populate the dateValue List ------
			Multimap<Date,IngredientSpecification> dateMultiMap = HashMultimap.create();
			
			for (IngredientSpecification ingSpec : ingSpecs){
				Date date = ingSpec.getCookingDate();
				dateMultiMap.put(date, ingSpec);
			}
			
		//  Converting MAPS to LISTS 
			List<Date> dateOfKeys = asSortedList(dateMultiMap.keySet());
			for (Date date : dateOfKeys) {
				Collection<IngredientSpecification> ingredientsSpecification = dateMultiMap.get(date);
				dateValues.add(new DateValue(date, Util.getCO2Value(ingredientsSpecification)));
			}
		}
			
		return dateValues;
	}
	
	// use for internationalisation again, not used at the moment
	public List<CategoryValue> getIngVals() {
		List<CategoryValue> ingredientValues 	= new ArrayList<CategoryValue>();
		
		Multimap<String,IngredientSpecification> ingMultiMap = HashMultimap.create();
		
		if (initializedMapping && ingredientsLoaded)
		{	
			// fill Ingredients Mulimap for worst Ingredient beast top 10 
			for (IngredientSpecification ingSpec : ingSpecs) {
				if (locale.equals(Locale.ENGLISH))
					ingMultiMap.put(Util.getIngredientName_en(ingSpec, ingredients), ingSpec);
				else if (locale.equals(Locale.GERMAN))
					ingMultiMap.put(ingSpec.getName(), ingSpec);
			}
			
			for (String name : ingMultiMap.keySet()) {
				Collection<IngredientSpecification> ingCollection = ingMultiMap.get(name);
				ingredientValues.add(new CategoryValue(name, Util.getCO2Value(ingCollection), Util.getWeight(ingCollection), Util.getCost(ingCollection)));
			}
			
			// sort CategoryValue by co2-value
			Collections.sort(ingredientValues,new CategoryValuesComparator());
		}
		return ingredientValues;	
	}
	
	/**
	 *  get ingredients per category
	 * @return String1: categoryName, String2: ingredientName, null if not initialized
	 * 
	 */
	
	public Set<Pair<String,Set<String>>> getIngredientsByCategory() {
		Logger rootLogger = Logger.getLogger("");
		
		Set<Pair<String,Set<String>>> ingredientsByCategory = new HashSet<Pair<String,Set<String>>>();
		
		if (initializedMapping && ingredientsLoaded){
			Multimap<String,IngredientSpecification> catMultiMap = HashMultimap.create();
			
			// String : Category, Long: id of Ingredient
			for (IngredientSpecification ingSpec : ingSpecs){
				if (getIngredient(ingSpec) == null) {
					rootLogger.log(Level.WARNING, "Ingredient can not be found. Id of IngredientSpecification: " + ingSpec.getId() + " Id of Ingredient: " + ingSpec.getZutat_id());
				}
				else {
					fillCatMultiMap(catMultiMap, ingSpec);
				}
			}
						
			for(CatMapping mapping : mappings)
			{
				Set<String> ingredientNames;
				Collection<IngredientSpecification> ingredientsSpecification = catMultiMap.get(mapping.category);
				if (locale == Locale.ENGLISH)
					ingredientNames = Util.getIngredientsNames_en(ingredientsSpecification, ingredients);
				else
					ingredientNames = Util.getIngredientsNames_de(ingredientsSpecification);
				ingredientsByCategory.add(new Pair<String,Set<String>>(mapping.category, ingredientNames));
			}
		}	
		else
			rootLogger.log(Level.WARNING, "CatRyzer not initialized. Returning ingredientsByCategory null");
		return ingredientsByCategory;
	}


	// return total co2 amount in grams
	public CO2Value getTotalCo2() {
		return Util.getCO2Value(ingSpecs);
	}
	
	// return total weight in grams
	public Double getTotalWeight() {
		return Util.getWeight(ingSpecs);
	}
	
	//returns total cost in currency which was inputed
	public Double getTotalCost() {
		return Util.getCost(ingSpecs);
	}
	
	public Pair<Double, Double> getTotalSeasonQuotient(Collection<IngredientSpecification> ingsSpecs) {
		return Util.getSeasonQuotient(ingSpecs);
	}
	
	public List<IngredientSpecification> getIngredientSpecifications() {
		return this.ingSpecs;
	}

	
	// -------------- Private Methods --------------
	
	public static
	<T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
	  List<T> list = new ArrayList<T>(c);
	  Collections.sort(list);
	  return list;
	}
	
	private void fillCatMultiMap(Multimap<String,IngredientSpecification> catMM, IngredientSpecification ingSpec)
	{
		List<String> tags = getIngredient(ingSpec).tags;
		if (tags != null && tags.size() != 0){
			for (String tag : tags) {
				for (CatMapping mapping : mappings) {
					if (mapping.hastags.contains(tag) && doesntContainThisTags(tags, mapping.hasnotthistags)) {
						catMM.put(mapping.category, ingSpec);
					}
				}
			}
		}
	}
	
	//returns null if not found
	private Ingredient getIngredient(IngredientSpecification ingspec) {
		return getIngredient(ingspec.getZutat_id());
	}
	
	//returns null if not found
	private Ingredient getIngredient(Long id){
		for(Ingredient zutat : ingredients){
			if (zutat.getId().equals(id)){
				return zutat;
			}
		}
		return null;
	}
	
	
	private void writeDatesToIngSpec(){
		Logger rootLogger = Logger.getLogger("");
		Calendar cal = Calendar.getInstance();
		cal.set(2001, 0, 1); //year is as expected, month is zero based, date is as expected
		for (Recipe recipe : recipes){
			for (IngredientSpecification ingSpec : recipe.getZutaten())
			{
				if (recipe.cookingDate != null)
					ingSpec.setCookingDate(recipe.cookingDate);
				else
					ingSpec.setCookingDate(cal.getTime());
				if (getIngredient(ingSpec) == null) {
					rootLogger.log(Level.SEVERE, "Ingredient " + ingSpec.getName() + " can not be found and has thus been removed. Id of Specification: " + ingSpec.getId() + " Id of Ingredient: " + ingSpec.getZutat_id());
					recipe.getZutaten().remove(ingSpec);
				}
			}
		}
	}
	
	// if just on tag of hasnotthistags is contained in tags, don't add the ingredient
	// tags - the object which should not contaion hasnotthistags
	private boolean doesntContainThisTags(List<String> tags, List<String> hasnotthistags){
		boolean doesntContainTags = true; 
		for (String hasnotthistag : hasnotthistags) {
			if (tags.contains(hasnotthistag))
				doesntContainTags = false;
		}
		return doesntContainTags;
	}

	

}