//package ch.eaternity.shared;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//import java.util.Set;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//import ch.eaternity.server.DAO;
//import ch.eaternity.shared.comparators.CategoryValuesComparator;
//
//import com.google.common.collect.HashMultimap;
//import com.google.common.collect.Multimap;
//
//
//
//public class CatRyzer {
//	
//	// -------------- Inner Classes --------------
//	public class CatMapping {
//		public String category;
//		public List<String> hastags = new ArrayList<String>();
//		public List<String> hasnotthistags  = new ArrayList<String>();
//		
//		public CatMapping() {}
//	}
//	
//	public class CatFormula {
//		public String category;
//		public String formula;
//		public boolean isHeading;
//		
//		public CatFormula() {}
//		
//		public CatFormula(String category, String formula, boolean isHeading) {
//			this.category = category;
//			this.formula = formula;
//			this.isHeading = isHeading;
//		}
//		
//		public CatFormula(String category, String formula) {
//			this.category = category;
//			this.formula = formula;
//			this.isHeading = false;
//		}
//	}
//	
//	public class Co2Value {
//		public double prodQuota;
//		public double transQuota;
//		public double condQuota;
//		public double noFactorsQuota;
//		public double totalValue;
//		
//		public Co2Value() {
//			this.prodQuota = 0.0;
//			this.transQuota = 0.0;
//			this.condQuota = 0.0;
//			this.noFactorsQuota = 0.0;
//			this.totalValue = 0.0;
//		}
//		
//		public Co2Value(double totalValue) {
//			this.totalValue = totalValue;
//		}
//		
//		public Co2Value(double prodQuota, double transQuota, double condQuota, double noFactorsQuota, double totalValue) {
//			this.prodQuota = prodQuota;
//			this.transQuota = transQuota;
//			this.condQuota = condQuota;
//			this.noFactorsQuota = noFactorsQuota;
//			this.totalValue = totalValue;
//		}
//		
//		public Co2Value add(Co2Value other) {
//			Co2Value sum = new Co2Value();
//			sum.prodQuota = this.prodQuota + other.prodQuota;
//			sum.transQuota = this.transQuota + other.transQuota;
//			sum.condQuota = this.condQuota + other.condQuota;
//			sum.noFactorsQuota = this.noFactorsQuota + other.noFactorsQuota;
//			sum.totalValue = this.totalValue + other.totalValue;
//			return sum;
//		}
//
//	}
//	
//	public class DateValue {
//		public Date date;
//		public Co2Value co2value;
//		
//		public DateValue() {}
//		
//		public DateValue(Date date, Co2Value co2value) {
//			this.date = date;
//			this.co2value = co2value;
//		}
//	}
//	
//	public class CategoryValue {
//		public String categoryName;
//		public Co2Value co2value;
//		public Double weight;
//		public Double cost;
//		
//		public CategoryValue(){}
//		
//		public CategoryValue(String name, Co2Value co2value) {
//			this.categoryName = name;
//			this.co2value = co2value;
//		}
//		public CategoryValue(String name, Co2Value co2value, Double amountGram) {
//			this(name,co2value);
//			this.weight = amountGram;
//		}
//		public CategoryValue(String name, Co2Value co2value, Double amountGram, Double cost) {
//			this(name,co2value);
//			this.weight = amountGram;
//			this.cost = cost;
//		}
//	}
//	
//	public class CategoryValuesByDates {
//		public List<CategoryValue> categories;
//		//mulltiple dates are possible, usually just one
//		public List<Date> date = new ArrayList<Date>();
//		public Co2Value co2value;
//		
//		public CategoryValuesByDates(){}
//		
//		public CategoryValuesByDates(List<CategoryValue> categories, List<Date> date){
//			this.categories = categories;
//			this.date = date;
//		}
//	}
//	
//	// -------------- Class Variables --------------
//	DAO dao = new DAO();
//	Logger rootLogger;
//	private List<Recipe> recipes 					= new ArrayList<Recipe>();
//	private List<Ingredient> ingSpecs  = new ArrayList<Ingredient>();
//	private List<FoodProduct> ingredients 			= new ArrayList<FoodProduct>();
//
//	private boolean initializedMapping = false;
//	private boolean recipesLoaded = false;
//
//	private List<DateValue> dateValues 				= new ArrayList<DateValue>();
//	private List<CategoryValue> categoryValues 		= new ArrayList<CategoryValue>();
//	private List<CategoryValuesByDates> categoryValuesByDatesList = new ArrayList<CategoryValuesByDates>();
//	private List<CategoryValue> ingredientValues 	= new ArrayList<CategoryValue>();
//	
//	private Locale locale;
//	
//	public Multimap<String,Ingredient> ingMultiMap = HashMultimap.create();
//	public Multimap<String,Ingredient> catMultiMap = HashMultimap.create();
//	public List<CatMapping> mappings 				= new ArrayList<CatMapping>();
//	
//	
//	
//	// -------------- Functions --------------
//	// Constructors
//	public CatRyzer() {
//		//Initialize the Logger
//		rootLogger = Logger.getLogger("");
//		ingredients = dao.getAllIngredients();
//	}
//	
//	public CatRyzer(List<Recipe> recipes, Locale locale)
//	{
//		this();
//		this.recipes = recipes;
//		this.locale = locale;
//		writeDatesToIngSpec();
//		//get all ingredients from all recipes, write into single list
//		for (Recipe recipe : recipes){
//			ingSpecs.addAll((Collection<Ingredient>)recipe.getIngredients());
//		}
//		recipesLoaded = true;
//	}
//	
//	
//	
//	// -------------- Public --------------
//	/***
//	 * Sets the current mapping of categories to tags
//	 * @param str_mappings (Category, Tag1, Tag2, -Tag3, ...)
//	 * @throws IllegalArgumentException
//	 */
//	public void setCatFormulas(List<CatFormula> formulas) throws IllegalArgumentException
//	{
//		// clear old mapping
//		mappings.clear();
//					
//		for (CatFormula formula : formulas)
//		{
//			String tag_ar[] = formula.formula.split(",");
//			if (tag_ar.length == 0)
//				throw new IllegalArgumentException("A Category with no tag is not valid.");
//			
//			CatMapping newmap = new CatMapping();
//			newmap.category = formula.category;
//			for (int i = 0; i < tag_ar.length;i++)
//			{
//				tag_ar[i].trim();
//				if (tag_ar[i].charAt(0) == '-')
//					newmap.hasnotthistags.add(tag_ar[i].substring(1));
//				else
//					newmap.hastags.add(tag_ar[i]);
//			}
//			mappings.add(newmap);
//		}
//		initializedMapping = true;
//	}
//	
//	// prepares the objects CategoryValue and CategoryValuesByDates
//	public void categoryze() throws IllegalStateException{
//		if (initializedMapping && recipesLoaded)
//		{	
//			
//			// ---- first populate the dateValue List ------
//			Multimap<Date,Ingredient> dateMultiMap = HashMultimap.create();
//			
//			for (Ingredient ingSpec : ingSpecs){
//				Date date = ingSpec.getCookingDate();
//				dateMultiMap.put(date, ingSpec);
//			}
//						
//			// ---- second populate the categoryValue List ------
//			// The Multimap could probably substitute categoryValues in the future ...
//			// ... when it wouldn't be so darn f*ing complicated to debug ; )
//			// String : Category, Long: id of Ingredient
//			
//			for (Ingredient ingSpec : ingSpecs){
//				if (getIngredient(ingSpec) == null) {
//					rootLogger.log(Level.SEVERE, "Ingredient can not be found. Id of IngredientSpecification: " + ingSpec.getId() + " Id of Ingredient: " + ingSpec.getId());
//				}
//				else {
//					fillCatMultiMap(catMultiMap, ingSpec);
//				}
//			}
//			
//			// ---- third populate the CategoryValuesByDates List ------
//			Map<Date,Multimap<String,Ingredient>> MapOfcatMultiMap = new HashMap<Date,Multimap<String,Ingredient>>();
//			
//			for (Ingredient ingSpec : ingSpecs){
//				Date date = ingSpec.getCookingDate();
//				
//				// check if inner Multimap already exist, if not, create one
//				Multimap<String,Ingredient> catMM;
//				if (!MapOfcatMultiMap.containsKey(date))
//					catMM = HashMultimap.create();
//				else
//					catMM = MapOfcatMultiMap.get(date);
//				
//				fillCatMultiMap(catMM, ingSpec);
//				MapOfcatMultiMap.put(date, catMM);
//			}
//			
//			// -------------------- Converting MAPS to LISTS -------------------------
//
//			// sort the Date Set
//			List<Date> dateOfKeys = asSortedList(dateMultiMap.keySet());
//			for (Date date : dateOfKeys) {
//				Collection<Ingredient> ingredientsSpecification = dateMultiMap.get(date);
//				dateValues.add(new DateValue(date, getCo2Value(ingredientsSpecification)));
//			}
//			
//			for (CatMapping mapping : mappings) {
//				Collection<Ingredient> ingSpecs = catMultiMap.get(mapping.category);
//				categoryValues.add(new CategoryValue(mapping.category, getCo2Value(ingSpecs), getWeight(ingSpecs), getCost(ingSpecs)));
//			}
//			
//
//			
//			// sort the date set
//			List<Date> dateOfKeys2 = asSortedList(MapOfcatMultiMap.keySet());
//			
//			for(Date date : dateOfKeys2)
//			{
//				List<CategoryValue> categoryValues = new ArrayList<CategoryValue>();
//				
//				Multimap<String,Ingredient> catMM = MapOfcatMultiMap.get(date);
//				
//				for (CatMapping mapping : mappings) {
//					categoryValues.add(new CategoryValue(mapping.category, getCo2Value(catMM.get(mapping.category))));
//				}
//				
//				CategoryValuesByDates categoryValuesByDates = new CategoryValuesByDates();
//				categoryValuesByDates.date.add(date);
//				categoryValuesByDates.categories = categoryValues;
//				categoryValuesByDates.co2value = new Co2Value();
//				for (CategoryValue catval : categoryValues){
//					categoryValuesByDates.co2value = categoryValuesByDates.co2value.add(catval.co2value);
//				}
//				categoryValuesByDatesList.add(categoryValuesByDates);
//			}
//			
//			// fill Ingredients Mulimap for worst Ingredient beast top 10 
//			for (Ingredient ingSpec : ingSpecs) {
//				if (locale.equals(Locale.ENGLISH))
//					ingMultiMap.put(getIngredientName_en(ingSpec), ingSpec);
//				else if (locale.equals(Locale.GERMAN))
//					ingMultiMap.put(ingSpec.getName(), ingSpec);
//			}
//			
//			for (String name : ingMultiMap.keySet()) {
//				Collection<Ingredient> ingCollection = ingMultiMap.get(name);
//				ingredientValues.add(new CategoryValue(name, getCo2Value(ingCollection), getWeight(ingCollection), getCost(ingCollection)));
//			}
//			
//			// sort CategoryValue by co2-value
//			Collections.sort(ingredientValues,new CategoryValuesComparator());
//		
//			
//		}
//		else
//			throw new IllegalStateException("Object not initialized");
//	}
//	
//	public List<CategoryValuesByDates> getCatValsByDates() {
//		return this.categoryValuesByDatesList;
//	}
//	
//	public List<CategoryValue> getCatVals() {
//		return this.categoryValues;	
//	}
//	
//	public List<DateValue> getDateValues() {
//		return this.dateValues;
//	}
//	
//	public List<CategoryValue> getIngVals() {
//		return this.ingredientValues;	
//	}
//
//	// return total co2 amount in grams
//	public Co2Value getTotalCo2() {
//		return getCo2Value(ingSpecs);
//	}
//	
//	// return total weight in grams
//	public Double getTotalWeight() {
//		return getWeight(ingSpecs);
//	}
//	
//	//returns total cost in currency which was inputed
//	public Double getTotalCost() {
//		return getCost(ingSpecs);
//	}
//	
//	// how many of the vegetables and fruits are seasonal, fresh from switzerland
//	// lies between zero and one
//	public Pair<Double, Double> getSeasonQuotient() {
//		Integer numFruitsAndVegetables = 0;
//		Integer numAreSeasonal = 0;
//		Double seasonalWeight = 0.0;
//		Double totalWeight = 0.0;
//		for(Ingredient ingSpec: ingSpecs) {
//			if (ingSpec.getCookingDate() != null && ingSpec.hasSeason()) {
//				numFruitsAndVegetables++;
//				totalWeight =  totalWeight + ingSpec.getWeight();
//				
//				SeasonDate dateStart = ingSpec.getStartSeason();
//				SeasonDate dateStop = ingSpec.getStopSeason();
//				SeasonDate dateCook = new SeasonDate();
//				dateCook.setDate(ingSpec.getCookingDate());
//				// have season?
//				if (dateCook.after(dateStart) && dateCook.before(dateStop)) {
//					// are fresh from switzerland
//					if (ingSpec.getExtraction().symbol.equals("Schweiz") && ingSpec.getCondition().symbol.equals("frisch")) {
//						numAreSeasonal++;
//						seasonalWeight = seasonalWeight + ingSpec.getWeight();
//					}
//				}
//
//			}
//			
//		}
//
//
//		Pair<Double,Double> pair = new Pair<Double,Double>(numAreSeasonal.doubleValue()/numFruitsAndVegetables.doubleValue(), seasonalWeight/totalWeight);
//		return pair;
//	}
//
//	
//	
//	// -------------- Private --------------
//
//	
//	public static
//	<T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
//	  List<T> list = new ArrayList<T>(c);
//	  Collections.sort(list);
//	  return list;
//	}
//	
//	private void fillCatMultiMap(Multimap<String,Ingredient> catMM, Ingredient ingSpec)
//	{
//		List<String> tags = getIngredient(ingSpec).getTags();
//		if (tags != null && tags.size() != 0){
//			for (String tag : tags) {
//				for (CatMapping mapping : mappings) {
//					if (mapping.hastags.contains(tag) && doesntContainThisTags(tags, mapping.hasnotthistags)) {
//						catMM.put(mapping.category, ingSpec);
//					}
//				}
//			}
//		}
//	}
//	
//	private Co2Value getCo2Value(Collection<Ingredient> ingsSpecs) {
//		Co2Value co2value = new Co2Value(0.0,0.0,0.0,0.0,0.0);
//		for (Ingredient ingSpec : ingsSpecs) {
//			co2value.condQuota = co2value.condQuota + ingSpec.getConditionQuota();
//			co2value.transQuota = co2value.transQuota + ingSpec.getTransportationQuota();
//			co2value.prodQuota = co2value.prodQuota + ingSpec.getProductionQuota();
//			co2value.noFactorsQuota = co2value.noFactorsQuota + ingSpec.calculateCo2ValueNoFactors();
//			co2value.totalValue = co2value.totalValue + ingSpec.getCalculatedCO2Value();
//		}
//		return co2value;
//	}
//	
//	private Double getWeight(Collection<Ingredient> ingredientsSpecifications) {
//		Double amount = 0.0;
//		for (Ingredient ingredientSpecification : ingredientsSpecifications) {
//			amount = amount + ingredientSpecification.getWeight();
//		}
//		return amount;
//	}
//	
//	private Double getCost(Collection<Ingredient> ingredientsSpecifications) {
//		Double cost = 0.0;
//		for (Ingredient ingredientSpecification : ingredientsSpecifications) {
//			cost = cost + ingredientSpecification.getCost();
//		}
//		return cost;
//	}
//	
//	
//	public Set<String> getIngredientsNames_de(Collection<Ingredient> ingSpecs){
//		Set<String> names = new HashSet<String>();
//		for (Ingredient ingSpec : ingSpecs){
//			names.add(ingSpec.getName());
//		}
//		
//		return names;
//	}
//	
//	public Set<String> getIngredientsNames_en(Collection<Ingredient> ingSpecs){
//		Set<String> names = new HashSet<String>();
//		for (Ingredient ingSpec : ingSpecs){
//				names.add(getIngredientName_en(ingSpec));
//		}	
//		return names;
//	}
//	
//	public String getIngredientName_en(Ingredient ingSpec){
//		FoodProduct ing = getIngredient(ingSpec);
//		if (ing == null)
//			return ingSpec.getName();
//		else if (ing.getSymbol_en() == null)
//			return ingSpec.getName() + "(no eng)";
//		else
//			return ing.getSymbol_en();
//	}
//	
//	//returns null if not found
//	private FoodProduct getIngredient(Ingredient ingspec) {
//		return getIngredient(ingspec.getId());
//	}
//	
//	//returns null if not found
//	private FoodProduct getIngredient(Long id){
//		for(FoodProduct zutat : ingredients){
//			if (zutat.getId().equals(id)){
//				return zutat;
//			}
//		}
//		return null;
//	}
//	
//	private void writeDatesToIngSpec(){
//		Calendar cal = Calendar.getInstance();
//		cal.set(2001, 0, 1); //year is as expected, month is zero based, date is as expected
//		for (Recipe recipe : recipes){
//			for (Ingredient ingSpec : recipe.getIngredients())
//			{
//				if (recipe.getCookingDate() != null)
//					ingSpec.setCookingDate(recipe.getCookingDate());
//				else
//					ingSpec.setCookingDate(cal.getTime());
//				if (getIngredient(ingSpec) == null) {
//					rootLogger.log(Level.SEVERE, "Ingredient " + ingSpec.getName() + " can not be found and has thus been removed. Id of Specification: " + ingSpec.getId() + " Id of Ingredient: " + ingSpec.getId());
//					recipe.getIngredients().remove(ingSpec);
//				}
//			}
//		}
//	}
//	
//	// if just on tag of hasnotthistags is contained in tags, don't add the ingredient
//	// tags - the object which should not contaion hasnotthistags
//	private boolean doesntContainThisTags(List<String> tags, List<String> hasnotthistags){
//		boolean doesntContainTags = true; 
//		for (String hasnotthistag : hasnotthistags) {
//			if (tags.contains(hasnotthistag))
//				doesntContainTags = false;
//		}
//		return doesntContainTags;
//	}
//	
//
//}