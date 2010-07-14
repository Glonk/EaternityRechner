package ch.eaternity.client;

import java.util.Date;

import ch.eaternity.client.ZutatDetails.SelectionStyle;
import ch.eaternity.shared.Zutat;
import ch.eaternity.shared.ZutatSpecification;
import ch.eaternity.shared.Zutat.Herkuenfte;
import ch.eaternity.shared.Zutat.Produktionen;
import ch.eaternity.shared.Zutat.Transportmittel;
import ch.eaternity.shared.Zutat.Zustaende;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class InfoZutatDialog extends Composite {
	interface Binder extends UiBinder<Widget, InfoZutatDialog> { }
	private static Binder uiBinder = GWT.create(Binder.class);
	@UiField HTML zutatName;
	@UiField PassedStyle passedStyle;
	@UiField Label hinweisPanel;
	
	static double distance = 0;

	@UiField SelectionStyle selectionStyle;
//	@UiField DisclosurePanel Labels;
	@UiField ListBox Herkunft;
	@UiField ToggleButton flugzeug;
	@UiField ToggleButton bio;
	@UiField ToggleButton treibhaus;
	@UiField ToggleButton tiefgekühlt;
	@UiField ToggleButton eingemacht;
	@UiField ToggleButton getrocknet;
	@UiField Label saison;
	@UiField HTMLPanel zustandHTML;
	@UiField HTMLPanel produktionHTML;
	@UiField HTMLPanel herkunftHTML;
	@UiField HTMLPanel saisonHTML;
	
	interface SelectionStyle extends CssResource {
		String selectedBlob();
	}
	
	interface PassedStyle extends CssResource {
		String hinweisPassed();
	}
	
	void styleHinweis( boolean selected) {
		
		String style = passedStyle.hinweisPassed();

		if (selected) {
			hinweisPanel.addStyleName(style);
		} else {
			hinweisPanel.removeStyleName(style);
		}
	
	}
	public void stylePanel(boolean onOff) {
		if (onOff) {
//			infoBox.setHeight("500px");
		} else {
			
		}
	
}

	public InfoZutatDialog(ZutatSpecification zutatSpec) {
		initWidget(uiBinder.createAndBindUi(this));
		zutatName.setHTML("<h1>"+ zutatSpec.getName() +"</h1>");
		// TODO Auto-generated constructor stub
	}

	
	
	public void setValues(ZutatSpecification zutatSpec, Zutat zutat){
		
		InfoZutat.zutatMenge.setText(Integer.toString(zutatSpec.getMengeGramm()));
		
		Herkunft.clear();
		for(  Herkuenfte herkunft: zutat.getHerkuenfte() ){
			Herkunft.addItem(herkunft.name());
		}
		Herkunft.setSelectedIndex(zutat.getHerkuenfte().indexOf(zutatSpec.getHerkunft()));
		
		Produktionen produktion = zutatSpec.getProduktion();
		bio.setDown(false);
		treibhaus.setDown(false);
		switch (produktion){
			case biologisch: bio.setDown(true);
			case Treibhaus: treibhaus.setDown(true);
		}
			
		Zustaende zustand = zutatSpec.getZustand();
		tiefgekühlt.setDown(false);
		eingemacht.setDown(false);
		getrocknet.setDown(false);
		switch (zustand){
			case tiefgekühlt: tiefgekühlt.setDown(true);
			case eingemacht: eingemacht.setDown(true);
			case getrocknet: getrocknet.setDown(true);
		}
		
		Transportmittel transport = zutatSpec.getTransportmittel();
		flugzeug.setDown(false);
		switch (transport){
			case Flugzeug: flugzeug.setDown(true);
		}
		
//		ZutatSpecification zutatSpecification = new ZutatSpecification(zutat.getId(), zutat.getSymbol(),
//				 new Date(),zutat.getStdZustand(), zutat.getStdProduktion(), 
//				zutat.getStdTransportmittel());
//		zutatSpecification.setHerkunft(zutat.getStdHerkunft());
//		zutatSpecification.setMengeGramm(zutat.getStdMengeGramm());
//		zutatSpecification.setSeason(zutat.getStdStartSeason(), zutat.getStdStopSeason());
//		zutatSpecification.setNormalCO2Value(zutat.getCO2eWert());
		
//		TODO check if there is something to do
//		InfoZutat.zutat = zutatSpec;
		updateSaison();

		
	}
	

	public void updateSaison() {
		
		Date date = DateTimeFormat.getFormat("MM").parse(Integer.toString(TopPanel.Monate.getSelectedIndex()+1));
		// In Tagen
//		String test = InfoZutat.zutat.getStartSeason();
		Date dateStart = DateTimeFormat.getFormat("dd.MM").parse( InfoZutat.zutat.getStartSeason() );		
		Date dateStop = DateTimeFormat.getFormat("dd.MM").parse( InfoZutat.zutat.getStopSeason() );
		
		if(		dateStart.before(dateStop)  && date.after(dateStart) && date.before(dateStop) ||
				dateStart.after(dateStop) && !( date.before(dateStart) && date.after(dateStop)  ) ){
			
			saison.setText("Diese Zutat hat Saison");
			styleLabel(saisonHTML,true);
			
			InfoZutat.styleHinweis(false);
			InfoZutat.hinweisPanel.setText("Angaben sind koherent.");
			
		} else {
			saison.setText("Diese Zutat hat keine Saison");
			styleLabel(saisonHTML,false);
			
			// unvollständig:
			
			InfoZutat.styleHinweis(true);
			InfoZutat.hinweisPanel.setText("Angaben sind unvollständig.");
		}
		
		//TODO uncomment this:
//		ZutatSpecification zutatSpec = getZutatSpecification(InfoZutat.zutat) ;
		
		//TODO uncomment this:
		
//		InfoZutat.updateZutatCO2(zutatSpec,EaternityRechner.selectedRow);
		
	}

	

	@UiHandler("bio")
	void onBioButtonClick(ClickEvent event) {
		treibhaus.setDown(false);
//		ZutatSpecification zutatSpec = getZutatSpecification(InfoZutat.zutat) ;
		//TODO uncomment this:
		//InfoZutat.updateZutatCO2(zutatSpec, EaternityRechner.selectedRow);
	}
	@UiHandler("treibhaus")
	void onTreibhausButtonClick(ClickEvent event) {
		bio.setDown(false);
//		ZutatSpecification zutatSpec = getZutatSpecification(InfoZutat.zutat) ;
		//TODO uncomment this:
		//InfoZutat.updateZutatCO2(zutatSpec, EaternityRechner.selectedRow);
	}

	@UiHandler("tiefgekühlt")
	void onTiefButtonClick(ClickEvent event) {
		getrocknet.setDown(false);
		eingemacht.setDown(false);
//		ZutatSpecification zutatSpec = getZutatSpecification(InfoZutat.zutat) ;
		//TODO uncomment this:
		//InfoZutat.updateZutatCO2(zutatSpec, EaternityRechner.selectedRow);
	}
	@UiHandler("getrocknet")
	void onTrockButtonClick(ClickEvent event) {
		eingemacht.setDown(false);
		tiefgekühlt.setDown(false);
//		ZutatSpecification zutatSpec = getZutatSpecification(InfoZutat.zutat) ;
		//TODO uncomment this:
		//InfoZutat.updateZutatCO2(zutatSpec, EaternityRechner.selectedRow);
	}
	@UiHandler("eingemacht")
	void onEingButtonClick(ClickEvent event) {
		getrocknet.setDown(false);
		tiefgekühlt.setDown(false);
//		ZutatSpecification zutatSpec = getZutatSpecification(InfoZutat.zutat) ;
		//TODO uncomment this:
		//InfoZutat.updateZutatCO2(zutatSpec, EaternityRechner.selectedRow);
	}
	
	@UiHandler("flugzeug")
	void onFlugButtonClick(ClickEvent event) {
//		ZutatSpecification zutatSpec = getZutatSpecification(InfoZutat.zutat) ;
		//TODO uncomment this:
		//InfoZutat.updateZutatCO2(zutatSpec, EaternityRechner.selectedRow);
	}
	
	@UiHandler("Herkunft")
	void onChange(ChangeEvent event) {
		String location;
		//		getDistance(Herkunft.getItemText(Herkunft.getSelectedIndex()),TopPanel.clientLocation.getText());
		if(TopPanel.currentLocation != null){
			location = TopPanel.currentLocation.getAddress();
		} else {
			location = TopPanel.clientLocation.getText();
		}
		
//		SimpleDirectionsDemo(Herkunft.getItemText(Herkunft.getSelectedIndex()),location, null);
//		ZutatSpecification zutatSpec = getZutatSpecification(InfoZutat.zutat) ;
		//TODO uncomment this:
		//InfoZutat.updateZutatCO2(zutatSpec, EaternityRechner.selectedRow);
		
	}
	
	
	private void styleLabel( HTMLPanel panel, boolean selected) {
		
		String style = selectionStyle.selectedBlob();

		if (selected) {
			panel.addStyleName(style);
		} else {
			panel.removeStyleName(style);
		}
	
}
}
