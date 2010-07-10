package ch.eaternity.client;

import ch.eaternity.client.InfoZutat.Binder;
import ch.eaternity.client.InfoZutat.PassedStyle;
import ch.eaternity.shared.ZutatSpecification;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class InfoZutatDialog extends Composite {
	interface Binder extends UiBinder<Widget, InfoZutatDialog> { }
	private static Binder uiBinder = GWT.create(Binder.class);
	@UiField HTML zutatName;
	@UiField PassedStyle passedStyle;
	
	interface PassedStyle extends CssResource {
		String hinweisPassed();
	}

	public InfoZutatDialog(ZutatSpecification zutatSpec) {
		initWidget(uiBinder.createAndBindUi(this));
		zutatName.setHTML("<h1>"+ zutatSpec.getName() +"</h1>");
		// TODO Auto-generated constructor stub
	}

}
