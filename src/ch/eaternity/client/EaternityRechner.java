package ch.eaternity.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;


import ch.eaternity.client.Search.Listener;
import ch.eaternity.client.Search.SelectionStyle;
import ch.eaternity.shared.Data;
import ch.eaternity.shared.Rezept;
import ch.eaternity.shared.Zutat;
import ch.eaternity.shared.ZutatSpecification;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLTable.Cell;


/**
 * This application demonstrates how to construct a relatively complex user
 * interface, similar to many common email readers. It has no back-end,
 * populating its components with hard-coded data.
 */

public class EaternityRechner implements EntryPoint {

	private static LoginInfo loginInfo = null;
	private final static DataServiceAsync rezeptService = GWT.create(DataService.class);
	private List<RezeptView> worksheet = new ArrayList<RezeptView>();
	// private VerticalPanel loginPanel = new VerticalPanel();
	// private Label loginLabel = new Label("Please sign in to your Google Account to access the eaternity Rechner application.");
	// private Anchor signInLink = new Anchor("Sign In");
	//private Anchor signOutLink = new Anchor("Sign Out");
	private static Data clientData = new Data();
	
	interface Binder extends UiBinder<DockLayoutPanel, EaternityRechner> { }
	
	

	interface GlobalResources extends ClientBundle {
		@NotStrict
		@Source("global.css")
		CssResource css();
	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField TopPanel topPanel;
	@UiField Search search;  

	@UiField
	static FlexTable rezeptList;



	@UiField SelectionStyle selectionStyle;

	static int selectedRezept = -1;
	
	private HandlerRegistration adminHandler;
	
	interface SelectionStyle extends CssResource {
		//String selectedRow();
	}
	
	/**
	 * This method constructs the application user interface by instantiating
	 * controls and hooking up event handler.
	 */
	public void onModuleLoad() {
		
		// now load the data
		loadData();

		// Inject global styles.
		GWT.<GlobalResources>create(GlobalResources.class).css().ensureInjected();
		// Create the UI defined in EaternityRechner.ui.xml.
		DockLayoutPanel outer = binder.createAndBindUi(this);

		// Get rid of scrollbars, and clear out the window's built-in margin,
		// because we want to take advantage of the entire client area.
		Window.enableScrolling(false);
		Window.setMargin("0px");



		// Listen for item selection, displaying the currently-selected item in
		// the detail area.
		//	    mailList.setListener(new MailList.Listener() {
		//	      public void onItemSelected(MailItem item) {
		//	        mailDetail.setItem(item);
		//	      }
		//	    });

		// Add the outer panel to the RootLayoutPanel, so that it will be
		// displayed.
		RootLayoutPanel root = RootLayoutPanel.get();
		root.add(outer);
		// mainPanel.add(signOutLink);
		
		// Move cursor focus to the input box.


		//	  
		// Check login status using login service.
		LoginServiceAsync loginService = GWT.create(LoginService.class);
		loginService.login(GWT.getHostPageBaseURL(), new AsyncCallback<LoginInfo>() {
			public void onFailure(Throwable error) {
				handleError(error);
			}

			public void onSuccess(LoginInfo result) {
				loginInfo = result;
				if(loginInfo.isLoggedIn()) {
					loadYourRechner();
					if(loginInfo.isAdmin()) {
						adminHandler = loadAdmin();
					} 
				} else   {
					loadLogin();
					if(adminHandler != null){
						adminHandler.removeHandler();
					}
				}
			}
		});
		


	}


	static void addRezept(final Rezept rezept) {
		rezeptService.addRezept(rezept, new AsyncCallback<String>() {
			public void onFailure(Throwable error) {
				handleError(error);
			}

			public void onSuccess(String ignore) {
//Window.alert("good");
//				Search.displayRezept(rezept);
			}
		});
	}
	private void removeRezept(final Long rezept_id) {
		rezeptService.removeRezept(rezept_id, new AsyncCallback<Void>() {
			public void onFailure(Throwable error) {
				handleError(error);
			}
			public void onSuccess(Void ignore) {
				undisplayRezept(rezept_id);
			}
		});
	}
	
	private void undisplayRezept(Long rezept_id) {

	}

	private void loadLogin() {
		// Assemble login panel.
		topPanel.signInLink.setHref(loginInfo.getLoginUrl());
		
		// TODO only show when logged in:
		//SaveRezeptPanel.setVisible(false);
	}

	private void loadYourRechner() {
		topPanel.signOutLink.setHref(loginInfo.getLogoutUrl());
		topPanel.signInLink.setVisible(false);
		topPanel.signOutLink.setVisible(true);
		topPanel.loginLabel.setText("Willkommen ".concat( loginInfo.getNickname() ));
		// TODO only show when logged in:
		//SaveRezeptPanel.setVisible(true);
		//load your personal recipes
		loadYourRezepte();



	}
	private HandlerRegistration loadAdmin() {
		HandlerRegistration adminHandler = topPanel.AdminLink.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				AdminDialog dlg = new AdminDialog();
				dlg.show();
				dlg.center();
			}
		});
		topPanel.AdminLink.setVisible(true);
		return adminHandler;
	}

	public static void ShowRezept(final Rezept rezept) {
		// create a new one
		selectedRezept = -1;
		List<ZutatSpecification> zutaten = rezept.getZutaten();
		AddZutatZumMenu(zutaten);
	}


	
	public static int AddZutatZumMenu( Zutat zutat) {
		// convert zutat to ZutatSpex and call the real method
		ZutatSpecification zutatSpecification = new ZutatSpecification(zutat.getId(), zutat.getSymbol(),
				 new Date(),zutat.getStdZustand(), zutat.getStdProduktion(), 
				zutat.getStdTransportmittel());
		zutatSpecification.setHerkunft(zutat.getStdHerkunft());
		zutatSpecification.setMengeGramm(zutat.getStdMengeGramm());
		zutatSpecification.setSeason(zutat.getStdStartSeason(), zutat.getStdStopSeason());
		zutatSpecification.setNormalCO2Value(zutat.getCO2eWert());

		List<ZutatSpecification> zutaten = new ArrayList<ZutatSpecification>();
		zutaten.add(zutatSpecification);
		int row = AddZutatZumMenu(zutaten);
		return row;
	}

	static int AddZutatZumMenu(final List<ZutatSpecification> zutaten) {
		if (selectedRezept == -1){
			// create new Rezept
			Rezept newRezept = new Rezept();
			selectedRezept = 0;
			rezeptList.insertRow(0);
			
			//same as below
			newRezept.addZutaten(zutaten);
			RezeptView rezeptView = new RezeptView(newRezept);
			rezeptList.setWidget(selectedRezept, 1, rezeptView);
			
			// is it necessary to have a worksheet or is rezeptList already containing everything?
			// worksheet.add(rezeptView);
			
		} else {
			// get the old one
			RezeptView rezeptView = (RezeptView) rezeptList.getWidget(selectedRezept,1);
			Rezept rezept = rezeptView.getRezept();
			
			// maybe same as above
			zutaten.addAll(zutaten);
			rezept.addZutaten(zutaten);
			rezeptView.setRezept(rezept);
			rezeptList.setWidget(selectedRezept, 1, rezeptView);
			//worksheet.set(selectedRezept, rezeptView);
		}
		// is this necessary?
		return selectedRezept;
	}
	


	



	private void loadData() {
		rezeptService.getData(new AsyncCallback<Data>() {
			public void onFailure(Throwable error) {
				handleError(error);
			}
			public void onSuccess(Data data) {
//				displayZutaten(data.getZutaten());
//				displayRezepte(data.getPublicRezepte());
//				displayRezepte(data.getYourRezepte());
				
				setClientData(data);
				Search.setClientData(data);
				Search.updateResults(" ");
				
				//TODO ist the oracle of need?
				//Set<String> itemIndex = data.getOrcaleIndex();
				//Search.initializeOracle(itemIndex);
				
				topPanel.loadingLabel.setText("");
			}
		});
	}
	

	private void loadYourRezepte() {
		rezeptService.getYourRezepte(new AsyncCallback<List<Rezept>>() {
			public void onFailure(Throwable error) {
				handleError(error);
			}
			public void onSuccess(List<Rezept> rezepte) {
				
				addClientDataRezepte(rezepte);
//				displayRezepte(rezepte);
			}
		});
	}

	private void displayRezepte(List<Rezept> rezepte) {
		for (Rezept rezept : rezepte) {
			if(rezept != null){ //why can it be 0?
				Search.displayRezept(rezept);
			}
		}
	}
	
	private void displayZutaten(List<Zutat> zutaten) {
		for (Zutat zutat : zutaten) {
			if(zutat != null){ //why can it be 0?
				Search.displayZutat(zutat);
			}
		}
		
	}


	private static void handleError(Throwable error) {
		Window.alert(error.getMessage());
		if (error instanceof NotLoggedInException) {
			Window.Location.replace(loginInfo.getLogoutUrl());
		}
	}

	public void setAdminHandler(HandlerRegistration adminHandler) {
		this.adminHandler = adminHandler;
	}

	public HandlerRegistration getAdminHandler() {
		return adminHandler;
	}

	public static void setClientData(Data clientData) {
		EaternityRechner.clientData = clientData;
	}
	
	public static void addClientDataRezepte(List<Rezept> yourRezepte) {
		EaternityRechner.clientData.setYourRezepte(yourRezepte);
	}

	public static Data getClientData() {
		return clientData;
	}
	
	

}


