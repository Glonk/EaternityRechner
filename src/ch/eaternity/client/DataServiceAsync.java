package ch.eaternity.client;


import java.util.ArrayList;
import java.util.List;

import ch.eaternity.shared.ClientData;
import ch.eaternity.shared.Commitment;
import ch.eaternity.shared.ClientData;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.Workgroup;
import ch.eaternity.shared.LoginInfo;
import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.SingleDistance;
import ch.eaternity.shared.Tag;
import ch.eaternity.shared.UploadedImage;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DataServiceAsync {
	public void saveRecipe(Recipe recipe, AsyncCallback<Long> async);
	public void removeRezept(Long rezeptId, AsyncCallback<Boolean> async);
	public void getYourRezepte(AsyncCallback<List<Recipe>> async);
	public void getOpenRecipe(AsyncCallback<List<Recipe>> async);
	public void getAdminRezepte(AsyncCallback<List<Recipe>> asyncCallback);
	public void approveRezept(Long id, Boolean approve,
			AsyncCallback<Boolean> asyncCallback);

	public void getData(String requestUri, AsyncCallback<ClientData> async);
	public void addDistances(ArrayList<SingleDistance> distances,AsyncCallback<Integer> asyncCallback);
	public void persistIngredients(ArrayList<Ingredient> ingredients,
			AsyncCallback<Boolean> asyncCallback);

	public void addKitchen(Workgroup kitchen, AsyncCallback<Long> async);
	public void removeKitchen(Long kitchenId, AsyncCallback<Boolean> async);
	public void getYourKitchens(AsyncCallback<List<Workgroup>> async);
	public void getAdminKitchens(AsyncCallback<List<Workgroup>> asyncCallback);
	public void approveKitchen(Long id, Boolean approve,
			AsyncCallback<Boolean> asyncCallback);
	public void setYourLastKitchen(Long i, AsyncCallback<Boolean> asyncCallback);
	
	// login
	public void login(String requestUri, AsyncCallback<LoginInfo> async);
	
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