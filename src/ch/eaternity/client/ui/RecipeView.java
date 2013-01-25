package ch.eaternity.client.ui;

import ch.eaternity.client.ClientFactory;
import ch.eaternity.client.DataController;
import ch.eaternity.client.activity.RechnerActivity;
import ch.eaternity.client.place.RechnerRecipeEditPlace;
import ch.eaternity.client.ui.widgets.RecipeEditView;
import ch.eaternity.client.ui.widgets.RecipeView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLTable.Cell;

public class RecipeView extends Composite {
	interface Binder extends UiBinder<Widget, RecipeView> { }
	private static Binder uiBinder = GWT.create(Binder.class);
	
	RechnerActivity presenter;
	ClientFactory clientFactory;
	DataController dco;
	
	public RecipeView(RechnerActivity presenter, ClientFactory clientFactory) {
		this.presenter = presenter;
		this.clientFactory = clientFactory;
		this.dco = presenter.getDCO();
	}

	private void openRecipeEdit() {
		dco.openEditRecipe();
		presenter.goTo(new RechnerRecipeEditPlace("RecipeName clicked"));
	}
	
	@UiHandler("addRecipeButton")
	public void onAddRecipeButtonPress(ClickEvent event) {
		dco.createRecipe();		
	}
	
	// REFACTOR:  correct. change editRecipe in Datacontroller send event maybe
		@UiHandler("rezeptList")
		void onRezeptClicked(ClickEvent event) {
			Cell cell = rezeptList.getCellForEvent(event);
			if (cell != null && selectedRezept != cell.getRowIndex()) {
				
				Widget rezeptViewWidget;
				
				if(selectedRezept > -1){
					rezeptViewWidget = rezeptList.getWidget(selectedRezept, 1);
					RecipeView rezeptViewOld = (RecipeView) rezeptViewWidget;
					rezeptViewOld.isSelected = false;
				}
				
				
				// color the right recipe, and get the selected row index
				styleRezept(selectedRezept, false);
				selectedRezept = cell.getRowIndex();
				styleRezept(selectedRezept, true);
				
				// this is the new recipe
				rezeptViewWidget = rezeptList.getWidget(selectedRezept, 1);
				RecipeView rezeptView = (RecipeView) rezeptViewWidget;
				rezeptView.isSelected = true;
				
				
				if(rezeptEditList.getRowCount() > 0){
				rezeptEditList.removeRow(0);
				}
				if(topDragArea.getWidgetCount() > 0){
					topDragArea.remove(0);
				}
				
				
				
				// put this recipe into the edit panel...
				if(rezeptEditList.getRowCount() == 0){
					rezeptEditList.insertRow(0);
				}
			
				
				rezeptEditView = new RecipeEditView(rezeptView, rezeptView.recipe, this);
				rezeptEditView.setPresenter(presenter);
				
				
				editCoverActivated = false;
				rezeptEditList.setWidget(0, 1, rezeptEditView);
				rezeptEditList.getRowFormatter().setStyleName(0, "recipe");
				
				// //REFACTOR: double 
				rezeptEditView.setRezept(rezeptView.recipe, rezeptView);
				rezeptEditView.showRezept(rezeptEditView.recipe);
				
				
				
				suggestionPanel.clear();
				suggestionPanel.add(new HTML("Es gibt hier noch keinen Vergleich"));
				// rezeptView.updtTopSuggestion();
				
				adjustStickyEditLayout();

			}
		}
	
}
