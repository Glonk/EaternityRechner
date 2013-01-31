
package ch.eaternity.client.ui;

import ch.eaternity.client.DataController;
import ch.eaternity.client.activity.RechnerActivity;
import ch.eaternity.client.events.UpdateRecipeViewEvent;
import ch.eaternity.client.events.UpdateRecipeViewEventHandler;
import ch.eaternity.client.place.RechnerRecipeEditPlace;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author aurelianjaggi
 *
 */
public class SearchIngredients extends Composite {
	interface Binder extends UiBinder<Widget, SearchIngredients> { }
	private static Binder uiBinder = GWT.create(Binder.class);
	
	// ---------------------- User Interface Elements --------------
	
	
	// ---------------------- Class Variables ----------------------
	
	RechnerActivity presenter;
	DataController dco;
	
	// ---------------------- public Methods -----------------------
	
	public SearchIngredients() {
		bind();
	}
	

	private void bind() {
		initWidget(uiBinder.createAndBindUi(this));
		
		//  Listen to the EventBus 
		presenter.getEventBus().addHandler(UpdateRecipeViewEvent.TYPE,
				new UpdateRecipeViewEventHandler() {
					@Override
					public void onEvent(UpdateRecipeViewEvent event) {
					}
				});
	}
	
	public void setPresenter(RechnerActivity presenter) {
		this.presenter = presenter;
		this.dco = presenter.getDCO();
	}
	
	// ---------------------- UI Handlers ----------------------
	
	
	/**
	 * @return true if recipe has changed since last save, false otherwise
	 */

	
	// ---------------------- private Methods ----------------------
	
}



