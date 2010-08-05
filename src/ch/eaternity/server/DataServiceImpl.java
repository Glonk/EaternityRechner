package ch.eaternity.server;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;


import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import ch.eaternity.client.NotLoggedInException;
import ch.eaternity.client.DataService;
import ch.eaternity.shared.Data;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.Rezept;
import ch.eaternity.shared.SingleDistance;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class DataServiceImpl extends RemoteServiceServlet implements DataService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6050252880920260705L;

	private static final Logger LOG = Logger.getLogger(DataServiceImpl.class.getName());
	private static final PersistenceManagerFactory PMF =
		JDOHelper.getPersistenceManagerFactory("transactions-optional");



	public Long addRezept(Rezept rezept) throws NotLoggedInException {
		checkLoggedIn();

		DAO dao = new DAO();

		UserRezept userRezept = new UserRezept(getUser());
		// TODO : this is not a propper approval process!!!
		userRezept.approvedOpen = rezept.isOpen();
		
		userRezept.setRezept(rezept);
		dao.ofy().put(userRezept);

		return userRezept.id;
	}

 
	public Boolean removeRezept(Long rezeptId) throws NotLoggedInException {
		checkLoggedIn();
		DAO dao = new DAO();
		dao.ofy().delete(UserRezept.class,rezeptId);
		return true;
		
	}



	public List<Rezept> getYourRezepte() throws NotLoggedInException {
		checkLoggedIn();
		DAO dao = new DAO();
		return dao.getYourRecipe(getUser());
	}

	public Data getData() throws NotLoggedInException {
		// reference:
		// http://code.google.com/p/googleappengine/source/browse/trunk/java/demos/gwtguestbook/src/com/google/gwt/sample/gwtguestbook/server/GuestServiceImpl.java
		PersistenceManager pm = getPersistenceManager();
		Data data = new Data();

		try {
//			
			
			ArrayList<SingleDistance> distances = new ArrayList<SingleDistance>();
			Query q4 = pm.newQuery(SingleDistance.class);
			List<SingleDistance> singleDistances = (List<SingleDistance>) q4.execute();
			for(SingleDistance singleDistance : singleDistances){
				if(!distances.contains(singleDistance)){
					distances.add(singleDistance);
				}
			}
			data.setDistances(distances);

		
		} finally {
			pm.close();
		}
		
		// haha, this looks so much easier.. I hope that works... yep it does
		DAO dao = new DAO();
		
		if (getUser() != null) {
		List<Rezept> rezeptePersonal = dao.getYourRecipe(getUser());
		data.setYourRezepte(rezeptePersonal);
		}
		
		
		List<Rezept> rezepte = dao.getOpenRecipe();
		if(data.YourRezepte != null){
			for(Rezept rezept: data.YourRezepte){
				int removeIndex = -1;
				for(Rezept rezept2:rezepte){
					if(rezept2.getId().equals(rezept.getId())){
						removeIndex = rezepte.indexOf(rezept2);
					}
				}
				if(removeIndex != -1){
					rezepte.remove(removeIndex);
				}
			}
		}
		data.setPublicRezepte(rezepte);
		
		
		ArrayList<Ingredient> ingredients = dao.getAllIngredients();
		data.setIngredients(ingredients);
		
		
		return data;
	}

	private void checkLoggedIn() throws NotLoggedInException {
		if (getUser() == null) {
			throw new NotLoggedInException("Not logged in.");
		}
	}

	private User getUser() {
		UserService userService = UserServiceFactory.getUserService();
		return userService.getCurrentUser();
	}

	static PersistenceManager getPersistenceManager() {
		return PMF.getPersistenceManager();
	}


	public int addDistances(ArrayList<SingleDistance> distances) throws NotLoggedInException {
		PersistenceManager pm = getPersistenceManager();

		try {
			for(SingleDistance singleDistance : distances){
				pm.makePersistent(singleDistance);
			}
			
		} finally {
			pm.close();
		}
		return distances.size();
	}
	
	public Boolean persistIngredients(ArrayList<Ingredient> ingredients)  throws NotLoggedInException
	{
		
		DAO dao = new DAO();

		Boolean success = dao.CreateIngredients(ingredients);
		return success;
		
	}
	
	public String getIngredientsXml()
	{
		DAO dao = new DAO();
		return dao.getAllIngredientsXml();
	}
}