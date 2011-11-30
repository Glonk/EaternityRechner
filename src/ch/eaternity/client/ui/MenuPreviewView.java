package ch.eaternity.client.ui;


import ch.eaternity.client.place.EaternityRechnerPlace;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.http.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;



public class MenuPreviewView extends DialogBox{
	interface Binder extends UiBinder<Widget, MenuPreviewView> { }
	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField HTMLPanel previewMenuHtmlPanel;
	@UiField Button closeButton;
	
	private HelloView.Presenter listener;
	public void setListener(HelloView.Presenter listener) {
		this.listener = listener;
	}

	private String name;

	private HelloViewImpl helloViewImpl;
	
	public MenuPreviewView(HelloViewImpl helloViewImpl) {
		this.helloViewImpl = helloViewImpl;
		openDialog();
	}
	
	public void setName(String menuName){
		setText("Ein leckeres Menu: " +menuName);
		this.name = menuName;
	}
	
	private void openDialog() {
		setWidget(binder.createAndBindUi(this));
		setAnimationEnabled(true);
		setGlassEnabled(true);
////		show();
		center();
		setHTML("bla");
		
		loadContent();
	}


	
	private void loadContent() {

		if(name != null){

			String contentURl = GWT.getModuleBaseURL() + "view.jsp?pid=" + name;
			RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(contentURl));

			try {
				Request request = builder.sendRequest(null, new RequestCallback() {
					public void onError(Request request, Throwable exception) {
						// Couldn't connect to server (could be timeout, SOP violation, etc.)     
					}

					public void onResponseReceived(Request request, Response response) {
						if (200 == response.getStatusCode()) {
							// Process the response in response.getText()
							previewMenuHtmlPanel.add(new HTML(response.getText()));

							// on loaded
							helloViewImpl.goTo(new EaternityRechnerPlace(name));
						} else {
							// Handle the error.  Can get the status text from response.getStatusText()
						}
					}       
				});
			} catch (RequestException e) {
				// Couldn't connect to server        
			}
		}
		
		
	}




	
	
	@UiHandler("closeButton")
	void onClickGoodbye(ClickEvent e)
	{	
		hide();
	}



}
