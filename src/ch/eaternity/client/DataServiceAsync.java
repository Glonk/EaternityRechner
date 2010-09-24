package ch.eaternity.client;


import java.util.ArrayList;
import java.util.List;

import ch.eaternity.shared.Data;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.Rezept;
import ch.eaternity.shared.SingleDistance;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DataServiceAsync {
  public void addRezept(Rezept rezept, AsyncCallback<Long> async);
  public void removeRezept(Long rezeptId, AsyncCallback<Boolean> async);
  public void getYourRezepte(AsyncCallback<List<Rezept>> async);
  public void getAdminRezepte(AsyncCallback<List<Rezept>> asyncCallback);
  public void getData(AsyncCallback<Data> async);
  public void addDistances(ArrayList<SingleDistance> distances,AsyncCallback<Integer> asyncCallback);
  public void persistIngredients(ArrayList<Ingredient> ingredients,
		AsyncCallback<Boolean> asyncCallback);
public void approveRezept(Long id, Boolean approve,
		AsyncCallback<Boolean> asyncCallback);

}