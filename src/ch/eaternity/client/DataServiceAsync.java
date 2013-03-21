package ch.eaternity.client;


import java.util.ArrayList;
import java.util.List;

import ch.eaternity.shared.ClientData;
import ch.eaternity.shared.Commitment;
import ch.eaternity.shared.ClientData;
import ch.eaternity.shared.FoodProduct;
import ch.eaternity.shared.Kitchen;
import ch.eaternity.shared.UserInfo;
import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.SingleDistance;
import ch.eaternity.shared.Tag;
import ch.eaternity.shared.UploadedImage;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DataServiceAsync {
	public void saveRecipe(Recipe recipe, AsyncCallback<Long> async);
	public void deleteRecipe(Long rezeptId, AsyncCallback<Boolean> async);
	public void getUserRecipes(AsyncCallback<List<Recipe>> async);
	public void getPublicRecipes(AsyncCallback<List<Recipe>> async);
	public void getAllRecipes(AsyncCallback<List<Recipe>> asyncCallback);
	public void approveRecipe(Long id, Boolean approve,
			AsyncCallback<Boolean> asyncCallback);

	public void getData(String requestUri, AsyncCallback<ClientData> async);
	public void addDistances(ArrayList<SingleDistance> distances,AsyncCallback<Integer> asyncCallback);
	public void persistIngredients(ArrayList<FoodProduct> ingredients,
			AsyncCallback<Boolean> asyncCallback);

	public void addKitchen(Kitchen kitchen, AsyncCallback<Long> async);
	public void removeKitchen(Long kitchenId, AsyncCallback<Boolean> async);
	public void getYourKitchens(AsyncCallback<List<Kitchen>> async);
	public void getAdminKitchens(AsyncCallback<List<Kitchen>> asyncCallback);

	public void setCurrentKitchen(Long i, AsyncCallback<Boolean> asyncCallback);
	
	// login
	public void login(String requestUri, AsyncCallback<UserInfo> async);
	
	//blob images
	public void getBlobstoreUploadUrl(AsyncCallback<String> callback);
	
	public void addCommitment(Commitment commitment, AsyncCallback<Long> async);

	void get(String key, AsyncCallback<UploadedImage> callback);

	void getRecentlyUploaded(AsyncCallback<List<UploadedImage>> callback);

	void deleteImage(String key, AsyncCallback<Void> callback);

	void tagImage(Tag tag,
			AsyncCallback<String> callback);

	void getTagsForImage(UploadedImage image, AsyncCallback<List<Tag>> callback);

}