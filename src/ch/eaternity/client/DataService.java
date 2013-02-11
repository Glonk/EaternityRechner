package ch.eaternity.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ch.eaternity.shared.ClientData;
import ch.eaternity.shared.Commitment;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.LoginInfo;
import ch.eaternity.shared.NotLoggedInException;
import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.SingleDistance;
import ch.eaternity.shared.Tag;
import ch.eaternity.shared.UploadedImage;
import ch.eaternity.shared.Kitchen;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


@RemoteServiceRelativePath("data")
public interface DataService extends RemoteService {
	public Long saveRecipe(Recipe recipe) throws NotLoggedInException, IOException;
	public Boolean deleteRecipe(Long rezeptId) throws NotLoggedInException;
	public List<Recipe> getYourRezepte() throws NotLoggedInException;
	public List<Recipe> getOpenRecipe();
	public List<Recipe> getAdminRezepte() throws NotLoggedInException;
	public Boolean approveRezept(Long rezeptId, Boolean approve) throws NotLoggedInException;

	public ClientData getData(String requestUri) throws NotLoggedInException;
	public int addDistances(ArrayList<SingleDistance> distances) throws NotLoggedInException;
	public Boolean persistIngredients(ArrayList<Ingredient> ingredients) throws NotLoggedInException;


	public Long addKitchen(Kitchen kitchen) throws NotLoggedInException;
	public Boolean removeKitchen(Long kitchenId) throws NotLoggedInException;
	public List<Kitchen> getYourKitchens() throws NotLoggedInException;
	public List<Kitchen> getAdminKitchens() throws NotLoggedInException;
	public Boolean setCurrentKitchen(Long i) throws NotLoggedInException;

	// login
	public LoginInfo login(String requestUri);
	
	public Long addCommitment(Commitment commitment) throws NotLoggedInException, IOException;
	
	// blob images
	public String getBlobstoreUploadUrl();
	public UploadedImage get(String key);
	public List<UploadedImage> getRecentlyUploaded();
	public void deleteImage(String key);
	public String tagImage(Tag tag);
	public List<Tag> getTagsForImage(UploadedImage image);


}