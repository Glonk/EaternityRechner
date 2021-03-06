package ch.eaternity.server;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import ch.eaternity.shared.Commitment;
import ch.eaternity.shared.Converter;
import ch.eaternity.shared.Extraction;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.IngredientCondition;
import ch.eaternity.shared.IngredientSpecification;
import ch.eaternity.shared.LoginInfo;
import ch.eaternity.shared.MoTransportation;
import ch.eaternity.shared.ProductLabel;
import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.RecipeComment;
import ch.eaternity.shared.SingleDistance;
import ch.eaternity.shared.Staff;
import ch.eaternity.shared.Workgroup;

import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.users.User;
import com.google.gwt.core.client.GWT;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;
import com.googlecode.objectify.util.DAOBase;

public class DAO extends DAOBase
{
	static {
		ObjectifyService.register(Ingredient.class);
		ObjectifyService.register(Extraction.class);
		ObjectifyService.register(IngredientCondition.class);
		ObjectifyService.register(MoTransportation.class);
		ObjectifyService.register(ProductLabel.class);
		ObjectifyService.register(RecipeComment.class);
		ObjectifyService.register(IngredientSpecification.class);
		ObjectifyService.register(Recipe.class);
		ObjectifyService.register(UserRecipeWrapper.class);
		ObjectifyService.register(ImageBlob.class);
		ObjectifyService.register(Workgroup.class);
		ObjectifyService.register(Staff.class);
		ObjectifyService.register(LoginInfo.class);
		ObjectifyService.register(Commitment.class);
		//        ObjectifyService.register(DeviceSpecification.class);
		//        ObjectifyService.register(Device.class);
	}

	private static final Logger log = Logger.getLogger(DAO.class.getName());
	
	// probably not working yet, still done with persistentManager in DataService...
	public List<SingleDistance> getDistances() {
		List<SingleDistance> distances = ofy().query(SingleDistance.class).list();
		return distances;
	}
	
	// probably not working yet, still done with persistentManager in DataService...
	public Boolean addDistances(List<SingleDistance> addDistances) {
		ofy().put(addDistances); 
		return true;
	}
	
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
		Query<UserRecipeWrapper> found = ofy().query(UserRecipeWrapper.class);

		//iterate over them
		QueryResultIterator<UserRecipeWrapper> iterator = found.iterator();
		while (iterator.hasNext()) {
			UserRecipeWrapper recipe = iterator.next();

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



	public Long saveKitchen(Workgroup kitchen){

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
					staffer.kitchensIds.add(kitchen.id);
				}
				staff = staffer;
				ofy().put(staffer);
			}

			if(!foundOne){
				// add a new one
				staff.kitchensIds = new ArrayList<Long>(1);
				staff.kitchensIds.add(kitchen.id);
				ofy().put(staff);
			}

		}

		// save that kitchen again
		ofy().put(kitchen);

		return kitchen.id;
	}

	public Workgroup getKitchen(Long KitchenID){
		Workgroup kitchen = ofy().get(Workgroup.class,KitchenID);
		return kitchen;
	}
	
	/**
	 * 
	 * @return a list of Staff with all users who have access to the calculator
	 */
	public List<Staff> getEnabledUsers() {
		List<Workgroup> kitchens = new ArrayList<Workgroup>();
		List<Staff> staffs = new ArrayList<Staff>();

		// The Query itself is Iterable
		Query<Workgroup> yourUserKitchens = ofy().query(Workgroup.class);
		QueryResultIterator<Workgroup> iterator = yourUserKitchens.iterator();

		while (iterator.hasNext()) {
			staffs.addAll(iterator.next().personal);
		}
		return staffs;

	}

	public List<Workgroup> getYourKitchens(User user){

		List<Workgroup> yourKitchens = new ArrayList<Workgroup>();

		// The Query itself is Iterable
		Query<Workgroup> yourUserKitchens = ofy().query(Workgroup.class).filter("emailAddressOwner", user.getEmail());
		QueryResultIterator<Workgroup> iterator = yourUserKitchens.iterator();

		while (iterator.hasNext()) {
			Workgroup kitchen = iterator.next();
			yourKitchens.add(kitchen);
		}

		Query<Staff> kitchenStaff = ofy().query(Staff.class).filter("userEmail ==", user.getEmail());
		QueryResultIterator<Staff> staffIterator = kitchenStaff.iterator();
		while (staffIterator.hasNext()) {
			Staff staffer = staffIterator.next();
			for(Long getThisKitchen:staffer.kitchensIds){
				try {
					Workgroup newKitchen = ofy().get(Workgroup.class,getThisKitchen);
					if(!yourKitchens.contains(newKitchen)){
						yourKitchens.add(newKitchen);
					}
				} catch (NotFoundException e){

				}
			}
		}

		return yourKitchens;

	}

	public List<Workgroup> adminGetKitchens(User user){

		List<Workgroup> adminKitchens = new ArrayList<Workgroup>();

		// The Query itself is Iterable
		Query<Workgroup> yourUserKitchens = ofy().query(Workgroup.class).filter("emailAddressOwner !=", user.getEmail());
		QueryResultIterator<Workgroup> iterator = yourUserKitchens.iterator();

		while (iterator.hasNext()) {
			Workgroup kitchen = iterator.next();
			adminKitchens.add(kitchen);
		}

		return adminKitchens;

	}

	public List<Workgroup> getOpenKitchen(){

		List<Workgroup> openKitchens = new ArrayList<Workgroup>();

		// The Query itself is Iterable
		Query<Workgroup> yourOpenKitchens = ofy().query(Workgroup.class).filter("approvedOpen", true);
		QueryResultIterator<Workgroup> iterator = yourOpenKitchens.iterator();

		while (iterator.hasNext()) {
			Workgroup kitchen = iterator.next();
			openKitchens.add(kitchen);
		}

		return openKitchens;

	}


	public Boolean saveRecipe(Recipe recipe){
		ofy().put(recipe);
		return true;
	}

	public UserRecipeWrapper getRecipe(Long recipeID){
		UserRecipeWrapper userRezept = ofy().get(UserRecipeWrapper.class,recipeID);
		return userRezept;
	}

	public List<Recipe> getYourRecipe(User user){

		List<Recipe> yourRecipes = new ArrayList<Recipe>();

		// The Query itself is Iterable
		Query<UserRecipeWrapper> yourUserRecipes = ofy().query(UserRecipeWrapper.class).filter("user", user);
		QueryResultIterator<UserRecipeWrapper> iterator = yourUserRecipes.iterator();

		while (iterator.hasNext()) {
			UserRecipeWrapper userRezept = iterator.next();
			Recipe recipe = userRezept.getRezept();
			recipe.setId( userRezept.id);
			yourRecipes.add(recipe);
		}

		return yourRecipes;

	}

	public List<Recipe> getRecipeByIds(String kitchenIdsString, Boolean isCoded){

		String[] kitchenIds = kitchenIdsString.split(",");
		//		Calendar rightNow = Calendar.getInstance();
		//		long date = rightNow.get(Calendar.WEEK_OF_YEAR);

		Date date = new Date();
		int iTimeStamp = (int) (date.getTime() * .00003);

		Calendar cal = Calendar.getInstance();
		cal.set(2012, 0, 1); //year is as expected, month is zero based, date is as expected
		Date dt = cal.getTime();
		

		List<Recipe> yourRecipes = new ArrayList<Recipe>();

		for (String kitchenIdString : kitchenIds){
			long code = 0L;
			
			try{
				code = Converter.fromString(kitchenIdString, 34);
			} catch(RuntimeException e){
				GWT.log(e.getLocalizedMessage());
			}

			long computeId;
			if(isCoded){
				computeId = code / iTimeStamp;
			} else {
				computeId = code;
			}

			if(computeId != 0L){
				UserRecipeWrapper userRezept = ofy().find(UserRecipeWrapper.class, computeId);
				if(userRezept != null){
					Recipe recipe = userRezept.getRezept();
					if(recipe.cookingDate == null){
						recipe.cookingDate = dt;
					}
					recipe.setId(userRezept.id);
					yourRecipes.add(recipe);
				}
			}
		}

		return yourRecipes;

	}

	public List<Recipe> adminGetRecipe(User user){

		List<Recipe> adminRecipes = new ArrayList<Recipe>();

		// The Query itself is Iterable
		Query<UserRecipeWrapper> yourUserRecipes = ofy().query(UserRecipeWrapper.class).filter("user !=", user);
		QueryResultIterator<UserRecipeWrapper> iterator = yourUserRecipes.iterator();

		while (iterator.hasNext()) {
			UserRecipeWrapper userRezept = iterator.next();
			Recipe recipe = userRezept.getRezept();
			recipe.setId(userRezept.id);
			adminRecipes.add(recipe);
		}

		return adminRecipes;

	}

	public List<Recipe> getOpenRecipe(){

		List<Recipe> openRecipes = new ArrayList<Recipe>();

		// The Query itself is Iterable
		Query<UserRecipeWrapper> yourOpenRecipes = ofy().query(UserRecipeWrapper.class).filter("approvedOpen", true);
		QueryResultIterator<UserRecipeWrapper> iterator = yourOpenRecipes.iterator();

		while (iterator.hasNext()) {
			UserRecipeWrapper userRezept = iterator.next();
			Recipe recipe = userRezept.getRezept();
			recipe.setId( userRezept.id);
			openRecipes.add(recipe);
		}

		return openRecipes;

	}

	public List<Recipe> getKitchenRecipes(Long kitchenId) {

		List<Recipe> kitchenRecipes = new ArrayList<Recipe>();

		// hack in a date, if there is none...
		Calendar cal = Calendar.getInstance();
		cal.set(2010, 0, 1); //year is as expected, month is zero based, date is as expected
		Date dt = cal.getTime();
		
		Query<UserRecipeWrapper> yourKitchenRecipes = ofy().query(UserRecipeWrapper.class).filter("kitchenIds", kitchenId);
		QueryResultIterator<UserRecipeWrapper> iterator = yourKitchenRecipes.iterator();

		while (iterator.hasNext()) {
			UserRecipeWrapper userRezept = iterator.next();
			Recipe recipe = userRezept.getRezept();
			if(recipe.cookingDate == null){
				recipe.cookingDate = dt;
				log.warning("no date set for recipe" + userRezept.getId().toString());
				if(recipe.getCreateDate() == null){
					
					log.warning("also no createdate set for recipe" + userRezept.getId().toString());
				} else {
					recipe.cookingDate = recipe.getCreateDate();
				}
			} else {
				log.info("date was set: " + recipe.cookingDate.toGMTString());
			}
			recipe.setId(userRezept.getId());
			
			// if(!kitchenRecipes.contains(recipe)){
				kitchenRecipes.add(recipe);
			// }
		}

		markDescendant(kitchenRecipes);
		// check if descendant is also in the own list
		Iterator<Recipe> iterator2 = kitchenRecipes.iterator();
		while(iterator2.hasNext()){
			Recipe recipeHasDesc = iterator2.next();
			for( Recipe recipeIsPossibleDesc :kitchenRecipes){
				// is the descendant in the own list
				if(recipeHasDesc.getDirectDescandentID().contains(recipeIsPossibleDesc.getId())){
					// remove recipeHasDesc
					iterator2.remove();
					break;
//					possibleRecipes.remove(recipeHasDesc);
				}
			}
		}

		return kitchenRecipes;
	}
	
	public void markDescendant(List<Recipe> recipesList) {
		for( Recipe checkRecipe: recipesList){
			if(checkRecipe.getDirectAncestorID() != null){
				// has ancestor...
				for( Recipe markRecipe: recipesList){
					
					if(markRecipe.getId().equals(checkRecipe.getDirectAncestorID())){
						// found descendants and mark him
						markRecipe.addDirectDescandentID(checkRecipe.getId());
//						checkRecipe.ancestorAlreadyMarked = true;
//						break;
					}
					
				}
							
			}
		}
	}
	
	
	public List<Recipe> getKitchenRecipes(User user) {
		//TODO why does this gets called twice on startup?

		List<Recipe> kitchenRecipes = new ArrayList<Recipe>();

		Query<Staff> kitchenStaff = ofy().query(Staff.class).filter("userEmail ==", user.getEmail());
		QueryResultIterator<Staff> staffIterator = kitchenStaff.iterator();
		while (staffIterator.hasNext()) {

			Staff staffer = staffIterator.next();
			//TODO some staffer still has a wrong kitchen id...
			for (Long kitchenId:staffer.kitchensIds){

				//	        		Kitchen kitchen = ofy().get(Kitchen.class,kitchenId);
				//TODO this is obviously wrong, when we have more kitchens, per recipe ... is this the answer?: does it work?
				//  http://code.google.com/p/objectify-appengine/wiki/IntroductionToObjectify#Multi-Value_Relationship
				Query<UserRecipeWrapper> yourKitchenRecipes = ofy().query(UserRecipeWrapper.class).filter("kitchenIds", kitchenId);
				QueryResultIterator<UserRecipeWrapper> iterator = yourKitchenRecipes.iterator();

				while (iterator.hasNext()) {
					UserRecipeWrapper userRezept = iterator.next();
					Recipe recipe = userRezept.getRezept();
					recipe.setId( userRezept.id);
					if(!kitchenRecipes.contains(recipe)){
						kitchenRecipes.add(recipe);
					}
				}


			}

		}

		Query<Workgroup> yourKitchen = ofy().query(Workgroup.class).filter("emailAddressOwner", user.getEmail());
		QueryResultIterator<Workgroup> kitchenIterator = yourKitchen.iterator();
		while (kitchenIterator.hasNext()) {

			Workgroup thisKitchen = kitchenIterator.next();
			Query<UserRecipeWrapper> moreKitchenRecipes = ofy().query(UserRecipeWrapper.class).filter("kitchenIds", thisKitchen.id);
			QueryResultIterator<UserRecipeWrapper> iteratorMore = moreKitchenRecipes.iterator();

			while (iteratorMore.hasNext()) {
				UserRecipeWrapper userRezept = iteratorMore.next();
				Recipe recipe = userRezept.getRezept();
				recipe.setId( userRezept.id);
				if(!kitchenRecipes.contains(recipe)){
					kitchenRecipes.add(recipe);
				}
			}
		}	


		// The Query itself is Iterable
		//TODO add also the recipes, of the kitchens, of which you are the owner


		return kitchenRecipes;
	}

	public Long saveCommitment(Commitment commitment) {
		ofy().put(commitment);
		return commitment.id;
	}




}