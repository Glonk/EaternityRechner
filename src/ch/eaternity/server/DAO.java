package ch.eaternity.server;

//import static com.googlecode.objectify.ObjectifyService.ofy;
import static ch.eaternity.server.OfyService.ofy;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.eaternity.shared.Commitment;
import ch.eaternity.shared.FoodProduct;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.Kitchen;
import ch.eaternity.shared.Pair;
import ch.eaternity.shared.UserInfo;
import ch.eaternity.shared.Recipe;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Key;

public class DAO 
{

	private static final Logger log = Logger.getLogger(DAO.class.getName());
	
	public DAO() {}
	
	/**
	 * 
	 * @param requestUri
	 * @return never null
	 */
	public UserInfo getUserInfo(String requestUri) {
		UserService userService = UserServiceFactory.getUserService();
		UserInfo userInfo = new UserInfo();
		User user = userService.getCurrentUser();
		//TODO remove User Id Hack before deployement
		//String userId = user.getUserId();
		String userId = "1";
		
		if (user != null) {
			userInfo = ofy().load().type(UserInfo.class).id(userId).get();

			
			// if there exists no corresponding loginInfo to User, create a new one (first time login)
			if (userInfo == null) {
				userInfo = new UserInfo();
				userInfo.setId(userId);
				userInfo.setLoggedIn(true);
			}
			
			// reset everything in case it got changed or if a new user is created
			userInfo.setEmailAddress(user.getEmail());
			userInfo.setNickname(user.getNickname());
			userInfo.setLogoutUrl(userService.createLogoutURL(requestUri));
			userInfo.setAdmin(userService.isUserAdmin());
			userInfo.setEnabled(isUserEnabled(userInfo.getId()));
			ofy().save().entity(userInfo);
		} 
		else {
			userInfo.setLoggedIn(false);
			userInfo.setLoginUrl(userService.createLoginURL(requestUri));
		}
		return userInfo;
	}
	
	/**
	 * 
	 * @return null if not found
	 */
	public UserInfo getUserInfo() {
		UserService userService = UserServiceFactory.getUserService();
		UserInfo userInfo = null;
		User user = userService.getCurrentUser();
		//TODO remove Hack before deployement
		//String userId = user.getUserId();
		String userId = "1";

		if (user != null) {
			userInfo = ofy().load().type(UserInfo.class).id(userId).get();
			userInfo.setEnabled(isUserEnabled(userInfo.getId()));
		}
		return userInfo;
	}

	/**
	 * Handles Exceptions,
	 * @return A List of all userids registrated in any kitchen or empty list if an exception occured
	 */
	public ArrayList<String> getAllKitchenUsers() {
		List<Kitchen> kitchens = new ArrayList<Kitchen>();
		ArrayList<String> userIds = new ArrayList<String>();
		
		try {
			
			Iterable<Key<Kitchen>> keys = ofy().load().type(Kitchen.class).keys();
			for (Key<Kitchen> key : keys)
				kitchens.add(ofy().load().key(key).get());
		}
		catch (Throwable e) {
			handleException(e);
			kitchens = new ArrayList<Kitchen>();
		}
		
		for (Kitchen kitchen : kitchens) {
			userIds.addAll(kitchen.getUserIds());
		}
		
		return userIds;
	}
	
	public Boolean saveUserInfo(UserInfo userInfo) {
		ofy().save().entity(userInfo).now();

		return true;
	}
	
	/**
	 * Iterates through all kitchens, collecting all user Ids, determining if a user is contained within a kitchen
	 * @param userId
	 * @return true if user is contained in any kitchen
	 */
	private Boolean isUserEnabled(String userId) {
		List<String> kitchenUserIds = getAllKitchenUsers();
		// TODO remove comments for just allowing kitchen Users to access calculator
		//return kitchenUserIds.contains(userId);
		return true;
	}
	
	// ------------------------ Ingredients -----------------------------------
	
	/**
	 * Saves the products without returning id's
	 * @param products
	 * @return 
	 */
	public Boolean saveFoodProducts(List<FoodProduct> products)
	{
		ofy().save().entities(products);

		return true;
	}
	
	/**
	 * 
	 * @param product
	 * @return the auto generated id or null if not found
	 */
	public Long saveFoodProduct(FoodProduct product)
	{
		ofy().save().entity(product).now();
		
		return product.getId();
	}
	
	/**
	 * 
	 * @return all the ingredients of the datastore, never null
	 */
	public ArrayList<FoodProduct> getAllFoodProducts()
	{
		ArrayList<FoodProduct> result = new ArrayList<FoodProduct>();
		Iterable<Key<FoodProduct>> keys = ofy().load().type(FoodProduct.class).keys();
		for (Key<FoodProduct> key : keys)
			result.add(ofy().load().key(key).get());
		
		return result;
	}
	
	/**
	 * @return the product or null if not found
	 */
	public FoodProduct getFoodProduct(Long id) {
		FoodProduct product = ofy().load().type(FoodProduct.class).id(id).get();
		
		return product;
	}
	
	// ------------------------ Recipes -----------------------------------

	public Long saveRecipe(Recipe recipe){
		ofy().save().entity(recipe).now();
		return recipe.getId();
	}
	
	public Boolean deleteRecipe(Long id) {
		ofy().delete().type(Recipe.class).id(id);
		return true;
	}

	/**
	 * @return the recipe or null if not found
	 */
	public Recipe getRecipe(Long recipeId) throws NoSuchElementException
	{
		Recipe recipe = ofy().load().type(Recipe.class).id(recipeId).get();
		
		// Load the foodproducts into the list of ingredients (the're ignored during persistance)
		if (recipe != null) {
			FoodProduct tempProduct;
			for (Ingredient ing : recipe.getIngredients()) {
				if (ing != null) {
					tempProduct = getFoodProduct(ing.getProductId());
					if (tempProduct != null)
						ing.setFoodProduct(tempProduct);
					else {
						String msg = "getRecipe: corresponding FoodProduct from Ingredient couldn't get loaded.";
						log.log(Level.SEVERE, msg);
						throw new NoSuchElementException(msg);
					}
				}
				
			}
		}
		
		return recipe;
	}

	/**
	 * @return the recipes which belong to the user with userId or null if not found
	 */
	public ArrayList<Recipe> getUserRecipes(String userId){
		List<Recipe> recipes = ofy().load().type(Recipe.class).filter("userId", userId).filter("deleted", false).list();
		// Do that to avoid ResultProxy to be returned. Convert into propper List
		return new ArrayList<Recipe>(recipes);
	}
	
	/**
	 * @return all recipes belonging to kitchen with @param kitchenId 
	 *  or null if not found
	 */
	public ArrayList<Recipe> getKitchenRecipes(Long kitchenId){
		List<Recipe> recipes = ofy().load().type(Recipe.class).filter("kitchenId", kitchenId).filter("deleted", false).list();
		return new ArrayList<Recipe>(recipes); 
	}
	
	/**
	 * @return the recipes which published, publicitly available
	 *  or null if not found
	 */
	public ArrayList<Recipe> getPublicRecipes(){
		List<Recipe> recipes = ofy().load().type(Recipe.class).filter("published", true).filter("deleted", false).list();
		return new ArrayList<Recipe>(recipes);
	}
	
	
	/**
	 * TODO To be specified in future, more concrete otherwise data load will get too big
	 * @return all recipes stored in the database for admin purposes
	 *  
	 */
	public ArrayList<Recipe> getAllRecipes(){
		ArrayList<Recipe> result = new ArrayList<Recipe>();
		
		Iterable<Key<Recipe>> keys = ofy().load().type(Recipe.class).filter("deleted", false).keys();
		for (Key<Recipe> key : keys)
			result.add(ofy().load().key(key).get());
		
		return result;
	}
	


	/**
	 * @return the recipes which are requested for publication but not published yet. the need to be approved
	 *  or null if not found
	 */
	public ArrayList<Recipe> getUnapprovedRecipes(){
		List<Recipe> recipes = ofy().load().type(Recipe.class).filter("publicationRequested", true).filter("published", false).filter("deleted", false).list();
		return new ArrayList<Recipe>(recipes);
	}

	

	public ArrayList<Recipe> getRecipeByIds(String kitchenIdsString, Boolean isCoded){
		/*
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
		*/
		return null;
	}


	// ------------------------ Kitchen -----------------------------------
	
	/**
	 *  fetches all the UserInfos belonging to this kitchen
	 * @param kitchen
	 * @return an empty list if an error occured
	 */
	private ArrayList<UserInfo> getUserInfosFromKitchen(Kitchen kitchen) {
		List<String> kitchenMailAdresses = new ArrayList<String>();
		for (Pair<String,String> pair : kitchen.getUnmatchedUsers()) {
			kitchenMailAdresses.add(pair.second());
		}
		List<UserInfo> kitchenUserInfos;
		try {
			kitchenUserInfos = ofy().load().type(UserInfo.class).filter("emailAdress in", kitchenMailAdresses).list();
			return new ArrayList<UserInfo>(kitchenUserInfos);
		}
		catch (Throwable e) {
			handleException(e);
			return new ArrayList<UserInfo>();
		}
	}

	public Long saveKitchen(Kitchen kitchen){
		// save for getting the kitchen id
		ofy().save().entity(kitchen).now();
		
		List<UserInfo> kitchenUserInfos = getUserInfosFromKitchen(kitchen);
		
		// Apply the many to many relationship
		for (UserInfo userInfo : kitchenUserInfos) {
			kitchen.getUserIds().clear();
			kitchen.getUserIds().add(userInfo.getId());
			/*
			if (!userInfo.getKitchenIDs().contains(kitchen.getId()) {
				saveUserInfo(userInfo);
			}
			*/
		}

		// now save again the relationships
		ofy().save().entity(kitchen).now();

		return kitchen.getId();
	}

	/**
	 * 
	 * @param kitchenId
	  * @return the kitchen or null if not found
	 */
	public Kitchen getKitchen(Long kitchenId) {
		Kitchen kitchen = ofy().load().type(Kitchen.class).id(kitchenId).get();
		return kitchen;
	}
	
	/**
	 * 
	 * @param kitchenId
	 * @return false if kitchen could't be found (many to many update failed
	 */
	public Boolean deleteKitchen(Long kitchenId) {
		Kitchen kitchen = getKitchen(kitchenId);
		if (kitchen != null) {
			/*
			List<UserInfo> kitchenUserInfos = getUserInfosFromKitchen(kitchen);
			
			// Apply the many to many relationship
			
			for (UserInfo userInfo : kitchenUserInfos) {
				userInfo.getKitchenIDs().remove(kitchenId);
				saveUserInfo(userInfo);
			}
			*/
			
			ofy().delete().type(Kitchen.class).id(kitchenId);

			return true;
		}
		else return false;
	}

	/**
	 * 
	 * @param userId
	 * @return never null, an empty list if an error occured
	 */
	public ArrayList<Kitchen> getUserKitchens(String userId){
		//DISCUSS could also be done via kitchenList of UserInfo
		List<Kitchen> kitchens = ofy().load().type(Kitchen.class).filter("userIds", userId).list();
		// Do that to avoid ResultProxy to be returned. Convert into propper List
		return new ArrayList<Kitchen>(kitchens);
	}

	/**
	 * 
	 * @return all kitchen from the databaseor an empty list if failed
	 */
	public ArrayList<Kitchen> getAdminKitchens(){
		List<Kitchen> kitchens = ofy().load().type(Kitchen.class).list();
		// Do that to avoid ResultProxy to be returned. Convert into propper List
		return new ArrayList<Kitchen>(kitchens);
	}

	

	public Long saveCommitment(Commitment commitment) {
		ofy().save().entity(commitment);
		return commitment.id;
	}
	
	// ------------------------ Distances -----------------------------------
	

	// ------------------------ Images -----------------------------------
	
	public Long CreateImage(ImageBlob image)
	{
		ofy().save().entity(image).now();
		//        ImagesService imgS = ImagesServiceFactory.getImagesService();
		//        String test = imgS.getServingUrl(image.getPicture());
		// I propably want something like this: http://jeremyblythe.blogspot.com/
		return image.getId();
	}

	public ImageBlob getImage(Long imageID)
	{
		return ofy().load().type(ImageBlob.class).id(imageID).get();
	}


	public Boolean clearDatabase() {
		try {
			Iterable<Key<FoodProduct>> productkeys = ofy().load().type(FoodProduct.class).keys();
			Iterable<Key<Recipe>> recipekeys = ofy().load().type(Recipe.class).keys();
			Iterable<Key<Kitchen>> kitchenkeys = ofy().load().type(Kitchen.class).keys();
			Iterable<Key<UserInfo>> userkeys = ofy().load().type(UserInfo.class).keys();
			
			ofy().delete().keys(productkeys);
			ofy().delete().keys(recipekeys);
			ofy().delete().keys(kitchenkeys);
			ofy().delete().keys(userkeys);
		
		}
		catch (Throwable e) {
			handleException(e);
			return false;
		} 
		return true;	
	}

	private void handleException(Throwable error) {
		if (error != null)
			log.log(Level.SEVERE, error.getClass().toString() + " throws " +  error.getMessage());
	}




}