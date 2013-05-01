package ch.eaternity.client;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.eaternity.shared.ClientData;
import ch.eaternity.shared.Commitment;
import ch.eaternity.shared.ClientData;
import ch.eaternity.shared.FoodProduct;
import ch.eaternity.shared.FoodProductInfo;
import ch.eaternity.shared.Kitchen;
import ch.eaternity.shared.NotLoggedInException;
import ch.eaternity.shared.RecipeInfo;
import ch.eaternity.shared.RecipeSearchRepresentation;
import ch.eaternity.shared.UserInfo;
import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.Route;
import ch.eaternity.shared.Tag;
import ch.eaternity.shared.UploadedImage;
import ch.eaternity.shared.Util.RecipePlace;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DataServiceAsync {
	
	public void getFoodProductInfos(Date date, AsyncCallback<ArrayList<FoodProductInfo>> asyncCallback);
	public void getFoodProduct(Long productId, AsyncCallback<FoodProduct> asyncCallback);
	
	public void getRecipe(Long id, AsyncCallback<Recipe> async);
	public void saveRecipe(Recipe recipe, AsyncCallback<Long> async);
	public void deleteRecipe(Long rezeptId, AsyncCallback<Boolean> async);
	public void getAllRecipes(AsyncCallback<ArrayList<Recipe>> asyncCallback);
	public void searchRecipes(RecipeSearchRepresentation search, AsyncCallback<ArrayList<RecipeInfo>> asyncCallback);

	public void getData(String requestUri, RecipePlace recipePlace, RecipeSearchRepresentation recipeSeachRepresentation, AsyncCallback<ClientData> async);
	public void addDistances(ArrayList<Route> distances,AsyncCallback<Integer> asyncCallback);
	public void persistIngredients(ArrayList<FoodProduct> ingredients,
			AsyncCallback<Boolean> asyncCallback);

	public void saveKitchen(Kitchen kitchen, AsyncCallback<Long> async);
	public void deleteKitchen(Long kitchenId, AsyncCallback<Boolean> async);
	public void getUserKitchens(AsyncCallback<ArrayList<Kitchen>> async);
	public void getAdminKitchens(AsyncCallback<ArrayList<Kitchen>> asyncCallback);
	
	// login
	public void getUserInfo(String requestUri, AsyncCallback<UserInfo> async);
	public void saveUserInfo(UserInfo userInfo, AsyncCallback<Boolean> async);
	
	//blob images
	public void getBlobstoreUploadUrl(AsyncCallback<String> callback);
	
	public void addCommitment(Commitment commitment, AsyncCallback<Long> async);

	public void get(String key, AsyncCallback<UploadedImage> callback);

	public void getRecentlyUploaded(AsyncCallback<ArrayList<UploadedImage>> callback);

	public void deleteImage(String key, AsyncCallback<Void> callback);

	public void tagImage(Tag tag,
			AsyncCallback<String> callback);

	public void getTagsForImage(UploadedImage image, AsyncCallback<ArrayList<Tag>> callback);

	public void clearDatabase(AsyncCallback<Boolean> callback);
}