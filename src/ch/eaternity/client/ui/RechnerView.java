package ch.eaternity.client.ui;

import ch.eaternity.client.DataController;
import ch.eaternity.client.activity.RechnerActivity;
import ch.eaternity.client.events.UpdateRecipeViewEvent;
import ch.eaternity.client.events.UpdateRecipeViewEventHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.DockLayoutPanel;
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
	@UiField TopPanel topPanel;
	@UiField SimplePanel searchPanel;
	@UiField SimplePanel recipePanel;

	private RechnerActivity presenter;
	private DataController dco;

	public RechnerView() {}
	
	public void setPresenter(RechnerActivity presenter) {
		this.presenter = presenter;
		this.dco = presenter.getDCO();

		topPanel = new TopPanel();
		topPanel.setPresenter(presenter);
		bind();
	}

	private void bind() {
		setWidget(uiBinder.createAndBindUi(this));
		
		// ---------------- Listen to the EventBus ----------------
		presenter.getEventBus().addHandler(UpdateRecipeViewEvent.TYPE,
				new UpdateRecipeViewEventHandler() {
					@Override
					public void onEvent(UpdateRecipeViewEvent event) {
					}
				});
	}

	public SimplePanel getSearchPanel() {
		return searchPanel;
	}

	public SimplePanel getRecipePanel() {
		return recipePanel;
	}

}
