package ch.eaternity.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import ch.eaternity.shared.Commitment;
import ch.eaternity.shared.Data;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.Workgroup;
import ch.eaternity.shared.LoginInfo;
import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.SingleDistance;
import ch.eaternity.shared.Tag;
import ch.eaternity.shared.UploadedImage;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


@RemoteServiceRelativePath("data")
public interface DataService extends RemoteService {
	public Long addRezept(Recipe recipe) throws NotLoggedInException, IOException;
	public Boolean removeRezept(Long rezeptId) throws NotLoggedInException;
	public List<Recipe> getYourRezepte() throws NotLoggedInException;
	public List<Recipe> getOpenRecipe();
	public List<Recipe> getAdminRezepte() throws NotLoggedInException;
	public Boolean approveRezept(Long rezeptId, Boolean approve) throws NotLoggedInException;

	public Data getData() throws NotLoggedInException;
	public int addDistances(ArrayList<SingleDistance> distances) throws NotLoggedInException;
	public Boolean persistIngredients(ArrayList<Ingredient> ingredients) throws NotLoggedInException;


	public Long addKitchen(Workgroup kitchen) throws NotLoggedInException;
	public Boolean removeKitchen(Long kitchenId) throws NotLoggedInException;
	public List<Workgroup> getYourKitchens() throws NotLoggedInException;
	public List<Workgroup> getAdminKitchens() throws NotLoggedInException;
	public Boolean approveKitchen(Long kitchenId, Boolean approve) throws NotLoggedInException;
	public Boolean setYourLastKitchen(Long i) throws NotLoggedInException;

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