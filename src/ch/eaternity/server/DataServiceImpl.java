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

import ch.eaternity.client.DataService;
import ch.eaternity.shared.ClientData;
import ch.eaternity.shared.Commitment;
import ch.eaternity.shared.Converter;
import ch.eaternity.shared.Data;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.NotLoggedInException;
import ch.eaternity.shared.ShortUrl;
import ch.eaternity.shared.Util;
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
		
		
		if(!recipe.kitchenId.isEmpty()){
			userRezept.kitchenIds = recipe.kitchenId;
		}
		// TODO : this is not a propper approval process!!!
		userRezept.requestedOpen = recipe.openRequested;
		
		// If recipe belongs to a kitchen, dont assign it a user mail
		if (recipe.kitchenId != null && recipe.kitchenId.size() == 0)
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
		List<Recipe> openRecipes = new ArrayList<Recipe>();
		openRecipes = dao.getOpenRecipe();
		Util.markDescendant(openRecipes);
		return openRecipes;	
	}

	public List<Recipe> getYourRezepte() throws NotLoggedInException {
		checkLoggedIn();
		DAO dao = new DAO();
		List<Recipe> yourRecipes = new ArrayList<Recipe>();
		yourRecipes = dao.getYourRecipe(getUser());
		Util.markDescendant(yourRecipes);
		return yourRecipes;	
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
		Util.markDescendant(adminRecipes);
		return adminRecipes;
		
	}

	public ClientData getData(String requestUri) throws NotLoggedInException {
		
		DAO dao = new DAO();
		ClientData data = new ClientData();
		data.loginInfo = dao.getLoginInfo(requestUri, getUser());
		
		// load all ingredients
		data.ingredients = dao.getAllIngredients();
		
		// DEPRECIATED PM
		// reference:
		// http://code.google.com/p/googleappengine/source/browse/trunk/java/demos/gwtguestbook/src/com/google/gwt/sample/gwtguestbook/server/GuestServiceImpl.java
		PersistenceManager pm = getPersistenceManager();
		

		try {
			ArrayList<SingleDistance> distances = new ArrayList<SingleDistance>();
			Query q4 = pm.newQuery(SingleDistance.class);
			List<SingleDistance> singleDistances = (List<SingleDistance>) q4.execute();
			for(SingleDistance singleDistance : singleDistances){
				if(!distances.contains(singleDistance)){
					distances.add(singleDistance);
				}
			}
			data.distances = distances;
		} finally {
			pm.close();
		}
		
		
		if (getUser() != null) {
			data.userRecipes = dao.getYourRecipe(getUser());
			data.publicRecipes = dao.getOpenRecipe();
			data.kitchenRecipes = dao.getKitchenRecipes(getUser()); 
			
			data.kitchens = dao.getYourKitchens(getUser());
			
			if (data.loginInfo.isAdmin()) {
				data.publicRecipes.addAll(getAdminRezepte());
				
				// remove double entries
				for(Recipe recipe: data.userRecipes){
					int removeIndex = -1;
					for(Recipe rezept2 : data.publicRecipes){
						if(rezept2.getId().equals(recipe.getId()))
							removeIndex = data.publicRecipes.indexOf(rezept2);
					}
					if(removeIndex != -1)
						data.publicRecipes.remove(removeIndex);
				}
				data.kitchens = getAdminKitchens();

			}
			else {
				data.kitchens = dao.getYourKitchens(getUser());
			}
		}
		else {
			data.kitchens = dao.getOpenKitchen();
		}
		
		// mark descendants of the recipes
		Util.markDescendant(data.userRecipes);
		Util.markDescendant(data.kitchenRecipes);
		Util.markDescendant(data.publicRecipes);
	
		return data;
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
			loginInfo.setIsInKitchen(true);
			dao.ofy().put(loginInfo);
			tryIt = true;
		} catch (NotFoundException e) {
			tryIt = false;
		} 
		return tryIt;
	}
	
	

	// not used anymore de
	public LoginInfo login(String requestUri) {
		DAO dao = new DAO();
		return dao.getLoginInfo(requestUri, getUser());
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

