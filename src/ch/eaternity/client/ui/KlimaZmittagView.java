package ch.eaternity.client.ui;


import java.util.List;

import ch.eaternity.shared.Commitment;
import ch.eaternity.shared.Recipe;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.IsWidget;

public interface KlimaZmittagView<T>  extends IsWidget{

	void setName(String helloName);
	void setPresenter(Presenter<T> presenter);

	public interface Presenter<T>
	{

			void goTo(Place place);

			void saveCommitment(Commitment commitment);


	}

	void populateRecipes(List<Recipe> recipes);


	
	

}
