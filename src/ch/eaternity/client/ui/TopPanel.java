package ch.eaternity.client.ui;

import java.util.ArrayList;
import java.util.Date;

import ch.eaternity.client.DataController;
import ch.eaternity.client.activity.RechnerActivity;
import ch.eaternity.client.events.KitchenChangedEvent;
import ch.eaternity.client.events.KitchenChangedEventHandler;
import ch.eaternity.client.ui.widgets.DistancesDialog;
import ch.eaternity.client.ui.widgets.IngredientsDialog;
import ch.eaternity.client.ui.widgets.IngredientsResultWidget;
import ch.eaternity.client.ui.widgets.KitchenDialog;
import ch.eaternity.client.ui.widgets.TooltipListener;
import ch.eaternity.shared.Workgroup;
import ch.eaternity.shared.SingleDistance;
import ch.eaternity.client.events.LoginChangedEvent;
import ch.eaternity.client.events.LoginChangedEventHandler;

import com.google.gwt.core.client.GWT;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.maps.client.geocode.Placemark;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;

import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Label;

/**
 * The top panel, which contains the 'welcome' message and various links.
 */
public class TopPanel extends Composite {

	interface Binder extends UiBinder<Widget, TopPanel> {
	}

	private static final Binder binder = GWT.create(Binder.class);
	public static String currentHerkunft = "Zürich, Schweiz";

	  @UiField public Button locationButton;
	  @UiField public Anchor signOutLink;
	  @UiField public Anchor signInLink;
	  @UiField public Anchor ingredientLink;
	  @UiField public Anchor editKitchen;
	  @UiField public InlineLabel loginLabel;
	  @UiField public ListBox Monate;
	  @UiField public HTMLPanel location;
	  @UiField Label locationLabel;
	  @UiField TextBox clientLocation;
	  @UiField public HTMLPanel isCustomer;
	  @UiField public InlineLabel isCustomerLabel;
	 
	  @UiField HTML pinHTML;
	  @UiField HTML calHTML;


	private DataController dco;
	private RechnerActivity presenter;

	public TopPanel(RechnerActivity presenter) {
		this.presenter = presenter;
		this.dco = presenter.getDCO();

		initWidget(binder.createAndBindUi(this));
		locationButton.setEnabled(false);
		ingredientLink.setVisible(false);
		signOutLink.setVisible(false);
		editKitchen.setVisible(false);

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

		pinHTML.addMouseListener(new TooltipListener(
				"Der Ort in dem Sie kochen.",
				5000 /* timeout in milliseconds */, "toolTipDown", -110, 10));

		bind();
	}

	private void bind() {
		// ---------------- Listen to the EventBus ----------------
		presenter.getEventBus().addHandler(KitchenChangedEvent.TYPE,
				new KitchenChangedEventHandler() {
					@Override
					public void onKitchenChanged(KitchenChangedEvent event) {
						if (event.id == -1) { // not in kitchen
							locationButton.setEnabled(true);
							location.setVisible(true);
							isCustomer.setVisible(false);
							editKitchen.setVisible(false);
						} else {
							locationButton.setEnabled(false);
							location.setVisible(false);
							editKitchen.setVisible(true);
							isCustomer.setVisible(true);
							isCustomerLabel
									.setText(" Sie befinden sich in der Küche: "
											+ dco.getCurrentKitchen()
													.getSymbol() + " ");
						}
					}
				});

		presenter.getEventBus().addHandler(LoginChangedEvent.TYPE,
				new LoginChangedEventHandler() {
					@Override
					public void onEvent(LoginChangedEvent event) {
						if (dco.getLoginInfo().isLoggedIn()) {
							signOutLink.setHref(dco.getLoginInfo()
									.getLogoutUrl());
							signInLink.setVisible(false);
							signOutLink.setVisible(true);

							loginLabel.setText("Willkommen "
									+ dco.getLoginInfo().getNickname() + ".");
						} else {
							// TODO sign out without reload of rechner...
							signInLink
									.setHref(dco.getLoginInfo().getLoginUrl());
							signInLink.setVisible(true);
							signOutLink.setVisible(false);

							loginLabel.setVisible(false);
						}
						if (event.loginInfo.isAdmin())
							ingredientLink.setVisible(true);
						else
							ingredientLink.setVisible(true);
					}
				});
	}

	@UiHandler("ingredientLink")
	public void onIngredientLinkClicked(ClickEvent event) {
		IngredientsDialog dlg = new IngredientsDialog();
		dlg.show();
		dlg.center();
	}

	@UiHandler("Monate")
	void onChange(ChangeEvent event) {
		dco.changeMonth(Monate.getSelectedIndex());
	}

	@UiHandler("locationButton")
	public void onClick(ClickEvent event) {
		DistancesDialog ddlg = new DistancesDialog();
		ddlg.setPresenter(presenter);
	}

	@UiHandler("editKitchen")
	public void onEditKitchenClick(ClickEvent event) {
		KitchenDialog kDlg = new KitchenDialog();
		kDlg.setPresenter(presenter);
	}

}
