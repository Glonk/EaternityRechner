package ch.eaternity.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import ch.eaternity.client.DataService;
import ch.eaternity.shared.ClientData;
import ch.eaternity.shared.Commitment;
import ch.eaternity.shared.FoodProduct;
import ch.eaternity.shared.LoginInfo;
import ch.eaternity.shared.NotLoggedInException;
import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.SingleDistance;
import ch.eaternity.shared.Tag;
import ch.eaternity.shared.UploadedImage;
import ch.eaternity.shared.Kitchen;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.NotFoundException;


/**
 * This Object provides a bridge between the DataAccessObject DAO and the RPC environment.
 * User rights permissions are controlled here, and also limiting amount of RPC calls in 
 * integrating some into a single call
 * 
 * @author aurelianjaggi
 *
 */
public class DataServiceImpl extends RemoteServiceServlet implements DataService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6050252880920260705L;

	private static final Logger Log = Logger.getLogger(DataServiceImpl.class.getName());
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

		return dao.saveKitchen(kitchen);
//		ofy().put(kitchen);


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
	

		

	 
	public Long saveRecipe(Recipe recipe) throws NotLoggedInException, IOException {
		checkLoggedIn();
		
		DAO dao = new DAO();
		
		dao.saveRecipe(recipe);

		return recipe.getId();
	}

	// TODO approve and disapprove Recipe
	public Boolean approveRezept(Long rezeptId, Boolean approve) throws NotLoggedInException {
		checkLoggedIn();
		DAO dao = new DAO();
		Recipe userRezept =  dao.getRecipe(rezeptId);
		userRezept.setOpen(approve);
		dao.saveRecipe(userRezept);
		return true;
	}
 
	public Boolean deleteRecipe(Long recipeId) throws NotLoggedInException {
		checkLoggedIn();
		DAO dao = new DAO();
		dao.deleteRecipe(recipeId);
		return true;
	}


	public List<Recipe> getOpenRecipe() {
		
		DAO dao = new DAO();
		List<Recipe> openRecipes = new ArrayList<Recipe>();
		openRecipes = dao.getOpenRecipe();
		return openRecipes;	
	}

	public List<Recipe> getYourRezepte() throws NotLoggedInException {
		checkLoggedIn();
		DAO dao = new DAO();
		List<Recipe> yourRecipes = new ArrayList<Recipe>();
		yourRecipes = dao.getUserRecipes(getUser());
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
		return adminRecipes;
		
	}

	public ClientData getData(String requestUri) throws NotLoggedInException {
		
		DAO dao = new DAO();
		ClientData data = new ClientData();
		
		// load all ingredients
		data.ingredients = dao.getAllIngredients();
		
		data.loginInfo = dao.getLoginInfo(requestUri);
		
		//TODO get Distances
		
		if (getUser() != null) {
			data.userRecipes = dao.getUserRecipes(getUser());
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
	
	public Boolean persistIngredients(ArrayList<FoodProduct> products)  throws NotLoggedInException
	{
		
		DAO dao = new DAO();

		dao.CreateIngredients(products);
		
		return true;
		
	}
	
	public String getIngredientsXml()
	{
		DAO dao = new DAO();
		return dao.getAllIngredientsXml();
	}

	public Boolean setCurrentKitchen(Long lastKitchen) throws NotLoggedInException {
		DAO dao = new DAO();
		try {
			LoginInfo loginInfo = dao.ofy().get(LoginInfo.class, getUser().getUserId());
			if (!loginInfo.setCurrentKitchen(lastKitchen))
				return false;
			dao.ofy().put(loginInfo);
		} catch (NotFoundException e) {
			return false;
		} 
		return true;
	}
	
	

	// not used anymore de
	public LoginInfo login(String requestUri) {

		DAO dao = new DAO();
		return dao.getLoginInfo(requestUri);

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

