package ch.eaternity.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.eaternity.client.DataService;
import ch.eaternity.shared.ClientData;
import ch.eaternity.shared.Commitment;
import ch.eaternity.shared.FoodProduct;
import ch.eaternity.shared.Pair;
import ch.eaternity.shared.UserInfo;
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


/**
 * This Object provides a bridge between the DataAccessObject DAO and the RPC environment.
 * User rights permissions are controlled here, and also limiting amount of RPC calls in 
 * integrating some into a single call
 * 
 * @author aurelian jaggi, manuel klarmann
 *
 */
public class DataServiceImpl extends RemoteServiceServlet implements DataService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6050252880920260705L;

	private static final Logger Log = Logger.getLogger(DataServiceImpl.class.getName());

	
	// ------------------------ Kitchen -----------------------------------
	
	/*
	public ArrayList<String> getKitchenStrings(Long userId) {}
	
	or
	
	public HashSet<Pair<Long, String>> getKitchenStrings(Long userId) {}
	
	because we need the ids for matchin with currentKitchenId as well...
	
	*/
	

	
	public Long addKitchen(Kitchen kitchen) throws NotLoggedInException {
		checkLoggedIn();
		UserService userService = UserServiceFactory.getUserService();
		if(userService.getCurrentUser() == null){
			throw new NotLoggedInException("Not logged in.");
		}
		DAO dao = new DAO();

		return dao.saveKitchen(kitchen);
	}
	
	
	public Boolean removeKitchen(Long kitchenId) throws NotLoggedInException {
		// TODO implement
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
	
	public Boolean setCurrentKitchen(Long id) throws NotLoggedInException {
		checkLoggedIn();
		DAO dao = new DAO();
		return dao.setCurrentKitchen(id, getUserId());
	}
	

	// ------------------------ Recipes -----------------------------------
	
	public Long saveRecipe(Recipe recipe) throws NotLoggedInException, IOException {
		checkLoggedIn();
		DAO dao = new DAO();
		return dao.saveRecipe(recipe);
	}

	public Boolean approveRecipe(Long id, Boolean approve) throws NotLoggedInException {
		checkLoggedIn();
		DAO dao = new DAO();
		return dao.approveRecipe(id, approve);
	}
 
	public Boolean deleteRecipe(Long recipeId) throws NotLoggedInException {
		checkLoggedIn();
		DAO dao = new DAO();
		return dao.deleteRecipe(recipeId);
	}


	public List<Recipe> getPublicRecipes() {
		DAO dao = new DAO();
		return dao.getPublicRecipes();
	}

	public List<Recipe> getUserRecipes() throws NotLoggedInException {
		checkLoggedIn();
		DAO dao = new DAO();
		return dao.getUserRecipes(getUserId());
	}
	
	/**
	 * just allowed for Admins
	 */
	public List<Recipe> getAllRecipes() throws NotLoggedInException{
		if (checkAdmin()) {
			DAO dao = new DAO();
			return dao.getAllRecipes();
		}
		else
			return null;
	}

	public ClientData getData(String requestUri) throws NotLoggedInException {
		DAO dao = new DAO();
		ClientData data = new ClientData();
		
		// load all ingredients
		data.ingredients = dao.getAllIngredients();
		
		data.userInfo = dao.getLoginInfo(requestUri);
		
		//TODO get Distances
		/*
		if (getUser() != null) {
			data.userRecipes = dao.getUserRecipes(getUserId());
			data.publicRecipes = dao.getPublicRecipes();
			
			if (data.userInfo.getCurrentKitchen() != null)
				data.currentKitchenRecipes = dao.getKitchenRecipes(getUserId()); 

			data.kitchens = dao.getYourKitchens(getUser());
			
			if (data.userInfo.isAdmin()) {
				data.publicRecipes.addAll(getAllRecipes());
				
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
		*/
		return data;
	}

	/**
	 * 
	 * @throws NotLoggedInException
	 */
	private void checkLoggedIn() throws NotLoggedInException {
		if (getUser() == null) {
			throw new NotLoggedInException("Not logged in.");
		}
	}
	
	private boolean checkAdmin() {
		UserService userService = UserServiceFactory.getUserService();
		return userService.isUserAdmin();
	}

	private User getUser() {
		UserService userService = UserServiceFactory.getUserService();
		return userService.getCurrentUser();
	}
	
	private Long getUserId() {
		Long id = null;
		try {
			id = Long.parseLong(getUser().getUserId());
		}
		catch (NumberFormatException nfe) {
			Log.log(Level.SEVERE, nfe.getMessage());
		}
		return id;
	}


	public int addDistances(ArrayList<SingleDistance> distances) throws NotLoggedInException {
		// TODO implement
		return 0;
	}
	
	public Boolean persistIngredients(ArrayList<FoodProduct> products)  throws NotLoggedInException
	{
		DAO dao = new DAO();
		return dao.saveFoodProducts(products);
	}
	
	public String getIngredientsXml()
	{
		return null;
	}
	

	// not used anymore
	public UserInfo login(String requestUri) {
		DAO dao = new DAO();
		return dao.getLoginInfo(requestUri);
	}
	
	// ----------------------- Images ---------------------------------
	
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

