package ch.eaternity.server;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.List;
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
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.ObjectifyService;

public class DAO 
{
	static {
		ObjectifyService.register(FoodProduct.class);
		ObjectifyService.register(Ingredient.class);
		ObjectifyService.register(Recipe.class);
		ObjectifyService.register(ImageBlob.class);
		ObjectifyService.register(Kitchen.class);
		ObjectifyService.register(UserInfo.class);
		ObjectifyService.register(Commitment.class);
	}

	private static final Logger log = Logger.getLogger(DAO.class.getName());
	
	
	public UserInfo getLoginInfo(String requestUri) {
		UserService userService = UserServiceFactory.getUserService();
		UserInfo loginInfo = new UserInfo();
		User user = userService.getCurrentUser();

		if (user != null) {
			try {
				loginInfo = ofy().load().type(UserInfo.class).id(user.getUserId()).get();
			} 
			catch (Exception e) {
				try {
					loginInfo.setId(Long.parseLong(user.getUserId()));;
				}
				catch (NumberFormatException nfe) {
					loginInfo.setLoggedIn(false);
					loginInfo.setLoginUrl(userService.createLoginURL(requestUri));
				}
				
				loginInfo.setLoggedIn(true);
			}
			
			// if there exists no correspongin loginInfo to User, create a new one
			if (loginInfo == null)
				loginInfo = new UserInfo();
			// reset everything in case it got changed or if a new user is created
			loginInfo.setEmailAddress(user.getEmail());
			loginInfo.setNickname(user.getNickname());
			loginInfo.setLogoutUrl(userService.createLogoutURL(requestUri));
			loginInfo.setAdmin(userService.isUserAdmin());
			ofy().save().entity(loginInfo);
		} 
		else {
			loginInfo.setLoggedIn(false);
			loginInfo.setLoginUrl(userService.createLoginURL(requestUri));
		}
		return loginInfo;
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
		catch (Exception e) { 
			log.log(Level.SEVERE, e.getMessage());
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
		catch (Exception e) { 
			log.log(Level.SEVERE, e.getMessage());
			return null; 
		}
		
		return product.getId();
	}
	
	/**
	 * 
	 * @return all the ingredients of the datastore or null if no ingredients exist or an exception occured
	 */
	public List<FoodProduct> getAllIngredients()
	{
		List<FoodProduct> result = null;
		try {
			result = ofy().load().type(FoodProduct.class).list();
		}
		catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage());
		}
		
		return result;
	}
	
	// ------------------------ Recipe -----------------------------------

		public Long saveRecipe(Recipe recipe){
			try {
				ofy().save().entity(recipe).now();
			}
			catch (Exception e) {
				log.log(Level.SEVERE, e.getMessage());
				return null;
			} 
			return recipe.getId();
		}
		
		public Boolean deleteRecipe(Long id) {
			try {
				ofy().delete().type(Recipe.class).id(id);
			}
			catch (Exception e) {
				log.log(Level.SEVERE, e.getMessage());
				return false;
			} 
			
			return true;
		}

		/**
		 * @return the recipe or null if not found or an exception occured
		 */
		public Recipe getRecipe(Long id){
			Recipe recipe = null;
			try {
				ofy().load().type(Recipe.class).id(id);
			}
			catch (Exception e) {
				log.log(Level.SEVERE, e.getMessage());
			} 
			
			// Testing Code
			for (Ingredient ing : recipe.getIngredients()) {
				FoodProduct product = ofy().load().type(FoodProduct.class).id(ing.getId()).get();
			}
			
			
			
			return recipe;
		}

		/**
		 * @return the recipes which belong to the user with userId or null if not found or an exception occured
		 */
		public List<Recipe> getUserRecipes(Long userId){
			try {
				List<Recipe> recipes = ofy().load().type(Recipe.class).filter("userId", userId).filter("deleted", false).list();
				return recipes;
			}
			catch (Exception e) {
				log.log(Level.SEVERE, e.getMessage());
				return null;
			} 
		}

		/**
		 * @return the recipes which are requested for publication but not published yet. the need to be approved
		 *  or null if not found or an exception occured
		 */
		public List<Recipe> getUnapprovedRecipes(){
			try {
				List<Recipe> recipes = ofy().load().type(Recipe.class).filter("publicationRequested", true).filter("published", false).filter("deleted", false).list();
				return recipes;
			}
			catch (Exception e) {
				log.log(Level.SEVERE, e.getMessage());
				return null;
			} 
		}
		
		/**
		 * TODO To be specified in future, more concrete otherwise data load will get too big
		 * @return all recipes stored in the database for admin purposes
		 *  or null if not found or an exception occured
		 */
		public List<Recipe> getAllRecipes(){
			try {
				List<Recipe> recipes = ofy().load().type(Recipe.class).filter("deleted", false).list();
				return recipes;
			}
			catch (Exception e) {
				log.log(Level.SEVERE, e.getMessage());
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
				return recipes;
			}
			catch (Exception e) {
				log.log(Level.SEVERE, e.getMessage());
				return null;
			} 
		}
		
		/**
		 * @return the recipes which are requested for publication but not published yet. the need to be approved
		 *  or null if not found or an exception occured
		 */
		public List<Recipe> getKitchenRecipes(Long id){
			try {
				List<Recipe> recipes = ofy().load().type(Recipe.class).filter("kitchenId", id).filter("deleted", false).list();
				return recipes;
			}
			catch (Exception e) {
				log.log(Level.SEVERE, e.getMessage());
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
	public Boolean setCurrentKitchen(Long currentKitchen, Long userId) {
		try {
			UserInfo loginInfo = ofy().load().type(UserInfo.class).filter("userId", userId).first().get();
			if (!loginInfo.setCurrentKitchen(currentKitchen))
				return false;
			ofy().save().entity(loginInfo);
		} 
		catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage());
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
			log.log(Level.SEVERE, e.getMessage());
		} 
		return kitchen;
		*/
		return null;
	}
	
	public Boolean removeKitchen(Long kitchenId) {
		try {
			ofy().delete().type(Kitchen.class).id(kitchenId);
		}
		catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage());
			return false;
		} 
		//TODO remove id's from LoginInfo's, so that no error occurs if wrong id is setted
		
		return true;
	}

	public List<Kitchen> getYourKitchens(User user){
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
		return null;

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




}