package ch.eaternity.client.ui;


import java.util.Iterator;
import java.util.List;

import ch.eaternity.client.ui.HelloView.Presenter;
import ch.eaternity.shared.Commitment;
import ch.eaternity.shared.Recipe;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.place.shared.Place;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;



public class KlimaZmittagViewImpl<T> extends SimpleLayoutPanel implements KlimaZmittagView<T> {
	
	private Presenter listener;
	private String name;
	
	@UiTemplate("KlimaZmittagViewImpl.ui.xml")
	interface KlimaZmittagViewUiBinder extends UiBinder<ScrollPanel, KlimaZmittagViewImpl> {}
	private static KlimaZmittagViewUiBinder uiBinder =
		GWT.create(KlimaZmittagViewUiBinder.class);
	
	
	@UiField FlowPanel menuAvailablePanel;
	@UiField FlowPanel menuSelectionPanel;
	@UiField Anchor addDateAnchor;
	@UiField SimplePanel certificate;
	@UiField Button commitCertificate;
	@UiField Button showCertificate;
	
	public KlimaZmittagViewImpl()
	{
		setWidget(uiBinder.createAndBindUi(this));
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
		
		
		HTMLPanel firstDate = createEmptyDate("Do. 24.September.");
		menuSelectionPanel.add(firstDate);
		
	}

	private HTMLPanel createEmptyDate(String date) {
		HTMLPanel firstDate = new HTMLPanel(date);
		
		firstDate.setHeight("100px");
		firstDate.setWidth("100px");
		
		firstDate.setStyleName("colored-white");

		return firstDate;
	}

	
	@UiHandler("addDateAnchor")
	public void onAddDateAnchorPress(ClickEvent event) {
		HTMLPanel firstDate = createEmptyDate("Neuer Tag.");
		menuSelectionPanel.add(firstDate);
	}
	
	@UiHandler("showCertificate")
	public void onShowButtonPress(ClickEvent event) {
		
		String contentURl;
		if(name != null && name != ""){
			contentURl = GWT.getHostPageBaseURL()+ "certificate.jsp?pid=" + name;
		} else {
			contentURl = "http://www.eaternity.ch/disclaimer-eaternity-rechner";
		}
		
	    Frame frame = new Frame(contentURl);
	    frame.setWidth("100%");
	    int large =  Window.getClientHeight() -100;
	    frame.setHeight(Integer.toString(large-40) + "px");
		certificate.add(frame);

		
		
	}
	
	@UiHandler("commitCertificate")
	public void onCommitButtonPress(ClickEvent event) {
		createNewCommitment();
	}
	
	public void createNewCommitment() {
		Commitment commitment = new Commitment();
		listener.saveCommitment(commitment);
		
	}

	public void goTo(Place place) {
		listener.goTo(place);
	}

	

	@Override
	public void populateRecipes(List<Recipe> recipes) {
		
		Iterator<Recipe> iterator = recipes.iterator();
		while (iterator.hasNext()) {
			showMenu(iterator.next());
		}
		

	}

	private void showMenu(Recipe recipe) {
		
		String image = "";
		if(recipe.getImage() != null) {
			image=  "<img src='" + recipe.getImage().getServingUrl() + "=s80-c' />";
		}
		HTML newMenu = new HTML(" <a href='"+recipe.getShortUrl()+"'>"+recipe.getTitle()+"</a>" + image);
		newMenu.setStyleName("colored-white");
		Anchor addToMenu = new Anchor("Zum Tag hinzufügen.");
		
		VerticalPanel addAll = new VerticalPanel();
		addAll.setStyleName("colored-white");
		
		addAll.add(newMenu);
		addAll.add(addToMenu);
		
	
		menuAvailablePanel.add(addAll);
		
	}

}
