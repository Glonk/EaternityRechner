package ch.eaternity.shared;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Date;
import java.util.Map;

import ch.eaternity.server.DAO;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;


public class CatRyzer {
	
	// -------------- Inner Classes --------------
	private class CatMapping {
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
	
	public class CategoryValue {
		public String categoryName;
		public Long co2value;
		
		public CategoryValue(){}
		
		public CategoryValue(String name, Long value) {
			this.categoryName = name;
			this.co2value = value;
		}
	}
	
	public class CategoryValuesByDates {
		public List<CategoryValue> category;
		//mulltiple dates are possible, usually just one
		public List<Date> date = new ArrayList<Date>();
		public Long co2value;
		
		public CategoryValuesByDates(){}
		
		public CategoryValuesByDates(List<CategoryValue> category, List<Date> date){
			this.category = category;
			this.date = date;
		}
	}
	
	// -------------- Class Variables --------------
	DAO dao = new DAO();
	private List<Recipe> recipes 					= new ArrayList<Recipe>();
	private List<IngredientSpecification> ingSpecs  = new ArrayList<IngredientSpecification>();
	private List<Ingredient> ingredients 			= new ArrayList<Ingredient>();
	private List<CatMapping> mappings 				= new ArrayList<CatMapping>();
	private boolean initializedMapping = false;
	private boolean recipesLoaded = false;

	private List<CategoryValue> categoryValues 		= new ArrayList<CategoryValue>();
	private List<CategoryValuesByDates> categoryValuesByDatesList = new ArrayList<CategoryValuesByDates>();
	
	// -------------- Functions --------------
	// Constructors
	public CatRyzer() {
		ingredients = dao.getAllIngredients();
	}
	
	public CatRyzer(List<Recipe> recipes)
	{
		this();
		this.recipes = recipes;
		writeDatesToIngSpec();
		//get all ingredients from all recipes, write into single list
		for (Recipe recipe : recipes){
			ingSpecs.addAll((Collection<IngredientSpecification>)recipe.getZutaten());
		}
		recipesLoaded = true;
	}
	
	
	
	// -------------- Public --------------
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
			//if (tag_ar.length <= 1)
			//	throw new IllegalArgumentException();
			
			
			
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
	
	// prepares the objects CategoryValue and CategoryValuesByDates
	public void categoryze() throws IllegalStateException{
		if (initializedMapping && recipesLoaded)
		{
			// ---- first populate the categoryValue List ------
			// The Multimap could probably substitute categoryValues in the future ...
			// String : Category, Long: id of Ingredient
			Multimap<String,Long> catMultiMap = HashMultimap.create();
			
			// iterate over all ingredientSpec, add them to the Map
			for (IngredientSpecification ingSpec : ingSpecs){
				List<String> tags = getIngredient(ingSpec).tags;
				for (String tag : tags) {
					for (CatMapping mapping : mappings) {
						if (mapping.hastags.contains(tag) && doesntContainThisTags(tags, mapping.hasnotthistags)) {
							catMultiMap.put(mapping.category, getIngredient(ingSpec).getId());
						}
					}
				}
			}
			
			// ---- second populate the CategoryValuesByDates List ------
			Map<Date,Multimap<String,Long>> MapOfcatMultiMap = new HashMap<Date,Multimap<String,Long>>();
			
			for (IngredientSpecification ingSpec : ingSpecs){
				Date date = ingSpec.getCookingDate();
				
				// check if inner Multimap already exist, if not, create one
				Multimap<String,Long> catMM;
				if (!MapOfcatMultiMap.containsKey(date))
					catMM = HashMultimap.create();
				else
					catMM = MapOfcatMultiMap.get(date);
				
				// fill inner Multimap
				List<String> tags = getIngredient(ingSpec).tags;
				for (String tag : tags) {
					for (CatMapping mapping : mappings) {
						if (mapping.hastags.contains(tag) && doesntContainThisTags(tags, mapping.hasnotthistags)) {
							catMM.put(mapping.category, getIngredient(ingSpec).getId());
						}
					}
				}
				MapOfcatMultiMap.put(date, catMM);
			}
			
			// filling own objects
			for(String category : catMultiMap.keySet())
			{
				Collection<Long> ingredientIds = catMultiMap.get(category);
				categoryValues.add(new CategoryValue(category, getCo2Value(ingredientIds)));
			}
			for(Date date : MapOfcatMultiMap.keySet())
			{
				List<CategoryValue> categoryValues = new ArrayList<CategoryValue>();
				
				Multimap<String,Long> catMM = MapOfcatMultiMap.get(date);
				for(String category : catMM.keySet())
				{
					Collection<Long> ingredientIds = catMultiMap.get(category);
					categoryValues.add(new CategoryValue(category, getCo2Value(ingredientIds)));
				}
				
				CategoryValuesByDates categoryValuesByDates = new CategoryValuesByDates();
				categoryValuesByDates.date.add(date);
				categoryValuesByDates.category = categoryValues;
				categoryValuesByDates.co2value = 0L;
				for (CategoryValue catval : categoryValues){
					categoryValuesByDates.co2value = categoryValuesByDates.co2value + catval.co2value;
				}
				categoryValuesByDatesList.add(categoryValuesByDates);
			}
			int i = 1;
			
		}
		else
			throw new IllegalStateException("Object not initialized");
	}
	
	public List<CategoryValuesByDates> getCatValsByDates() {
		return this.categoryValuesByDatesList;
	}
	
	public List<CategoryValue> getCatVals() {
		return this.categoryValues;	
	}
	
	// -------------- Private --------------
	
	
	// ids refer to an IngredientSPecification Object
	private Long getCo2Value(Collection<Long> ids) {
		Long co2value = 0L;
		for (Long id : ids) {
			co2value = co2value + getIngredient(id).getCo2eValue();
		}
		return co2value;
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
		for (Recipe recipe : recipes){
			for (IngredientSpecification ingSpec : recipe.getZutaten())
			{
				ingSpec.setCookingDate(recipe.cookingDate);
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