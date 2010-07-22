package ch.eaternity.client;

import java.util.ArrayList;
import java.util.List;

import ch.eaternity.shared.Data;
import ch.eaternity.shared.Rezept;
import ch.eaternity.shared.SingleDistance;
import ch.eaternity.shared.Zutat;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("data")
public interface DataService extends RemoteService {
  public String addRezept(Rezept rezept) throws NotLoggedInException;
  public String addZutat(Zutat zutat) throws NotLoggedInException;
  public void removeRezept(Rezept rezept) throws NotLoggedInException;
  public List<Rezept> getYourRezepte() throws NotLoggedInException;
  public Data getData() throws NotLoggedInException;
  public int addDistances(ArrayList<SingleDistance> distances) throws NotLoggedInException;
}