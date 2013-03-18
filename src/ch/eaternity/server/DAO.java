package ch.eaternity.server;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import ch.eaternity.shared.Commitment;
import ch.eaternity.shared.Condition;
import ch.eaternity.shared.Converter;
import ch.eaternity.shared.Extraction;
import ch.eaternity.shared.FoodProduct;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.Kitchen;
import ch.eaternity.shared.LoginInfo;
import ch.eaternity.shared.ProductLabel;
import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.RecipeComment;
import ch.eaternity.shared.Staff;
import ch.eaternity.shared.Transportation;

import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.core.client.GWT;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;
import com.googlecode.objectify.util.DAOBase;

public class DAO extends DAOBase
{
	static {
		ObjectifyService.register(FoodProduct.class);
		ObjectifyService.register(Extraction.class);
		ObjectifyService.register(Condition.class);
		ObjectifyService.register(Transportation.class);
		ObjectifyService.register(ProductLabel.class);
		ObjectifyService.register(RecipeComment.class);
		ObjectifyService.register(Ingredient.class);
		ObjectifyService.register(Recipe.class);
		ObjectifyService.register(UserRecipeWrapper.class);
		ObjectifyService.register(ImageBlob.class);
		ObjectifyService.register(Kitchen.class);
		ObjectifyService.register(Staff.class);
		ObjectifyService.register(LoginInfo.class);
		ObjectifyService.register(Commitment.class);
		//        ObjectifyService.register(DeviceSpecification.class);
		//        ObjectifyService.register(Device.class);
	}

	private static final Logger log = Logger.getLogger(DAO.class.getName());
	
	
	public LoginInfo getLoginInfo(String requestUri) {
		UserService userService = UserServiceFactory.getUserService();
		LoginInfo loginInfo = new LoginInfo();
		User user = userService.getCurrentUser();

		if (user != null) {
			try {
				loginInfo = ofy().get(LoginInfo.class, user.getUserId());
			} catch (NotFoundException e) {
				try {
					loginInfo.setId(Long.parseLong(user.getUserId()));;
				}
				catch (NumberFormatException nfe) {
					loginInfo.setLoggedIn(false);
					loginInfo.setLoginUrl(userService.createLoginURL(requestUri));
				}
				
				loginInfo.setLoggedIn(true);
			}
			// reset everything in case it got changed...
			loginInfo.setEmailAddress(user.getEmail());
			loginInfo.setNickname(user.getNickname());
			loginInfo.setLogoutUrl(userService.createLogoutURL(requestUri));
			loginInfo.setAdmin(userService.isUserAdmin());
			ofy().put(loginInfo);
		} else {
			loginInfo.setLoggedIn(false);
			loginInfo.setLoginUrl(userService.createLoginURL(requestUri));
		}
		return loginInfo;
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
	
	/** Your DAO can have your own useful methods */
	public Long updateOrCreateIngredient(FoodProduct ingredient)
	{
		FoodProduct found = ofy().find(FoodProduct.class, ingredient.getId());

		if (found == null)
			ofy().put(ingredient);
		else
			ofy().delete(found);
		ofy().put(ingredient);

		return ingredient.getId();
	}
	
	public ArrayList<FoodProduct> getAllIngredients()
	{
		//        Objectify ofy = ObjectifyService.begin();
		Query<FoodProduct> found = ofy().query(FoodProduct.class);


		ArrayList<FoodProduct> ingredients = new ArrayList<FoodProduct>(found.count());

		QueryResultIterator<FoodProduct> iterator = found.iterator();
		while (iterator.hasNext()) {
			FoodProduct ingredient = iterator.next();
			ingredients.add(ingredient);
		}

		return ingredients;
	}


	public String getAllIngredientsXml() {
		// TODO export...
		ArrayList<FoodProduct> ingredients = getAllIngredients();
		return null;
	}



	public Long saveKitchen(Kitchen kitchen){

		// here we need more logic
		// got through the Users, find them in the Storage, add to each the key of this kitchen

		if(kitchen.getId() == null){
			ofy().put(kitchen);
		}

		// here we add more logic... 
		// creates References to correct Staff object
		for(Staff staff : kitchen.getPersonal()){
			Query<Staff> kitchenStaff = ofy().query(Staff.class).filter("userEmail ==", staff.userEmail);

			QueryResultIterator<Staff> iterator = kitchenStaff.iterator();

			boolean foundOne = false;
			while (iterator.hasNext()) {
				foundOne = true;
				Staff staffer = iterator.next();
				boolean doAdd = true;
				for(Long aKitchenId:staffer.kitchensIds){
					if(aKitchenId.compareTo(kitchen.getId()) == 0){
						doAdd = false;
						break;
					}

				}
				if(doAdd){
					staffer.kitchensIds.add(kitchen.getId());
				}
				staff = staffer;
				ofy().put(staffer);
			}

			if(!foundOne){
				// add a new one
				staff.kitchensIds = new ArrayList<Long>(1);
				staff.kitchensIds.add(kitchen.getId());
				ofy().put(staff);
			}

		}

		// save that kitchen again
		ofy().put(kitchen);

		return kitchen.getId();
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
				try {
					Kitchen newKitchen = ofy().get(Kitchen.class,getThisKitchen);
					if(!yourKitchens.contains(newKitchen)){
						yourKitchens.add(newKitchen);
					}
				} catch (NotFoundException e){

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
	
	public Boolean deleteRecipe(Long id) {
		Recipe recipe = getRecipe(id);
		recipe.setDeleted(true);	
		ofy().put(recipe);
		return true;
	}

	public Recipe getRecipe(Long recipeID){
		Recipe userRezept = ofy().get(Recipe.class,recipeID);
		return userRezept;
	}

	public List<Recipe> getUserRecipes(User user){

		List<Recipe> yourRecipes = new ArrayList<Recipe>();

		// The Query itself is Iterable
		Query<Recipe> yourUserRecipes = ofy().query(Recipe.class).filter("userID", user.getUserId());
		QueryResultIterator<Recipe> iterator = yourUserRecipes.iterator();

		while (iterator.hasNext()) {
			Recipe recipe = iterator.next();
			if (recipe.isDeleted() == false)
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
					Recipe recipe = userRezept.getRecipe();
					if(recipe.getCookingDate() == null){
						recipe.setCookingDate(dt);
					}
					recipe.setId(userRezept.id);
					if (recipe.isDeleted() == false)
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
			Recipe recipe = userRezept.getRecipe();
			recipe.setId(userRezept.id);
			if (recipe.isDeleted() == false)
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
			Recipe recipe = userRezept.getRecipe();
			recipe.setId( userRezept.id);
			if (recipe.isDeleted() == false)
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
			Recipe recipe = userRezept.getRecipe();
			if(recipe.getCookingDate() == null){
				recipe.setCookingDate(dt);
				log.warning("no date set for recipe" + userRezept.getId().toString());
				if(recipe.getCreateDate() == null){
					
					log.warning("also no createdate set for recipe" + userRezept.getId().toString());
				} else {
					recipe.setCookingDate(recipe.getCreateDate());
				}
			} else {
				log.info("date was set: " + recipe.getCookingDate().toGMTString());
			}
			recipe.setId(userRezept.getId());
			
			if (recipe.isDeleted() == false)
				kitchenRecipes.add(recipe);
			
		}


		return kitchenRecipes;
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
					Recipe recipe = userRezept.getRecipe();
					recipe.setId( userRezept.id);
					if(!kitchenRecipes.contains(recipe)){
						if (recipe.isDeleted() == false)
							kitchenRecipes.add(recipe);
					}
				}


			}

		}

		Query<Kitchen> yourKitchen = ofy().query(Kitchen.class).filter("emailAddressOwner", user.getEmail());
		QueryResultIterator<Kitchen> kitchenIterator = yourKitchen.iterator();
		while (kitchenIterator.hasNext()) {

			Kitchen thisKitchen = kitchenIterator.next();
			Query<UserRecipeWrapper> moreKitchenRecipes = ofy().query(UserRecipeWrapper.class).filter("kitchenIds", thisKitchen.getId());
			QueryResultIterator<UserRecipeWrapper> iteratorMore = moreKitchenRecipes.iterator();

			while (iteratorMore.hasNext()) {
				UserRecipeWrapper userRezept = iteratorMore.next();
				Recipe recipe = userRezept.getRecipe();
				recipe.setId( userRezept.id);
				if(!kitchenRecipes.contains(recipe)){
					if (recipe.isDeleted() == false)
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