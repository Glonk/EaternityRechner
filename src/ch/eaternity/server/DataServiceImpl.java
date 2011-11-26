package ch.eaternity.server;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;


import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import ch.eaternity.client.NotLoggedInException;
import ch.eaternity.client.DataService;
import ch.eaternity.shared.Data;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.IngredientSpecification;
import ch.eaternity.shared.Kitchen;
import ch.eaternity.shared.LoginInfo;
import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.SingleDistance;
import ch.eaternity.shared.Staff;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.NotFoundException;

public class DataServiceImpl extends RemoteServiceServlet implements DataService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6050252880920260705L;

	private static final Logger LOG = Logger.getLogger(DataServiceImpl.class.getName());
	private static final PersistenceManagerFactory PMF =
		JDOHelper.getPersistenceManagerFactory("transactions-optional");


	
	public Long addKitchen(Kitchen kitchen) throws NotLoggedInException {
//		checkLoggedIn();
		UserService userService = UserServiceFactory.getUserService();
		if(userService.getCurrentUser() == null){
			throw new NotLoggedInException("Not logged in.");
		}
		DAO dao = new DAO();

		if(kitchen.getEmailAddressOwner() == null){
			if(userService.getCurrentUser().getEmail() != null){
				kitchen.setEmailAddressOwner(userService.getCurrentUser().getEmail() );
			} else {
				kitchen.setEmailAddressOwner(userService.getCurrentUser().getNickname());
			}
		}
		kitchen.open = false;

		return dao.saveKitchen(kitchen);
//		ofy().put(kitchen);


	}
	
	public Boolean approveKitchen(Long kitchenId, Boolean approve) throws NotLoggedInException {
		checkLoggedIn();
		DAO dao = new DAO();
		Kitchen kitchen =  dao.getKitchen(kitchenId);
		kitchen.open = approve;
		kitchen.approvedOpen = approve;
		dao.ofy().put(kitchen);
		return true;
	}
	
	public Boolean removeKitchen(Long kitchenId) throws NotLoggedInException {
		checkLoggedIn();
		DAO dao = new DAO();
		dao.ofy().delete(Kitchen.class,kitchenId);
		return true;
	}
	
	public List<Kitchen> getYourKitchens() throws NotLoggedInException {
		checkLoggedIn();
		DAO dao = new DAO();
		return dao.getYourKitchens(getUser());	
	}
	
	public List<Kitchen> getAdminKitchens() throws NotLoggedInException{
		UserService userService = UserServiceFactory.getUserService();
		List<Kitchen> adminRecipes = new ArrayList<Kitchen>();
		if(userService.getCurrentUser() != null){
			if(userService.isUserAdmin()){
				DAO dao = new DAO();
				adminRecipes = dao.adminGetKitchens(userService.getCurrentUser());
			}
		}
		return adminRecipes;
		
	}
	

	public Long addRezept(Recipe recipe) throws NotLoggedInException {
//		checkLoggedIn();
		UserService userService = UserServiceFactory.getUserService();
		if(userService.getCurrentUser() == null){
			throw new NotLoggedInException("Not logged in.");
		}
		DAO dao = new DAO();

		UserRecipeWrapper userRezept = new UserRecipeWrapper(getUser());
		userRezept.id = recipe.getId();
		
		if(!recipe.kitchenIds.isEmpty()){
			userRezept.kitchenIds = recipe.kitchenIds;
		}
		// TODO : this is not a propper approval process!!!
		userRezept.requestedOpen = recipe.openRequested;
		if(userService.getCurrentUser().getEmail() != null){
			recipe.setEmailAddressOwner(userService.getCurrentUser().getEmail() );
		} else {
			recipe.setEmailAddressOwner(userService.getCurrentUser().getNickname());
		}
		recipe.open = false;
		userRezept.approvedOpen = recipe.open;
		
		
		userRezept.setRezept(recipe);
		dao.ofy().put(userRezept);

		return userRezept.id;
	}

	// TODO approve and disapprove Recipe
	public Boolean approveRezept(Long rezeptId, Boolean approve) throws NotLoggedInException {
		checkLoggedIn();
		DAO dao = new DAO();
		UserRecipeWrapper userRezept =  dao.getRecipe(rezeptId);
		userRezept.recipe.open = approve;
		userRezept.approvedOpen = approve;
		dao.ofy().put(userRezept);
		return true;
	}
 
	public Boolean removeRezept(Long rezeptId) throws NotLoggedInException {
		checkLoggedIn();
		DAO dao = new DAO();
		dao.ofy().delete(UserRecipeWrapper.class,rezeptId);
		return true;
	}



	public List<Recipe> getYourRezepte() throws NotLoggedInException {
		checkLoggedIn();
		DAO dao = new DAO();
		return dao.getYourRecipe(getUser());	
	}
	
	public List<Recipe> getAdminRezepte() throws NotLoggedInException{
		UserService userService = UserServiceFactory.getUserService();
		List<Recipe> adminRecipes = new ArrayList<Recipe>();
		if(userService.getCurrentUser() != null){
		if(userService.isUserAdmin()){
			DAO dao = new DAO();
			adminRecipes = dao.adminGetRecipe(userService.getCurrentUser());
		}
		}
		return adminRecipes;
		
	}

	public Data getData() throws NotLoggedInException {
		// reference:
		// http://code.google.com/p/googleappengine/source/browse/trunk/java/demos/gwtguestbook/src/com/google/gwt/sample/gwtguestbook/server/GuestServiceImpl.java
		PersistenceManager pm = getPersistenceManager();
		Data data = new Data();

		try {
//			
			ArrayList<SingleDistance> distances = new ArrayList<SingleDistance>();
			Query q4 = pm.newQuery(SingleDistance.class);
			List<SingleDistance> singleDistances = (List<SingleDistance>) q4.execute();
			for(SingleDistance singleDistance : singleDistances){
				if(!distances.contains(singleDistance)){
					distances.add(singleDistance);
				}
			}
			data.setDistances(distances);

		
		} finally {
			pm.close();
		}
		
		// haha, this looks so much easier.. I hope that works... yep it does
		DAO dao = new DAO();
		
		if (getUser() != null) {
			List<Recipe> rezeptePersonal = dao.getYourRecipe(getUser());
			data.setYourRezepte(rezeptePersonal); // personal
			
			// here we add the persons recipes belonging to a kitchen
			// this should be getting all recipes the person belongs to
			data.KitchenRecipes = dao.getKitchenRecipes(getUser()); // kitchen
		}
		
		
		List<Recipe> rezepte = getAdminRezepte();
		if(rezepte.isEmpty()){
			rezepte = dao.getOpenRecipe();
		}
		// remove double entries
		if(data.yourRecipes != null){
			for(Recipe recipe: data.yourRecipes){
				int removeIndex = -1;
				for(Recipe rezept2:rezepte){
					if(rezept2.getId().equals(recipe.getId())){
						removeIndex = rezepte.indexOf(rezept2);
					}
				}
				if(removeIndex != -1){
					rezepte.remove(removeIndex);
				}
			}
		}
		data.setPublicRezepte(rezepte); // public
		
		// mark descendants of the recipes
		markDescendant(data.yourRecipes);
		markDescendant(data.KitchenRecipes);
		markDescendant(data.PublicRezepte);
		

		// add all ingredients
		ArrayList<Ingredient> ingredients = dao.getAllIngredients();
		data.setIngredients(ingredients);
		
		// get kitchen
		if (getUser() != null) {
			// if you are the admin, you also get all the others!
			data.kitchens = getAdminKitchens();
			
			List<Kitchen> kitchenPersonal = dao.getYourKitchens(getUser());
			
			for(Kitchen yourKitchen : kitchenPersonal){
				Boolean notFound = true;
				for(Kitchen isThere : data.kitchens){
					if(isThere.id == yourKitchen.id){
						notFound = false;
					}
				}
				if(notFound){
					data.kitchens.add(yourKitchen);
				}
			}
			
		} else {
			List<Kitchen> kitchensOpen = dao.getOpenKitchen();
			data.kitchens = kitchensOpen;
		}
		
		// get last kitchen id
		if (getUser() != null) {
		    try {
		    	LoginInfo loginInfo = dao.ofy().get(LoginInfo.class, getUser().getUserId());
		    	data.lastKitchen = loginInfo.getLastKitchen();
		    } catch (NotFoundException e) {
		    	
		    }
		} else {
			data.lastKitchen = 0L;
		}
	
		return data;
	}

	public void markDescendant(List<Recipe> recipesList) {
		for( Recipe checkRecipe: recipesList){
			if(checkRecipe.getDirectAncestorID() != null){
				// has ancestor...
				for( Recipe markRecipe: recipesList){
					
					if(markRecipe.getId().equals(checkRecipe.getDirectAncestorID())){
						// found descendants and mark him
						markRecipe.setDirectDescandentID(checkRecipe.getId());
//						checkRecipe.ancestorAlreadyMarked = true;
//						break;
					}
					
				}
							
			}
		}
	}

	private void checkLoggedIn() throws NotLoggedInException {
		if (getUser() == null) {
			throw new NotLoggedInException("Not logged in.");
		}
	}

	private User getUser() {
		UserService userService = UserServiceFactory.getUserService();
		return userService.getCurrentUser();
	}

	static PersistenceManager getPersistenceManager() {
		return PMF.getPersistenceManager();
	}


	public int addDistances(ArrayList<SingleDistance> distances) throws NotLoggedInException {
		PersistenceManager pm = getPersistenceManager();

		try {
			for(SingleDistance singleDistance : distances){
				pm.makePersistent(singleDistance);
			}
			
		} finally {
			pm.close();
		}
		return distances.size();
	}
	
	public Boolean persistIngredients(ArrayList<Ingredient> ingredients)  throws NotLoggedInException
	{
		
		DAO dao = new DAO();

		Boolean success = dao.CreateIngredients(ingredients);
		return success;
		
	}
	
	public String getIngredientsXml()
	{
		DAO dao = new DAO();
		return dao.getAllIngredientsXml();
	}

	@Override
	public Boolean setYourLastKitchen(Long lastKitchen) throws NotLoggedInException {
		DAO dao = new DAO();
		boolean tryIt = false;
		try {
			LoginInfo loginInfo = dao.ofy().get(LoginInfo.class, getUser().getUserId());
			loginInfo.setLastKitchen(lastKitchen);
			dao.ofy().put(loginInfo);
			tryIt = true;
		} catch (NotFoundException e) {
			tryIt = false;
		} 
		// TODO Auto-generated method stub
		return tryIt;
	}
}