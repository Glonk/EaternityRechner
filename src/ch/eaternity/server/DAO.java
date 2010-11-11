package ch.eaternity.server;


import java.util.ArrayList;
import java.util.List;

import ch.eaternity.shared.IngredientCondition;
import ch.eaternity.shared.Extraction;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.MoTransportation;
import ch.eaternity.shared.ProductLabel;
import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.IngredientSpecification;

import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
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
        ObjectifyService.register(IngredientSpecification.class);
        ObjectifyService.register(Recipe.class);
        ObjectifyService.register(UserRezept.class);
        ObjectifyService.register(ImageBlob.class);
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
    
    public Long CreateImage(ImageBlob image)
    {
        ofy().put(image);
//        ImagesService imgS = ImagesServiceFactory.getImagesService();
//        String test = imgS.getServingUrl(image.getPicture());
        // I propably want something like this: http://jeremyblythe.blogspot.com/
        return image.getId();
    }
    
    public ImageBlob getImage(Long imageID)
    {
    	return ofy().get(ImageBlob.class, imageID);
         
    }

    public Boolean CreateIngredients(ArrayList<Ingredient> ingredients)
    {
        ofy().put(ingredients);
        return true;
    }
    
    public Boolean CreateIngredientSpecifications(ArrayList<IngredientSpecification> ingredientPecifications)
    {
        ofy().put(ingredientPecifications);
        return true;
    }
    
	public String getAllIngredientsXml() {
		// TODO export...
		ArrayList<Ingredient> ingredients = getAllIngredients();
		return null;
	}

	public Boolean saveRecipe(Recipe recipe){
        ofy().put(recipe);
        return true;
	}
	
	public UserRezept getRecipe(Long recipeID){
		UserRezept userRezept = ofy().get(UserRezept.class,recipeID);
        return userRezept;
	}
	
	public List<Recipe> getYourRecipe(User user){
		
		List<Recipe> yourRecipes = new ArrayList<Recipe>();

		// The Query itself is Iterable
		Query<UserRezept> yourUserRecipes = ofy().query(UserRezept.class).filter("user", user);
        QueryResultIterator<UserRezept> iterator = yourUserRecipes.iterator();
        
        while (iterator.hasNext()) {
        	UserRezept userRezept = iterator.next();
        	Recipe recipe = userRezept.getRezept();
        	recipe.setId( userRezept.id);
        	yourRecipes.add(recipe);
        }
        
        return yourRecipes;

	}
	
	public List<Recipe> adminGetRecipe(User user){
		
		List<Recipe> adminRecipes = new ArrayList<Recipe>();

		// The Query itself is Iterable
		Query<UserRezept> yourUserRecipes = ofy().query(UserRezept.class).filter("user !=", user);
        QueryResultIterator<UserRezept> iterator = yourUserRecipes.iterator();
        
        while (iterator.hasNext()) {
        	UserRezept userRezept = iterator.next();
        	Recipe recipe = userRezept.getRezept();
        	recipe.setId( userRezept.id);
        	adminRecipes.add(recipe);
        }
        
        return adminRecipes;

	}
	
	public List<Recipe> getOpenRecipe(){
		
		List<Recipe> openRecipes = new ArrayList<Recipe>();

		// The Query itself is Iterable
		Query<UserRezept> yourOpenRecipes = ofy().query(UserRezept.class).filter("approvedOpen", true);
        QueryResultIterator<UserRezept> iterator = yourOpenRecipes.iterator();
        
        while (iterator.hasNext()) {
        	UserRezept userRezept = iterator.next();
        	Recipe recipe = userRezept.getRezept();
        	recipe.setId( userRezept.id);
        	openRecipes.add(recipe);
        }
        
        return openRecipes;

	}
    
}