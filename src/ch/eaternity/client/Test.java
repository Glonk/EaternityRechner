package ch.eaternity.client;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Date;

import com.google.gwt.thirdparty.guava.common.collect.Multimap;

import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.IngredientSpecification;
import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.Categories;
import ch.eaternity.shared.Workgroup;


public class Test {
// Input: List<recipe>
	Constructor ( List<Recipe> recipes )
	
	Map<String,List<IngredientSpecification>> getIngsByCategory(List<IngredientSpecification> ingredients, String category){}

	Map<Date,List<IngredientSpecification>> getIngsByDate(List<IngredientSpecification> ingredients, List<Date> dates) {}
	
	List<Date> getAllDates(List<IngredientSpecification> ingredients) {}
	
	List<String> getAllCats() {}
	
	List<Categories.CategoryValuesByDates> getCategoryValuesByDates() {}
	
	Map<String,List<IngredientSpecification>> getIngredientsPerCategory() {}
	
	// new implementation used
	List<IngredientSpecification> getIngsFromKitchen(Workgroup kitchen) {}
	
	Long calculateTotalC02Value(List<IngredientSpecification> ingredients) {}
	
	List<IngredientSpecification> getIngredientsByFormula(List<IngredientSpecification> ingredients, String formula) {}
	
	Ingredient getIngFromId(Long id) {}
	
	boolean catMatchesTags(List<String> tags, String cat) {}
	
	private class Categories
	
// 	Parameters
	Workgroup kitchen;
	
//	Objects
	public class MM_Cat extends Multimap<String, IngredientSpecification> { }
	//Multimap<String, IngredientSpecification> mm_cat;
	
	public class MM_Date extends Multimap<Date, MM_Cat> { }
	//Multimap<Date, Multimap<String, IngredientSpecification>> mm_date;
	

	
	//Category...

	
//Implementation
	List<IngredientSpecification> getIngsFromKitchen(Workgroup kitchen) 
	{
		
	}
	
	private List<IngredientSpecification> ingredients;
	private MM_Date mm_date = new MM_Date();
	private MM_Cat mm_cat = new MM_Cat();
	
	public MM_Date getMM_Date() {
		Iterator<IngredientSpecification> it = ingredients.iterator();
		
		for (Date date : getAllDates()) {
			MM_Cat innerMM_cat = new MM_Cat();
			for (String cat : getAllCats()) {
				if (it.hasNext()) {
					
					IngredientSpecification spec = it.next();
					Ingredient ing = getIngFromId(spec.getZutat_id());
					if (spec.getCookingDate() == date)
					{
						if (catMatchesTags(ing.tags, cat))
						{
							// fill the Category MultiMap
							innerMM_cat.put(cat, spec);
						}
					}
				}
			}
			// Fill the date MultiMap
			mm_date.put(date, innerMM_cat);
		}
		return mm_date;
	}
	
	public MM_Cat getMM_Cat()
	{
		Iterator<IngredientSpecification> it = ingredients.iterator();
		
		for (String cat : getAllCats()) {
			if (it.hasNext()) {
				
				IngredientSpecification spec = it.next();
				Ingredient ing = getIngFromId(spec.getZutat_id());
				if (catMatchesTags(ing.tags, cat))
				{
					// fill the Category MultiMap
					mm_cat.put(cat, spec);
				}
			}
		}
		return mm_cat;
	}
	
	
//	Iteration
	// Date and Category
   for (Date date : mm_date.keySet()) {
	   Multimap<String, IngredientSpecification> mm_cat2 = mm_date.get(date);
	   // Display the date via date object
	   for (String cat : mm_cat.keySet()) {
		   List<IngredientSpecification> ingSpecs = mm_cat2.get(cat);
		   // display category via String
		   Long totC02 = calculateTotalC02Value(ingSpecs);
		   // display C02 Value
	   }
   }
	
	// Categories
   for (String cat : mm_cat.keySet()) {
	   List<IngredientSpecification> ingSpecs = mm_cat.values();
	   // display category via String
	   Long totC02 = calculateTotalC02Value(ingSpecs);
	   // display C02 Value
   }
  
   
	
	// Dates
   for (Date date : mm_date.keySet()) {
	   Multimap<String, IngredientSpecification> mm_cat2 = mm_date.get(date);
	   // Display the date via date object
	   List<IngredientSpecification> ingSpecs = mm_cat2.values();
	   Long totC02 = calculateTotalC02Value(ingSpecs);
	   // display C02 Value
   }

// -------------------------------------
/*
Liste:
(("<strong>Vegetable Products</strong>"),("meat", "animal-based")),
Rice products
Spices & herbs
Sweets
Vegetable oils and fat
Vegetables and fruits
Preprocessed vegetable products
Bread and Grain Products -> grain + bread + pasta
Nuts und seeds

<strong><strong>Animal Products</strong></strong>
Non-ruminants
Ruminants
Fish and seafood

Animal based fats -> oil and fats + animal-based
Ripened cheese
Fresh cheese and diary products

Eggs and egg based products
Canned and finished products ->
Sauces

<strong>Drinks</strong>
Drinks (alkohol based)
Drinks (fruit based)
Drinks (milk based)

*/
/*
List<Ingredient> ingredients = new ArrayList<Ingredient>();
ingredients = dao.getAllIngredients()

for(Ingredients ingredient: ingredients){
	ingredient.tags
}


// add
List<Ingredients> getIngredientsByTagAdded(List<Ingredients> ingredients, List<Tag> tags) {
	List<Ingredients> filteredList = new ArrayList<Ingredients>();
	
	for(Tag tag: tags) {
		filteredList.append(getIngredientsByTag(ingredients, tag));
	}
	
}

// subtract

List<Ingredients> getIngredientsByTagSubtract(List<Ingredients> ingredients, List<Tag> tags) {
	List<Ingredients> filteredList = new ArrayList<Ingredients>();
	
	for(Tag tag: tags) {
		filteredList.removeAll(getIngredientsByTag(ingredients, tag));
	}
	
}

filteredList.removeDuplicates();


// filter
List<Ingredients> getIngredientsByTag(List<Ingredients>, Tag tag){
	
}
}*/
   