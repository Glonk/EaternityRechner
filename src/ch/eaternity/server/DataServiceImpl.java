package ch.eaternity.server;

import java.util.ArrayList;
import java.util.Date;
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
import ch.eaternity.shared.ZutatSpecification;

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



	@SuppressWarnings("unchecked")
	public List<Rezept> getYourRezepte() throws NotLoggedInException {
		checkLoggedIn();
		PersistenceManager pm = getPersistenceManager();
		List<Rezept> rezepte = new ArrayList<Rezept>();
		try {
//			if (getUser() != null) {
//				Query q = pm.newQuery(UserRezept.class, "user == u");
//				q.declareParameters("com.google.appengine.api.users.User u");
//				q.setOrdering("createDate");
//				List<UserRezept> rezepteUser = (List<UserRezept>) q.execute(getUser());
//
//				for (UserRezept userRezept : rezepteUser) {
//					List<ZutatSpecification> specsList =  new ArrayList<ZutatSpecification>();
//					Rezept rezept = userRezept.getRezept();
//					Long key = rezept.id;
//					Query zutaten = pm.newQuery(ZutatSpecification.class, "RezeptKey == key");
//					zutaten.declareParameters("Long lastNameParam");
//					List<ZutatSpecification> zutatenList = (List<ZutatSpecification>) zutaten.execute(key);
//					for(ZutatSpecification zutat : zutatenList){
////						if(zutat.getRezeptKey().compareTo(key) == 0){
////						ZutatSpecification newZutat = new ZutatSpecification(zutat.getZutat_id(), zutat.getName(),
////								zutat.getCookingDate(),zutat.getZustand(),zutat.getProduktion(), 
////								zutat.getTransportmittel());
////						newZutat.setMengeGramm(zutat.getMengeGramm());
////						newZutat.setNormalCO2Value(zutat.getNormalCO2Value());
////						newZutat.setHerkunft(zutat.getHerkunft());
////						newZutat.setSeason(zutat.getStartSeason(), zutat.getStopSeason());
////						specsList.add(newZutat);
////						}
//					}
//					rezept.Zutaten = specsList;
//					rezepte.add(rezept);
//				}
//
//			}


		} finally {
			pm.close();
		}
		return rezepte;
	}

	public Data getData() throws NotLoggedInException {
		// reference:
		// http://code.google.com/p/googleappengine/source/browse/trunk/java/demos/gwtguestbook/src/com/google/gwt/sample/gwtguestbook/server/GuestServiceImpl.java
		PersistenceManager pm = getPersistenceManager();
		List<Rezept> rezeptePersonal = new ArrayList<Rezept>();
		List<Rezept> rezepte = new ArrayList<Rezept>();
		//		List<Zutat> zutaten = new ArrayList<Zutat>();
		Data data = new Data();

		try {
////			Query specs = pm.newQuery(ZutatSpecification.class);
////			List<ZutatSpecification> specsList = (List<ZutatSpecification>) specs.execute();
//			
//			if (getUser() != null) {
//				Query q = pm.newQuery(UserRezept.class, "user == u");
//				q.declareParameters("com.google.appengine.api.users.User u");
//				q.setOrdering("createDate");
//				List<UserRezept> rezepteUser = (List<UserRezept>) q.execute(getUser());
//
//				for (UserRezept userRezept : rezepteUser) {
//					List<ZutatSpecification> specsList =  new ArrayList<ZutatSpecification>();
//					Rezept rezept = userRezept.getRezept();
//					Long key = rezept.id;
//					Query zutaten = pm.newQuery(ZutatSpecification.class, "RezeptKey == key");
//					zutaten.declareParameters("Long lastNameParam");
//					List<ZutatSpecification> zutatenList = (List<ZutatSpecification>) zutaten.execute(key);
//					for(ZutatSpecification zutat : zutatenList){
////						if(zutat.getRezeptKey().compareTo(key) == 0){
////						ZutatSpecification newZutat = new ZutatSpecification(zutat.getZutat_id(), zutat.getName(),
////								zutat.getCookingDate(),zutat.getZustand(),zutat.getProduktion(), 
////								zutat.getTransportmittel());
////						newZutat.setMengeGramm(zutat.getMengeGramm());
////						newZutat.setNormalCO2Value(zutat.getNormalCO2Value());
////						newZutat.setHerkunft(zutat.getHerkunft());
////						newZutat.setSeason(zutat.getStartSeason(), zutat.getStopSeason());
////						specsList.add(newZutat);
////						}
//					}
//					rezept.Zutaten = specsList;
//					rezeptePersonal.add(rezept);
//				}
//
				data.setYourRezepte(rezeptePersonal);
//			}
//			Query q2 = pm.newQuery(Rezept.class, "open == true");
//			q2.setOrdering("createDate");
//			List<Rezept> rezeptePublic =   (List<Rezept>) q2.execute();
//			
//
////			return (Employee[]) employees.toArray(new Employee[0]);
//			// pm.detachCopyAll(
//			for (Rezept rezeptPublic : rezeptePublic) {
//				List<ZutatSpecification> specsList =  new ArrayList<ZutatSpecification>();
//				Rezept rezept2 = rezeptPublic.getRezept();
//				Long key = rezept2.id;
//				Query zutaten = pm.newQuery(ZutatSpecification.class, "RezeptKey == key");
//				zutaten.declareParameters("Long lastNameParam");
//				List<ZutatSpecification> zutatenList = (List<ZutatSpecification>) zutaten.execute(key);
//				for(ZutatSpecification zutat : zutatenList){
////					if(zutat.getRezeptKey().compareTo(key) == 0){
////					ZutatSpecification newZutat = new ZutatSpecification(zutat.getZutat_id(), zutat.getName(),
////							zutat.getCookingDate(),zutat.getZustand(),zutat.getProduktion(), 
////							zutat.getTransportmittel());
////					newZutat.setMengeGramm(zutat.getMengeGramm());
////					newZutat.setHerkunft(zutat.getHerkunft());
////					newZutat.setNormalCO2Value(zutat.getNormalCO2Value());
////					newZutat.setSeason(zutat.getStartSeason(), zutat.getStopSeason());
////					specsList.add(newZutat);
////					}
//				}
//				rezept2.Zutaten = specsList;
////				rezept2.addZutaten(specsList);
//				rezepte.add(rezept2);
//			}
			data.setPublicRezepte(rezepte);
//
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

	private PersistenceManager getPersistenceManager() {
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