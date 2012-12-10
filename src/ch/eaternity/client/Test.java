package ch.eaternity.client;

import java.util.List;
import java.util.Map;
import java.util.Date;

import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.IngredientSpecification;
import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.Categories;


public class Test {
// Input: List<recipe>
	Constructor ( List<Recipe> recipes )
	
	Map<String,List<IngredientSpecification>> getIngsByCategory(List<IngredientSpecification> ingredients, String category){}

	Map<Date,List<IngredientSpecification>> getIngsByDate(List<IngredientSpecification> ingredients, List<Date> dates) {}
	
	Date getAllDates(List<IngredientSpecification> ingredients) {}
	
	List<IngredientSpecification> getIngsFromRecipes(List<Recipe> recipes) {}
	
	Long calculateTotalC02Value(List<IngredientSpecification> ingredients) {}
	
	List<IngredientSpecification> getIngredientsByFormula(List<IngredientSpecification> ingredients, String formula) {}
	
	Ingredient getIngFromId(int id) {}
	
	List<Categories.CategoryValuesByDates> getCategoryValuesByDates() {}
	
	Map<String,List<IngredientSpecification>> getIngredientsPerCategory() {}
	
	


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
}