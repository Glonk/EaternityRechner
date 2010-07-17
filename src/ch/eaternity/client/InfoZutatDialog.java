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
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
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
	private ZutatSpecification zutatSpec;
	private int selectedRow;
	private FlexTable menuTable;
	
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

	public InfoZutatDialog(ZutatSpecification zutatSpec, Zutat zutat, TextBox amount, FlexTable menuTable, int selectedRow) {
		initWidget(uiBinder.createAndBindUi(this));
		zutatName.setHTML("<h1>"+ zutatSpec.getName() +"</h1>");
		// TODO Auto-generated constructor stub
		this.setZutatSpec(zutatSpec);
		this.setSelectedRow(selectedRow);
		this.menuTable = menuTable;
		setValues( zutat);
	}

	
	
	public void setValues( Zutat zutat){
		
//		InfoZutat.zutatMenge.setText(Integer.toString(zutatSpec.getMengeGramm()));
		
		Herkunft.clear();
		for(  Herkuenfte herkunft: zutat.getHerkuenfte() ){
			Herkunft.addItem(herkunft.name());
		}
		Herkunft.setSelectedIndex(zutat.getHerkuenfte().indexOf(zutatSpec.getHerkunft()));
		
		Produktionen produktion = zutatSpec.getProduktion();
		bio.setDown(false);
		treibhaus.setDown(false);
		switch (produktion){
			case biologisch: bio.setDown(true);break;
			case Treibhaus: treibhaus.setDown(true);
		}
			
		Zustaende zustand = zutatSpec.getZustand();
		tiefgekühlt.setDown(false);
		eingemacht.setDown(false);
		getrocknet.setDown(false);
		switch (zustand){
			case tiefgekühlt: tiefgekühlt.setDown(true);break;
			case eingemacht: eingemacht.setDown(true);break;
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
		updateSaison(zutatSpec);

		
	}
	

	public void updateSaison(ZutatSpecification zutatSpec) {
		
		Date date = DateTimeFormat.getFormat("MM").parse(Integer.toString(TopPanel.Monate.getSelectedIndex()+1));
		// In Tagen
//		String test = InfoZutat.zutat.getStartSeason();
		Date dateStart = DateTimeFormat.getFormat("dd.MM").parse( zutatSpec.getStartSeason() );		
		Date dateStop = DateTimeFormat.getFormat("dd.MM").parse( zutatSpec.getStopSeason() );
		
		if(		dateStart.before(dateStop)  && date.after(dateStart) && date.before(dateStop) ||
				dateStart.after(dateStop) && !( date.before(dateStart) && date.after(dateStop)  ) ){
			
			saison.setText("Diese Zutat hat Saison");
			styleLabel(saisonHTML,true);
			
			styleHinweis(false);
			hinweisPanel.setText("Angaben sind koherent.");
			
		} else {
			saison.setText("Diese Zutat hat keine Saison");
			styleLabel(saisonHTML,false);
			
			// unvollständig:
			
			styleHinweis(true);
			hinweisPanel.setText("Angaben sind unvollständig.");
		}
		
		//TODO uncomment this:
//		ZutatSpecification zutatSpec = getZutatSpecification(InfoZutat.zutat) ;
		
		//TODO uncomment this:
		
//		InfoZutat.updateZutatCO2(zutatSpec,EaternityRechner.selectedRow);
		
	}

	

	@UiHandler("bio")
	void onBioButtonClick(ClickEvent event) {
		treibhaus.setDown(false);
		if(!bio.isDown()){
			zutatSpec.setProduktion(Produktionen.konventionell);
		} else {
			zutatSpec.setProduktion(Produktionen.biologisch);
		}
		updateZutatCO2();
		
		
	}
	@UiHandler("treibhaus")
	void onTreibhausButtonClick(ClickEvent event) {
		bio.setDown(false);
		if(!treibhaus.isDown()){
			zutatSpec.setProduktion(Produktionen.konventionell);
		} else {
			zutatSpec.setProduktion(Produktionen.Treibhaus);
		}
		updateZutatCO2();
	}

	@UiHandler("tiefgekühlt")
	void onTiefButtonClick(ClickEvent event) {
		getrocknet.setDown(false);
		eingemacht.setDown(false);

		if(!tiefgekühlt.isDown()){
			zutatSpec.setZustand(Zustaende.frisch);
		} else {
			zutatSpec.setZustand(Zustaende.tiefgekühlt);
		}
		updateZutatCO2();
	}
	@UiHandler("getrocknet")
	void onTrockButtonClick(ClickEvent event) {
		eingemacht.setDown(false);
		tiefgekühlt.setDown(false);

		if(!getrocknet.isDown()){
			zutatSpec.setZustand(Zustaende.frisch);
		} else {
			zutatSpec.setZustand(Zustaende.getrocknet);
		}
		updateZutatCO2();
	}
	@UiHandler("eingemacht")
	void onEingButtonClick(ClickEvent event) {
		getrocknet.setDown(false);
		tiefgekühlt.setDown(false);

		if(!eingemacht.isDown()){
			zutatSpec.setZustand(Zustaende.frisch);
		} else {
			zutatSpec.setZustand(Zustaende.eingemacht);
		}
		updateZutatCO2();
	}
	
	@UiHandler("flugzeug")
	void onFlugButtonClick(ClickEvent event) {
		if(!flugzeug.isDown()){
			zutatSpec.setTransportmittel(Transportmittel.LKW);
		} else {
			zutatSpec.setTransportmittel(Transportmittel.Flugzeug);
		}
		updateZutatCO2();
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
	
	//TODO here comes all the CO2 Logic
	public void updateZutatCO2(){
		String formatted = NumberFormat.getFormat("##").format( zutatSpec.getCalculatedCO2Value() );
//		valueLabel.setText(formatted + "g CO2-Äquivalent");
		if(selectedRow != -1){
//			if(EaternityRechner.zutatImMenu.contains(zutat)){
//				EaternityRechner.zutatImMenu.set(EaternityRechner.zutatImMenu.indexOf(zutat), zutat);
//				
				menuTable.setText(selectedRow, 4, ": ca. "+formatted + "g CO2-Äquivalent");
//			}
			//TODO uncomment this:
			// EaternityRechner.MenuTable.setText(row, 4, ": ca. "+formatted + "g CO2-Äquivalent");
		}
		
		
	}
	
	private void styleLabel( HTMLPanel panel, boolean selected) {
		
		String style = selectionStyle.selectedBlob();

		if (selected) {
			panel.addStyleName(style);
		} else {
			panel.removeStyleName(style);
		}
	
}
	public void setZutatSpec(ZutatSpecification zutatSpec) {
		this.zutatSpec = zutatSpec;
	}
	public ZutatSpecification getZutatSpec() {
		return zutatSpec;
	}
	public void setSelectedRow(int selectedRow) {
		this.selectedRow = selectedRow;
	}
	public int getSelectedRow() {
		return selectedRow;
	}
}
