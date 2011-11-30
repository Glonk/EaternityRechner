package ch.eaternity.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.place.shared.Place;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import ch.eaternity.client.KitchenDialog;
import ch.eaternity.client.place.EaternityRechnerPlace;
import ch.eaternity.client.place.GoodbyePlace;

public class HelloViewImpl extends SimpleLayoutPanel implements HelloView
{
	private static HelloViewImplUiBinder uiBinder = GWT.create(HelloViewImplUiBinder.class);

	interface HelloViewImplUiBinder extends UiBinder<Widget, HelloViewImpl>
	{
	}


	private Presenter listener;
	private String name;
	private MenuPreviewView menuPreviewDialog;
	
	public HelloViewImpl()
	{
		setWidget(uiBinder.createAndBindUi(this));
//		menuPreviewDialog.show();
	}
	
	public void setMenuPreviewDialog(MenuPreviewView menuPreviewDialog){
		this.menuPreviewDialog = menuPreviewDialog;
		menuPreviewDialog.setName(name);
//		menuPreviewDialog.setListener(listener);
	}

	@Override
	public void onResize() {
		if(menuPreviewDialog != null){
			menuPreviewDialog.center();
			menuPreviewDialog.positionDialog();
		}
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
