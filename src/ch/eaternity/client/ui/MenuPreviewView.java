package ch.eaternity.client.ui;


import ch.eaternity.client.place.EaternityRechnerPlace;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
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
		
//		loadContent();
	}

	private void loadContent() {
		// TODO Auto-generated method stub
		
		
		// on loaded
		helloViewImpl.goTo(new EaternityRechnerPlace(name));
	}


	@UiHandler("closeButton")
	void onClickGoodbye(ClickEvent e)
	{	
		hide();
	}



}
