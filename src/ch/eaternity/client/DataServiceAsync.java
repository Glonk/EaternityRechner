package ch.eaternity.client;


import java.util.List;

import ch.eaternity.shared.Data;
import ch.eaternity.shared.Rezept;
import ch.eaternity.shared.Zutat;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DataServiceAsync {
  public void addRezept(Rezept rezept, AsyncCallback<String> async);
  public void addZutat(Zutat zutat, AsyncCallback<String> async);
  public void removeRezept(Long rezept_id, AsyncCallback<Void> async);
  public void getYourRezepte(AsyncCallback<List<Rezept>> async);
  public void getData(AsyncCallback<Data> async);
}