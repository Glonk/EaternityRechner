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
import ch.eaternity.shared.Rezept;
import ch.eaternity.shared.SingleDistance;
import ch.eaternity.shared.Zutat;
import ch.eaternity.shared.ZutatSpecification;
import ch.eaternity.shared.Zutat.Herkuenfte;
import ch.eaternity.shared.Zutat.Produktionen;
import ch.eaternity.shared.Zutat.Transportmittel;
import ch.eaternity.shared.Zutat.Zustaende;



import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class DataServiceImpl extends RemoteServiceServlet implements DataService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6050252880920260705L;

	private static final Logger LOG = Logger.getLogger(DataServiceImpl.class.getName());
	private static final PersistenceManagerFactory PMF =
		JDOHelper.getPersistenceManagerFactory("transactions-optional");

	public String addZutat(Zutat zutat) throws NotLoggedInException {
		checkLoggedIn();
		PersistenceManager pm = getPersistenceManager();

		try {
			pm.makePersistent(zutat);
		} finally {
			pm.close();
		}
		return zutat.getSymbol();
	}


	public String addRezept(Rezept rezept) throws NotLoggedInException {
		checkLoggedIn();
		PersistenceManager pm = getPersistenceManager();
		
//		Rezept newRezept = new Rezept();
//		newRezept.setSymbol(rezept.getSymbol());
//		newRezept.addZutaten(rezept.getZutaten());
//		newRezept.setOpen(rezept.isOpen());
		

		try {
			UserRezept userRezept = new UserRezept(getUser());
//			pm.makePersistent(rezept);
			userRezept.setRezept(rezept);
			
//			userRezept.setRezeptKey(key);
			pm.makePersistent(userRezept);
			String key = userRezept.getRezept().getId();
			userRezept.setRezeptKey(key);
//			userRezept.getRezept().setRezeptKey(key);
			
//			TODO why do i need to do this twice???? damn keys
			pm.makePersistent(userRezept);
			
//			List<String> zutatSpecificationKeys = new ArrayList<String>();
			for(ZutatSpecification zutat: rezept.getZutaten()){
				zutat.setRezeptKey(key);
				pm.makePersistent(zutat);
			}

//			userRezept.setRezept(rezept);
//			pm.makePersistent(rezept);
        } finally {
			pm.close();
		}
		return rezept.getId();
	}

 
	public void removeRezept(Rezept rezeptDelete) throws NotLoggedInException {
		checkLoggedIn();
		String rezept_id = rezeptDelete.getId();
		PersistenceManager pm = getPersistenceManager();
		try {
			//			user verification is missing here! ist it necessary?
//			Rezept rezept =	pm.getObjectById(Rezept.class,rezept_id);
			Query rezeptUser = pm.newQuery(UserRezept.class, "rezeptKey == key");
			rezeptUser.declareParameters("Long lastNameParam");
			List<UserRezept> rezeptList = (List<UserRezept>) rezeptUser.execute(rezept_id);
			for(UserRezept userRezept : rezeptList){
//				GWT.log("entferne " + userRezept.getRezept().getSymbol(), null);
//				TODO get parent
				
//				
				pm.deletePersistent(userRezept);

			}
			
			
			Rezept rezept =	pm.getObjectById(Rezept.class,rezept_id);
			pm.deletePersistent(rezept);
			
//			Query rezepte = pm.newQuery(Rezept.class, "RezeptKey == key");
//			rezepte.declareParameters("Long lastNameParam");
//			List<Rezept> rezepteList = (List<Rezept>) rezepte.execute(rezept_id);
//			for(Rezept rezept2 : rezepteList){
//				pm.deletePersistent(rezept2);
//			}

			
			Query zutaten = pm.newQuery(ZutatSpecification.class, "RezeptKey == key");
			zutaten.declareParameters("Long lastNameParam");
			List<ZutatSpecification> zutatenList = (List<ZutatSpecification>) zutaten.execute(rezept_id);
			for(ZutatSpecification zutat : zutatenList){
				pm.deletePersistent(zutat);
			}
			
			

			//			Query q = pm.newQuery(UserRezept.class, "user == u");
			//			q.declareParameters("com.google.appengine.api.users.User u");
			//			List<UserRezept> rezepte = (List<UserRezept>) q.execute(getUser());


		} finally {
			pm.close();
		}
	}



	@SuppressWarnings("unchecked")
	public List<Rezept> getYourRezepte() throws NotLoggedInException {
		checkLoggedIn();
		PersistenceManager pm = getPersistenceManager();
		List<Rezept> rezepte = new ArrayList<Rezept>();
		try {
			if (getUser() != null) {
				Query q = pm.newQuery(UserRezept.class, "user == u");
				q.declareParameters("com.google.appengine.api.users.User u");
				q.setOrdering("createDate");
				List<UserRezept> rezepteUser = (List<UserRezept>) q.execute(getUser());

				for (UserRezept userRezept : rezepteUser) {
					List<ZutatSpecification> specsList =  new ArrayList<ZutatSpecification>();
					Rezept rezept = userRezept.getRezept();
					String key = rezept.getId();
					Query zutaten = pm.newQuery(ZutatSpecification.class, "RezeptKey == key");
					zutaten.declareParameters("Long lastNameParam");
					List<ZutatSpecification> zutatenList = (List<ZutatSpecification>) zutaten.execute(key);
					for(ZutatSpecification zutat : zutatenList){
						if(zutat.getRezeptKey().compareTo(key) == 0){
						ZutatSpecification newZutat = new ZutatSpecification(zutat.getZutat_id(), zutat.getName(),
								zutat.getCookingDate(),zutat.getZustand(),zutat.getProduktion(), 
								zutat.getTransportmittel());
						newZutat.setMengeGramm(zutat.getMengeGramm());
						newZutat.setNormalCO2Value(zutat.getNormalCO2Value());
						newZutat.setHerkunft(zutat.getHerkunft());
						newZutat.setSeason(zutat.getStartSeason(), zutat.getStopSeason());
						specsList.add(newZutat);
						}
					}
					rezept.Zutaten = specsList;
					rezepte.add(rezept);
				}

			}


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
//			Query specs = pm.newQuery(ZutatSpecification.class);
//			List<ZutatSpecification> specsList = (List<ZutatSpecification>) specs.execute();
			
			if (getUser() != null) {
				Query q = pm.newQuery(UserRezept.class, "user == u");
				q.declareParameters("com.google.appengine.api.users.User u");
				q.setOrdering("createDate");
				List<UserRezept> rezepteUser = (List<UserRezept>) q.execute(getUser());

				for (UserRezept userRezept : rezepteUser) {
					List<ZutatSpecification> specsList =  new ArrayList<ZutatSpecification>();
					Rezept rezept = userRezept.getRezept();
					String key = rezept.getId();
					Query zutaten = pm.newQuery(ZutatSpecification.class, "RezeptKey == key");
					zutaten.declareParameters("Long lastNameParam");
					List<ZutatSpecification> zutatenList = (List<ZutatSpecification>) zutaten.execute(key);
					for(ZutatSpecification zutat : zutatenList){
						if(zutat.getRezeptKey().compareTo(key) == 0){
						ZutatSpecification newZutat = new ZutatSpecification(zutat.getZutat_id(), zutat.getName(),
								zutat.getCookingDate(),zutat.getZustand(),zutat.getProduktion(), 
								zutat.getTransportmittel());
						newZutat.setMengeGramm(zutat.getMengeGramm());
						newZutat.setNormalCO2Value(zutat.getNormalCO2Value());
						newZutat.setHerkunft(zutat.getHerkunft());
						newZutat.setSeason(zutat.getStartSeason(), zutat.getStopSeason());
						specsList.add(newZutat);
						}
					}
					rezept.Zutaten = specsList;
					rezeptePersonal.add(rezept);
				}

				data.setYourRezepte(rezeptePersonal);
			}
			Query q2 = pm.newQuery(Rezept.class, "open == true");
			q2.setOrdering("createDate");
			List<Rezept> rezeptePublic =   (List<Rezept>) q2.execute();
			

//			return (Employee[]) employees.toArray(new Employee[0]);
			// pm.detachCopyAll(
			for (Rezept rezeptPublic : rezeptePublic) {
				List<ZutatSpecification> specsList =  new ArrayList<ZutatSpecification>();
				Rezept rezept2 = rezeptPublic.getRezept();
				String key = rezept2.getId();
				Query zutaten = pm.newQuery(ZutatSpecification.class, "RezeptKey == key");
				zutaten.declareParameters("Long lastNameParam");
				List<ZutatSpecification> zutatenList = (List<ZutatSpecification>) zutaten.execute(key);
				for(ZutatSpecification zutat : zutatenList){
					if(zutat.getRezeptKey().compareTo(key) == 0){
					ZutatSpecification newZutat = new ZutatSpecification(zutat.getZutat_id(), zutat.getName(),
							zutat.getCookingDate(),zutat.getZustand(),zutat.getProduktion(), 
							zutat.getTransportmittel());
					newZutat.setMengeGramm(zutat.getMengeGramm());
					newZutat.setHerkunft(zutat.getHerkunft());
					newZutat.setNormalCO2Value(zutat.getNormalCO2Value());
					newZutat.setSeason(zutat.getStartSeason(), zutat.getStopSeason());
					specsList.add(newZutat);
					}
				}
				rezept2.Zutaten = specsList;
//				rezept2.addZutaten(specsList);
				rezepte.add(rezept2);
			}
			data.setPublicRezepte(rezepte);

			Query q3 = pm.newQuery(Zutat.class);
			q3.setOrdering("createDate");
			List<Zutat> zutatenQuery = (List<Zutat>) q3.execute();

			List<Zutat> zutaten = new ArrayList<Zutat>(zutatenQuery.size());
			for (Zutat zutat : zutatenQuery) {

				List<Long> alternativen = new ArrayList<Long>(zutat.getAlternativen().size());
				for(Long alternative : zutat.getAlternativen()){
					alternativen.add(alternative);
				}


				//		    	  ZutatSpecification stdMengen = new ZutatSpecification(zutat.getId(),zutat.getSymbol());
				//		    	  if(zutat.getZutatStdWerte_id() != null){
				//		    		  ZutatSpecification stdMengeRequest =	pm.getObjectById(ZutatSpecification.class,zutat.getZutatStdWerte_id());
				//		    		  stdMengen.setHerkunft(stdMengeRequest.getHerkunft());
				//		    		  stdMengen.setMengeGramm(stdMengeRequest.getMengeGramm());
				//		    	  }
				//		    			  											,,
				//		    			  											null, zutat.getZutatStdWerte().getZustand(), zutat.getZutatStdWerte().getProduktion(),
				//		    			  											zutat.getZutatStdWerte().getTransportmittel(), zutat.getZutatStdWerte().getLabel());
				//		    		  zutat.getZutatStdWerte();
//				pm.detachCopy(zutat);
				ArrayList<Herkuenfte> herkuenfte = new ArrayList<Herkuenfte>();
				for( Herkuenfte herkunft : zutat.getHerkuenfte()){
					herkuenfte.add(Herkuenfte.valueOf(herkunft.name()));
				}
				
				zutaten.add(new Zutat( zutat.getId(), zutat.getSymbol(), zutat.getCreateDate(), zutat.getCO2eWert(),
						alternativen, zutat.getStdHerkunft(),zutat.getStdZustand(),zutat.getStdProduktion(),zutat.getStdTransportmittel(),
						zutat.getStdMengeGramm() , herkuenfte, zutat.getStdStartSeason(),zutat.getStdStopSeason()));
				

				//		    			  zutat_id, herkunft, cookingDate, zustand, produktion, transportmittel, labe)
			}
			data.setZutaten(zutaten);
			
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
}