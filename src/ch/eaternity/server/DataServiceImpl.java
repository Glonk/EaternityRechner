package ch.eaternity.server;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;



import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import ch.eaternity.client.NotLoggedInException;
import ch.eaternity.client.DataService;
import ch.eaternity.shared.Commitment;
import ch.eaternity.shared.Converter;
import ch.eaternity.shared.Data;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.ShortUrl;
import ch.eaternity.shared.Workgroup;
import ch.eaternity.shared.LoginInfo;
import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.SingleDistance;
import ch.eaternity.shared.Tag;
import ch.eaternity.shared.UploadedImage;

import com.google.gson.Gson;
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


	
	public Long addKitchen(Workgroup kitchen) throws NotLoggedInException {
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
		Workgroup kitchen =  dao.getKitchen(kitchenId);
		kitchen.open = approve;
		kitchen.approvedOpen = approve;
		dao.ofy().put(kitchen);
		return true;
	}
	
	public Boolean removeKitchen(Long kitchenId) throws NotLoggedInException {
		checkLoggedIn();
		DAO dao = new DAO();
		dao.ofy().delete(Workgroup.class,kitchenId);
		return true;
	}
	
	public List<Workgroup> getYourKitchens() throws NotLoggedInException {
		checkLoggedIn();
		DAO dao = new DAO();
		return dao.getYourKitchens(getUser());	
	}
	
	public List<Workgroup> getAdminKitchens() throws NotLoggedInException{
		UserService userService = UserServiceFactory.getUserService();
		List<Workgroup> adminRecipes = new ArrayList<Workgroup>();
		if(userService.getCurrentUser() != null){
			if(userService.isUserAdmin()){
				DAO dao = new DAO();
				adminRecipes = dao.adminGetKitchens(userService.getCurrentUser());
			}
		}
		return adminRecipes;
		
	}
	

		

	 
	public Long addRezept(Recipe recipe) throws NotLoggedInException, IOException {
//		checkLoggedIn();
		UserService userService = UserServiceFactory.getUserService();
		if(userService.getCurrentUser() == null){
			throw new NotLoggedInException("Not logged in.");
		}
		
		DAO dao = new DAO();
		UserRecipeWrapper userRezept = new UserRecipeWrapper(getUser());
		
		
		if(!recipe.kitchenIds.isEmpty()){
			userRezept.kitchenIds = recipe.kitchenIds;
		}
		// TODO : this is not a propper approval process!!!
		userRezept.requestedOpen = recipe.openRequested;
		
		// If recipe belongs to a kitchen, dont assign it a user mail
		if (recipe.kitchenIds.size() == 0)
		{
			if(userService.getCurrentUser().getEmail() != null){
				recipe.setEmailAddressOwner(userService.getCurrentUser().getEmail() );
			} else {
				recipe.setEmailAddressOwner(userService.getCurrentUser().getNickname());
			}
		}
		recipe.open = false;
		userRezept.approvedOpen = recipe.open;
		
		
		userRezept.setRezept(recipe);
		dao.ofy().put(userRezept);
		userRezept.recipe.setId(userRezept.id);
		
		// add the shortener call:
		
		// define URL to shorten
		String clear = Converter.toString(recipe.getId(),34);
	    String longUrl = getBaseUrl() + "view.jsp?pid=" + clear;
	    
		
		//TODO a try and catch (and async?) here
		HTTPRequest req = new HTTPRequest(
				new URL("https://www.googleapis.com/urlshortener/v1/url?key=AIzaSyAkdIvs2SM0URQn5656q9NugoU-3Ix2LYg"),
				HTTPMethod.POST);
		req.addHeader(
				new HTTPHeader("Content-Type", "application/json"));
		req.setPayload(
				new String("{\"longUrl\": \"" + longUrl + "\"}").getBytes());
		
		HTTPResponse res = URLFetchServiceFactory.getURLFetchService().fetch(req);
		String response = new String(res.getContent());
//		System.out.println(response);


		Gson gson = new Gson();
		ShortUrl shortURL = gson.fromJson(response, ShortUrl.class);   
		
		//assign to the recipe
		userRezept.recipe.ShortUrl = shortURL.id;
		
		// then save the recipe again (now with the shortUrl)
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


	public List<Recipe> getOpenRecipe() {
		
		DAO dao = new DAO();
		return dao.getOpenRecipe();	
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
			
			List<Workgroup> kitchenPersonal = dao.getYourKitchens(getUser());
			
			for(Workgroup yourKitchen : kitchenPersonal){
				Boolean notFound = true;
				for(Workgroup isThere : data.kitchens){
					if(isThere.id == yourKitchen.id){
						notFound = false;
					}
				}
				if(notFound){
					data.kitchens.add(yourKitchen);
				}
			}
			
		} else {
			List<Workgroup> kitchensOpen = dao.getOpenKitchen();
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
						markRecipe.addDirectDescandentID(checkRecipe.getId());
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

	public Boolean setYourLastKitchen(Long lastKitchen) throws NotLoggedInException {
		DAO dao = new DAO();
		boolean tryIt = false;
		try {
			LoginInfo loginInfo = dao.ofy().get(LoginInfo.class, getUser().getUserId());
			loginInfo.setLastKitchen(lastKitchen);
			loginInfo.setUsedLastKitchen(true);
			dao.ofy().put(loginInfo);
			tryIt = true;
		} catch (NotFoundException e) {
			tryIt = false;
		} 
		return tryIt;
	}
	
	

	public LoginInfo login(String requestUri) {
	    UserService userService = UserServiceFactory.getUserService();
	    User user = userService.getCurrentUser();
	    DAO dao = new DAO();
	    LoginInfo loginInfo = new LoginInfo();

	    if (user != null) {
	    	
		    try {
		    	loginInfo = dao.ofy().get(LoginInfo.class, user.getUserId());
		    	
		    } 
		    catch (NotFoundException e) {}
		    
		      loginInfo.setId(user.getUserId());
		      loginInfo.setLoggedIn(true);
		      loginInfo.setEmailAddress(user.getEmail());
		      loginInfo.setNickname(user.getNickname());
		      loginInfo.setLogoutUrl(userService.createLogoutURL(requestUri));
		      loginInfo.setAdmin(userService.isUserAdmin());
		      dao.ofy().put(loginInfo);
		      
	    } else {
	      loginInfo.setLoggedIn(false);
	      loginInfo.setLoginUrl(userService.createLoginURL(requestUri));
	    }
	    return loginInfo;
	  }
	
	public String getBlobstoreUploadUrl() {
		BlobstoreService blobstoreService = BlobstoreServiceFactory
				.getBlobstoreService();
		return blobstoreService.createUploadUrl("/upload");
	}

	@Override
	public UploadedImage get(String key) {
		UploadedImageDao dao = new UploadedImageDao();
		UploadedImage image = dao.get(key);
		return image;
	}

	@Override
	public List<UploadedImage> getRecentlyUploaded() {
		UploadedImageDao dao = new UploadedImageDao();
		List<UploadedImage> images = dao.getRecent(); 
		return images;
	}

	@Override
	public void deleteImage(String key) {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		UploadedImageDao dao = new UploadedImageDao();
		UploadedImage image = dao.get(key);
		if(image.getOwnerId().equals(user.getUserId())) {
			dao.delete(key);
		}
	}

	public String tagImage(Tag tag) {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		TagDao dao = new TagDao();

		// TODO: Do validation here of x, y, ImageId
		
		tag.setTaggerId(user.getUserId());
		tag.setCreatedAt(new Date());
		
		String key = dao.put(tag);
		return key;
	}

	@Override
	public List<Tag> getTagsForImage(UploadedImage image) {
		TagDao dao = new TagDao();
		List<Tag> tags = dao.getForImage(image);
		return tags;
	}
	
	public String getBaseUrl(){
		String hostUrl; 
		String environment = System.getProperty("com.google.appengine.runtime.environment");
		if (environment.contentEquals("Production")) {
		    String applicationId = System.getProperty("com.google.appengine.application.id");
		    String version = System.getProperty("com.google.appengine.application.version");
		    hostUrl = "http://"+version+"."+applicationId+".appspot.com/";
		} else {
		    hostUrl = "http://localhost:8887/";
		}
		return hostUrl;
	}

	@Override
	public Long addCommitment(Commitment commitment)
			throws NotLoggedInException, IOException {
		DAO dao = new DAO();
		return dao.saveCommitment(commitment);
	}
	
	
}

