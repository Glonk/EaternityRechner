package ch.eaternity.client.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.ListIterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLTable.Cell;

import ch.eaternity.client.DataController;
import ch.eaternity.client.events.KitchenChangedEvent;
import ch.eaternity.client.events.KitchenChangedEventHandler;
import ch.eaternity.client.events.RecipeAddedEvent;
import ch.eaternity.client.events.RecipeAddedEventHandler;
import ch.eaternity.client.place.GoodbyePlace;
import ch.eaternity.client.ui.EaternityRechnerView.Presenter;
import ch.eaternity.client.ui.widgets.ImageOverlay;
import ch.eaternity.client.ui.widgets.IngredientsDialog;
import ch.eaternity.client.ui.widgets.RecipeEditView;
import ch.eaternity.client.ui.widgets.RecipeView;
import ch.eaternity.client.ui.widgets.Search;
import ch.eaternity.client.ui.widgets.TopPanel;
import ch.eaternity.shared.DeviceSpecification;
import ch.eaternity.shared.Extraction;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.IngredientSpecification;
import ch.eaternity.shared.LoginInfo;
import ch.eaternity.shared.Workgroup;
import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.SingleDistance;

public class EaternityRechnerViewImpl<T> extends SimpleLayoutPanel implements EaternityRechnerView<T>
{
	@UiTemplate("EaternityRechnerViewImpl.ui.xml")
	interface EaternityRechnerViewUiBinder extends UiBinder<DockLayoutPanel, EaternityRechnerViewImpl> {}
	private static EaternityRechnerViewUiBinder uiBinder =
		GWT.create(EaternityRechnerViewUiBinder.class);


	interface SelectionStyle extends CssResource {
		String selectedRezept();
	}


	@UiField TopPanel topPanel;
	@UiField Search searchPanel;  

	@UiField FlexTable rezeptList;
	@UiField FlexTable rezeptEditList;
	@UiField HTML titleHTML;
	@UiField DockLayoutPanel topSticky;
	@UiField HTMLPanel panelNorth;
	@UiField AbsolutePanel topDragArea;
	@UiField HTMLPanel topOverflowArea;

	@UiField ScrollPanel scrollWorkspace;
	@UiField Button addRezeptButton;
	@UiField SelectionStyle selectionStyle;
	@UiField HorizontalPanel suggestionPanel;

	// ----------------- Class Variables ------------------- 

	static int selectedRezept = -1;

	private HandlerRegistration adminHandler;
	private HandlerRegistration ingredientHandler;
	static String styleNameOverlap = "overlap";

	private Presenter<T> presenter;
	private DataController dco;
	private String name;
	
	static RecipeEditView rezeptEditView;

	public EaternityRechnerViewImpl()
	{
		setWidget(uiBinder.createAndBindUi(this));
		// Get rid of scrollbars, and clear out the window's built-in margin,
		// because we want to take advantage of the entire client area.
		Window.enableScrolling(false);
		Window.setMargin("0px");


		// TODO Move cursor focus to the Search box.
		// TODO adjust this with something that makes more sense / what would that be? 1024 width...
		rezeptList.getColumnFormatter().setWidth(1, "750px");
		rezeptEditList.getColumnFormatter().setWidth(1, "750px");

	
	    Element topElem = topSticky.getWidgetContainerElement(panelNorth);
	    topElem.getStyle().setZIndex(2);
	    topElem.getStyle().setOverflow(Overflow.VISIBLE);
		
		//TODO Reactivate for later use
		suggestionPanel.setVisible(false);
	}
	
	private void bind() {
		// ---------------- Listen to the EventBus ----------------
		presenter.getEventBus().addHandler(RecipeAddedEvent.TYPE, new RecipeAddedEventHandler() {
			@Override
			public void onEvent(RecipeAddedEvent event) {
				RecipeView rezeptView = createNewRecipeView();
				displayRecipeEditView(rezeptView);
			    adjustStickyEditLayout();
			}
		});
	}
	
	@Override
	public void setPresenter(Presenter<T> presenter)
	{
		this.presenter = presenter;
		this.dco = presenter.getDCO();
		searchPanel.setPresenter(presenter);
		topPanel.setPresenter(presenter);
	}

	
	@Override
	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public TopPanel getTopPanel() {
		return topPanel;
	}
	
	@Override
	public Search getSearchPanel() {
		return searchPanel;
	}

	@Override
	public FlexTable getRezeptList() {
		return rezeptList;
	}

	@Override
	public FlexTable getRezeptEditList() {
		return rezeptEditList;
	}
	
	// REFACTOR:  Correct
	@UiHandler("scrollWorkspace")
    public void onScroll(ScrollEvent event) { 
		// here we still have an error, when the recipes differ in size...
		adjustStickyEditLayout();
    }

	// some local variables for the scrolling behavior
	public boolean editCoverActivated = false;
	int displayHeight = 120;
	private MenuPreviewView menuPreview;
	private MenuPreviewView menuPreviewDialog;
	
	HTML spaceholder = new HTML();
	Widget recipeEditObject;
	Integer saveHeight;
	
	public void setEditCoverActivated(boolean b){
		editCoverActivated = b;
	}

	// REFACTOR:  What is this for 
	public void adjustStickyEditLayout() {
//		titleHTML.setHTML("EditView: " + Integer.toString(rezeptEditView.getOffsetHeight()) + " scroll: " + Integer.toString(scrollWorkspace.getVerticalScrollPosition()));

		// all this procedure is only relevant, if the recipe is open
		// and this is the case, when a recipe is selected!
		if(selectedRezept != -1){

			if(!editCoverActivated && (rezeptEditView.getOffsetHeight() < (scrollWorkspace.getVerticalScrollPosition()+displayHeight))){

				recipeEditObject = rezeptEditView.dragArea.getWidget(0);
				saveHeight = rezeptEditView.getOffsetHeight();

				spaceholder.setHTML("<div style='height: " + Integer.toString(recipeEditObject.getOffsetHeight()) + "px;width:764px'></div>");

				//			rezeptEditView.dragArea.setHeight(Integer.toString(recipeEditObject.getOffsetHeight()));

				rezeptEditView.dragArea.remove(recipeEditObject);
				rezeptEditView.dragArea.add(spaceholder);


				topDragArea.add(recipeEditObject);
				panelNorth.setHeight("142px");
				topOverflowArea.setHeight("120px");

				editCoverActivated = true;

			} 

			if(editCoverActivated && (saveHeight >= (scrollWorkspace.getVerticalScrollPosition()+displayHeight))){
				Widget recipeEditObject = topDragArea.getWidget(0);
				//			topDragArea.add(new HTML("<div style='height: " + recipeEditObject.getOffsetHeight() + "px'></div>"));
				topDragArea.remove(recipeEditObject);
				rezeptEditView.dragArea.add(recipeEditObject);
				rezeptEditView.dragArea.remove(spaceholder);
				panelNorth.setHeight("22px");
				topOverflowArea.setHeight("0px");
				editCoverActivated = false;
			}

		}
	} 

	@UiHandler("addRezeptButton")
	public void onAddRezeptButtonPress(ClickEvent event) {
		dco.createRecipe();		
	}
	
	//REFACTOR: Recipe included, DataController, Event send - DONE
	public void cloneRecipe(Recipe recipe) {
		// This is basically right now a clone procedure!
		// which is okay, if you don't own that recipe already...
		// otherwise you would want to edit the old one (or at least signal the 
		// clone procedure by pressing a "Duplicate this Menu" Button.
		
		// this should actually be in the activity class
		
		final RecipeView rezeptView = createNewRecipeView();
	
		// call copy constructor
		rezeptView.recipe = new Recipe(recipe);
		
		displayRecipeEditView(rezeptView);
	    adjustStickyEditLayout();
	}

	//REFACTOR: correct, kommuniziert mit seinen Views
	public RecipeView createNewRecipeView() {
		// unstyle the old recipe
		styleRezept(selectedRezept, false);
		
		
		Recipe newRezept = new Recipe();
		final RecipeView rezeptView = new RecipeView(newRezept, this);
		rezeptView.setPresenter(presenter);
		
		selectedRezept = 0;
		
//		This is a new recipe, so we add it to the list
		rezeptList.insertRow(0);
		

			
		rezeptList.setWidget(selectedRezept, 1, rezeptView);
		rezeptList.getRowFormatter().setStyleName(0, "recipe");
		styleRezept(selectedRezept, true);
		
		return rezeptView;
	
		
	}
	
	// REFACTOR: Logik + Listeners in activity, darstellung selber in view

	// REFACTOR: correct, should listen to eventbus addRecipe
	public void displayRecipeEditView(final RecipeView rezeptView) {
		// recipe edit view procedure 
		if(rezeptEditList.getRowCount() == 0){
			rezeptEditList.insertRow(0);
		}

		rezeptEditView = new RecipeEditView( rezeptView, rezeptView.recipe,this);
		rezeptEditView.setPresenter(presenter);
		rezeptEditView.showRezept(rezeptView.recipe);
		
		rezeptEditList.setWidget(0, 1, rezeptEditView);
		
		rezeptEditList.getRowFormatter().setStyleName(0, "recipe");
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
	

	
//REFACTOR: Event MonthChanged
	public void updateSaisonAndMore() {

		rezeptEditView.updateSaison();
		for(IngredientSpecification zutat : rezeptEditView.recipe.getZutaten()){
			rezeptEditView.changeIcons(rezeptEditView.recipe.getZutaten().indexOf(zutat), zutat);
		}
		
		for( Widget rezeptViewWidget : rezeptList){
			RecipeView rezeptView = (RecipeView) rezeptViewWidget;
//			rezeptView.updateSaison();
			for(IngredientSpecification zutat : rezeptView.recipe.getZutaten()){
//				rezeptView.changeIcons(rezeptView.recipe.getZutaten().indexOf(zutat), zutat);
			}
		}
	}
	
	//REFACTOR: correct
	public void styleRezept(int row, boolean selected) {
		if (row != -1) {
			String style = selectionStyle.selectedRezept();

			if (selected) {
				// color the recipe
				rezeptList.getRowFormatter().addStyleName(row, style);
				// TODO maybe it makes sense to color even more elements in here
			} else {
				rezeptList.getRowFormatter().removeStyleName(row, style);
			}
		}
	}
	


	@Override
	public void setSelectedRecipeNumber(int rezeptPositionInList) {
		this.selectedRezept = rezeptPositionInList;
		
	}

	@Override
	public int getSelectedRecipeNumber() {
		return selectedRezept;
	}
	

	@Override
	public HorizontalPanel getSuggestionPanel() {
		return suggestionPanel;
	}

	@Override
	public AbsolutePanel getDragArea() {
		return topDragArea;
	}

	@Override
	public void setTitleHTML(String string) {
		titleHTML.setHTML(string);
	}

	@Override
	public void setMenuPreviewDialog(MenuPreviewView menuPreviewDialog){
		this.menuPreviewDialog = menuPreviewDialog;
//		menuPreviewDialog.setName(name);
	}

	@Override
	public void onResize() {
		// this is not getting called
		if(menuPreviewDialog != null){
//			menuPreviewDialog.center();
			menuPreviewDialog.positionDialog();
		}
	}

	
	//REFACTOR: correct
	@Override
	public void closeRecipeEditView() {

		//TODO we need to cover the case when the topCoverView is activated!!!
		if(editCoverActivated){
			
			// this is a replicate from the adjustStickyEdit Function!
			Widget recipeEditObject = topDragArea.getWidget(0);
			topDragArea.remove(recipeEditObject);
			rezeptEditView.dragArea.remove(spaceholder);
			panelNorth.setHeight("22px");
			topOverflowArea.setHeight("0px");
			editCoverActivated = false;

		} 
		else {
			getRezeptEditList().remove(rezeptEditView);
			if(getDragArea().getWidgetCount() > 0){
				getDragArea().remove(0);
			}
		}
		styleRezept(getSelectedRecipeNumber(), false);
		setSelectedRecipeNumber(-1);
		getSuggestionPanel().clear();

		setTitleHTML("Sie bearbeiten soeben kein Menu.");
		
	}

	@Override
	public RecipeView<?> getSelectedRecipeView() {
		RecipeView<?> recipeView = (RecipeView<?>) rezeptList.getWidget(selectedRezept,1);
		return recipeView;
	}




}
