package ch.eaternity.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;

import ch.eaternity.client.place.EaternityRechnerPlace;
import ch.eaternity.client.place.GoodbyePlace;

public class HelloViewImpl extends DialogBox implements HelloView
{
	private static HelloViewImplUiBinder uiBinder = GWT.create(HelloViewImplUiBinder.class);

	interface HelloViewImplUiBinder extends UiBinder<Widget, HelloViewImpl>
	{
	}

	@UiField SpanElement nameSpan;
	@UiField Anchor startYourEngineLink;
	private Presenter listener;
	private String name;

	public HelloViewImpl()
	{
		setWidget(uiBinder.createAndBindUi(this));
		setAnimationEnabled(true);
		setGlassEnabled(true);
		
		show();
		center();
	}

	@Override
	public void setName(String name)
	{
		this.name = name;
		nameSpan.setInnerText(name);
		setText(name);
	}

	@UiHandler("startYourEngineLink")
	void onClickGoodbye(ClickEvent e)
	{
		listener.goTo(new EaternityRechnerPlace(name));
	}

	@Override
	public void setPresenter(Presenter listener)
	{
		this.listener = listener;
	}
}
