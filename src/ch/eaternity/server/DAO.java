package ch.eaternity.server;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ch.eaternity.shared.Converter;
import ch.eaternity.shared.Device;
import ch.eaternity.shared.DeviceSpecification;
import ch.eaternity.shared.IngredientCondition;
import ch.eaternity.shared.Extraction;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.Kitchen;
import ch.eaternity.shared.MoTransportation;
import ch.eaternity.shared.ProductLabel;
import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.IngredientSpecification;
import ch.eaternity.shared.Staff;

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
        ObjectifyService.register(UserRecipe.class);
        ObjectifyService.register(ImageBlob.class);
        ObjectifyService.register(Kitchen.class);
        ObjectifyService.register(Staff.class);
//        ObjectifyService.register(DeviceSpecification.class);
//        ObjectifyService.register(Device.class);
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
        
        
        ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>(found.count());

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
        
        // and now update all the recipes, that got changed...
        
        // get all recipes
        Query<UserRecipe> found = ofy().query(UserRecipe.class);

        //iterate over them
        QueryResultIterator<UserRecipe> iterator = found.iterator();
        while (iterator.hasNext()) {
        	UserRecipe recipe = iterator.next();
        	
        	// for each recipe find all ingredients Specifications
        	for(IngredientSpecification ingSpec : recipe.recipe.Zutaten ){
        		for(Ingredient ingredient: ingredients){
        			if(ingSpec.getZutat_id().equals(ingredient.getId())){
        				ingSpec.setNormalCO2Value(ingredient.getCo2eValue());
        				break;
        			}
        		}
        		
        	}
        	recipe.recipe.setCO2Value();
    		ofy().put(recipe);
        	
        }
        
        
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


	
	public Long saveKitchen(Kitchen kitchen){
		
		// here we need more logic
		// got through the Users, find them in the Storage, add to each the key of this kitchen
		
		if(kitchen.id == null){
			ofy().put(kitchen);
		}
        
		// here we add more logic...
		for(Staff staff:kitchen.personal){
			Query<Staff> kitchenStaff = ofy().query(Staff.class).filter("userEmail ==", staff.userEmail);
			
			QueryResultIterator<Staff> iterator = kitchenStaff.iterator();
	        
			boolean foundOne = false;
	        while (iterator.hasNext()) {
	        	foundOne = true;
	        	Staff staffer = iterator.next();
	        	boolean doAdd = true;
	        	for(Long aKitchenId:staffer.kitchensIds){
	        		if(aKitchenId.compareTo(kitchen.id) == 0){
	        			doAdd = false;
	        			break;
	        		}
	        		
	        	}
	        	if(doAdd){
	        		staffer.kitchensIds[staffer.kitchensIds.length] = kitchen.id;
	        	}
	        	staff = staffer;
	        	ofy().put(staffer);
	        }
	        
	        if(!foundOne){
	        	// add a new one
	        	staff.kitchensIds = new Long[1];
	        	staff.kitchensIds[0] = kitchen.id;
	        	ofy().put(staff);
	        }
			
		}
		
		// save that kitchen again
		ofy().put(kitchen);
        return kitchen.id;
	}

	public Kitchen getKitchen(Long KitchenID){
		Kitchen kitchen = ofy().get(Kitchen.class,KitchenID);
        return kitchen;
	}
	
	public List<Kitchen> getYourKitchens(User user){
		
		List<Kitchen> yourKitchens = new ArrayList<Kitchen>();

		// The Query itself is Iterable
		Query<Kitchen> yourUserKitchens = ofy().query(Kitchen.class).filter("emailAddressOwner", user.getEmail());
        QueryResultIterator<Kitchen> iterator = yourUserKitchens.iterator();
        
        while (iterator.hasNext()) {
        	Kitchen kitchen = iterator.next();
        	yourKitchens.add(kitchen);
        }
        
        Query<Staff> kitchenStaff = ofy().query(Staff.class).filter("userEmail ==", user.getEmail());
		QueryResultIterator<Staff> staffIterator = kitchenStaff.iterator();
		while (staffIterator.hasNext()) {
        	Staff staffer = staffIterator.next();
        	for(Long getThisKitchen:staffer.kitchensIds){
        		Kitchen newKitchen = ofy().get(Kitchen.class,getThisKitchen);
        		if(!yourKitchens.contains(newKitchen)){
        			yourKitchens.add(newKitchen);
        		}
        	}
		}
        
        return yourKitchens;

	}
	
	public List<Kitchen> adminGetKitchens(User user){
		
		List<Kitchen> adminKitchens = new ArrayList<Kitchen>();

		// The Query itself is Iterable
		Query<Kitchen> yourUserKitchens = ofy().query(Kitchen.class).filter("emailAddressOwner !=", user.getEmail());
        QueryResultIterator<Kitchen> iterator = yourUserKitchens.iterator();
        
        while (iterator.hasNext()) {
        	Kitchen kitchen = iterator.next();
        	adminKitchens.add(kitchen);
        }
        
        return adminKitchens;

	}
	
	public List<Kitchen> getOpenKitchen(){
		
		List<Kitchen> openKitchens = new ArrayList<Kitchen>();

		// The Query itself is Iterable
		Query<Kitchen> yourOpenKitchens = ofy().query(Kitchen.class).filter("approvedOpen", true);
        QueryResultIterator<Kitchen> iterator = yourOpenKitchens.iterator();
        
        while (iterator.hasNext()) {
        	Kitchen kitchen = iterator.next();
        	openKitchens.add(kitchen);
        }
        
        return openKitchens;

	}
	
	
	public Boolean saveRecipe(Recipe recipe){
        ofy().put(recipe);
        return true;
	}
	
	public UserRecipe getRecipe(Long recipeID){
		UserRecipe userRezept = ofy().get(UserRecipe.class,recipeID);
        return userRezept;
	}
	
	public List<Recipe> getYourRecipe(User user){
		
		List<Recipe> yourRecipes = new ArrayList<Recipe>();

		// The Query itself is Iterable
		Query<UserRecipe> yourUserRecipes = ofy().query(UserRecipe.class).filter("user", user);
        QueryResultIterator<UserRecipe> iterator = yourUserRecipes.iterator();
        
        while (iterator.hasNext()) {
        	UserRecipe userRezept = iterator.next();
        	Recipe recipe = userRezept.getRezept();
        	recipe.setId( userRezept.id);
        	yourRecipes.add(recipe);
        }
        
        return yourRecipes;

	}
	
	public List<Recipe> getRecipeByIds(String kitchenIdsString){
		
		String[] kitchenIds = kitchenIdsString.split(",");
		Calendar rightNow = Calendar.getInstance();
		int date = rightNow.get(Calendar.WEEK_OF_YEAR);

 
		
		List<Recipe> yourRecipes = new ArrayList<Recipe>();
		
		for (String kitchenIdString : kitchenIds){

			int code = Converter.fromString(kitchenIdString, 34);
			long computeId = code / date;
			
			if(computeId != 0){
				UserRecipe userRezept = ofy().find(UserRecipe.class, computeId);
				if(userRezept != null){
		        	Recipe recipe = userRezept.getRezept();
		        	recipe.setId( userRezept.id);
		        	yourRecipes.add(recipe);
				}
			}
		}
        
        return yourRecipes;

	}
	
	public List<Recipe> adminGetRecipe(User user){
		
		List<Recipe> adminRecipes = new ArrayList<Recipe>();

		// The Query itself is Iterable
		Query<UserRecipe> yourUserRecipes = ofy().query(UserRecipe.class).filter("user !=", user);
        QueryResultIterator<UserRecipe> iterator = yourUserRecipes.iterator();
        
        while (iterator.hasNext()) {
        	UserRecipe userRezept = iterator.next();
        	Recipe recipe = userRezept.getRezept();
        	recipe.setId(userRezept.id);
        	adminRecipes.add(recipe);
        }
        
        return adminRecipes;

	}
	
	public List<Recipe> getOpenRecipe(){
		
		List<Recipe> openRecipes = new ArrayList<Recipe>();

		// The Query itself is Iterable
		Query<UserRecipe> yourOpenRecipes = ofy().query(UserRecipe.class).filter("approvedOpen", true);
        QueryResultIterator<UserRecipe> iterator = yourOpenRecipes.iterator();
        
        while (iterator.hasNext()) {
        	UserRecipe userRezept = iterator.next();
        	Recipe recipe = userRezept.getRezept();
        	recipe.setId( userRezept.id);
        	openRecipes.add(recipe);
        }
        
        return openRecipes;

	}

	public List<Recipe> getKitchenRecipes(User user) {
		
		
		List<Recipe> kitchenRecipes = new ArrayList<Recipe>();
		
		  Query<Staff> kitchenStaff = ofy().query(Staff.class).filter("userEmail ==", user.getEmail());
			QueryResultIterator<Staff> staffIterator = kitchenStaff.iterator();
			while (staffIterator.hasNext()) {
				
	        	Staff staffer = staffIterator.next();
	        	for (Long kitchenId:staffer.kitchensIds){
	        		
//	        		Kitchen kitchen = ofy().get(Kitchen.class,kitchenId);
	        		
		        	Query<UserRecipe> yourKitchenRecipes = ofy().query(UserRecipe.class).filter("kitchenId", kitchenId);
		            QueryResultIterator<UserRecipe> iterator = yourKitchenRecipes.iterator();
		            
		            while (iterator.hasNext()) {
		            	UserRecipe userRezept = iterator.next();
		            	Recipe recipe = userRezept.getRezept();
		            	recipe.setId( userRezept.id);
		            	if(!kitchenRecipes.contains(recipe)){
		            		kitchenRecipes.add(recipe);
		            	}
		            }
	        		
	        	}


			}
	
		

		// The Query itself is Iterable
		
        
        return kitchenRecipes;
	}
	
   

    
}