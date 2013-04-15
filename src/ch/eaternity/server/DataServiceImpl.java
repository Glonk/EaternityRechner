package ch.eaternity.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.eaternity.client.DataService;
import ch.eaternity.client.place.LoginPlace;
import ch.eaternity.shared.ClientData;
import ch.eaternity.shared.Commitment;
import ch.eaternity.shared.FoodProduct;
import ch.eaternity.shared.FoodProductInfo;
import ch.eaternity.shared.Pair;
import ch.eaternity.shared.RecipeInfo;
import ch.eaternity.shared.RecipeSearchRepresentation;
import ch.eaternity.shared.Season;
import ch.eaternity.shared.SeasonDate;
import ch.eaternity.shared.UserInfo;
import ch.eaternity.shared.NotLoggedInException;
import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.SingleDistance;
import ch.eaternity.shared.Tag;
import ch.eaternity.shared.UploadedImage;
import ch.eaternity.shared.Kitchen;
import ch.eaternity.shared.Util.RecipePlace;
import ch.eaternity.shared.Util.RecipeScope;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;


/**
 * This Class provides a bridge between the DataAccessObject DAO and the RPC environment.
 * User rights permissions are controlled here, and also limiting amount of RPC calls in 
 * integrating some into a single call
 * 
 * This Class also makes all the User authorisation & Control, dao isnot responsible for that
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

	
	
	 // ------------------------ Ingredients -----------------------------------
	
	public ArrayList<FoodProductInfo> getFoodProductInfos(Integer month) {
		DAO dao = new DAO();
		List<FoodProduct> foodProducts = dao.getAllFoodProducts();
		ArrayList<FoodProductInfo> productInfos = new ArrayList<FoodProductInfo>();
		
		for (FoodProduct product : foodProducts) {
			FoodProductInfo info = new FoodProductInfo(product);
			
			info.setInSeason(false);
			Season season = product.getSeason();
			if(product.isSeasonDependant() && season != null){
				SeasonDate date = new SeasonDate(month,1);
				if( date.after(season.getBeginning()) && date.before(season.getEnd()) )
					info.setInSeason(true);
			}
			
			productInfos.add(info);
		}
		
		return productInfos;
	}
	
	public FoodProduct getFoodProduct(Long productId) {
		DAO dao = new DAO();
		FoodProduct product = dao.getFoodProduct(productId);
		if (product == null)
			throw new NoSuchElementException("Ingredient with id " + productId + "not found in database or error occured.");
		return product;
	}
	
	public Boolean persistIngredients(ArrayList<FoodProduct> products) 
	{
		DAO dao = new DAO();
		return dao.saveFoodProducts(products);
	}
	
	public String getIngredientsXml()
	{
		return null;
	}
	

	// ------------------------ Recipes -----------------------------------
	/**
	 * @throws NotLoggedInException if the User doesn't own this recipe
	 * @return The recipe with @param id
	 */
	@Override
	public Recipe getRecipe(Long id) throws NotLoggedInException {
		DAO dao = new DAO();
		
		Recipe recipe = dao.getRecipe(id);
		UserInfo userInfo = dao.getUserInfo();
		 
		if (userInfo != null) {
			if (!userInfo.getId().equals(recipe.getUserId()))
				throw new NotLoggedInException("User " + userInfo.getNickname() + " doesn't own this recipe.");
		}
		else if (recipe.isPublished());
		else
			throw new NotLoggedInException();
		
		return recipe;
	}
	
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

	/**
	 * @return always a valid (non null) list of recipes
	 */
	public ArrayList<RecipeInfo> searchRecipes(RecipeSearchRepresentation search) {
		DAO dao = new DAO();
		ArrayList<RecipeInfo> recipeInfos = new ArrayList<RecipeInfo>();
		
		ArrayList<Recipe> recipes = new ArrayList<Recipe>();
		switch (search.getScope()) {
		case PUBLIC: 
			recipes = (ArrayList<Recipe>) dao.getPublicRecipes(); 
			break;
		case USER: 
			recipes = dao.getUserRecipes(getUserId()); 
			break;
		case KITCHEN: 
			for (Long kitchenId : search.getKitchenIds())
				recipes.addAll((ArrayList<Recipe>) dao.getKitchenRecipes(kitchenId)); 
			break;
		}
		
		if (recipes != null) {
			for (Recipe recipe : recipes) {
				recipeInfos.add(new RecipeInfo(recipe));
			}
			return recipeInfos;
		}
		else
			return new ArrayList<RecipeInfo>();	
	}
	
	public ArrayList<Recipe> getAllRecipes() throws NotLoggedInException {
		if (checkAdmin()) {
			DAO dao = new DAO();
			return dao.getAllRecipes();
		} 
		else
			return new ArrayList<Recipe>();
	}

	public ClientData getData(String requestUri, RecipePlace recipePlace, RecipeSearchRepresentation recipeSeachRepresentation) throws NotLoggedInException {
		
		//TODO Refactor with Transactions for parallelism, not serialized access to dao and so fort
		
		DAO dao = new DAO();
		ClientData data = new ClientData();
		
		// ------ RecipePlace independant -------
		data.kitchens = dao.getUserKitchens(getUserId());
		
		data.userInfo = dao.getUserInfo(requestUri);
		
		
		// ------ RecipePlace View -------
		if (recipePlace == RecipePlace.VIEW) {
			data.recipeInfos = searchRecipes(recipeSeachRepresentation);
		}
		
		// ------ RecipePlace EDIT -------
		if (recipePlace == RecipePlace.EDIT) {
			//TODO Remove Date, substitute with correct
			Date date = new Date();
			data.productInfos = getFoodProductInfos(date.getMonth() + 1);
			
			//TODO get Distances
		}
		return data;
	}

	// ------------------------ Kitchen -----------------------------------
		
		public Long saveKitchen(Kitchen kitchen) throws NotLoggedInException {
			checkLoggedIn();
			DAO dao = new DAO();
			return dao.saveKitchen(kitchen);
		}
		
		
		public Boolean deleteKitchen(Long kitchenId) throws NotLoggedInException {
			DAO dao = new DAO();
			return dao.deleteKitchen(kitchenId);
		}
		
		public ArrayList<Kitchen> getUserKitchens() throws NotLoggedInException {
			checkLoggedIn();
			DAO dao = new DAO();
			return dao.getUserKitchens(getUserId());	
		}
		
		public ArrayList<Kitchen> getAdminKitchens() throws NotLoggedInException{
			ArrayList<Kitchen> adminKitchens = new ArrayList<Kitchen>();
			if(checkAdmin()){
				DAO dao = new DAO();
				adminKitchens = dao.getAdminKitchens();
			}
			return adminKitchens;
			
		}

		
		
		// ------------------------ Various -----------------------------------
		
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
	
	private String getUserId() {
		//TODO remove User Id Hack before deployement
		//return getUser().getUserId();
		return new String("1");
	}


	public int addDistances(ArrayList<SingleDistance> distances) throws NotLoggedInException {
		// TODO implement
		return 0;
	}
	

	// not used anymore
	public UserInfo getUserInfo(String requestUri) {
		DAO dao = new DAO();
		return dao.getUserInfo(requestUri);
	}
	
	public Boolean saveUserInfo(UserInfo userInfo) {
		DAO dao = new DAO();
		return dao.saveUserInfo(userInfo);
	}
	
	public Boolean isUserEnabled() {
		DAO dao = new DAO();
		UserInfo userInfo = dao.getUserInfo();
		
		List<String> kitchenUserIds = dao.getAllKitchenUsers();
		
		//return kitchenUserIds.contains(userInfo.getId());
		return true;
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
	public ArrayList<UploadedImage> getRecentlyUploaded() {
		UploadedImageDao dao = new UploadedImageDao();
		ArrayList<UploadedImage> images = dao.getRecent(); 
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
	public ArrayList<Tag> getTagsForImage(UploadedImage image) {
		TagDao dao = new TagDao();
		ArrayList<Tag> tags = dao.getForImage(image);
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
	
	public Boolean clearDatabase() {
		DAO dao = new DAO();
		
		return dao.clearDatabase();
	}


	
	
}

