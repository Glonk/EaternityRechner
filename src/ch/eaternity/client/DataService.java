package ch.eaternity.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.eaternity.shared.ClientData;
import ch.eaternity.shared.Commitment;
import ch.eaternity.shared.FoodProduct;
import ch.eaternity.shared.FoodProductInfo;
import ch.eaternity.shared.RecipeInfo;
import ch.eaternity.shared.RecipeSearchRepresentation;
import ch.eaternity.shared.UserInfo;
import ch.eaternity.shared.NotLoggedInException;
import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.Route;
import ch.eaternity.shared.Tag;
import ch.eaternity.shared.UploadedImage;
import ch.eaternity.shared.Kitchen;
import ch.eaternity.shared.Util.RecipePlace;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


@RemoteServiceRelativePath("data")
public interface DataService extends RemoteService {
	
	public ArrayList<FoodProductInfo> getFoodProductInfos(Date date);
	public FoodProduct getFoodProduct(Long productId);
	public Boolean persistIngredients(ArrayList<FoodProduct> ingredients) throws NotLoggedInException;
	
	public Recipe getRecipe(Long id) throws NotLoggedInException;
	public Long saveRecipe(Recipe recipe) throws NotLoggedInException, IOException;
	public Boolean deleteRecipe(Long rezeptId) throws NotLoggedInException;
	public ArrayList<Recipe> getAllRecipes() throws NotLoggedInException;
	public ArrayList<RecipeInfo> searchRecipes(RecipeSearchRepresentation search);
	
	
	public ClientData getData(String requestUri, RecipePlace recipePlace, RecipeSearchRepresentation recipeSeachRepresentation);
	public int addDistances(ArrayList<Route> distances) throws NotLoggedInException;

	public Long saveKitchen(Kitchen kitchen) throws NotLoggedInException;
	public Boolean deleteKitchen(Long kitchenId) throws NotLoggedInException;
	public ArrayList<Kitchen> getUserKitchens() throws NotLoggedInException;
	public ArrayList<Kitchen> getAdminKitchens() throws NotLoggedInException;

	// login
	public UserInfo getUserInfo(String requestUri);
	public Boolean saveUserInfo(UserInfo userInfo);
	
	public Long addCommitment(Commitment commitment) throws NotLoggedInException, IOException;
	
	// blob images
	public String getBlobstoreUploadUrl();
	public UploadedImage get(String key);
	public ArrayList<UploadedImage> getRecentlyUploaded();
	public void deleteImage(String key);
	public String tagImage(Tag tag);
	public ArrayList<Tag> getTagsForImage(UploadedImage image);

	public Boolean clearDatabase();
}