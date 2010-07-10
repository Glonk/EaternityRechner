package ch.eaternity.client;



import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import ch.eaternity.shared.Zutat;
import ch.eaternity.shared.ZutatSpecification;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Label;

public class InfoZutat extends ResizeComposite{
	interface Binder extends UiBinder<Widget, InfoZutat> { }
	interface PassedStyle extends CssResource {
		String hinweisPassed();
	}
	private static final Binder uiBinder = GWT.create(Binder.class);
	  
	  @UiField
		static PassedStyle passedStyle;
	  @UiField
	static HTML zutatName;
	  @UiField
	static TextBox zutatMenge;
	  @UiField
	static ZutatVarianten zutatVarianten;
	  @UiField DockLayoutPanel infoBox;
	  @UiField
	static Button addZutat;
	  @UiField
	static Label hinweisPanel;
	  @UiField
		static Label valueLabel;
	  
	  static HandlerRegistration handel;
	  
	  public static double distance;
	  
	  public static ZutatSpecification zutat;
	  
	  public InfoZutat() {
		    initWidget(uiBinder.createAndBindUi(this));
		    if(zutat != null){

		    	
		    }
		    
	  }

	public InfoZutat(ZutatSpecification zutatSpec) {
		initWidget(uiBinder.createAndBindUi(this));
		zutatName.setHTML("<h1>"+ zutatSpec.getName() +"</h1>");
		//TODO uncomment this:
		//zutatVarianten.setWerte(zutatSpec,zutat,row);
		
	}

	public static void setZutat(final ZutatSpecification zutatSpec,final Zutat zutat, int row) {

			try {
				handel.removeHandler(); 
			} catch (Exception ignored) {      // one or multiple
			}
			 finally {
				handel = addZutat.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						
//						ZutatSpecification zutatSpecification = new ZutatSpecification(zutat.getId(), zutat.getSymbol(),
//								 new Date(),zutat.getStdZustand(), zutat.getStdProduktion(), 
//								zutat.getStdTransportmittel());
//						zutatSpecification.setHerkunft(zutat.getStdHerkunft());
//						zutatSpecification.setMengeGramm(zutat.getStdMengeGramm());
//						zutatSpecification.setSeason(zutat.getStdStartSeason(), zutat.getStdStopSeason());
//						zutatSpecification.setNormalCO2Value(zutat.getCO2eWert());
//						
							ZutatSpecification zutatSpecification = ZutatVarianten.getZutatSpecification(zutatSpec) ;
							InfoZutat.zutat = zutatSpecification;
							List<ZutatSpecification> zutaten = new ArrayList<ZutatSpecification>(1);
							zutaten.add(zutatSpecification);
							int row = EaternityRechner.AddZutatZumMenu(zutaten);
					}
				});
			} 

			zutatName.setHTML("<h1>"+ zutatSpec.getName() +"</h1>");
			zutatVarianten.setWerte(zutatSpec,zutat,row);
			
		}
	  
	  
	public static void setZutat(final Zutat zutat) {
		
//		final int row2 = EaternityRechner.MenuTable.getRowCount();
		
		try {
			handel.removeHandler(); 
		} catch (Exception ignored) {      // one or multiple
		                    
		}
		 finally {
			handel = addZutat.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					
					ZutatSpecification zutatSpecification = new ZutatSpecification(zutat.getId(), zutat.getSymbol(),
							 new Date(),zutat.getStdZustand(), zutat.getStdProduktion(), 
							zutat.getStdTransportmittel());
					zutatSpecification.setHerkunft(zutat.getStdHerkunft());
					zutatSpecification.setMengeGramm(zutat.getStdMengeGramm());
					zutatSpecification.setSeason(zutat.getStdStartSeason(), zutat.getStdStopSeason());
					zutatSpecification.setNormalCO2Value(zutat.getCO2eWert());
					
					
						ZutatSpecification zutatSpec = ZutatVarianten.getZutatSpecification(zutatSpecification) ;
						InfoZutat.zutat = zutatSpec;
						List<ZutatSpecification> zutaten = new ArrayList<ZutatSpecification>(1);
						zutaten.add(zutatSpec);
						int row = EaternityRechner.AddZutatZumMenu(zutaten);
				}
			});
		} 

		update(zutat);
//		final int row3 = EaternityRechner.MenuTable.getRowCount();
		
	}
	
	
	static void styleHinweis( boolean selected) {
		
		String style = passedStyle.hinweisPassed();

		if (selected) {
			hinweisPanel.addStyleName(style);
		} else {
			hinweisPanel.removeStyleName(style);
		}
	
	}
	
	
	public void stylePanel(boolean onOff) {
			if (onOff) {
//				infoBox.setHeight("500px");
			} else {
				
			}
		
	}
	
	public ZutatSpecification getZutat() {
		return zutat;
	}
	
	
	private static void update(Zutat zutat){
		zutatName.setHTML("<h1>"+ zutat.getSymbol() +"</h1>");
		zutatVarianten.setStdWerte(zutat);
		
		
	}
	
	
	//TODO here comes all the CO2 Logic
	public static void updateZutatCO2(ZutatSpecification zutat, int row){
		String formatted = NumberFormat.getFormat("##").format( zutat.getCalculatedCO2Value() );
		valueLabel.setText(formatted + "g CO2-Äquivalent");
		if(row != -1){
//			if(EaternityRechner.zutatImMenu.contains(zutat)){
//				EaternityRechner.zutatImMenu.set(EaternityRechner.zutatImMenu.indexOf(zutat), zutat);
//				
//				
//			}
			//TODO uncomment this:
			// EaternityRechner.MenuTable.setText(row, 4, ": ca. "+formatted + "g CO2-Äquivalent");
		}
	}
	
	@UiHandler("zutatMenge")
	void onKeyUp(KeyUpEvent event) {
			int keyCode = event.getNativeKeyCode();
			if ((!Character.isDigit((char) keyCode)) && (keyCode != KeyCodes.KEY_TAB)
					&& (keyCode != KeyCodes.KEY_BACKSPACE)
					&& (keyCode != KeyCodes.KEY_DELETE) && (keyCode != KeyCodes.KEY_ENTER) 
					&& (keyCode != KeyCodes.KEY_HOME) && (keyCode != KeyCodes.KEY_END)
					&& (keyCode != KeyCodes.KEY_LEFT) && (keyCode != KeyCodes.KEY_UP)
					&& (keyCode != KeyCodes.KEY_RIGHT) && (keyCode != KeyCodes.KEY_DOWN)) {
				// TextBox.cancelKey() suppresses the current keyboard event.
				zutatMenge.cancelKey();
				zutatMenge.setText( zutatMenge.getText().substring(0, zutatMenge.getText().length()-1) );
			} else {
				if(zutatMenge.getText().length()>6){
					zutatMenge.setText(  zutatMenge.getText().substring(0, 6) );
					return;
				}
				
				if(zutatMenge.getText().length() == 0){
					zutatMenge.setText("0");
				}
				ZutatSpecification zutatSpec = ZutatVarianten.getZutatSpecification(InfoZutat.zutat) ;
				// TODO uncomment this:
				//InfoZutat.updateZutatCO2(zutatSpec, EaternityRechner.selectedRow);
		
//				String MengeZutatWert;
//				int rowhere = getWidgetRow(MengeZutat,MenuTable);
//				if(MengeZutat.getText() != ""){
//					MengeZutatWert = MengeZutat.getText();
//					zutatSpec.setMengeGramm(Integer.valueOf(MengeZutatWert));
//				} else {
//					MengeZutatWert = "0";
//				}
//
////				int length = (int)  Math.round(Double.valueOf(MengeZutatWert).doubleValue() *0.001);
//				MenuTable.setText(rowhere,3,Double.toString(zutatSpec.getCalculatedCO2Value()).concat("g CO₂-Äquivalent"));
//				MenuTable.setHTML(rowhere, 4, "<div style='background:#ff0;width:".concat(Double.toString(zutatSpec.getCalculatedCO2Value()/1000).concat("px'>.</div>")));
//				updateSuggestion();
			}


		}


	
	  
}
