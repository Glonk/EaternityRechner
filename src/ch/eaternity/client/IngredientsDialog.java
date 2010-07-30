package ch.eaternity.client;


import java.util.ArrayList;

//import ch.eaternity.server.Ingredient;
import ch.eaternity.shared.Condition;
import ch.eaternity.shared.Extraction;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.MoTransportation;
import ch.eaternity.shared.ProductLabel;
import ch.eaternity.shared.Production;
import ch.eaternity.shared.Zutat;
import ch.eaternity.shared.Zutat.Herkuenfte;
import ch.eaternity.shared.Zutat.Produktionen;
import ch.eaternity.shared.Zutat.Transportmittel;
import ch.eaternity.shared.Zutat.Zustaende;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.DOMException;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;
import com.googlecode.objectify.Key;

public class IngredientsDialog extends DialogBox{

	interface Binder extends UiBinder<Widget, IngredientsDialog> { }
	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField Button closeButton;
	@UiField Button executeButton;
	@UiField TextArea xmlText;
	@UiField Label statusLabel;
	
	private final DataServiceAsync ingredientsService = GWT.create(DataService.class);
	
	public IngredientsDialog() {
		
		// Use this opportunity to set the dialog's caption.
		setText("Add here your Ingredients.xml");
		setWidget(binder.createAndBindUi(this));

		setAnimationEnabled(true);
		setGlassEnabled(true);
		xmlText.setCharacterWidth(40);
		xmlText.setVisibleLines(10);

		executeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				String file = xmlText.getText();
				if (file.length() == 0) {
					Window.alert("no text");
				} else {
					processFile(file);
				}
			}
		});
//		getIngredients();
		
	}
	
	
	private void processFile(String messageXml) {
		try {
			ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
			// parse the XML document into a DOM
			Document messageDom = XMLParser.parse(messageXml);
//			Window.alert("Root element " + messageDom.getDocumentElement().getNodeName());
			NodeList zutatenLst = messageDom.getElementsByTagName("ingredient");
			// Window.alert( Integer.toString(zutatenLst.getLength()) );
			for (int s = 0; s < zutatenLst.getLength(); s++) {

				Node zutat = zutatenLst.item(s);

				if (zutat.getNodeType() == Node.ELEMENT_NODE) {

					Element zutatElmnt = (Element) zutat;
					
					// id
					NodeList zutatIdElmntLst = zutatElmnt.getElementsByTagName("id");
					Element zutatIdElmnt = (Element) zutatIdElmntLst.item(0);
					NodeList zutatId = zutatIdElmnt.getChildNodes();
//					Window.alert("Zutat Id : "  + ((Node) zutatId.item(0)).getNodeValue());
					Ingredient newIngredient = new Ingredient(Long.parseLong(((Node) zutatId.item(0)).getNodeValue().trim()) );
					ingredients.add(newIngredient);
					
					// symbol
					NodeList symbolElmntLst = zutatElmnt.getElementsByTagName("symbol");
					Element symbolElmnt = (Element) symbolElmntLst.item(0);
					NodeList symbol = symbolElmnt.getChildNodes();
//					Window.alert("Zutat Name : "  + ((Node) symbol.item(0)).getNodeValue());
					newIngredient.setSymbol( ((Node) symbol.item(0)).getNodeValue() );
					
					// CO2eWert
					NodeList CO2eWertElmntLst = zutatElmnt.getElementsByTagName("co2eValue");
					Element CO2eWertElmnt = (Element) CO2eWertElmntLst.item(0);
					NodeList CO2eWert = CO2eWertElmnt.getChildNodes();
//					Window.alert("CO2eWert : "  + ((Node) CO2eWert.item(0)).getNodeValue());
					newIngredient.setCo2eValue( Integer.parseInt( ((Node) CO2eWert.item(0)).getNodeValue() ) );
					
					
					// alternatives
					NodeList alternativen = zutatElmnt.getElementsByTagName("Alternatives");
					Element alternativeElement = (Element) alternativen.item(0);

					if (alternativeElement.getNodeType() == Node.ELEMENT_NODE) {

						NodeList alternativeElmntLst = alternativeElement.getElementsByTagName("zutatId");
						Long[] newAlternativen = new Long[alternativeElmntLst.getLength()];
						
						for(int i=0; i<alternativeElmntLst.getLength(); i++){
							Node alternativenId = alternativeElmntLst.item(i);
							if (alternativenId.getNodeType() == Node.ELEMENT_NODE) {
								
								Element alternativenIdElement = (Element) alternativenId;
								NodeList alternativeId = alternativenIdElement.getChildNodes();
								
//								Window.alert("Alterntive Id : "  + ((Node) alternativeId.item(0)).getNodeValue());
								newAlternativen[i] = Long.parseLong(((Node) alternativeId.item(0)).getNodeValue().trim());
							}

						}
						newIngredient.setAlternatives(newAlternativen);
					}
					

					NodeList herkuenfte = zutatElmnt.getElementsByTagName("Extractions");
					Element herkuenfteElement = (Element) herkuenfte.item(0);

					if (herkuenfteElement.getNodeType() == Node.ELEMENT_NODE) {
						
						NodeList herkunftIdElmntLst = herkuenfteElement.getElementsByTagName("extraction");
						Extraction[] newHerkuenfte = new Extraction[herkunftIdElmntLst.getLength()];
						for(int i=0; i<herkunftIdElmntLst.getLength(); i++){
							Element extractionElement = (Element) herkunftIdElmntLst.item(i);
							
								
								
								NodeList ExtractionSymbolElmntLst = extractionElement.getElementsByTagName("symbol");
								Element ExtractionSymbolElmnt = (Element) ExtractionSymbolElmntLst.item(0);
								NodeList ExtractionSymbol = ExtractionSymbolElmnt.getChildNodes();
//								Window.alert("Zutat Name : "  + ((Node) symbol.item(0)).getNodeValue());
								Extraction extraction = new Extraction( ((Node) ExtractionSymbol.item(0)).getNodeValue() );
								
								// std startSeason
								NodeList startSeasonElmntLst = extractionElement.getElementsByTagName("startSeason");
								Element startSeasonElmnt = (Element) startSeasonElmntLst.item(0);
								NodeList startSeason = startSeasonElmnt.getChildNodes();
//								Window.alert("std startSeason : "  + ((Node) startSeason.item(0)).getNodeValue());
								extraction.startSeason =  ((Node) startSeason.item(0)).getNodeValue();
								
								
								// std stopSeason
								NodeList stopSeasonElmntLst = extractionElement.getElementsByTagName("stopSeason");
								Element stopSeasonElmnt = (Element) stopSeasonElmntLst.item(0);
								NodeList stopSeason = stopSeasonElmnt.getChildNodes();
//								Window.alert("std stopSeason : "  + ((Node) stopSeason.item(0)).getNodeValue());
								extraction.stopSeason =	((Node)  stopSeason.item(0)).getNodeValue();
								
								
								// std zustand
								NodeList zustandElmntLst = extractionElement.getElementsByTagName("condition");
								Element zustandElmnt = (Element) zustandElmntLst.item(0);
								NodeList zustand = zustandElmnt.getChildNodes();
//								Window.alert("std zustand : "  + ((Node) zustand.item(0)).getNodeValue());
								Condition condition = new Condition( ((Node) zustand.item(0)).getNodeValue() );
								extraction.stdCondition = condition;
								
								// std produktion
								NodeList produktionElmntLst = extractionElement.getElementsByTagName("production");
								Element produktionElmnt = (Element) produktionElmntLst.item(0);
								NodeList produktion = produktionElmnt.getChildNodes();
//								Window.alert("std produktion : "  + ((Node) produktion.item(0)).getNodeValue());
								Production production = new Production( ((Node) produktion.item(0)).getNodeValue() );
								extraction.stdProduction = production;
								
								// std transportmittel
								NodeList transportmittelElmntLst = extractionElement.getElementsByTagName("moTransportation");
								Element transportmittelElmnt = (Element) transportmittelElmntLst.item(0);
								NodeList transportmittel = transportmittelElmnt.getChildNodes();
//								Window.alert("std transportmittel : "  + ((Node) transportmittel.item(0)).getNodeValue());
								MoTransportation moTransportation = new MoTransportation( ((Node) transportmittel.item(0)).getNodeValue() );
								extraction.stdMoTransportation = moTransportation;
								
								

								

								NodeList labels = extractionElement.getElementsByTagName("Labels");
								Element labelsElement = (Element) labels.item(0);

								if (labelsElement.getNodeType() == Node.ELEMENT_NODE) {
									
									NodeList labelExtractionIdElmntLst = labelsElement.getElementsByTagName("label");
									ProductLabel[] newLabels = new ProductLabel[labelExtractionIdElmntLst.getLength()];
									for(int j=0; j<labelExtractionIdElmntLst.getLength(); j++){
										Node labelsId = labelExtractionIdElmntLst.item(j);
										if (labelsId.getNodeType() == Node.ELEMENT_NODE) {
											
											Element labelIdElement = (Element) labelsId;
											NodeList labelId = labelIdElement.getChildNodes();
//											Window.alert("Label Id : "  + ((Node) labelId.item(0)).getNodeValue());
											ProductLabel productLabel = new ProductLabel(((Node) labelId.item(0)).getNodeValue() );
											newLabels[j] = productLabel;
											
										}

									}

									extraction.stdProductLabels = newLabels;
								}
								
								
								
								newHerkuenfte[i] = extraction;
								
							

						}
						// even so it doesn't work no more?
						newIngredient.setExtractions(newHerkuenfte);
						
					}
				}
			}
			// finally persist the whole array ( if not empty)
			if(!ingredients.isEmpty())
			{
				persistIngredients(ingredients);
			}
			
		} 	catch (DOMException e) {
			Window.alert(e.getMessage());
		}
	}

	private void persistIngredients(ArrayList<Ingredient> ingredients) {
		ingredientsService.persistIngredients(ingredients, new AsyncCallback<Boolean>() {
			public void onFailure(Throwable error) {
				Window.alert(error.getMessage());
			}
			public void onSuccess(Boolean success) {
				if(success){
					Window.alert("yipiieee!");
				}
			}
		});
	}
	
	
	private void getIngredients() {
		ingredientsService.getIngredientsXml( new AsyncCallback<String>() {
			public void onFailure(Throwable error) {
				Window.alert(error.getMessage());
			}
			public void onSuccess(String success) {
				xmlText.setText(success);
			}
		});
	}
	
	

	@Override
	protected void onPreviewNativeEvent(NativePreviewEvent preview) {
		super.onPreviewNativeEvent(preview);

		NativeEvent evt = preview.getNativeEvent();
		if (evt.getType().equals("keydown")) {
			// Use the popup's key preview hooks to close the dialog when either
			// enter or escape is pressed.
			switch (evt.getKeyCode()) {
			case KeyCodes.KEY_ENTER:
				String file = xmlText.getText();
				if (file.length() != 0) {
					processFile(file);
				}
			case KeyCodes.KEY_ESCAPE:
				hide();
				break;
			}
		}
	}

	@UiHandler("closeButton")
	void onSignOutClicked(ClickEvent event) {
		hide();
	}
}
