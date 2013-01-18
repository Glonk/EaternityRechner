package ch.eaternity.client.ui;

import ch.eaternity.client.DataController;
import ch.eaternity.client.ui.widgets.RecipeView;
import ch.eaternity.client.ui.widgets.Search;
import ch.eaternity.client.ui.widgets.TopPanel;
import ch.eaternity.shared.ClientData;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.LoginInfo;
import ch.eaternity.shared.Recipe;


import com.google.api.gwt.services.urlshortener.shared.Urlshortener;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * View interface. Extends IsWidget so a view impl can easily provide
 * its container widget.
 *
 * @author drfibonacci
 */
public interface EaternityRechnerView<T> extends IsWidget
{



	void setName(String helloName);
	void setPresenter(Presenter<T> presenter);

	public interface Presenter<T>
	{
		DataController getDCO();

		void goTo(Place place);
		
		TopPanel getTopPanel();
		Search getSearchPanel();

		int getSelectedMonth();

		void addNewRecipe();

		LoginInfo getLoginInfo();

		Urlshortener getUrlShortener();

		void addRezept(Recipe recipe, RecipeView rezeptView);

		void removeRecipe(Recipe recipe);
		
		void removeRecipeFromWorkplace(RecipeView recipeToRemove);
		
		void removeAllRecipesFromWorkplace();

		ClientData getClientData();

		EventBus getEventBus();
	}
	
	TopPanel getTopPanel();
	Search getSearchPanel();
	
	void cloneRecipe(Recipe recipe);
	FlexTable getRezeptEditList();
	FlexTable getRezeptList();
	void setSelectedRecipeNumber(int i);
	int getSelectedRecipeNumber();
	HorizontalPanel getSuggestionPanel();
	ComplexPanel getDragArea();
	void setTitleHTML(String string);
	void styleRezept(int selectedRezept, boolean b);
	void updateSaisonAndMore();
	void addOneIngredientToMenu(Ingredient item, RecipeView rezeptView, int grams);
	void setMenuPreviewDialog(MenuPreviewView menuPreviewDialog);
	

	void onResize();
	void adjustStickyEditLayout();
	void setEditCoverActivated(boolean b);
	void closeRecipeEditView();
	RecipeView createNewRecipeView();
	RecipeView getSelectedRecipeView();
	void displayRecipeEditView(RecipeView rezeptView);
}