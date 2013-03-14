package ch.eaternity.client.ui;

import java.util.Date;

import ch.eaternity.client.DataController;
import ch.eaternity.client.activity.RechnerActivity;
import ch.eaternity.client.events.KitchenChangedEvent;
import ch.eaternity.client.events.KitchenChangedEventHandler;
import ch.eaternity.client.events.LoginChangedEvent;
import ch.eaternity.client.events.LoginChangedEventHandler;
import ch.eaternity.client.place.RechnerRecipeViewPlace;
import ch.eaternity.client.ui.widgets.IngredientsDialog;
import ch.eaternity.client.ui.widgets.TooltipListener;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * The top panel, which contains the 'welcome' message and various links.
 */
public class TopPanel extends Composite {

	interface Binder extends UiBinder<Widget, TopPanel> {}
	private static final Binder binder = GWT.create(Binder.class);


	  @UiField Anchor signOutLink;
	  @UiField Anchor signInLink;
	  @UiField Anchor ingredientLink;
	  @UiField ListBox Monate;
	  
	  @UiField Button userRecipesButton;
	  @UiField Button kitchenRecipesButton;
	  @UiField Button publicRecipesButton;
	 
	  @UiField HTML calHTML;


	private DataController dco;
	private RechnerActivity presenter;
	
	public void setPresenter(RechnerActivity presenter) {
		this.presenter = presenter;
		this.dco = presenter.getDCO();
		bind();
		RecipeScope recipeScope = dco.getRecipeScope();
		colorRecipeScope(recipeScope);
	}

	public TopPanel() {
		initWidget(binder.createAndBindUi(this));
		
		ingredientLink.setVisible(false);
		signInLink.setVisible(false);
		signOutLink.setVisible(false);
		userRecipesButton.setVisible(false);
		kitchenRecipesButton.setVisible(false);

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

	private void bind() {
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
						kitchenRecipesButton.setText(dco.getCurrentKitchen().getSymbol());
						}
					}
				);

		presenter.getEventBus().addHandler(LoginChangedEvent.TYPE,
				new LoginChangedEventHandler() {
					@Override
					public void onEvent(LoginChangedEvent event) {
						if (dco.getLoginInfo().isLoggedIn()) {
							signOutLink.setHref(dco.getLoginInfo()
									.getLogoutUrl());
							signInLink.setVisible(false);
							signOutLink.setVisible(true);

							if (dco.getKitchens() != null)
								kitchenRecipesButton.setVisible(true);	
							
							userRecipesButton.setVisible(true);
							
							if (event.loginInfo.isAdmin())
								ingredientLink.setVisible(true);
							else
								ingredientLink.setVisible(false);
							
						} else {
							// TODO sign out without reload of rechner...
							signInLink.setHref(dco.getLoginInfo().getLoginUrl());
							signInLink.setVisible(true);
							signOutLink.setVisible(false);

							kitchenRecipesButton.setVisible(false);			
							userRecipesButton.setVisible(false);
							ingredientLink.setVisible(false);
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
	

	@UiHandler("ingredientLink")
	public void onIngredientLinkClicked(ClickEvent event) {
		IngredientsDialog dlg = new IngredientsDialog(presenter);
		dlg.show();
		dlg.center();
	}

	@UiHandler("Monate")
	void onChange(ChangeEvent event) {
		dco.changeMonth(Monate.getSelectedIndex());
	}
	
	
	@UiHandler("userRecipesButton")
	public void onUserClicked(ClickEvent event) {
		presenter.goTo(new RechnerRecipeViewPlace(RecipeScope.USER.toString()));
	}
	@UiHandler("kitchenRecipesButton")
	public void onKitchenClicked(ClickEvent event) {
		presenter.goTo(new RechnerRecipeViewPlace(RecipeScope.KITCHEN.toString()));
	}
	@UiHandler("publicRecipesButton")
	public void onRecipesClicked(ClickEvent event) {
		presenter.goTo(new RechnerRecipeViewPlace(RecipeScope.PUBLIC.toString()));
	}

}
