package ch.eaternity.client.ui;

import ch.eaternity.client.RecipeView;
import ch.eaternity.client.Search;
import ch.eaternity.client.TopPanel;
import ch.eaternity.shared.Data;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.LoginInfo;
import ch.eaternity.shared.Recipe;

import com.google.api.gwt.services.urlshortener.shared.Urlshortener;
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
//		void onAddButtonClicked();
//		void onDeleteButtonClicked();
//		void onItemClicked(T clickedItem);
//		void onItemSelected(T selectedItem);
		void goTo(Place place);
		
		TopPanel getTopPanel();
		Search getSearchPanel();

		int getSelectedMonth();

		void addNewRecipe();

		void loadYourRezepte();

		LoginInfo getLoginInfo();

		Urlshortener getUrlShortener();

		void addRezept(Recipe recipe, RecipeView rezeptView);


		void removeRecipe(Recipe recipe);

		void recipeApproval(Recipe recipe, boolean b);

		Data getClientData();
	}
	
	TopPanel getTopPanel();
	Search getSearchPanel();
	
	int getSelectedMonth();
	void showRecipeClone(Recipe recipe);
	void loadYourRechner();
	HandlerRegistration loadAdmin();
	void loadLogin();
	FlexTable getRezeptEditList();
	FlexTable getRezeptList();
	void setSelectedRezept(int i);
	int getSelectedRezept();
	HorizontalPanel getSuggestionPanel();
	ComplexPanel getDragArea();
	void setTitleHTML(String string);
	void styleRezept(int selectedRezept, boolean b);
	void updateSaisonAndMore();
	int AddZutatZumMenu(Ingredient item);
}