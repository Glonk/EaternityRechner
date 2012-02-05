package ch.eaternity.client.ui;


import ch.eaternity.client.ClientFactory;
import ch.eaternity.client.place.EaternityRechnerPlace;
import ch.eaternity.client.ui.EaternityRechnerView.Presenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.http.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;



public class MenuPreviewView<T> extends DialogBox{
	interface Binder extends UiBinder<Widget, MenuPreviewView> { }
	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField HTMLPanel previewMenuHtmlPanel;
	@UiField Button closeButton;
//	@UiField ScrollPanel scrollPanel;
	

	private String name;

	public MenuPreviewView() {

		
	}
	
	public void setName(String menuName){
//		setText("Ein leckeres Menu: " +menuName);
		this.name = menuName;
		
		openDialog();
		loadContent();
	}
	
	private DialogBox openDialog() {
		setWidget(binder.createAndBindUi(this));
		setAnimationEnabled(true);
		setGlassEnabled(true);
////		show();
		center();
//		setHTML("bla");
		positionDialog();
		return this;
	}

	
	Frame frame = null;

	private ClientFactory clientFactory;
	void positionDialog(){
		if(this.isShowing()){
			int left = (Window.getClientWidth() - getOffsetWidth()) >> 1;
			//	    int top = (Window.getClientHeight() - getOffsetHeight()) >> 1;
			int top = 20;
			setPopupPosition(Math.max(Window.getScrollLeft() + left, 0), Math.max(
					Window.getScrollTop() + top, 0));
			int large =  Window.getClientHeight() -100;
			//		scrollPanel.setHeight(Integer.toString(large) + "px");
			
			if(frame != null){
			    frame.setHeight(Integer.toString(large-40) + "px");
			}
		}

	}

	private void loadContent() {
		String contentURl;
		if(name != null && name != ""){
			contentURl = GWT.getHostPageBaseURL()+ "view.jsp?pid=" + name;


//		    
//			RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(contentURl));
//
//			try {
//				Request request = builder.sendRequest(null, new RequestCallback() {
//					public void onError(Request request, Throwable exception) {
//						// Couldn't connect to server (could be timeout, SOP violation, etc.)     
//					}
//
//					public void onResponseReceived(Request request, Response response) {
//						if (200 == response.getStatusCode()) {
//							// Process the response in response.getText()
//							HTML content = new HTML(response.getText());
//							previewMenuHtmlPanel.add(content);
//
//							// on loaded
////							helloViewImpl.goTo(new EaternityRechnerPlace(name));
//						} else {
//							// Handle the error.  Can get the status text from response.getStatusText()
//							GWT.log(response.getStatusText());
//						}
//					}       
//				});
//			} catch (RequestException e) {
//				// Couldn't connect to server    
//				GWT.log(e.getLocalizedMessage());
//			}
		} else {
			contentURl = "http://www.eaternity.ch/disclaimer-eaternity-rechner";
		}
		
	    frame = new Frame(contentURl);
	    frame.setWidth("100%");
	    int large =  Window.getClientHeight() -100;
	    frame.setHeight(Integer.toString(large-40) + "px");
	    previewMenuHtmlPanel.add(frame);
		
	}




	
	
	@UiHandler("closeButton")
	void onClickGoodbye(ClickEvent e)
	{	
		clientFactory.getPlaceController().goTo(new EaternityRechnerPlace(name));
		closeButton.setText("Bitte einen Moment warten, der Rechner wird geladen.");
		closeButton.setEnabled(false);
	}

	public void setClientFactory(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}



}
