package ch.eaternity.client.ui;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * View interface. Extends IsWidget so a view impl can easily provide
 * its container widget.
 *
 * @author drfibonacci
 */
public interface HelloView extends IsWidget
{
	void setName(String helloName);
	void setPresenter(Presenter listener);

	public interface Presenter
	{
		void goTo(Place place);


		DialogBox getDialogBox();
	}

	//public void loadContent(String contentURL);
	
	//public void removeFrame();
	public void setLoginUrl(String url);
	
	public void setStatusInfo(String info);
	
	public void setButtonText(String text) ;
	
	public void setVisibility(boolean visible);
}
