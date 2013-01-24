package ch.eaternity.client.ui;

import ch.eaternity.client.activity.RechnerActivity;

import com.google.gwt.user.client.ui.Composite;

public class RecipeEdit extends Composite {
	RechnerActivity presenter;
	
	public RecipeEdit(RechnerActivity presenter) {
		this.presenter = presenter;
	}

	/**
	 * 
	 * @return true if recipe has changed since last save, false otherwise
	 */
	public boolean hasChanged() {
		// TODO Auto-generated method stub
		return false;
	}

}
