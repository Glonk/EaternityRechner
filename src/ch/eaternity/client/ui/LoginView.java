package ch.eaternity.client.ui;

import ch.eaternity.client.activity.LoginActivity;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.Modal;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.place.shared.Place;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;


public class LoginView extends SimpleLayoutPanel
{
	interface Binder extends UiBinder<Widget, LoginView> {}

	private static Binder uiBinder = GWT.create(Binder.class);

	//@UiField HTMLPanel framePanel;
	@UiField HTML statusInfo;
	@UiField Button loginButton;
	// @UiField Modal modal;
	private LoginActivity presenter;
	private String name;
	private Frame frame;
	private String loginUrl;



	public LoginView()
	{
		setWidget(uiBinder.createAndBindUi(this));
		this.setVisible(false);
		
		/* The Login Info could be a Modal box, so disappearing and showing the calc would be fancy (toggle on loading complete)
		modal.setVisible(false);
		modal.setBackdrop(null);
		modal.setAnimation(true);
		modal.toggle();
		*/
		
		
	}

	/*
	public void loadContent(String contentURL) {
		
	    frame = new Frame(contentURL);
	    frame.setWidth("100%");
	    int large =  Window.getClientHeight() -100;
	    frame.setHeight(Integer.toString(large-40) + "px");
	    framePanel.add(frame);
		
	}
	
	public void removeFrame() {
		if (frame != null)
			framePanel.remove(frame);
	}
	*/

	public void setVisibility(boolean visible) {
		this.setVisible(visible);
	}

	public void setLoginUrl(String url) {
		loginUrl = url;
	}

	public void setStatusInfo(String info) {
		statusInfo.setHTML(info);
	}

	public void setButtonText(String text) {
		loginButton.setText(text);
	}

	@UiHandler("loginButton")
	public void onClick(ClickEvent event) {
		Window.open(loginUrl, "_self", "");
	}

	@UiHandler("applyButton")
	public void onApplyClick(ClickEvent event) {
		Window.open("https://docs.google.com/forms/d/1dxxJ7cEpgVajUtIETZ5dQv3Kf-eqiZF5EGeo0KkHXho", "_blank", "");
	}

	public void onResize() {
	}


	public void setName(String name)
	{
		this.name = name;
	}


	public void setPresenter(LoginActivity listener)
	{
		this.presenter = listener;

	}

	public void goTo(Place place) {
		presenter.goTo(place);
	}
}
