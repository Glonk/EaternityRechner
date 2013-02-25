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
import ch.eaternity.server.jsp.StaticProperties;
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
	
	
	public class CategoryValuesByDate {
		public List<CategoryQuantities> categories;
		//mulltiple dates are possible, usually just one
		public List<Date> date = new ArrayList<Date>();
		public CO2Value co2value;
		
		public CategoryValuesByDate(){}
		
		public CategoryValuesByDate(List<CategoryQuantities> categories, List<Date> date){
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
	
	private StaticProperties props;
	public List<CatMapping> mappings 				= new ArrayList<CatMapping>();
	
	
	// -------------- Functions --------------
	// Constructors
	public CatRyzer() {
		DAO dao = new DAO();
		ingredients = dao.getAllIngredients();
	}
	
	public CatRyzer(List<Recipe> recipes, StaticProperties props)
	{
		this();
		this.props = props;
		setRecipes(recipes);
	}
	
	public CatRyzer(Collection<IngredientSpecification> ingSpecs, StaticProperties props) {
		this();
		this.props = props;
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

	
	public List<CategoryValuesByDate> getCatValsByDates() {
		List<CategoryValuesByDate> categoryValuesByDatesList = new ArrayList<CategoryValuesByDate>();
		
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
				List<CategoryQuantities> categoryValues = new ArrayList<CategoryQuantities>();
				
				Multimap<String,IngredientSpecification> catMM = MapOfcatMultiMap.get(date);
				
				for (CatMapping mapping : mappings) {
					Collection<IngredientSpecification> ingredientSpecs = catMM.get(mapping.category);
					categoryValues.add(new CategoryQuantities(mapping.category, Util.getCO2Value(ingredientSpecs), Util.getWeight(ingredientSpecs), Util.getCost(ingredientSpecs), null));
				}
				
				CategoryValuesByDate categoryValuesByDates = new CategoryValuesByDate();
				categoryValuesByDates.date.add(date);
				categoryValuesByDates.categories = categoryValues;
				categoryValuesByDates.co2value = new CO2Value();
				for (CategoryQuantities catval : categoryValues){
					categoryValuesByDates.co2value = categoryValuesByDates.co2value.add(catval.co2Value);
				}
				categoryValuesByDatesList.add(categoryValuesByDates);
			}
		}
		return categoryValuesByDatesList;
	}
	
	public List<CategoryQuantities> getCatVals() {
		Logger rootLogger = Logger.getLogger("");
		List<CategoryQuantities> categoryValues = new ArrayList<CategoryQuantities>();
		
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
				categoryValues.add(new CategoryQuantities(mapping.category, Util.getCO2Value(ingSpecs), Util.getWeight(ingSpecs), Util.getCost(ingSpecs), null));
			}
		}
		return categoryValues;	
	}
	
	public List<CategoryQuantities> getDateValues() {
		List<CategoryQuantities> dateValues = new ArrayList<CategoryQuantities>();
		
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
				Collection<IngredientSpecification> ingCollection = dateMultiMap.get(date);
				dateValues.add(new CategoryQuantities(props.dateFormatter.format(date), Util.getCO2Value(ingCollection), Util.getWeight(ingCollection), Util.getCost(ingCollection), date));
			}
		}
			
		return dateValues;
	}
	
	// use for internationalisation again, not used at the moment
	public List<CategoryQuantities> getIngVals() {
		List<CategoryQuantities> ingredientValues 	= new ArrayList<CategoryQuantities>();
		
		Multimap<String,IngredientSpecification> ingMultiMap = HashMultimap.create();
		
		if (initializedMapping && ingredientsLoaded)
		{	
			// fill Ingredients Mulimap for worst Ingredient beast top 10 
			for (IngredientSpecification ingSpec : ingSpecs) {
				if (props.locale.equals(Locale.ENGLISH))
					ingMultiMap.put(Util.getIngredientName_en(ingSpec, ingredients), ingSpec);
				else if (props.locale.equals(Locale.GERMAN))
					ingMultiMap.put(ingSpec.getName(), ingSpec);
			}
			
			for (String name : ingMultiMap.keySet()) {
				Collection<IngredientSpecification> ingCollection = ingMultiMap.get(name);
				ingredientValues.add(new CategoryQuantities(name, Util.getCO2Value(ingCollection), Util.getWeight(ingCollection), Util.getCost(ingCollection), null));
			}
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
				if (props.locale == Locale.ENGLISH)
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