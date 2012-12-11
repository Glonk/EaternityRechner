package ch.eaternity.shared;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Date;

import ch.eaternity.server.DAO;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;


public class CatRyzer {
	
	// -------------- Inner Classes --------------
	private class CatMapping {
		public String category;
		public List<String> hastags;
		public List<String> hasnottags;
		
		public CatMapping() {}
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
		public List<Date> date;
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
	private List<IngredientSpecification> ing_specs = new ArrayList<IngredientSpecification>();
	private List<Ingredient> ingredients 			= new ArrayList<Ingredient>();
	private List<CatMapping> mappings 				= new ArrayList<CatMapping>();
	private boolean initializedMapping = false;
	private boolean recipesLoaded = false;

	private List<CategoryValue> categoryValues 		= new ArrayList<CategoryValue>();
	private List<CategoryValuesByDates> categoryValuesByDates = new ArrayList<CategoryValuesByDates>();
	
	// Constructors
	public CatRyzer() {
		ingredients = dao.getAllIngredients();
	}
	
	public CatRyzer(List<Recipe> recipes)
	{
		this.recipes = recipes;
		recipesLoaded = true;
	}
	
	// -------------- Functions --------------
	
	// -------------- Public --------------
	public void setMapping(List<String> str_mappings) throws IllegalArgumentException
	{
		for (String str_mapping : str_mappings)
		{
			String map_ar[] = str_mapping.split(",");
			if (map_ar.length <= 1)
				throw new IllegalArgumentException();
			
			// clear old mapping
			mappings.clear();
			
			CatMapping newmap = new CatMapping();
			newmap.category = map_ar[1];
			for (int i = 1; i < map_ar.length;i++)
			{
				if (map_ar[i].charAt(0) == '-')
					newmap.hasnottags.add(map_ar[i].substring(1));
				else
					newmap.hastags.add(map_ar[i]);
			}
			mappings.add(newmap);
		}
		initializedMapping = true;
	}
	
	// prepares the objects CategoryValue and CategoryValuesByDates
	public void categoryze() throws IllegalStateException{
		if (initializedMapping && recipesLoaded)
		{
			// The Multimap could probably substitute categoryValues in the future ...
			Multimap<String,Long> catMultiMap = HashMultimap.create();
			
			
			// filling own object
			for(String category : catMultiMap.keySet())
			{
				Collection<Long> ingSpecIds = catMultiMap.get(category);
				categoryValues.add(new CategoryValue(category, getCo2ValueByIds(ingSpecIds)));
			}
			
			
			
		}
		else
			throw new IllegalStateException("Object not initialized");
	}
	
	public List<CategoryValuesByDates> getCatValsByDates() {
		return this.categoryValuesByDates;
	}
	
	public List<CategoryValue> getCatVals() {
		return this.categoryValues;
	}
	
	// -------------- Private --------------
	// ids refer to an IngredientSPecification Object
	private Long getCo2ValueByIds(Collection<Long> ids) {
		Long co2value = 0L;
		for (Long id : ids) {
			co2value = co2value + getIngFromIngSpecId(id).getCo2eValue();
		}
		return co2value;
	}
	
	private Ingredient getIngFromIngSpecId(Long id) {
		for (IngredientSpecification ingSpec : ing_specs) {
			if (ingSpec.getId().equals(id)) {
				for(Ingredient zutat : ingredients){
					if (zutat.getId() == ingSpec.getId()){
						return zutat;
					}
				}
			}
		}
		return null;
	}
	
	private void writeDatesToIngSpec()
	{
		
	}
	

}