package ch.eaternity.client.ui;

import java.util.Date;
import java.util.List;

import ch.eaternity.client.DataController;
import ch.eaternity.client.activity.RechnerActivity;
import ch.eaternity.client.events.KitchenChangedEvent;
import ch.eaternity.client.events.KitchenChangedEventHandler;
import ch.eaternity.client.events.LoadedDataEvent;
import ch.eaternity.client.events.LoadedDataEventHandler;
import ch.eaternity.client.events.LoginChangedEvent;
import ch.eaternity.client.events.LoginChangedEventHandler;
import ch.eaternity.client.events.SpinnerEvent;
import ch.eaternity.client.events.SpinnerEventHandler;
import ch.eaternity.client.place.RechnerRecipeEditPlace;
import ch.eaternity.client.place.RechnerRecipeViewPlace;
import ch.eaternity.client.resources.Resources;
import ch.eaternity.client.ui.RecipeEdit.TextErrorStyle;
import ch.eaternity.client.ui.widgets.IngredientsDialog;
import ch.eaternity.client.ui.widgets.KitchenDialog;
import ch.eaternity.client.ui.widgets.TooltipListener;
import ch.eaternity.shared.Kitchen;
import ch.eaternity.shared.Util.RecipeScope;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.datepicker.client.ui.DateBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
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
	
	@UiField Image calendarImage;
	@UiField DateBox dateBox;
	@UiField Image contactImage;
	@UiField Image signOutImage;
	  
	@UiField HorizontalPanel spinnerColumn;
	@UiField HTML spinnerHTML;
	@UiField Label spinnerLabel;
	  
	/*
	@UiField Button userRecipesButton;
	@UiField Button kitchenRecipesButton;
	@UiField Button publicRecipesButton;
	 */

	@UiField SelectedRecipeScopeStyle selectedRecipeScopeStyle;
	
	interface SelectedRecipeScopeStyle extends CssResource {
		String selectedRecipeScopeMarking();
	}

	private DataController dco;
	private RechnerActivity presenter;
	
	private MenuItem userMenu;
	private MenuBar kitchenMenu = new MenuBar(true);
	private MenuItem publicMenu;
	
	private String logoutUrl;

	public TopPanel() {
		initWidget(binder.createAndBindUi(this));
		
		adminMenuBar.setVisible(false);
		dateBox.setVisible(false);
		dateBox.setStartView("MONTH");
		
		spinnerColumn.setVisible(false);
		spinnerHTML.setHTML("<img src='images/spinner_small.gif' />");
		spinnerLabel.setText("loading ...");
		
		kitchenMenu.setVisible(false);
		kitchenMenu.setAutoOpen(true);
		

	}
	
	public void setPresenter(RechnerActivity presenter) {
		this.presenter = presenter;
		this.dco = presenter.getDCO();
		bind();
		RecipeScope recipeScope = dco.getRecipeScope();
		colorRecipeScope(recipeScope);
	}
	
	private void updateRecipesMenu() {
		kitchenMenu.clearItems();
		kitchenMenu.setVisible(false);
		List<Kitchen> kitchens = dco.getKitchens();
		Kitchen currentKitchen = dco.getCurrentKitchen();
		
		int listedKitchens = 0;
		
		if (kitchens != null && kitchens.size() > 0) {
			
			// put current Kitchen on beginning of list
			/*
			if (currentKitchen != null) {
				kitchens.remove(currentKitchen);
				kitchens.add(0,currentKitchen);
			} */
			
			
			for (final Kitchen kitchen : kitchens) {
				if (!kitchen.isBeta()) {
					kitchenMenu.addItem(new MenuItem(kitchen.getSymbol(), new Command() {
						public void execute() {
							dco.changeCurrentKitchen(kitchen);
							presenter.goTo(new RechnerRecipeViewPlace(RecipeScope.KITCHEN.toString()));
						}
					}));
					listedKitchens++;
				}
			}
			if (listedKitchens > 0)
				kitchenMenu.setVisible(true);
		}
		
		if (dco.getUserInfo() != null && dco.getUserInfo().isAdmin()) {
			kitchenMenu.addItem(new MenuItem("Küchen bearbeiten", new Command() {
				public void execute() {
					KitchenDialog kDlg = new KitchenDialog(); 
					kDlg.setPresenter(presenter);
				}
			}));
			kitchenMenu.setVisible(true);
		}
		
		recipesMenuBar.clearItems();
		recipesMenuBar.addItem(userMenu);
		if (listedKitchens > 0) {
			if (currentKitchen != null)
				recipesMenuBar.addItem(currentKitchen.getSymbol(), kitchenMenu);
			else
				recipesMenuBar.addItem("Küchen", kitchenMenu);
		}
		recipesMenuBar.addItem(publicMenu);
		
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
		
		userMenu = new MenuItem("Meine Rezepte", new Command() {
			public void execute() {
				dco.changeCurrentKitchen(null);
				presenter.goTo(new RechnerRecipeViewPlace(RecipeScope.USER.toString()));
			}
		});
		
		publicMenu = new MenuItem("Öffentliche Rezepte", new Command() {
			public void execute() {
				dco.changeCurrentKitchen(null);
				presenter.goTo(new RechnerRecipeViewPlace(RecipeScope.PUBLIC.toString()));
			}
		});
		
		userMenu.setVisible(false);
		
		updateRecipesMenu();
		
		
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
		
		presenter.getEventBus().addHandler(LoadedDataEvent.TYPE,
				new LoadedDataEventHandler() {
					@Override
					public void onEvent(LoadedDataEvent event) {
						updateRecipesMenu();
					}
				});
		
		presenter.getEventBus().addHandler(KitchenChangedEvent.TYPE,
				new KitchenChangedEventHandler() {
					@Override
					public void onKitchenChanged(KitchenChangedEvent event) {
						updateRecipesMenu();
					}

				});
		
		presenter.getEventBus().addHandler(SpinnerEvent.TYPE,
				new SpinnerEventHandler() {
					@Override
					public void onEvent(SpinnerEvent event) {
						if (event.spinning) {
							spinnerLabel.setText("  " + event.action + "");
							spinnerHTML.setVisible(true);
							spinnerColumn.setVisible(true);
						}
						else if (!event.spinning && event.action != null) {
							spinnerLabel.setText("  " + event.action + "");
							spinnerHTML.setVisible(false);
							spinnerColumn.setVisible(true);
						}
						else {
							spinnerColumn.setVisible(false);
						}
							
					}
				});

		presenter.getEventBus().addHandler(LoginChangedEvent.TYPE,
				new LoginChangedEventHandler() {
					@Override
					public void onEvent(LoginChangedEvent event) {
						updateRecipesMenu();
						if (dco.getUserInfo().isLoggedIn()) {
							logoutUrl = dco.getUserInfo().getLogoutUrl();
		
							userMenu.setVisible(true);
							if (event.loginInfo.isAdmin()) {
								adminMenuBar.setVisible(true);
							}
							else {
								adminMenuBar.setVisible(false);
							}
						} else {
							userMenu.setVisible(false);
							kitchenMenu.setVisible(false);
							adminMenuBar.setVisible(false);
						}
					}
				});
	}
	
	private void colorRecipeScope(RecipeScope recipeScope) {
		String selectedRecipeScopeMarking = selectedRecipeScopeStyle.selectedRecipeScopeMarking();
		userMenu.removeStyleName(selectedRecipeScopeMarking);
		kitchenMenu.removeStyleName(selectedRecipeScopeMarking);
		publicMenu.removeStyleName(selectedRecipeScopeMarking);
		
		switch (recipeScope) {
			case USER: userMenu.addStyleName(selectedRecipeScopeMarking); break;
			case KITCHEN: kitchenMenu.addStyleName(selectedRecipeScopeMarking);break;
			case PUBLIC: publicMenu.addStyleName(selectedRecipeScopeMarking);break;
		}	
	}
	@UiHandler("calendarImage")
	public void onCalendarClick(ClickEvent event) {
		dateBox.setVisible(!dateBox.isVisible());
	}
	
	@UiHandler("contactImage")
	public void onContactClick(ClickEvent event) {
		Window.open("mailto:info@eaternity.ch", "_blank", "");
	}
	
	@UiHandler("signOutImage")
	public void onLogoutClick(ClickEvent event) {
		if (logoutUrl != null)
			Window.open(logoutUrl, "_self", ""); 
	}
	

	@UiHandler("dateBox")
	void onValueChange(ValueChangeEvent<Date> event) {
		dateBox.setVisible(false);
		dco.changeDate(dateBox.getValue());
	}
}
