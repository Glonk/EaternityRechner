package ch.eaternity.client.ui;

import ch.eaternity.client.DataController;
import ch.eaternity.client.activity.RechnerActivity;
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
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class RechnerView extends SimpleLayoutPanel {
	@UiTemplate("RechnerView.ui.xml")
	interface Binder extends UiBinder<ScrollPanel, RechnerView> {
	}

	private static Binder uiBinder = GWT.create(Binder.class);

	interface SelectionStyle extends CssResource {
		String selectedRezept();
	}

	// ----------------- Class Variables -------------------
	@UiField ScrollPanel workspace;
	@UiField TopPanel topPanel;
	@UiField SimplePanel searchPanel;
	@UiField SimplePanel recipePanel;
	@UiField StickyTop stickyTop;
	@UiField HTMLPanel sidebar;

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
		presenter.getEventBus().addHandler(UpdateRecipeViewEvent.TYPE,
				new UpdateRecipeViewEventHandler() {
					@Override
					public void onEvent(UpdateRecipeViewEvent event) {
					}
				});
	}
	
	
	// ---------------- Sidebar stick to top ----------------
	
	
	/*
	 * this does not render properly
	 * this is the same as illustraded here with the fixed-top navbar setting: http://gwtbootstrap.github.io/gwt-bootstrap/apidocs/com/github/gwtbootstrap/client/ui/Navbar.html
		 
	@UiHandler("workspace")
    public void onScroll(ScrollEvent event) { 
	
		if(workspace.getVerticalScrollPosition() > 160) {
			
			sidebar.addStyleName(stickyTop.stickToTop());
		} else {
			sidebar.removeStyleName(stickyTop.stickToTop());
		}
		
    }
	*/
	

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
