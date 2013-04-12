package ch.eaternity.client.ui;

import java.util.Date;
import java.util.List;

import ch.eaternity.client.DataController;
import ch.eaternity.client.activity.RechnerActivity;
import ch.eaternity.client.events.KitchenChangedEvent;
import ch.eaternity.client.events.KitchenChangedEventHandler;
import ch.eaternity.client.events.LoginChangedEvent;
import ch.eaternity.client.events.LoginChangedEventHandler;
import ch.eaternity.client.events.SpinnerEvent;
import ch.eaternity.client.events.SpinnerEventHandler;
import ch.eaternity.client.place.RechnerRecipeViewPlace;
import ch.eaternity.client.ui.widgets.IngredientsDialog;
import ch.eaternity.client.ui.widgets.KitchenDialog;
import ch.eaternity.client.ui.widgets.TooltipListener;
import ch.eaternity.shared.Kitchen;
import ch.eaternity.shared.Util.RecipeScope;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

/**
 * The top panel, which contains the 'welcome' message and various links.
 */
public class TopPanel extends Composite {

	interface Binder extends UiBinder<Widget, TopPanel> {}
	private static final Binder binder = GWT.create(Binder.class);

	@UiField MenuBar adminMenuBar;
	@UiField MenuBar recipesMenuBar;
	
	@UiField Anchor signOutLink;
	@UiField Anchor signInLink;
	@UiField ListBox Monate;
	  
	@UiField HorizontalPanel spinnerPanel;
	@UiField HTML spinnerHTML;
	@UiField Label spinnerLabel;
	  
	@UiField Button userRecipesButton;
	@UiField Button kitchenRecipesButton;
	@UiField Button publicRecipesButton;
	 
	@UiField HTML calHTML;


	private DataController dco;
	private RechnerActivity presenter;
	
	MenuBar kitchenMenu = new MenuBar(true);
	
	
	public void setPresenter(RechnerActivity presenter) {
		this.presenter = presenter;
		this.dco = presenter.getDCO();
		bind();
		RecipeScope recipeScope = dco.getRecipeScope();
		colorRecipeScope(recipeScope);
	}

	public TopPanel() {
		initWidget(binder.createAndBindUi(this));
		
		adminMenuBar.setVisible(false);
		signInLink.setVisible(false);
		signOutLink.setVisible(false);
		userRecipesButton.setVisible(false);
		kitchenRecipesButton.setVisible(false);
		
		spinnerPanel.setVisible(false);
		spinnerHTML.setHTML("<img src='images/spinner_small.gif' />");
		spinnerLabel.setText("loading ...");

		Monate.addItem("Januar");
		Monate.addItem("Februar");
		Monate.addItem("März");
		Monate.addItem("April");
		Monate.addItem("Mai");
		Monate.addItem("Juni");
		Monate.addItem("Juli");
		Monate.addItem("August");
		Monate.addItem("September");
		Monate.addItem("Oktober");
		Monate.addItem("November");
		Monate.addItem("Dezember");

		Date date = new Date();
		Monate.setSelectedIndex(date.getMonth());

		calHTML.addMouseListener(new TooltipListener(
				"Der Monat in dem Sie kochen.",
				5000 /* timeout in milliseconds */, "toolTipDown", -130, 10));
		
		
	}
	
	private void updateKitchenMenu() {
		kitchenMenu.clearItems();
		
		List<Kitchen> kitchens = dco.getKitchens();
		Kitchen currentKitchen = dco.getCurrentKitchen();
		
		if (kitchens != null && kitchens.size() > 0) {
			
			// put current Kitchen on beginning of list
			if (currentKitchen != null) {
				kitchens.remove(currentKitchen);
				kitchens.add(0,currentKitchen);
			}
			
			for (final Kitchen kitchen : kitchens) {
				kitchenMenu.addItem(new MenuItem(kitchen.getSymbol(), new Command() {
					public void execute() {
						dco.changeCurrentKitchen(kitchen);
						presenter.goTo(new RechnerRecipeViewPlace(RecipeScope.KITCHEN.toString()));
					}
				}));
			}
		}
		
		if (dco.getUserInfo() != null && dco.getUserInfo().isAdmin()) {
			kitchenMenu.addItem(new MenuItem("Küchen bearbeiten", new Command() {
				public void execute() {
					KitchenDialog kDlg = new KitchenDialog(); 
					kDlg.setPresenter(presenter);
				}
			}));
		}
		
		
	}

	private void bind() {
		// ------------- Admin Menu ----------------
		MenuBar adminMenu = new MenuBar(true);
		
		adminMenu.addItem(new MenuItem("Zutaten hinzufügen", new Command() {
			public void execute() {
				IngredientsDialog dlg = new IngredientsDialog(presenter);
				dlg.show();
				dlg.center();
			}
		}));
		
		adminMenu.addItem(new MenuItem("Datenbank löschen", new Command() {
			public void execute() {
				dco.clearDatabase();
			}
		}));
		
		adminMenuBar.addItem("Admin Menü", adminMenu);
		
		// ------------- Kitchen Menu ----------------
		
		
		recipesMenuBar.addItem("Küchen", kitchenMenu);
		
		
		// ---------------- Listen to the EventBus ----------------
		presenter.getEventBus().addHandler(PlaceChangeEvent.TYPE,
				new PlaceChangeEvent.Handler() {
					@Override
					public void onPlaceChange(PlaceChangeEvent event) {
						if (event.getNewPlace() instanceof RechnerRecipeViewPlace) {
							RecipeScope recipeScope = ((RechnerRecipeViewPlace)event.getNewPlace()).getRecipeScope();
							colorRecipeScope(recipeScope);
						}
					}
				});
		
		presenter.getEventBus().addHandler(KitchenChangedEvent.TYPE,
				new KitchenChangedEventHandler() {
					@Override
					public void onKitchenChanged(KitchenChangedEvent event) {
						updateKitchenMenu();
					}

				});
		
		presenter.getEventBus().addHandler(SpinnerEvent.TYPE,
				new SpinnerEventHandler() {
					@Override
					public void onEvent(SpinnerEvent event) {
						if (event.spinning) {
							spinnerLabel.setText("  " + event.action + "");
							spinnerHTML.setVisible(true);
							spinnerPanel.setVisible(true);
						}
						else if (!event.spinning && event.action != null) {
							spinnerLabel.setText("  " + event.action + "");
							spinnerHTML.setVisible(false);
							spinnerPanel.setVisible(true);
						}
						else {
							spinnerPanel.setVisible(false);
						}
							
					}
				});

		presenter.getEventBus().addHandler(LoginChangedEvent.TYPE,
				new LoginChangedEventHandler() {
					@Override
					public void onEvent(LoginChangedEvent event) {
						updateKitchenMenu();
						if (dco.getUserInfo().isLoggedIn()) {
							signOutLink.setHref(dco.getUserInfo()
									.getLogoutUrl());
							signInLink.setVisible(false);
							signOutLink.setVisible(true);

							if (dco.getKitchens() != null)
								kitchenRecipesButton.setVisible(true);	
							
							userRecipesButton.setVisible(true);
							
							if (event.loginInfo.isAdmin()) {
								adminMenuBar.setVisible(true);
							}
							else {
								adminMenuBar.setVisible(false);
							}
						} else {
							// TODO sign out without reload of rechner...
							signInLink.setHref(dco.getUserInfo().getLoginUrl());
							signInLink.setVisible(true);
							signOutLink.setVisible(false);

							kitchenRecipesButton.setVisible(false);			
							userRecipesButton.setVisible(false);
							adminMenuBar.setVisible(false);
						}
					}
				});
	}
	
	private void colorRecipeScope(RecipeScope recipeScope) {
		userRecipesButton.setType(ButtonType.DEFAULT);
		kitchenRecipesButton.setType(ButtonType.DEFAULT);
		publicRecipesButton.setType(ButtonType.DEFAULT);
		
		switch (recipeScope) {
			case USER: userRecipesButton.setType(ButtonType.PRIMARY); break;
			case KITCHEN: kitchenRecipesButton.setType(ButtonType.PRIMARY);break;
			case PUBLIC: publicRecipesButton.setType(ButtonType.PRIMARY);break;
		}	
	}
	

	@UiHandler("Monate")
	void onChange(ChangeEvent event) {
		dco.changeMonth(Monate.getSelectedIndex());
	}
	
	
	@UiHandler("userRecipesButton")
	public void onUserClicked(ClickEvent event) {
		presenter.goTo(new RechnerRecipeViewPlace(RecipeScope.USER.toString()));
		dco.changeCurrentKitchen(null);
	}
	/*
	@UiHandler("kitchenRecipesButton")
	public void onKitchenClicked(ClickEvent event) {
		presenter.goTo(new RechnerRecipeViewPlace(RecipeScope.KITCHEN.toString()));
		dco.changeCurrentKitchen(null);
	}
	*/
	
	@UiHandler("publicRecipesButton")
	public void onRecipesClicked(ClickEvent event) {
		presenter.goTo(new RechnerRecipeViewPlace(RecipeScope.PUBLIC.toString()));
		dco.changeCurrentKitchen(null);
	}

}
