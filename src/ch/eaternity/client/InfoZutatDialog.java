package ch.eaternity.client;

import java.util.Date;
import java.util.List;


import ch.eaternity.client.ui.EaternityRechnerView.Presenter;
import ch.eaternity.client.widgets.ImageOverlay;
import ch.eaternity.shared.IngredientCondition;
import ch.eaternity.shared.Extraction;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.MoTransportation;
import ch.eaternity.shared.Production;
import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.SingleDistance;
import ch.eaternity.shared.IngredientSpecification;


import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.geocode.DirectionQueryOptions;
import com.google.gwt.maps.client.geocode.DirectionResults;
import com.google.gwt.maps.client.geocode.Directions;
import com.google.gwt.maps.client.geocode.DirectionsCallback;
import com.google.gwt.maps.client.geocode.DirectionsPanel;
import com.google.gwt.maps.client.geocode.Geocoder;
import com.google.gwt.maps.client.geocode.LocationCallback;
import com.google.gwt.maps.client.geocode.Placemark;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class InfoZutatDialog<T> extends Composite {
	interface Binder extends UiBinder<Widget, InfoZutatDialog> { }
	private static Binder uiBinder = GWT.create(Binder.class);
	
	// Variables
	IngredientSpecification zutatSpec;
	Ingredient stdIngredient;
	RecipeEditView rezeptviewParent;
	private Recipe recipe;
	
	static double distance = 0;
	
	private int selectedRow;
	private FlexTable menuTable;
	private InlineHTML kmText = new InlineHTML();
	private FlexTable suggestTable;
	FlowPanel flowTransport = null;
	
	private final static Geocoder geocoder = new Geocoder();
	final TextBox newExtractionBox = new TextBox();
	ClickHandler clickerHandler = null;
	KeyDownHandler keyDownHandler = null;
	Boolean handlersNotAdded = true;
	
	
	
	// UI Fields
	@UiField HTML zutatName;
	@UiField PassedStyle passedStyle;
	@UiField Label hinweisPanel;
	@UiField Label hinweisDetails;
	@UiField Label closeLabel;
	
	@UiField SelectionStyle selectionStyle;
	@UiField FlexTable specificationTable;


	
	private Presenter<T> presenter;
	private Ingredient zutat;
	public void setPresenter(Presenter<T> presenter){
		this.presenter = presenter;
		setValues( zutat);
		
	}
	
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
			hinweisDetails.setStyleName("black");
		} else {
			hinweisPanel.removeStyleName(style);
			hinweisDetails.setStyleName("grey");
		}
	
	}
	public void stylePanel(boolean onOff) {
		if (onOff) {
			//			infoBox.setHeight("500px");
		} else {

		}

	}
	
	@UiHandler("closeLabel")
	void onCloseClicked(ClickEvent event) {
		rezeptviewParent.addInfoPanel.getWidget(1).setVisible(false);
		rezeptviewParent.menuDecoInfo.setVisible(true);
		rezeptviewParent.styleRow(selectedRow, false);
		rezeptviewParent.infoDialogIsOpen = false;
//		rezeptviewParent.addInfoPanel.insert(new HTML("test"), 1);
		
	}

	public InfoZutatDialog(IngredientSpecification zutatSpec, Ingredient zutat, TextBox amount, FlexTable menuTable, int selectedRow, Recipe recipe, FlexTable suggestTable, RecipeEditView editRecipeView) {
		initWidget(uiBinder.createAndBindUi(this));
		zutatName.setHTML( zutatSpec.getName() );
		specificationTable.setCellSpacing(0);
		// TODO Auto-generated constructor stub
		this.rezeptviewParent = editRecipeView;
		this.setZutatSpec(zutatSpec);
		this.stdIngredient = zutat;
		this.setSelectedRow(selectedRow);
		this.setRezept(recipe);
		this.menuTable = menuTable;
		this.suggestTable = suggestTable;
		this.zutat = zutat;
		
		//add custom extraction if not in list
		boolean extractionExists = false;
    	for (Extraction extr : zutat.getExtractions())
    	{
    		if (zutatSpec.getHerkunft().symbol == extr.symbol)
    			extractionExists = true;
    	}
    	if (!extractionExists)
    		this.zutat.getExtractions().add(0,zutatSpec.getHerkunft());

	}


	
	private void differentOriginSelected(final ListBox herkuenfte){
		int width = herkuenfte.getOffsetWidth();
		herkuenfte.setVisible(false);
		
		newExtractionBox.setWidth(Integer.toString(width)+"px");
		newExtractionBox.setVisible(true);
		newExtractionBox.setText("");
		
		kmText.setHTML("<a style='margin-left:3px;cursor:pointer;cursor:hand;'>berechnen</a>");
		
		clickerHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				calculateExtractionDistance(herkuenfte);
			}
		};
		
		keyDownHandler = new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if(KeyCodes.KEY_ENTER == event.getNativeKeyCode()) {
					calculateExtractionDistance(herkuenfte);
				}
				if(KeyCodes.KEY_TAB == event.getNativeKeyCode()) {
					
				}
			}
		};
		
		if(handlersNotAdded){
			kmText.addClickHandler(clickerHandler);
			newExtractionBox.addKeyDownHandler(keyDownHandler);
			handlersNotAdded = false;
		}
		newExtractionBox.setFocus(true);
	}
			
	private void calculateExtractionDistance(final ListBox herkuenfte) {
		// Don't add if already exists
	  	boolean found = false;
	  	for (int i = 0; i<herkuenfte.getItemCount(); i++) {
	  		if (herkuenfte.getValue(i).equals(newExtractionBox.getText())) {
	  			found = true;
	  			newExtractionBox.setVisible(false);
	  			herkuenfte.setSelectedIndex(i);
	  			herkuenfte.setVisible(true);
	  			herkuenfte.setFocus(true);
	  			triggerHerkunftChange(zutat, herkuenfte);
	  		}
		}	
	  	
	  	if (found == false) {	
			try {
				Geocoder geocoder = new Geocoder();
				geocoder.setBaseCountryCode("ch");
			    geocoder.getLocations(newExtractionBox.getText(), new LocationCallback() {
			    	
				    public void onFailure(int statusCode) {
				    	  kmText.setHTML("Adresse nicht auffindbar!");
				    	  Timer t = new Timer() {
				    		  public void run() {
				    			  kmText.setHTML("<a style='margin-left:3px;cursor:pointer;cursor:hand;'>berechnen</a>");
				    		  }
				    	  };
				    	  t.schedule(1000);
				    }
	
				    public void onSuccess(JsArray<Placemark> locations) {
				    	  Placemark place = locations.get(0);
				    	  GWT.log("Sie befinden sich in: " +place.getAddress() +"  ");
				    	  // Don't add if already exists
				    	  boolean found = false;
				    	  for (int i = 0; i<herkuenfte.getItemCount(); i++) {
				    		  if (herkuenfte.getValue(i).equals(place.getAddress()))
				    			  found = true;
				    	  }
				    	  if (found == false)
				    		  herkuenfte.insertItem(place.getAddress(), 0);
				    	  Extraction element = new Extraction(place.getAddress());
				    	  // TODO come up with stuff like seasons and so forth..
				    	  element.startSeason = zutat.stdExtraction.startSeason;
				    	  element.stopSeason = zutat.stdExtraction.stopSeason;
				    	  element.stdCondition = zutat.stdExtraction.stdCondition;
				    	  element.stdMoTransportation = zutat.stdExtraction.stdMoTransportation;
				    	  element.stdProduction = zutat.stdExtraction.stdProduction;
				    	  
				    	  //don't add new extraction if already in the list
				    	  if (!zutat.getExtractions().contains(element))
				    		  zutat.getExtractions().add(0, element);
				    	  newExtractionBox.setVisible(false);
				    	  herkuenfte.setVisible(true);
				    	  herkuenfte.setSelectedIndex(0);
				    	  herkuenfte.setFocus(true);
				    	  triggerHerkunftChange(zutat, herkuenfte);
			        }
			    });
			}
			finally {
			}
		}			
	}
	
	public void setValues( final Ingredient zutat){
		
		hinweisDetails.setText("");
		
		//Season
		if(zutat.hasSeason != null && zutat.hasSeason){
			specificationTable.setHTML(0, 0, "Saison");
			updateSaison(zutatSpec);
		}
		
		//Cost
		int row2 = specificationTable.getRowCount();
		specificationTable.setHTML(row2, 0, "Kosten");
		HorizontalPanel horPanel = new HorizontalPanel();
		final TextBox costTextBox = new TextBox();
		final HTML costError = new HTML();
		costError.setStyleName("costError");
		costTextBox.setWidth("60px");
		specificationTable.setWidget(row2,1,horPanel);
		
		NumberFormat df = NumberFormat.getFormat("00.##");
		double cost = zutatSpec.getCost();
		
		if(cost != 0.0d)
			costTextBox.setText(df.format(cost));
		
		costTextBox.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event)  {
				String text = costTextBox.getText();
				try { 
					if ("".equals(text)) {}
					else {
						//DecimalFormat df = new DecimalFormat();
						//df.setMaximumFractionDigits(2);
						//zutatSpec.setCost(df.parse(text).doubleValue());
						costError.setText("");
						double cost = Double.parseDouble(text);
						if (cost >= 0.0)
							zutatSpec.setCost(cost);
						else
							costError.setText("Wert ungueltig");
					}
				}
				catch (NumberFormatException nfe) {
					costError.setText("Wert ungueltig");
				}
			}
		});
		
		HTML costLabel = new HTML(" CHF");
		horPanel.add(costTextBox);
		horPanel.add(costLabel);
		horPanel.add(costError);
		
		//Extractions
		if(zutat.getExtractions() != null && zutat.getExtractions().size()>0){
			
			final ListBox herkuenfte = new ListBox();
			herkuenfte.setWidth("170px");
			
			for(Extraction extraction : zutat.getExtractions()){
				herkuenfte.addItem(extraction.symbol);
			}
			herkuenfte.addItem("andere...");

			final ChangeHandler onHerkunftChange = new ChangeHandler(){
				public void onChange(ChangeEvent event){
					int count = herkuenfte.getItemCount();
					int selected = herkuenfte.getSelectedIndex();
					if(count-1 == selected){
						// we have selected the "andere" item
						differentOriginSelected(herkuenfte);
						
					} else {
						triggerHerkunftChange(zutat, herkuenfte);
					}
				}

			};
			herkuenfte.addChangeHandler(onHerkunftChange);
		    
			int row = specificationTable.getRowCount();
			specificationTable.setHTML(row, 0, "Herkunft");
			final FlowPanel flow = new FlowPanel();
//			flow.setBorderWidth(0);
//			flow.setSpacing(0);
			flow.addStyleName("littleZutatSpec");
			specificationTable.setWidget(row,1,flow);
			flow.add(herkuenfte);
			
			kmText.setHTML("ca. " + zutatSpec.getKmDistanceRounded() + "km");
	    	flow.add(kmText);
	    	flow.insert(newExtractionBox,0);
	    	
	    	newExtractionBox.setVisible(false);
			
	    	// ------------------ This part of the code not really in use?? -------------
	    	//TODO each extraction has its own season... this has flaws .. hack? right now it is anyway a hack
	    	final Anchor moreExtractions = new Anchor("+");
	    	moreExtractions.setStylePrimaryName("ohhLittlePlus");
	    	moreExtractions.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					moreExtractions.setVisible(false);
					int width = herkuenfte.getOffsetWidth();
					herkuenfte.setVisible(false);
					newExtractionBox.setWidth(Integer.toString(width)+"px");
					newExtractionBox.setVisible(true);
					
					kmText.setHTML("<a style='margin-left:3px;cursor:pointer;cursor:hand;'>berechnen</a>");

					clickerHandler = new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							try {
								Geocoder geocoder = new Geocoder();
								geocoder.setBaseCountryCode("ch");
							    geocoder.getLocations(newExtractionBox.getText(), new LocationCallback() {
							    public void onFailure(int statusCode) {
							    	  kmText.setHTML("Adresse nicht auffindbar!");
							    	  Timer t = new Timer() {
							    		  public void run() {
							    			  kmText.setHTML("<a style='margin-left:3px;cursor:pointer;cursor:hand;'>berechnen</a>");
							    		  }
							    	  };
							    	  t.schedule(1000);
							    }

							    public void onSuccess(JsArray<Placemark> locations) {
						    	  Placemark place = locations.get(0);
						    	  String adress = place.getAddress();
						    	  GWT.log("Sie befinden sich in: " +adress +"  ");
						    	  herkuenfte.insertItem(adress, 0);
						    	  Extraction element = new Extraction(adress);
						    	  // TODO come up with stuff like seasons and so forth..
						    	  element.startSeason = zutat.stdExtraction.startSeason;
						    	  element.stopSeason = zutat.stdExtraction.stopSeason;
						    	  element.stdCondition = zutat.stdExtraction.stdCondition;
						    	  element.stdMoTransportation = zutat.stdExtraction.stdMoTransportation;
						    	  element.stdProduction = zutat.stdExtraction.stdProduction;

						    	  zutat.getExtractions().add(0, element);
						    	  zutatSpec.setHerkunft(element);
						    	  newExtractionBox.setVisible(false);
						    	  herkuenfte.setVisible(true);
						    	  herkuenfte.setSelectedIndex(0);
						    	  triggerHerkunftChange(zutat, herkuenfte);
						    	  moreExtractions.setVisible(true);
						        }
						    });
							    
							} finally {
						    	//TODO no request happens if you are not online
								// check earlier an deactiate the moreExtractions "plus"
						    }
						}
					};
					if(handlersNotAdded){
						kmText.addClickHandler(clickerHandler);
						handlersNotAdded = false;
					}
				}
			});
					

	    	// i want no more plus, but the "andere" field
//	    	flow.add(moreExtractions);
			
	    	// select the current extraction in the list
	    	// compare extraction places (names)
	    	int index = -1;
	    	for (Extraction extr : zutat.getExtractions())
	    	{
	    		if (zutatSpec.getHerkunft().symbol == extr.symbol)
	    			index = zutat.getExtractions().indexOf(extr);
	    	}
	    	if (index != -1)
	    		herkuenfte.setSelectedIndex(index);
	    	// TODO what happens if index is not found?
	    		
		}
		

		
		if(zutat.moTransportations != null && zutat.moTransportations.size()>0){
			int row = specificationTable.getRowCount();
			specificationTable.setHTML(row, 0, "Transport");
			flowTransport = new FlowPanel();
			specificationTable.setWidget(row,1,flowTransport);
			
			for(final MoTransportation moTransportations : zutat.moTransportations){
				RadioButton transport = new RadioButton("Transportations",moTransportations.symbol);
				
				// hack to take the first one...
				if(zutatSpec.getTransportmittel() == null){
					MoTransportation transportmittel = new MoTransportation(moTransportations.symbol,moTransportations.factor);
					zutatSpec.setTransportmittel(transportmittel);
				}
				
				
				if(moTransportations.symbol.equalsIgnoreCase(zutatSpec.getTransportmittel().symbol)){
					transport.setValue(true);
				}
				transport.addClickHandler(new ClickHandler() {
				      public void onClick(ClickEvent event) {
				        boolean checked = ((RadioButton) event.getSource()).isChecked();
				        if(checked){
				        	zutatSpec.setTransportmittel(moTransportations);
				        	updateZutatCO2();
				        }
				      }
				    });
				
				flowTransport.add(transport);
			}
			
		}
		
		if(zutat.productions != null && zutat.productions.size()>0){
			int row = specificationTable.getRowCount();
			specificationTable.setHTML(row, 0, "Herstellung");
			FlowPanel flow = new FlowPanel();
			specificationTable.setWidget(row,1,flow);
			
			for(final Production production : zutat.productions){
				RadioButton productionBox = new RadioButton("productions",production.symbol);
				
				// hack to take the first one...
				if(zutatSpec.getProduktion() == null){
					Production herstellung = new Production(production.symbol);
					zutatSpec.setProduktion(herstellung);
				}

				if(production.symbol.equalsIgnoreCase(zutatSpec.getProduktion().symbol)){
					productionBox.setValue(true);
				}
				
				productionBox.addClickHandler(new ClickHandler() {
				      public void onClick(ClickEvent event) {
				        boolean checked = ((RadioButton) event.getSource()).isChecked();
				        if(checked){
				        	zutatSpec.setProduktion(production);
				        	updateZutatCO2();
				        }
				      }
				    });

				flow.add(productionBox);

			}
		}
		
		
		if(zutat.conditions != null && zutat.conditions.size()>0){
			int row = specificationTable.getRowCount();
			specificationTable.setHTML(row, 0, "Zustand");
			FlowPanel flow = new FlowPanel();
			specificationTable.setWidget(row,1,flow);
			
			for(final IngredientCondition condition : zutat.conditions){
				RadioButton conditionBox = new RadioButton("conditions",condition.symbol);
				// hack to take the first one...
				if(zutatSpec.getZustand() == null){
					IngredientCondition zustand = new IngredientCondition(condition.symbol);
					zutatSpec.setZustand(zustand);
				}
				if(condition.symbol.equalsIgnoreCase(zutatSpec.getZustand().symbol)){
					conditionBox.setValue(true);
				}
				conditionBox.addClickHandler(new ClickHandler() {
				      public void onClick(ClickEvent event) {
				        boolean checked = ((RadioButton) event.getSource()).isChecked();
				        if(checked){
				        	zutatSpec.setZustand(condition);
				        	updateZutatCO2();
				        }
				      }
				    });

				flow.add(conditionBox);
			}
		}	
		// set the focus corectly
		costTextBox.setFocus(true);
		costTextBox.selectAll();
	}
	

	private void triggerHerkunftChange(final Ingredient zutat,
			final ListBox herkuenfte) {
		Boolean notChanged = true;
		// TODO update also choice for moTransportations
		zutatSpec.setHerkunft(zutat.getExtractions().get((herkuenfte.getSelectedIndex())) );
		// the case that nothing changed
		if(TopPanel.currentHerkunft.contentEquals(zutatSpec.getHerkunft().symbol)){
			
			zutatSpec.setDistance(0.0);
			kmText.setHTML("ca. " + zutatSpec.getKmDistanceRounded() + "km");
	    	notChanged = false;
	    	
		} else {
		for(SingleDistance singleDistance : presenter.getClientData().getDistances()){
			if(singleDistance.getFrom().contentEquals(TopPanel.currentHerkunft) && 
					singleDistance.getTo().contentEquals(zutatSpec.getHerkunft().symbol)){
				
				zutatSpec.setDistance(singleDistance.getDistance());
				notChanged = false;
				boolean deselect = false;
				if(singleDistance.getTriedRoad() && !singleDistance.getRoad() ){
					if(zutatSpec.getTransportmittel().symbol.equalsIgnoreCase("LKW") ){
						deselect = true;
					}
					
					// TODO
					if(flowTransport != null){
						for(int i=0;i<flowTransport.getWidgetCount();i++){
							RadioButton radioTransport = (RadioButton) flowTransport.getWidget(i);
							if(radioTransport.getText().equalsIgnoreCase("Schiff")){
								radioTransport.setEnabled(true);
							if(deselect){
								radioTransport.setChecked(true);
								for(final MoTransportation moTransportations : zutat.moTransportations){
									if(moTransportations.symbol.equalsIgnoreCase("Schiff")){
										zutatSpec.setTransportmittel(moTransportations);
									}
								}
							}}
							if(radioTransport.getText().equalsIgnoreCase("LKW")){
								radioTransport.setEnabled(false);
							}
						}
					}
					
				} else if(singleDistance.getTriedRoad() && singleDistance.getRoad()) {
					if(zutatSpec.getTransportmittel().symbol.equalsIgnoreCase("Schiff") ){
						deselect = true;
					}
					
					// TODO
					if(flowTransport != null){
						for(int i=0;i<flowTransport.getWidgetCount();i++){
							RadioButton radioTransport = (RadioButton) flowTransport.getWidget(i);
							if( radioTransport.getText().equalsIgnoreCase("LKW")){
								radioTransport.setEnabled(true);
								
							if(deselect){
								radioTransport.setChecked(true);
								for(final MoTransportation moTransportations : zutat.moTransportations){
									if(moTransportations.symbol.equalsIgnoreCase("LKW")){
										zutatSpec.setTransportmittel(moTransportations);
									}
								}
							}}
							if(radioTransport.getText().equalsIgnoreCase("Schiff")){
								radioTransport.setEnabled(false);
							}
						}
					}
				}

				kmText.setHTML("ca. " + zutatSpec.getKmDistanceRounded() + "km");
		    	break;
			}

		}
		
		if(notChanged){
			kmText.setHTML("Strecke nicht gefunden");
			zutatSpec.setDistance(0.0);
			
			final String to = zutatSpec.getHerkunft().symbol;
			final String from = TopPanel.currentHerkunft;
			
			geocoder.getLocations(to, new LocationCallback() {
			      public void onFailure(int statusCode) {
			        Window.alert("Herkunft nicht auffindbar: "+ to);
			      }

			      public void onSuccess(final JsArray<Placemark> locationsTo) {

			    	  geocoder.getLocations(from, new LocationCallback() {
					      public void onFailure(int statusCode) {
					        Window.alert("Adresse nicht auffindbar: " + from);
					      }

							      public void onSuccess(JsArray<Placemark> locationsFrom) {

							    		Placemark place = locationsTo.get(0);
							    		double distance = locationsFrom.get(0).getPoint().distanceFrom(place.getPoint());
							    		zutatSpec.setDistance(distance);

							    		kmText.setHTML("ca. " + zutatSpec.getKmDistanceRounded() + "km");
							      }
							    });
			      }
			    });
			
		}
		}

		updateZutatCO2();
	}
	
	
	
	
	public void updateSaison(IngredientSpecification zutatSpec) {
		// if it is Greenhouse, or conserved then it should be kohärent...
		
		Date date = DateTimeFormat.getFormat("MM").parse(Integer.toString(presenter.getSelectedMonth()));
		// In Tagen
//		String test = InfoZutat.zutat.getStartSeason();
		Date dateStart = DateTimeFormat.getFormat("dd.MM").parse( zutatSpec.getStartSeason() );		
		Date dateStop = DateTimeFormat.getFormat("dd.MM").parse( zutatSpec.getStopSeason() );
		
		if(		dateStart.before(dateStop)  && date.after(dateStart) && date.before(dateStop) ||
				dateStart.after(dateStop) && !( date.before(dateStart) && date.after(dateStop)  ) ){
			
			
			specificationTable.setHTML(0, 1, "Diese Zutat hat Saison");
			
			styleHinweis(false);
			hinweisPanel.setText("Angaben sind kohärent.");
			hinweisDetails.setText("Es ist möglich die Zutat frisch und lokal zu beziehen.");
			
		} else {
			specificationTable.setHTML(0, 1, "Diese Zutat hat keine Saison");
			
			
			if(zutatSpec.getHerkunft().symbol.equalsIgnoreCase(stdIngredient.stdExtractionSymbol)
					&& zutatSpec.getProduktion() != null && !zutatSpec.getProduktion().symbol.equalsIgnoreCase("GH")
					&& zutatSpec.getZustand() != null && zutatSpec.getZustand().symbol.equalsIgnoreCase("frisch")){
			
			// unvollständig:
			
			styleHinweis(true);
			hinweisPanel.setText("Angaben sind unvollständig.");
			hinweisDetails.setText("Bitte geben Sie an ob die Zutat importiert, konserviert oder im Gewächaus produziert wurde.");
			} else {
				styleHinweis(false);
				hinweisPanel.setText("Angaben sind kohärent.");
				String text = "Die Zutat wurde ";
				
				if(!zutatSpec.getHerkunft().symbol.equalsIgnoreCase(stdIngredient.stdExtractionSymbol)){
					text = text +"aus "+ zutatSpec.getHerkunft().symbol +" mit dem " +zutatSpec.getTransportmittel().symbol + " importiert, ";
				} 
				if(zutatSpec.getProduktion() != null && zutatSpec.getProduktion().symbol.equalsIgnoreCase("GH")){
					text = text +"im Gewächshaus produziert, ";
				}
				if(zutatSpec.getZustand() != null && !zutatSpec.getZustand().symbol.equalsIgnoreCase("frisch")){
					text = text + zutatSpec.getZustand().symbol + ", ";
				}
				String shortText = text.substring(0, text.length()-2);
				int indexShortText = shortText.lastIndexOf(", ");
				String readyText;
				if(indexShortText == -1){
					readyText = shortText +".";
				} else {
				 readyText = shortText.substring(0, indexShortText) + " und " +shortText.substring(indexShortText + 2, shortText.length()) + ".";
				}
				 hinweisDetails.setText( readyText);
			}
		}
		
		
	}

	
	
	//TODO here comes all the CO2 Logic
	public void updateZutatCO2(){
		
		if(stdIngredient.hasSeason != null && stdIngredient.hasSeason){
			updateSaison(zutatSpec);
		}
		
//		String formatted = NumberFormat.getFormat("##").format( zutatSpec.getCalculatedCO2Value() );
		
//		valueLabel.setText(formatted + "g CO2-Äquivalent");
		if(selectedRow != -1){
//			if(EaternityRechner.zutatImMenu.contains(zutat)){
//				EaternityRechner.zutatImMenu.set(EaternityRechner.zutatImMenu.indexOf(zutat), zutat);
//				
//				menuTable.setHTML(selectedRow, 3, "ca "+formatted + "g *");
				recipe.Zutaten.set(selectedRow, zutatSpec);
//				Double MenuLabelWert = getRezeptCO2(recipe.Zutaten);
				
//				String formattedMenu = NumberFormat.getFormat("##").format(MenuLabelWert);
//				suggestTable.setHTML(0,1,"ca <b>"+formattedMenu+"g</b> *");
				
				rezeptviewParent.changeIcons(selectedRow, zutatSpec);
				rezeptviewParent.updateSuggestion();
//				rezeptviewParent.updateSuggestion(EaternityRechner.SuggestTable, EaternityRechner.MenuTable);
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
	public void setZutatSpec(IngredientSpecification zutatSpec) {
		this.zutatSpec = zutatSpec;
	}
	public IngredientSpecification getZutatSpec() {
		return zutatSpec;
	}
	public void setSelectedRow(int selectedRow) {
		this.selectedRow = selectedRow;
	}
	public int getSelectedRow() {
		return selectedRow;
	}
	public void setRezept(Recipe recipe) {
		this.recipe = recipe;
	}
	public Recipe getRezept() {
		return recipe;
	}
	private Double getRezeptCO2(List<IngredientSpecification> Zutaten) {
		Double MenuLabelWert = 0.0;
		for (IngredientSpecification zutatSpec : Zutaten) { 
			MenuLabelWert +=zutatSpec.getCalculatedCO2Value();

		}
		return MenuLabelWert;
	}
}
