package ch.eaternity.client;

import java.util.ArrayList;
import java.util.List;

import ch.eaternity.shared.Data;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.Rezept;
import ch.eaternity.shared.SingleDistance;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


@RemoteServiceRelativePath("data")
public interface DataService extends RemoteService {
  public Long addRezept(Rezept rezept) throws NotLoggedInException;
  public Boolean removeRezept(Long rezeptId) throws NotLoggedInException;
  public List<Rezept> getYourRezepte() throws NotLoggedInException;
  public Data getData() throws NotLoggedInException;
  public int addDistances(ArrayList<SingleDistance> distances) throws NotLoggedInException;
  public Boolean persistIngredients(ArrayList<Ingredient> ingredients) throws NotLoggedInException;
}