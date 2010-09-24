package ch.eaternity.server;


import java.util.ArrayList;
import java.util.List;

import ch.eaternity.shared.IngredientCondition;
import ch.eaternity.shared.Extraction;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.MoTransportation;
import ch.eaternity.shared.ProductLabel;
import ch.eaternity.shared.Rezept;
import ch.eaternity.shared.ZutatSpecification;

import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;
import com.googlecode.objectify.helper.DAOBase;

public class DAO extends DAOBase
{
    static {
        ObjectifyService.register(Ingredient.class);
        ObjectifyService.register(Extraction.class);
        ObjectifyService.register(IngredientCondition.class);
        ObjectifyService.register(MoTransportation.class);
        ObjectifyService.register(ProductLabel.class);
        ObjectifyService.register(ZutatSpecification.class);
        ObjectifyService.register(Rezept.class);
        ObjectifyService.register(UserRezept.class);
    }

    /** Your DAO can have your own useful methods */
    public Long updateOrCreateIngredient(Ingredient ingredient)
    {
    	Ingredient found = ofy().find(Ingredient.class, ingredient.getId());
    	
        if (found == null)
        	ofy().put(ingredient);
        else
        	ofy().delete(found);
        	ofy().put(ingredient);
        
        return ingredient.getId();
    }
    
    public ArrayList<Ingredient> getAllIngredients()
    {
//        Objectify ofy = ObjectifyService.begin();
        Query<Ingredient> found = ofy().query(Ingredient.class);
        
        
        ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>(found.countAll());

        QueryResultIterator<Ingredient> iterator = found.iterator();
        while (iterator.hasNext()) {
        	Ingredient ingredient = iterator.next();
        	ingredients.add(ingredient);
        }
        
        return ingredients;
    }
    

    public Boolean CreateIngredients(ArrayList<Ingredient> ingredients)
    {
        ofy().put(ingredients);
        return true;
    }
    
    public Boolean CreateIngredientSpecifications(ArrayList<ZutatSpecification> ingredientPecifications)
    {
        ofy().put(ingredientPecifications);
        return true;
    }
    
	public String getAllIngredientsXml() {
		// TODO export...
		ArrayList<Ingredient> ingredients = getAllIngredients();
		return null;
	}

	public Boolean saveRecipe(Rezept recipe){
        ofy().put(recipe);
        return true;
	}
	
	public UserRezept getRecipe(Long recipeID){
		UserRezept userRezept = ofy().get(UserRezept.class,recipeID);
        return userRezept;
	}
	
	public List<Rezept> getYourRecipe(User user){
		
		List<Rezept> yourRecipes = new ArrayList<Rezept>();

		// The Query itself is Iterable
		Query<UserRezept> yourUserRecipes = ofy().query(UserRezept.class).filter("user", user);
        QueryResultIterator<UserRezept> iterator = yourUserRecipes.iterator();
        
        while (iterator.hasNext()) {
        	UserRezept userRezept = iterator.next();
        	Rezept rezept = userRezept.getRezept();
        	rezept.setId( userRezept.id);
        	yourRecipes.add(rezept);
        }
        
        return yourRecipes;

	}
	
	public List<Rezept> adminGetRecipe(User user){
		
		List<Rezept> adminRecipes = new ArrayList<Rezept>();

		// The Query itself is Iterable
		Query<UserRezept> yourUserRecipes = ofy().query(UserRezept.class).filter("user !=", user);
        QueryResultIterator<UserRezept> iterator = yourUserRecipes.iterator();
        
        while (iterator.hasNext()) {
        	UserRezept userRezept = iterator.next();
        	Rezept rezept = userRezept.getRezept();
        	rezept.setId( userRezept.id);
        	adminRecipes.add(rezept);
        }
        
        return adminRecipes;

	}
	
	public List<Rezept> getOpenRecipe(){
		
		List<Rezept> openRecipes = new ArrayList<Rezept>();

		// The Query itself is Iterable
		Query<UserRezept> yourOpenRecipes = ofy().query(UserRezept.class).filter("approvedOpen", true);
        QueryResultIterator<UserRezept> iterator = yourOpenRecipes.iterator();
        
        while (iterator.hasNext()) {
        	UserRezept userRezept = iterator.next();
        	Rezept rezept = userRezept.getRezept();
        	rezept.setId( userRezept.id);
        	openRecipes.add(rezept);
        }
        
        return openRecipes;

	}
    
}