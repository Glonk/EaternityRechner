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
			try {
				userInfo = ofy().load().type(UserInfo.class).id(userId).get();
			} 
			catch (Throwable e) {
				handleException(e);
			}
			
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
			try {
				userInfo = ofy().load().type(UserInfo.class).id(userId).get();
				userInfo.setEnabled(isUserEnabled(userInfo.getId()));
			} 
			catch (Throwable e) {
				handleException(e);
			}
		}
		return userInfo;
	}

	/**
	 * 
	 * @return A List of all userids registrated in any kitchen
	 */
	public List<String> getAllKitchenUsers() {
		List<Kitchen> kitchens = new ArrayList<Kitchen>();
		List<String> userIds = new ArrayList<String>();
		
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
		try {
			ofy().save().entity(userInfo).now();
		}
		catch (Throwable e) {
			handleException(e);
			return false;
		} 
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
		try {
			ofy().save().entities(products);
		}
		catch (Throwable e) { 
			handleException(e);
			return false; 
		}
			
		return true;
	}
	
	/**
	 * 
	 * @param product
	 * @return the auto generated id or null if not found or an exception occured
	 */
	public Long saveFoodProduct(FoodProduct product)
	{
		try {
			ofy().save().entity(product).now();
		}
		catch (Throwable e) { 
			handleException(e);
			return null; 
		}
		
		return product.getId();
	}
	
	/**
	 * 
	 * @return all the ingredients of the datastore or null an exception occured
	 */
	public List<FoodProduct> getAllFoodProducts()
	{
		List<FoodProduct> result = new ArrayList<FoodProduct>();
		try {
			Iterable<Key<FoodProduct>> keys = ofy().load().type(FoodProduct.class).keys();
			for (Key<FoodProduct> key : keys)
				result.add(ofy().load().key(key).get());
		}
		catch (Throwable e) {
			handleException(e);
			result = null;
		}
		
		return result;
	}
	
	/**
	 * @return the product or null if not found or an exception occured
	 */
	public FoodProduct getFoodProduct(Long id) {
		FoodProduct product = null;
		try {
			product = ofy().load().type(FoodProduct.class).id(id).get();
		}
		catch (Throwable e) {
			handleException(e);
		} 
		
		return product;
		}
	
	// ------------------------ Recipes -----------------------------------

		public Long saveRecipe(Recipe recipe){
			try {
				ofy().save().entity(recipe).now();
			}
			catch (Throwable e) {
				handleException(e);
				return null;
			} 
			return recipe.getId();
		}
		
		public Boolean deleteRecipe(Long id) {
			try {
				ofy().delete().type(Recipe.class).id(id);
			}
			catch (Throwable e) {
				handleException(e);
				return false;
			} 
			
			return true;
		}

		/**
		 * @return the recipe or null if not found or an exception occured
		 */
		public Recipe getRecipe(Long recipeId){
			Recipe recipe = null;
			try {
				recipe = ofy().load().type(Recipe.class).id(recipeId).get();
			}
			catch (Throwable e) {
				handleException(e);
			} 
			
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
		 * @return the recipes which belong to the user with userId or null if not found or an exception occured
		 */
		public ArrayList<Recipe> getUserRecipes(String userId){
			try {
				List<Recipe> recipes = ofy().load().type(Recipe.class).filter("userId", userId).filter("deleted", false).list();
				// Do that to avoid ResultProxy to be returned. Convert into propper List
				return new ArrayList<Recipe>(recipes);
			}
			catch (Throwable e) {
				handleException(e);
				return null;
			} 
		}
		
		/**
		 * @return all recipes belonging to kitchen with @param kitchenId 
		 *  or null if not found or an exception occured
		 */
		public List<Recipe> getKitchenRecipes(Long kitchenId){
			try {
				List<Recipe> recipes = ofy().load().type(Recipe.class).filter("kitchenId", kitchenId).filter("deleted", false).list();
				return new ArrayList<Recipe>(recipes);
			}
			catch (Throwable e) {
				handleException(e);
				return null;
			} 
		}
		
		/**
		 * @return the recipes which published, publicitly available
		 *  or null if not found or an exception occured
		 */
		public List<Recipe> getPublicRecipes(){
			try {
				List<Recipe> recipes = ofy().load().type(Recipe.class).filter("published", true).filter("deleted", false).list();
				return new ArrayList<Recipe>(recipes);
			}
			catch (Throwable e) {
				handleException(e);
				return null;
			} 
		}
		
		
		/**
		 * TODO To be specified in future, more concrete otherwise data load will get too big
		 * @return all recipes stored in the database for admin purposes
		 *  or null if not found or an exception occured
		 */
		public List<Recipe> getAllRecipes(){
			List<Recipe> result = new ArrayList<Recipe>();
			try {
				//result = ofy().load().type(Recipe.class).filter("deleted", false).list();
				
				Iterable<Key<Recipe>> keys = ofy().load().type(Recipe.class).filter("deleted", false).keys();
				for (Key<Recipe> key : keys)
					result.add(ofy().load().key(key).get());
				
			}
			catch (Throwable e) {
				handleException(e);
				result = new ArrayList<Recipe>();;
			}
			
			return result;
		}
		


		/**
		 * @return the recipes which are requested for publication but not published yet. the need to be approved
		 *  or null if not found or an exception occured
		 */
		public List<Recipe> getUnapprovedRecipes(){
			try {
				List<Recipe> recipes = ofy().load().type(Recipe.class).filter("publicationRequested", true).filter("published", false).filter("deleted", false).list();
				return new ArrayList<Recipe>(recipes);
			}
			catch (Throwable e) {
				handleException(e);
				return null;
			} 
		}

		

		public List<Recipe> getRecipeByIds(String kitchenIdsString, Boolean isCoded){
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
		
		public Boolean approveRecipe(Long id, Boolean approve) {
			// TODO Auto-generated method stub
			return true;
		}


	// ------------------------ Kitchen -----------------------------------
	
	/**
	 * 
	 * @param currentKitchen
	 * @param userId
	 * @return false if - datastore exception happend (see log for details)
	 * 					- currentkitchen didn't wasn't in the list of the kitchens of the user
	 */
	public Boolean setCurrentKitchen(Long currentKitchen, String userId) {
		try {
			UserInfo loginInfo = ofy().load().type(UserInfo.class).filter("userId", userId).first().get();
			if (!loginInfo.setCurrentKitchen(currentKitchen))
				return false;
			ofy().save().entity(loginInfo);
		} 
		catch (Throwable e) {
			handleException(e);
			return false;
		} 
		return true;
	}

	public Long saveKitchen(Kitchen kitchen){
		/*
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
		*/
		return 0L;
	}

	/**
	 * 
	 * @param kitchenId
	  * @return the kitchen or null if not found or an exception occured
	 */
	public Kitchen getKitchen(Long kitchenId) {
		/*
		Kitchen kitchen = null;
		try {
			kitchen = ofy().load().type(Kitchen.class).id(kitchenId).get();
		}
		catch (Exception e) {
			log.log(Level.SEVERE, e.getCause() + " EXCEPTION TYPE: " + e.getClass().toString());
		} 
		return kitchen;
		*/
		return null;
	}
	
	public Boolean removeKitchen(Long kitchenId) {
		try {
			ofy().delete().type(Kitchen.class).id(kitchenId);
		}
		catch (Throwable e) {
			handleException(e);
			return false;
		} 
		//TODO remove id's from LoginInfo's, so that no error occurs if wrong id is setted
		
		return true;
	}

	public List<Kitchen> getUserKitchens(User user){
		/*
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
		*/
		return new ArrayList<Kitchen>();

	}

	public List<Kitchen> adminGetKitchens(User user){
		/*
		List<Kitchen> adminKitchens = new ArrayList<Kitchen>();

		// The Query itself is Iterable
		Query<Kitchen> yourUserKitchens = ofy().query(Kitchen.class).filter("emailAddressOwner !=", user.getEmail());
		QueryResultIterator<Kitchen> iterator = yourUserKitchens.iterator();

		while (iterator.hasNext()) {
			Kitchen kitchen = iterator.next();
			adminKitchens.add(kitchen);
		}

		return adminKitchens;
	*/
		return null;
	}

	public List<Kitchen> getOpenKitchen(){
		/*
		List<Kitchen> openKitchens = new ArrayList<Kitchen>();

		// The Query itself is Iterable
		Query<Kitchen> yourOpenKitchens = ofy().query(Kitchen.class).filter("approvedOpen", true);
		QueryResultIterator<Kitchen> iterator = yourOpenKitchens.iterator();

		while (iterator.hasNext()) {
			Kitchen kitchen = iterator.next();
			openKitchens.add(kitchen);
		}
		return openKitchens;
	*/
		return null;
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