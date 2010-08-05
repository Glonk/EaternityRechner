package ch.eaternity.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;



import ch.eaternity.shared.Data;
import ch.eaternity.shared.Extraction;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.Rezept;
import ch.eaternity.shared.SingleDistance;
import ch.eaternity.shared.ZutatSpecification;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLTable.Cell;


/**
 * This application demonstrates how to construct a relatively complex user
 * interface, similar to many common email readers. It has no back-end,
 * populating its components with hard-coded data.
 */

public class EaternityRechner implements EntryPoint {

	static LoginInfo loginInfo = null;
	private final static DataServiceAsync rezeptService = GWT.create(DataService.class);
	private List<RezeptView> worksheet = new ArrayList<RezeptView>();
	// private VerticalPanel loginPanel = new VerticalPanel();
	// private Label loginLabel = new Label("Please sign in to your Google Account to access the eaternity Rechner application.");
	// private Anchor signInLink = new Anchor("Sign In");
	//private Anchor signOutLink = new Anchor("Sign Out");
	private static Data clientData = new Data();
	
	interface Binder extends UiBinder<DockLayoutPanel, EaternityRechner> { }
	
	interface SelectionStyle extends CssResource {
		String selectedRezept();
	}

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
	@UiField Button addRezeptButton;
	@UiField
	static SelectionStyle selectionStyle;
	@UiField static HorizontalPanel suggestionPanel;

	static int selectedRezept = -1;
	
	private HandlerRegistration adminHandler;
	private HandlerRegistration ingredientHandler;

	
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
		
		// TODO Move cursor focus to the Search box.
		// TODO remove this with something that makes more sense
		rezeptList.getColumnFormatter().setWidth(1, "900px");

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
						ingredientHandler.removeHandler();
					}
				}
			}
		});
		


	}

	

	static void addRezept(final Rezept rezept, final RezeptView rezeptView) {
		rezeptService.addRezept(rezept, new AsyncCallback<Long>() {
			public void onFailure(Throwable error) {
				handleError(error);
			}

			public void onSuccess(Long id) {
//Window.alert("good");
//				Search.displayRezept(rezept);
				rezept.setId(id);
				Search.getClientData().getYourRezepte().add(rezept);
				Search.updateResults(Search.SearchBox2.getText());
				rezeptView.saved = true;
				
			}
		});
	}
	static void removeRezept(final Rezept rezept) {
		rezeptService.removeRezept(rezept.getId(), new AsyncCallback<Boolean>() {
			public void onFailure(Throwable error) {
				handleError(error);
			}
			public void onSuccess(Boolean ignore) {
				Search.getClientData().getYourRezepte().remove(rezept);
				if(rezept.isOpen()){
				Search.getClientData().getPublicRezepte().remove(rezept);
				}
			}
		});
	}
	
	private static void undisplayRezept(String rezept_id) {

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
		
		ingredientHandler = topPanel.ingredientLink.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				IngredientsDialog dlg = new IngredientsDialog();
				dlg.show();
				dlg.center();
			}
		});
		topPanel.ingredientLink.setVisible(true);
		
		return adminHandler;
	}

	public static void ShowRezept(final Rezept rezept) {
		// create a new one
		
		styleRezept(selectedRezept, false);
		
		selectedRezept = -1;
		ArrayList<ZutatSpecification> zutaten = new ArrayList<ZutatSpecification>();
		zutaten.clear();
		for(ZutatSpecification zutatNew : rezept.getZutaten()){
			
			// TODO that nothing is missing
			final ZutatSpecification zutat = new ZutatSpecification(zutatNew.getId(), zutatNew.getName(),
					zutatNew.getCookingDate(),zutatNew.getZustand(),zutatNew.getProduktion(), 
					zutatNew.getTransportmittel());
			zutat.setDistance(zutatNew.getDistance());
			zutat.setHerkunft(zutatNew.getHerkunft());
			zutat.setMengeGramm(zutatNew.getMengeGramm());
			zutat.setSeason(zutatNew.getStartSeason(), zutatNew.getStopSeason());
			zutat.setZutat_id(zutatNew.getZutat_id());
			zutat.setNormalCO2Value(zutatNew.getNormalCO2Value());
			zutaten.add(zutat);
		}

		
		AddZutatZumMenu(zutaten);
		RezeptView rezeptView = (RezeptView) rezeptList.getWidget(selectedRezept,1);
		rezeptView.RezeptName.setText(rezept.getSymbol());
		
		rezeptView.rezeptNameTop.setText("Ihr " + rezept.getSymbol());
		rezeptView.makePublic.setValue(rezept.isOpen());
		
	    if(rezept.imageUrl != null){
	    	HTML showImage = new HTML();
	    	showImage.setHTML("<img src='" +GWT.getModuleBaseURL()+ rezept.imageUrl + "' />");
	    	rezeptView.imageUploaderHP.add(showImage);
	    }
	    rezeptView.amountPersons.setText(rezept.getPersons().toString());
	    rezeptView.cookingInstr.setText(rezept.getCookInstruction());
		
		
	}
	
	@UiHandler("addRezeptButton")
	public void onButtonPress(ClickEvent event) {
		Rezept rezept = new Rezept();
		rezept.setSymbol("unbenanntes Rezept");
		ShowRezept(rezept);	
	}
	
	@UiHandler("rezeptList")
	void onRezeptClicked(ClickEvent event) {
		Cell cell = rezeptList.getCellForEvent(event);
		if (cell != null) {
			styleRezept(selectedRezept, false);
			selectedRezept = cell.getRowIndex();
			styleRezept(selectedRezept, true);
			
			Widget rezeptViewWidget = rezeptList.getWidget(selectedRezept, 1);
			RezeptView rezeptView = (RezeptView) rezeptViewWidget;
			rezeptView.updtTopSuggestion();
			
			
			
		}
	}
	
	public static void styleRezept(int row, boolean selected) {
		if (row != -1) {
			String style = selectionStyle.selectedRezept();

			if (selected) {
				rezeptList.getRowFormatter().addStyleName(row, style);
			} else {
				rezeptList.getRowFormatter().removeStyleName(row, style);
			}
		}
	}
	
	public static int AddZutatZumMenu( Ingredient item) {
		// convert zutat to ZutatSpex and call the real method
		Extraction stdExtraction = null;
		for(Extraction extraction: item.getExtractions()){
			if(item.stdExtractionSymbol.equalsIgnoreCase(extraction.symbol)){
				stdExtraction = extraction;
			}
		}
		ZutatSpecification zutatSpecification = new ZutatSpecification(item.getId(), item.getSymbol(),
				 new Date(),stdExtraction.stdCondition, stdExtraction.stdProduction, 
				 stdExtraction.stdMoTransportation);
		zutatSpecification.setHerkunft(stdExtraction);
		zutatSpecification.setMengeGramm(item.stdAmountGramm);
		zutatSpecification.setSeason(stdExtraction.startSeason, stdExtraction.stopSeason);
		zutatSpecification.setNormalCO2Value(item.getCo2eValue());
		ArrayList<ZutatSpecification> zutaten = new ArrayList<ZutatSpecification>();
		
		
		zutaten.add(zutatSpecification);
		int row = AddZutatZumMenu(zutaten);
		return row;
	}

	static int AddZutatZumMenu(final ArrayList<ZutatSpecification> zutatenNew) {
		ArrayList<ZutatSpecification> zutaten = (ArrayList<ZutatSpecification>) zutatenNew.clone();
		ListIterator<ZutatSpecification> iterator = zutaten.listIterator();
		while(iterator.hasNext()){
			ZutatSpecification zutatSpec = iterator.next();
			for(SingleDistance singleDistance : Search.getClientData().getDistances()){
				if(singleDistance.getFrom().contentEquals(TopPanel.currentHerkunft) && 
						singleDistance.getTo().contentEquals(zutatSpec.getHerkunft().symbol)){
					
					zutatSpec.setDistance(singleDistance.getDistance());
					iterator.set(zutatSpec);
					break;
				}

			}
		}
		
		if (selectedRezept == -1){
			// create new Rezept
			Rezept newRezept = new Rezept();
			selectedRezept = 0;
			rezeptList.insertRow(0);
			
			//same as below
			newRezept.addZutaten(zutaten);
			RezeptView rezeptView = new RezeptView(newRezept);
			rezeptList.setWidget(selectedRezept, 1, rezeptView);
			styleRezept(selectedRezept, true);
			
			// is it necessary to have a worksheet or is rezeptList already containing everything?
			// worksheet.add(rezeptView);
			
		} else {
			// get the old one
			RezeptView rezeptView = (RezeptView) rezeptList.getWidget(selectedRezept,1);
			Rezept rezept = rezeptView.getRezept();
			
			// maybe same as above
			//zutaten.addAll(zutaten);
			rezept.addZutaten(zutaten);
			rezeptView.setRezept(rezept);
//			rezeptList.setWidget(selectedRezept, 1, rezeptView);
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
				
//				setClientData(data);
				Search.setClientData(data);
				Search.updateResults(" ");
				topPanel.locationButton.setEnabled(true);
				
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
//				TODO wtf is this?
				Search.displayRezept(rezept,false);
			}
		}
	}
	
	private void displayZutaten(List<Ingredient> zutaten) {
		for (Ingredient zutat : zutaten) {
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



	public static void updateSaison() {
		for( Widget rezeptViewWidget : rezeptList){
			RezeptView rezeptView = (RezeptView) rezeptViewWidget;
			rezeptView.updateSaison();
		}
	}
	
	

}


