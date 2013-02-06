package ch.eaternity.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.place.shared.Place;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class HelloViewImpl extends SimpleLayoutPanel implements HelloView
{
	private static HelloViewImplUiBinder uiBinder = GWT.create(HelloViewImplUiBinder.class);
	interface HelloViewImplUiBinder extends UiBinder<Widget, HelloViewImpl>{}

	//@UiField HTMLPanel framePanel;
	@UiField HTML statusInfo;
	@UiField Button loginButton;
	@UiField Image logoImage;
	
	private Presenter listener;
	private String name;
	private Frame frame;
	private String loginUrl;
	
	
	public HelloViewImpl()
	{
		setWidget(uiBinder.createAndBindUi(this));
		logoImage.setUrl("/images/logo-eaternity.png");
		logoImage.setSize("300px", "131px");
		
		this.setVisible(false);
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

	@Override
	public void onResize() {
	}
	
	@Override
	public void setName(String name)
	{
		this.name = name;
	}


	@Override
	public void setPresenter(Presenter listener)
	{
		this.listener = listener;
		
	}

	public void goTo(Place place) {
		listener.goTo(place);
	}
}
