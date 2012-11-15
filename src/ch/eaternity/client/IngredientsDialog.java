package ch.eaternity.client;


import java.util.ArrayList;
import java.util.List;


//import ch.eaternity.server.Ingredient;
import ch.eaternity.shared.IngredientCondition;
import ch.eaternity.shared.Extraction;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.MoTransportation;
import ch.eaternity.shared.ProductLabel;
import ch.eaternity.shared.Production;


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
			NodeList zutatenLst = messageDom.getElementsByTagName("ROW");
			 Window.alert( Integer.toString(zutatenLst.getLength()) + " Ingredients found." );
			 
			Long lastid = null;
			String tmpNodeVal1, tmpNodeVal2;
			boolean isValidIng = true;
			int amInvalidIngs = 0;
			
			
			for (int s = 0; s < zutatenLst.getLength(); s++) {
				Node zutat = zutatenLst.item(s);
				if (zutat.getNodeType() == Node.ELEMENT_NODE) {
					Element zutatElmnt = (Element) zutat;
					
					// id
//					Window.alert("Zutat Id : "  + ((Node) zutatId.item(0)).getNodeValue());
					tmpNodeVal1 = getTagContent(zutatElmnt, "Identifikations_Nummer");
					Ingredient newIngredient = new Ingredient(0L);
					if (tmpNodeVal1 != null) newIngredient.setId(Long.parseLong( tmpNodeVal1 ));
					else isValidIng = false;
					
					// symbol
//					Window.alert("Zutat Name : "  + ((Node) symbol.item(0)).getNodeValue());
					tmpNodeVal1 = getTagContent(zutatElmnt, "Zutat_Name");
					if (tmpNodeVal1 != null) newIngredient.setSymbol( tmpNodeVal1 );
					else isValidIng = false;
					
					// CO2eWert		
					tmpNodeVal1 = getTagContent(zutatElmnt, "CO2eq_Wert");
					//Window.alert("CO2eWert : "  + Math.round(Float.parseFloat( nodeValue )*1000));
					if (tmpNodeVal1 != null) newIngredient.setCo2eValue( Math.round(Float.parseFloat( tmpNodeVal1 )*1000) );
					else isValidIng = false;
					
					/*
					//id/symbol/CO2 are necessary, before its nod added
					if (isValidIng) ingredients.add(newIngredient);
					else 
					{
						amInvalidIngs++;
						continue;
					}*/
					
					// alternatives
					tmpNodeVal1 = getTagContent(zutatElmnt, "Alternativen");
					if (tmpNodeVal1 != null){
						String alt_ar[] = tmpNodeVal1.split(",");
						Long[] newAlternativen = new Long[alt_ar.length];
						
						for(int i=0; i<alt_ar.length;i++)
						{
							alt_ar[i] = alt_ar[i].trim();
							newAlternativen[i] = Long.parseLong(alt_ar[i]);
							
						}
						newIngredient.setAlternatives(newAlternativen);
					}
					//else isValidIng = false;
					
					// std mengeGramm
					tmpNodeVal1 = getTagContent(zutatElmnt, "Std_Menge");
					if (tmpNodeVal1 != null) newIngredient.stdAmountGramm = Integer.parseInt( tmpNodeVal1 );
					else continue;
					
					// std herkunft
					tmpNodeVal1 = getTagContent(zutatElmnt, "Std_Herkunft");
					if (tmpNodeVal1 != null) newIngredient.stdExtractionSymbol = tmpNodeVal1;
					else isValidIng = false;
					
					//Conditions
					// TODO catch if factors doesn't match symbols
					tmpNodeVal1 = getTagContent(zutatElmnt, "Konservierungen");
					tmpNodeVal2 = getTagContent(zutatElmnt, "Konservierungen_Faktoren");	
					if (tmpNodeVal1 != null) {
						String cond_ar1[] = tmpNodeVal1.split(",");
						String cond_ar2[] = tmpNodeVal2.split(",");
						ArrayList<IngredientCondition> newConditions = new ArrayList<IngredientCondition>(cond_ar1.length);
						
						for(int i=0; i<cond_ar1.length;i++) {
							cond_ar1[i] = cond_ar1[i].trim();
							cond_ar2[i] = cond_ar2[i].trim();
							IngredientCondition tmpCond = new IngredientCondition(cond_ar1[i]);
							tmpCond.factor = Double.parseDouble(cond_ar2[i]);
							newConditions.add(tmpCond);
						}
						newIngredient.conditions = newConditions;
					}
					else isValidIng = false;
					
					//Productions
					// TODO catch if factors doesn't match symbols
					tmpNodeVal1 = getTagContent(zutatElmnt, "Herstellungen");
					tmpNodeVal2 = getTagContent(zutatElmnt, "Herstellungen_Faktoren");
					
					if (tmpNodeVal1 != null) {
						String prod_ar1[] = tmpNodeVal1.split(",");
						String prod_ar2[] = tmpNodeVal2.split(",");
						ArrayList<Production> newProductions = new ArrayList<Production>(prod_ar1.length);
						
						for(int i=0; i<prod_ar1.length;i++) {
							prod_ar1[i] = prod_ar1[i].trim();
							prod_ar2[i] = prod_ar2[i].trim();
							Production tmpProd = new Production(prod_ar1[i]);
							tmpProd.factor = Double.parseDouble(prod_ar2[i]);
							newProductions.add(tmpProd);
						}
						newIngredient.productions = newProductions;
					}
					else isValidIng = false;
					
					//Transportations
					// TODO catch if factors doesn't match symbols
					tmpNodeVal1 = getTagContent(zutatElmnt, "Transportmittel");
					tmpNodeVal2 = getTagContent(zutatElmnt, "Transportmittel_Faktoren");
					
					if (tmpNodeVal1 != null) {
						String trans_ar1[] = tmpNodeVal1.split(",");
						String trans_ar2[] = tmpNodeVal2.split(",");
						ArrayList<MoTransportation> newTransportations = new ArrayList<MoTransportation>(trans_ar1.length);
						
						for(int i=0; i<trans_ar1.length;i++) {
							trans_ar1[i] = trans_ar1[i].trim();
							trans_ar2[i] = trans_ar2[i].trim();
							MoTransportation tmpTrans = new MoTransportation(trans_ar1[i]);
							tmpTrans.factor = Double.parseDouble(trans_ar2[i]);
							newTransportations.add(tmpTrans);
						}
						newIngredient.moTransportations = newTransportations;
					}
					else isValidIng = false;
				
						
					// Extractions (Herkunft)
					// DEFAULT VALUES, remove after final implementation of database format
					IngredientCondition stdCond = new IngredientCondition("frisch");
					Production stdProd = new Production("konventionell");
					MoTransportation stdTrans = new MoTransportation("LKW");
					List<ProductLabel> labels = new ArrayList<ProductLabel>();
					labels.add(new ProductLabel("knospe"));
					
					tmpNodeVal1 = getTagContent(zutatElmnt, "herkünfte");
					
					if (tmpNodeVal1 != null) {
						String trans_ar1[] = tmpNodeVal1.split(",");
						ArrayList<Extraction> newExtractions = new ArrayList<Extraction>(trans_ar1.length);
						
						for(int i=0; i<trans_ar1.length;i++) {
							trans_ar1[i] = trans_ar1[i].trim();
							Extraction tmpExtr = new Extraction(trans_ar1[i]);
							tmpExtr.startSeason = "01.01";
							tmpExtr.stopSeason = "10.11";
							tmpExtr.stdCondition = stdCond;
							tmpExtr.stdProduction = stdProd;
							tmpExtr.stdMoTransportation = stdTrans;
							tmpExtr.stdProductLabels = labels;
							newExtractions.add(tmpExtr);
						}
						newIngredient.setExtractions(newExtractions);
					}
					else isValidIng = false;
					
					// all elements are properly parsed add ingredient
					if (isValidIng) ingredients.add(newIngredient);
					else 
					{
						amInvalidIngs++;
						continue;
					}
							

					lastid = newIngredient.getId();
				}
				
			}
			// finally persist the whole array ( if not empty)
			if(!ingredients.isEmpty())
			{
				Window.alert(amInvalidIngs + " invalid ingredients in xml.\n" + ingredients.size() + " valid ingrediens added.");
				persistIngredients(ingredients);
			}
			else
			{
				Window.alert("No proper formatet ingredients found.");
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
					Window.alert("XML File import successful!");
				}
			}
		});
	}
	
	private String getTagContent(Element zutatElmnt, String elementName)
	{
		NodeList ElmntLst = zutatElmnt.getElementsByTagName(elementName);
		if (ElmntLst.getLength() != 0)
		{
			Node Elmnt = ElmntLst.item(0).getFirstChild();
			if (Elmnt != null)
			{
				String nodeVal = Elmnt.getNodeValue();
				if (nodeVal != null)
				{
					return nodeVal.trim();
				}
			}	
		}
		return null;
	}
	
	
//	private void getIngredients() {
//		ingredientsService.getIngredientsXml( new AsyncCallback<String>() {
//			public void onFailure(Throwable error) {
//				Window.alert(error.getMessage());
//			}
//			public void onSuccess(String success) {
//				xmlText.setText(success);
//			}
//		});
//	}
	
	

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
