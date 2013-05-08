package ch.eaternity.client.ui;

import ch.eaternity.client.DataController;
import ch.eaternity.client.activity.RechnerActivity;
import ch.eaternity.client.events.AlertEvent;
import ch.eaternity.client.events.AlertEventHandler;
import ch.eaternity.client.events.UpdateRecipeViewEvent;
import ch.eaternity.client.events.UpdateRecipeViewEventHandler;

import com.github.gwtbootstrap.client.ui.Column;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class RechnerView extends SimpleLayoutPanel {
	@UiTemplate("RechnerView.ui.xml")
	interface Binder extends UiBinder<DockLayoutPanel, RechnerView> {
	}

	private static Binder uiBinder = GWT.create(Binder.class);

	interface SelectionStyle extends CssResource {
		String selectedRezept();
	}

	// ----------------- Class Variables -------------------
//	@UiField SimplePanel alertPanel;
	@UiField DockLayoutPanel workspace;
	@UiField TopPanel topPanel;
	@UiField SimplePanel searchPanel;
	@UiField SimplePanel recipePanel;

	private RechnerActivity presenter;
	private DataController dco;
	
	interface StickyTop extends CssResource {
		String stickToTop();
	}

	public RechnerView() {
		topPanel = new TopPanel();
	}
	
	public void setPresenter(RechnerActivity presenter) {
		this.presenter = presenter;
		this.dco = presenter.getDCO();
		
		topPanel.setPresenter(presenter);
		bind();
	}

	private void bind() {
		setWidget(uiBinder.createAndBindUi(this));
		
		// ---------------- Listen to the EventBus ----------------
		presenter.getEventBus().addHandler(AlertEvent.TYPE,
				new AlertEventHandler() {
					@Override
					public void onEvent(final AlertEvent event) {
						if (event.destination == AlertEvent.Destination.VIEW || event.destination == AlertEvent.Destination.EDIT || event.destination == AlertEvent.Destination.BOTH) {
							//alertPanel.setWidget(event.alert);
							
							Timer t = new Timer() {
								public void run() {
									//event.alert.close();
								}
							};
							if (event.timeDisplayed != null)
								t.schedule(event.timeDisplayed);
						}
					}
				});
	}
	
	
	// ---------------- Sidebar stick to top ----------------
	
	

	

	public SimplePanel getSearchPanel() {
		return searchPanel;
	}

	public SimplePanel getRecipePanel() {
		return recipePanel;
	}
	
	public TopPanel getTopPanel() {
		return topPanel;
	}


}
