package ch.eaternity.client.ui.widgets;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eaticious.common.QuantityImpl;
import org.eaticious.common.Season;
import org.eaticious.common.SeasonDate;
import org.eaticious.common.SeasonDateImpl;
import org.eaticious.common.SeasonImpl;
import org.eaticious.common.SeasonType;
import org.eaticious.common.Unit;


import ch.eaternity.client.DataService;
import ch.eaternity.client.DataServiceAsync;
import ch.eaternity.client.activity.RechnerActivity;
import ch.eaternity.client.events.IngredientsLoadedEvent;
import ch.eaternity.client.events.UpdateRecipeViewEvent;
//import ch.eaternity.server.Ingredient;
import ch.eaternity.shared.Condition;
import ch.eaternity.shared.Extraction;
import ch.eaternity.shared.FoodProduct;
import ch.eaternity.shared.Transportation;
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
	private RechnerActivity presenter;
	
	public IngredientsDialog(RechnerActivity presenter) {
		this.presenter = presenter;
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
	}
	
	
	private void processFile(String messageXml) {
		try {
			ArrayList<FoodProduct> ingredients = new ArrayList<FoodProduct>();
			ArrayList<FoodProduct> invalid_ingredients = new ArrayList<FoodProduct>();
			
			// parse the XML document into a DOM
			Document messageDom = XMLParser.parse(messageXml);
//			Window.alert("Root element " + messageDom.getDocumentElement().getNodeName());
			NodeList zutatenLst = messageDom.getElementsByTagName("ROW");
			//Window.alert( Integer.toString(zutatenLst.getLength()) + " Ingredients found." );
			 
			String tmpNodeVal1, tmpNodeVal2;
			boolean isValidIng = true;
			int amValidIngs = 0;
			
			
			for (int s = 0; s < zutatenLst.getLength(); s++) {
				isValidIng = true;
				Node zutat = zutatenLst.item(s);
				if (zutat.getNodeType() == Node.ELEMENT_NODE) {
					Element zutatElmnt = (Element) zutat;
					
					// id
//					Window.alert("Zutat Id : "  + ((Node) zutatId.item(0)).getNodeValue());
					tmpNodeVal1 = getTagContent(zutatElmnt, "Identifikations_Nummer");
					FoodProduct newIngredient = new FoodProduct();
					if (tmpNodeVal1 != null) newIngredient.setId(Long.parseLong( tmpNodeVal1 ));
					else {
						isValidIng = false;
						continue;
					}

					
					// symbol
//					Window.alert("Zutat Name : "  + ((Node) symbol.item(0)).getNodeValue());
					tmpNodeVal1 = getTagContent(zutatElmnt, "Zutat_Name");
					if (tmpNodeVal1 != null) newIngredient.setName( tmpNodeVal1, Locale.GERMAN );
					else isValidIng = false;
					
					// CO2eWert		
					tmpNodeVal1 = getTagContent(zutatElmnt, "CO2eq_Wert");
					//Window.alert("CO2eWert : "  + Math.round(Float.parseFloat( nodeValue )*1000));
					if (tmpNodeVal1 != null) newIngredient.setCo2eValue( new QuantityImpl(Double.parseDouble( tmpNodeVal1 )*1000, Unit.KILOGRAM) );
					else isValidIng = false;
					
					
					// alternatives
					tmpNodeVal1 = getTagContent(zutatElmnt, "Alternativen");
					if (tmpNodeVal1 != null){
						String alt_ar[] = tmpNodeVal1.split(",");
						List<Long> newAlternativen = new ArrayList<Long>();
						
						for(int i=0; i<alt_ar.length;i++)
						{
							alt_ar[i] = alt_ar[i].trim();
							newAlternativen.add(Long.parseLong(alt_ar[i]));
							
						}
						newIngredient.setSubstitutes(newAlternativen);
					}
					//else isValidIng = false;
					
					
					
					// std mengeGramm
					tmpNodeVal1 = getTagContent(zutatElmnt, "Std_Menge");
					if (tmpNodeVal1 != null) newIngredient.setStdWeight(new QuantityImpl(Double.parseDouble( tmpNodeVal1 ), Unit.GRAM));
					else newIngredient.setStdWeight(new QuantityImpl(100.0, Unit.GRAM));
				
					
					//Conditions
					// TODO catch if factors doesn't match symbols
					tmpNodeVal1 = getTagContent(zutatElmnt, "Konservierungen");
					tmpNodeVal2 = getTagContent(zutatElmnt, "Konservierungen_Faktoren");	
					if (tmpNodeVal1 != null && tmpNodeVal2 != null) {
						String cond_ar1[] = tmpNodeVal1.split(",");
						String cond_ar2[] = tmpNodeVal2.split(",");
						ArrayList<Condition> newConditions = new ArrayList<Condition>(cond_ar1.length);
						
						if (cond_ar1.length == cond_ar2.length)
						{
							for(int i=0; i<cond_ar1.length;i++) {
								cond_ar1[i] = cond_ar1[i].trim();
								cond_ar2[i] = cond_ar2[i].trim();
								Condition tmpCond = new Condition(cond_ar1[i]);
								tmpCond.factor = Double.parseDouble(cond_ar2[i]);
								newConditions.add(tmpCond);
							}
						}
						else
							isValidIng = false;
						newIngredient.setConditions(newConditions);
					}
					else {
						isValidIng = false;
						Window.alert("Problem with:"+ newIngredient.getName(Locale.GERMAN));
					}
					
					// Tags oder Labels
					tmpNodeVal1 = getTagContent(zutatElmnt, "Tags");
					if (tmpNodeVal1 != null) {
						String prod_ar1[] = tmpNodeVal1.split(",");
						List<String> tags = new ArrayList<String>(prod_ar1.length);
						
						for(int i=0; i<prod_ar1.length;i++) tags.add(prod_ar1[i].trim());

						newIngredient.setTags(tags);
					}
					
					
					//Productions
					// TODO catch if factors doesn't match symbols
					tmpNodeVal1 = getTagContent(zutatElmnt, "Herstellungen");
					tmpNodeVal2 = getTagContent(zutatElmnt, "Herstellungen_Faktoren");
					
					if (tmpNodeVal1 != null && tmpNodeVal2 != null) {
						String prod_ar1[] = tmpNodeVal1.split(",");
						String prod_ar2[] = tmpNodeVal2.split(",");
						ArrayList<Production> newProductions = new ArrayList<Production>(prod_ar1.length);
						
						if (prod_ar1.length == prod_ar2.length)
						{
							for(int i=0; i<prod_ar1.length;i++) {
								prod_ar1[i] = prod_ar1[i].trim();
								prod_ar2[i] = prod_ar2[i].trim();
								Production tmpProd = new Production(prod_ar1[i]);
								tmpProd.factor = Double.parseDouble(prod_ar2[i]);
								newProductions.add(tmpProd);
							}
						}
						else
							isValidIng = false;
						newIngredient.setProductions(newProductions);
					}
					else isValidIng = false;
					
					//Transportations
					// TODO catch if factors doesn't match symbols
					tmpNodeVal1 = getTagContent(zutatElmnt, "Transportmittel");
					tmpNodeVal2 = getTagContent(zutatElmnt, "Transportmittel_Faktoren");
					
					if (tmpNodeVal1 != null) {
						String trans_ar1[] = tmpNodeVal1.split(",");
						String trans_ar2[] = tmpNodeVal2.split(",");
						ArrayList<Transportation> newTransportations = new ArrayList<Transportation>(trans_ar1.length);
						
						if (trans_ar1.length == trans_ar2.length)
						{
							for(int i=0; i<trans_ar1.length;i++) {
								trans_ar1[i] = trans_ar1[i].trim();
								trans_ar2[i] = trans_ar2[i].trim();
								Transportation tmpTrans = new Transportation(trans_ar1[i]);
								tmpTrans.factor = Double.parseDouble(trans_ar2[i]);
								newTransportations.add(tmpTrans);
							}
						}
						else
							isValidIng = false;
						newIngredient.setTransportations(newTransportations);
					}
					else isValidIng = false;
					
					// Season
					Season season = null;
					
					tmpNodeVal1 = getTagContent(zutatElmnt, "saison_start");
					tmpNodeVal2 = getTagContent(zutatElmnt, "saison_stop");
						
					if (tmpNodeVal1 != null && tmpNodeVal1 != null)	{
						SeasonDate startDate = new SeasonDateImpl();
						SeasonDate stopDate = new SeasonDateImpl();
						if (startDate.setDate(tmpNodeVal1) && stopDate.setDate(tmpNodeVal2)) 
							season = new SeasonImpl(startDate, stopDate, SeasonType.MAIN_SEASON);
					}
					
						
					// Extractions (Herkunft)
					tmpNodeVal1 = getTagContent(zutatElmnt, "herkunfte");
					
					if (tmpNodeVal1 != null) {
						String trans_ar1[] = tmpNodeVal1.split(",");
						ArrayList<Extraction> newExtractions = new ArrayList<Extraction>(trans_ar1.length);
						
						for(int i=0; i<trans_ar1.length;i++) {
							trans_ar1[i] = trans_ar1[i].trim();
							Extraction tmpExtr = new Extraction(trans_ar1[i]);
							newExtractions.add(tmpExtr);
						}
						newIngredient.setExtractions(newExtractions);
						
					}
					else isValidIng = false;
					
					
					// symbol english
					tmpNodeVal1 = getTagContent(zutatElmnt, "Zutat_Name_Englisch");
					if (tmpNodeVal1 != null) newIngredient.setName( tmpNodeVal1.trim(), Locale.ENGLISH );
					//else isValidIng = false;

					// all elements are properly parsed add ingredient
					if (isValidIng) {
						ingredients.add(newIngredient);
						amValidIngs++;
					}
					else 
						invalid_ingredients.add(newIngredient);
				}
				
			}
			// finally persist the whole array ( if not empty)
			if(!ingredients.isEmpty())
			{
				String invalid_ingredients_names = "";
				for (FoodProduct ing : invalid_ingredients)
				{
					invalid_ingredients_names = invalid_ingredients_names + ing.getName(Locale.GERMAN) + "\n";
				}
				Window.alert(
					amValidIngs + " valid ingredients in xml.\n" + 
					ingredients.size() + " valid ingrediens added.\n" + 
					"Follwing Ingredients are invalid:\n" + 
					invalid_ingredients_names
				);
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

	private void persistIngredients(ArrayList<FoodProduct> ingredients) {
		ingredientsService.persistIngredients(ingredients, new AsyncCallback<Boolean>() {
			public void onFailure(Throwable error) {
				Window.alert(error.getMessage());
			}
			public void onSuccess(Boolean success) {
				if(success){
					Window.alert("XML File import successful!");
					presenter.getEventBus().fireEvent(new IngredientsLoadedEvent());
					hide();
				}
			}
		});
	}
	
	private String getTagContent(Element zutatElmnt, String elementName)
	{
		NodeList ElmntLst = zutatElmnt.getElementsByTagName(elementName);
		if (ElmntLst.getLength() > 0)
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