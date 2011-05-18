package ch.eaternity.client;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;


import ch.eaternity.shared.Device;
import ch.eaternity.shared.Extraction;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.Kitchen;
import ch.eaternity.shared.SingleDistance;
import ch.eaternity.shared.IngredientSpecification;

import com.google.gwt.cell.client.AbstractEditableCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.TextInputCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.geocode.DirectionQueryOptions;
import com.google.gwt.maps.client.geocode.DirectionResults;
import com.google.gwt.maps.client.geocode.Directions;
import com.google.gwt.maps.client.geocode.DirectionsCallback;
import com.google.gwt.maps.client.geocode.DirectionsPanel;
import com.google.gwt.maps.client.geocode.Geocoder;
import com.google.gwt.maps.client.geocode.LatLngCallback;
import com.google.gwt.maps.client.geocode.LocationCallback;
import com.google.gwt.maps.client.geocode.Placemark;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

public class KitchenDialog extends DialogBox{

	 private static final List<Device> CONTACTS = Arrays.asList(
		      new Device("","",0.0,  Arrays.asList(1l,5l,10l,20l), 10l));
	 
	 
	
	interface Binder extends UiBinder<Widget, KitchenDialog> { }
	private static final Binder binder = GWT.create(Binder.class);

	private final static Geocoder geocoder = new Geocoder();
	private final DataServiceAsync distancesService = GWT.create(DataService.class);

	@UiField Button executeButton;
	@UiField FlexTable mapsTable;
	@UiField ScrollPanel scrollPanel;
	@UiField FlexTable summaryTable;
	@UiField FlexTable deviceTable;
	@UiField FlexTable personTable;
	@UiField Button locationButton;
	@UiField Button addDevice;
	@UiField Button addPerson;
	@UiField TextBox clientLocationDialog;
	@UiField TextBox kitchenNameTextBox;
	@UiField static ListBox energyMix;
	@UiField static ListBox kitchens;
//	@UiField Button commitButton;
	@UiField
	static CellTable<Device> cellTable  = new CellTable<Device>();
//	@UiField SimplePager pager;
	
	ArrayList<SingleDistance> allDistances = new ArrayList<SingleDistance>();
	String currentLocation;
	String kitchenName;
	


	Integer TimeToWait = 1;

	public KitchenDialog(String string) {	
//		String kitchenName = kitchens.getItemText(kitchens.getSelectedIndex());
		String kitchenName = "test";
		processAddress(string,true);
		this.kitchenName = kitchenName;
		

	
	}


	private void openDialog() {
		setWidget(binder.createAndBindUi(this));
		show();
		scrollPanel.setHeight("420px");
		center();
		clientLocationDialog.setText(currentLocation);
		setText(kitchenName);
		setAnimationEnabled(true);
		setGlassEnabled(true);
		
		energyMix.addItem("NatureStar");
		
		initCellTable();
	    
	}
	
	


	private void initCellTable() {
		

		
		cellTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		editableCells = new ArrayList<AbstractEditableCell<?, ?>>();

	    // Add a text column to show the name.
		addColumn(new EditTextCell(), "Gerät", new GetValue<String>() {
	      public String getValue(Device contact) {
	        return contact.deviceName;
	      }
	    }, new FieldUpdater<Device, String>() {
	      public void update(int index, Device object, String value) {
	        pendingChanges.add(new DeviceNameChange(object, value));
	      }
	    });
		
	    // Add a text column to show the name.
		addColumn(new EditTextCell(), "Spezifikation", new GetValue<String>() {
	      public String getValue(Device contact) {
	        return contact.deviceSpec;
	      }
	    }, new FieldUpdater<Device, String>() {
	      public void update(int index, Device object, String value) {
	        pendingChanges.add(new DeviceSpecChange(object, value));
	      }
	    });
	    
	 

	    // Cell for kWConsumption
		Column<Device, String> numberColumn = addColumn(new EditTextCell(),
		        "Verbrauch (kWh/h)", new GetValue<String>() {
		          @SuppressWarnings("deprecation")
		          public String getValue(Device object) {
		        	  return object.kWConsumption.toString();
		          }
		        },  new FieldUpdater<Device, String>() {
		  	      public void update(int index, Device object, String value) {
		  	        pendingChanges.add(new DeviceSpecChange(object, value));
		  	      }
		  	    });
//	    cellTable.addColumn(numberColumn, "Energieverbrauch in kWh/h");
	    
	    // Cell for durations
	    Column<Device, String> durationsColumn = addColumn(new EditTextCell(),
	        "Laufzeiten", new GetValue<String>() {
	          @SuppressWarnings("deprecation")
	          public String getValue(Device object) {
	        	  return object.durations.toString().replace(']', ' ').replace('[', ' ');
	          }
	        },  new FieldUpdater<Device, String>() {
		  	      public void update(int index, Device object, String value) {
			  	        pendingChanges.add(new DeviceDurationsChange(object, value));
			  	      }
			  	    });
	    
//	    cellTable.addColumn(durationsColumn, "Laufzeiten");
	    
	    
	    // SelectionCell.
	    final Category[] categories;
	    categories = new Category[6];
	    for (int i = 0; i < 6; i++) {
	        categories[i] = new Category("bla");
	      }
	    List<String> options = new ArrayList<String>();
	    for (Category category : categories) {
	      options.add(category.getDisplayName());
	    }
//	    
//	    addColumn(new CheckboxCell(), "Checkbox", new GetValue<Boolean>() {
//	      public Boolean getValue(ContactInfo contact) {
//	        // Checkbox indicates that the contact is a relative.
//	        // Index 0 = Family.
//	        return contact.getCategory() == categories[0];
//	      }
//	    }, new FieldUpdater<ContactInfo, Boolean>() {
//	      public void update(int index, ContactInfo object, Boolean value) {
//	        if (value) {
//	          // If a relative, use the Family Category.
//	          pendingChanges.add(new CategoryChange(object, categories[0]));
//	        } else {
//	          // If not a relative, use the Contacts Category.
//	          pendingChanges.add(new CategoryChange(object,
//	              categories[categories.length - 1]));
//	        }
//	      }
//	    });
	    

	    addColumn(new SelectionCell(options), "Std-Laufzeit", new GetValue<String>() {
	      public String getValue(Device contact) {
	        return contact.stdDuration.toString();
	      }
	    }, new FieldUpdater<Device, String>() {
	      public void update(int index, Device object, String value) {
	        for (Category category : categories) {
	          if (category.getDisplayName().equals(value)) {
	            pendingChanges.add(new CategoryChange(object, category));
	            break;
	          }
	        }
	      }
	    });
	    
	    
//	    Column<Device, Number> stdDurationColumn = addColumn(new NumberCell(),
//		        "Std-Laufzeit", new GetValue<Number>() {
//		          @SuppressWarnings("deprecation")
//		          public Number getValue(Device object) {
//		        	  return object.stdDuration;
//		          }
//		        }, new FieldUpdater<Device, String>() {
//			  	      public void update(int index, Device object, String value) {
//				  	        pendingChanges.add(new DeviceSpecChange(object, value));
//				  	      }
//				  	    });
//	    cellTable.addColumn(stdDurationColumn, "Std-Laufzeit");
	    

	    // Add a selection model to handle user selection.
	    final SingleSelectionModel<Device> selectionModel = new SingleSelectionModel<Device>();
	    cellTable.setSelectionModel(selectionModel);
	    selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
	      public void onSelectionChange(SelectionChangeEvent event) {
	    	  Device selected = selectionModel.getSelectedObject();
	        if (selected != null) {
//	          Window.alert("You selected: " + selected.deviceName);
	        }
	      }
	    });

	    // Set the total row count. This isn't strictly necessary, but it affects
	    // paging calculations, so its good habit to keep the row count up to date.
	    cellTable.setRowCount(CONTACTS.size(), true);

	    // Push the data into the widget.
	    cellTable.setRowData(0, CONTACTS);
	}

	/**
	   * Add a column with a header.
	   *
	   * @param <C> the cell type
	   * @param cell the cell used to render the column
	   * @param headerText the header string
	   * @param getter the value getter for the cell
	   */
	  private <C> Column<Device, C> addColumn(Cell<C> cell, String headerText,
	      final GetValue<C> getter, FieldUpdater<Device, C> fieldUpdater) {
	    Column<Device, C> column = new Column<Device, C>(cell) {
	      @Override
	      public C getValue(Device object) {
	        return getter.getValue(object);
	      }
	    };
	    column.setFieldUpdater(fieldUpdater);
	    if (cell instanceof AbstractEditableCell<?, ?>) {
	      editableCells.add((AbstractEditableCell<?, ?>) cell);
	    }
	    cellTable.addColumn(column, headerText);
	    return column;
	  }
	  private static interface GetValue<C> {
		    C getValue(Device contact);
		  }

	  /**
	   * The list of cells that are editable.
	   */
	  private List<AbstractEditableCell<?, ?>> editableCells;

	  /**
	   * The list of pending changes.
	   */
	  private List<PendingChange<?>> pendingChanges = new ArrayList<
	      PendingChange<?>>();
	  
	  private abstract static class PendingChange<T> {
		    private final Device contact;
		    private final T value;

		    public PendingChange(Device contact, T value) {
		      this.contact = contact;
		      this.value = value;
		    }

		    /**
		     * Commit the change to the contact.
		     */
		    public void commit() {
		      doCommit(contact, value);
		    }

		    /**
		     * Update the appropriate field in the {@link ContactInfo}.
		     *
		     * @param contact the contact to update
		     * @param value the new value
		     */
		    protected abstract void doCommit(Device contact, T value);
		  }
	  
	  /**
	   * Updates the device name.
	   */
	  private static class DeviceNameChange extends PendingChange<String> {

	    public DeviceNameChange(Device contact, String value) {
	      super(contact, value);
	      
//	      if (value.length() > 1){
//	    	  int row = cellTable.getRowCount();
//	    	  
//	    	  contact.
//	    	  cellTable.setRowData(row, CONTACTS);
//	      }
	    }

	    @Override
	    protected void doCommit(Device contact, String value) {
	      contact.deviceName = value;
	    }
	  }
	  
	  /**
	   * Updates the category.
	   */
	  private static class CategoryChange extends PendingChange<Category> {

	    public CategoryChange(Device contact, Category value) {
	      super(contact, value);
	    }

	    @Override
	    protected void doCommit(Device contact, Category value) {
	      contact.stdDuration = Long.parseLong(value.getDisplayName());
	    }
	  }

	  /**
	   * A contact category.
	   */
	  public static class Category {

	    private final String displayName;

	    private Category(String displayName) {
	      this.displayName = displayName;
	    }

	    public String getDisplayName() {
	      return displayName;
	    }
	  }
	  
	  /**
	   * Updates the specifiation name.
	   */
	  private static class DeviceSpecChange extends PendingChange<String> {

	    public DeviceSpecChange(Device contact, String value) {
	      super(contact, value);
	    }

	    @Override
	    protected void doCommit(Device contact, String value) {
	      contact.deviceSpec = value;
	    }
	  }
	  
	  /**
	   * Updates the Durations List.
	   */
	  private static class DeviceDurationsChange extends PendingChange<String> {

	    public DeviceDurationsChange(Device contact, String value) {
	      super(contact, value);
	    }

	    @Override
	    protected void doCommit(Device contact, String value) {
	    	contact.durations = parsePropsToList(value,",");
	    }
	  }
	  
	  public static List<Long> parsePropsToList(
			  String propName,
			  String delim){
		  
//		  for(int i = 1;i <= listToFill.size(); i++){
//			  listToFill.remove(i);
//		  }
//		  List<Long> listToFill = Arrays.asList({});
		  ArrayList<Long>listToFill = new ArrayList<Long>();
		  
		   //This is paired down for convenience - assume getSplitList correctly parses to List<String>
		   List<String> stringList = getSplitList(propName, delim);
		   for(String s : stringList){
//		       listToFill.add((T)s.trim());
			   Long addLong = Long.parseLong( s.trim());
			   if(addLong != null){
			   listToFill.add(addLong);
			   }
		   }
		   return listToFill;
		}
	  
	private static List<String> getSplitList(String propName, String delim) {
		List<String> myarray = null;
		String[] parts = propName.split(delim);
		myarray = Arrays.asList(parts);
//		StringTokenizer tokens = new StringTokenizer(propName,delim);
//		int i = 0;
//		while(tokens.hasMoreTokens()) {
//		
//		myarray.add(i++, tokens.nextToken());
//		}
		return myarray;
	}

	final static LatLngCallback geocodeResultListener = new
	LatLngCallback()
	{
		public void onSuccess(LatLng latlng)
		{

			//			herkunft.add(latlng.getLatitude());
			//			herkunft.add(latlng.getLongitude());
			Window.alert("- " + latlng.getLongitude() + " -");

		}

		public void onFailure()
		{
			Window.alert("Unable to geocode");
		}

	};

	@Override
	protected void onPreviewNativeEvent(NativePreviewEvent preview) {
		super.onPreviewNativeEvent(preview);

		NativeEvent evt = preview.getNativeEvent();
		if (evt.getType().equals("keydown")) {
			// Use the popup's key preview hooks to close the dialog when either
			// enter or escape is pressed.
			switch (evt.getKeyCode()) {
			case KeyCodes.KEY_ENTER:
				mapsTable.removeAllRows();
				processAddress(clientLocationDialog.getText(),false);
				break;
			case KeyCodes.KEY_ESCAPE:
				hide();
				break;
			}
		}
	}


	private void saveDistances() {

		if(!allDistances.isEmpty()){
			distancesService.addDistances(allDistances, new AsyncCallback<Integer>() {
				public void onFailure(Throwable error) {
					Window.alert("Fehler : "+ error.getMessage());
				}
				public void onSuccess(Integer ignore) {
					Search.getClientData().getDistances().addAll(allDistances);
					updateAllZutaten(); 
					//				Window.alert(Integer.toString(ignore) + " Distanzen gespeichert.");
				}
			});
		}

		hide();
	}

	private  void calculateDistances(String string, boolean firstTime) {
		ArrayList<SingleDistance> distances = (ArrayList<SingleDistance>) Search.getClientData().getDistances();
		ArrayList<String> distancesRequested = new ArrayList<String>();
		boolean notFound;
		List<Ingredient> zutaten = Search.getClientData().getIngredients();
		for( Ingredient zutat : zutaten){
			for(Extraction herkunft : zutat.getExtractions()){

				notFound = true;
				for(SingleDistance singleDistance : distances){
					if(singleDistance.getFrom().contentEquals(string) && 
							singleDistance.getTo().contentEquals(herkunft.symbol)){
						notFound = false;
					}
				}
				if(notFound && !distancesRequested.contains(herkunft.symbol) ){

					distancesRequested.add(herkunft.symbol);
					getDistance( string, herkunft.symbol);
				}


			}

		}
		
		// we always want the dialog, as there are also other informations
//		if(!distancesRequested.isEmpty() && firstTime){
			openDialog();
//		} else {
//			updateAllZutaten();
//		}
	}


	private void updateAllZutaten() {
		for(Widget widget : EaternityRechner.rezeptList){
			RezeptView rezeptView = ((RezeptView) widget);
			List<IngredientSpecification> zutaten = new ArrayList<IngredientSpecification>();
			zutaten.addAll(rezeptView.getRezept().Zutaten);
			for(IngredientSpecification zutatSpec : zutaten ){
				int index = rezeptView.getRezept().Zutaten.indexOf(zutatSpec);
				for(SingleDistance singleDistance : Search.getClientData().getDistances()){
					if(singleDistance.getFrom().contentEquals(TopPanel.currentHerkunft) && 
							singleDistance.getTo().contentEquals(zutatSpec.getHerkunft().symbol)){

						zutatSpec.setDistance(singleDistance.getDistance());
						rezeptView.getRezept().Zutaten.set(index, zutatSpec);

						break;
					}

				}
			}
			rezeptView.updateSuggestion();

			if(rezeptView.addInfoPanel.getWidgetCount() ==2){
				rezeptView.addInfoPanel.remove(1);
			}
		}
	}


	private void showTable() {

		summaryTable.removeAllRows();
		//	    while(iter.hasNext()){
		for(SingleDistance singleDistance : allDistances){
			String to = singleDistance.getTo();
			String formatted = NumberFormat.getFormat("##").format( singleDistance.getDistance()/100000 );
			if(formatted.contentEquals("0")){
				summaryTable.setText(summaryTable.getRowCount(), 0, "nach " + to +" : ca. " + formatted + "km");
			}else{
				summaryTable.setText(summaryTable.getRowCount(), 0, "nach " + to +" : ca. " + formatted + "00km");
			}
		}

	}

	@UiHandler("executeButton")
	void onOkayClicked(ClickEvent event) {
		
        for (PendingChange<?> pendingChange : pendingChanges) {
	          pendingChange.commit();
	        }
        pendingChanges.clear();
		
		saveDistances();
		
		final Kitchen kitchen = new Kitchen(kitchenName);
		kitchen.energyMix = energyMix.getItemText(energyMix.getSelectedIndex());
		kitchen.location = currentLocation;
		kitchen.setEmailAddressOwner(EaternityRechner.loginInfo.getEmailAddress());
		
		
		  
		for(int i = 0; i < deviceTable.getRowCount();i++){
			TextBox name = (TextBox) deviceTable.getWidget(i, 0);
			TextBox cons = (TextBox) deviceTable.getWidget(i, 1);
			TextBox dura = (TextBox) deviceTable.getWidget(i, 2);
			
			String[] test = dura.getText().split(",");
			Long[] durations = null;
			for(int j = 0; j < test.length; j++){
				durations[j] = Long.parseLong(test[i]);
			}
			
//			kitchen.devices.add(new Device(name.getText(),Double.valueOf(cons.getText()),durations));
		}
		
		for(int i = 0; i < personTable.getRowCount();i++){
//			TextBox name = (TextBox) personTable.getWidget(i, 0);
			TextBox email = (TextBox) personTable.getWidget(i, 1);		
			
			kitchen.personal.add(email.getText());
		}
		
		distancesService.addKitchen(kitchen, new AsyncCallback<Long>() {
			public void onFailure(Throwable error) {
				Window.alert("Fehler : "+ error.getMessage());
			}
			public void onSuccess(Long ignore) {
				Search.getClientData().kitchens.add(kitchen);
				kitchens.addItem(kitchen.getSymbol());
			}
		});
		
		
		hide();
	}

	@UiHandler("locationButton")
	void onLocClicked(ClickEvent event) {
		mapsTable.removeAllRows();
		processAddress(clientLocationDialog.getText(),false);
	}


	void processAddress(final String address, final boolean firstTime) {
		if (address.length() > 1) { 
			geocoder.setBaseCountryCode("ch");
			geocoder.getLocations(address, new LocationCallback() {
				public void onFailure(int statusCode) {
					TopPanel.locationLabel.setText("Wir können diese Adresse nicht finden: ");
				}

				public void onSuccess(JsArray<Placemark> locations) {
					Placemark place = locations.get(0);
					TopPanel.locationLabel.setText("Sie befinden sich in: " +place.getAddress() +"  ");
					currentLocation = place.getAddress();
					setText("Berechne alle Routen von: " + place.getAddress());
					TopPanel.currentHerkunft = place.getAddress();

					calculateDistances(place.getAddress(),firstTime);

				}
			});
		} else {
			TopPanel.locationLabel.setText("Bitte geben Sie hier Ihre Adresse ein: ");
		}

	}







	void getDistance(final String from, final String to ) {

		Timer t = new Timer() {
			public void run() {



				geocoder.getLocations(to, new LocationCallback() {
					public void onFailure(int statusCode) {
						Window.alert("Diese Zutat hat einen falsche Herkunft: "+ to);
					}

					public void onSuccess(final JsArray<Placemark> locationsTo) {

						geocoder.getLocations(from, new LocationCallback() {
							public void onFailure(int statusCode) {
								Window.alert("Wir können Ihre Adresse nicht zuordnen: " + from);
							}

							public void onSuccess(JsArray<Placemark> locationsFrom) {

								simpleDirectionsDemo(locationsFrom,locationsTo);
							}
						});
					}
				});
				TimeToWait = TimeToWait - 1000;
			}
		};
		t.schedule(TimeToWait);
		TimeToWait = TimeToWait + 1000;
	}


	public void simpleDirectionsDemo(final JsArray<Placemark> locationFrom, final JsArray<Placemark> locationTo) {
		Placemark placeFrom = locationFrom.get(0);
		Placemark placeTo = locationTo.get(0);
		final String from = placeFrom.getAddress();
		final String to = placeTo.getAddress();

		MapWidget map = new MapWidget(locationFrom.get(0).getPoint(), 15);
		map.setHeight("300px");
		DirectionsPanel directionsPanel = new DirectionsPanel();
		DirectionQueryOptions opts = new DirectionQueryOptions(map, directionsPanel);
		int currentRow = mapsTable.getRowCount();
		mapsTable.setWidget(currentRow, 0, map);
		mapsTable.setWidget(currentRow, 1, directionsPanel);

		opts.setLocale("de_DE");
		opts.setPreserveViewport(true);
		opts.setRetrievePolyline(false);
		opts.setRetrieveSteps(false);

		String query = "from: "+from+" to: "+to;
		Directions.load(query, opts, new DirectionsCallback() {

			public void onFailure(int statusCode) {
				//	        Window.alert("Es ist ein Fehler aufgetreten...");
				getDistanceEstimateToCurrent(locationFrom,locationTo,to,true);
				// remove last row
				mapsTable.removeRow(mapsTable.getRowCount()-1);
			}

			public void onSuccess(DirectionResults result) {
				//	    	Window.alert(Double.toString(result.getDistance().inMeters())+"m from "+from+" to "+to);
				GWT.log("Successfully loaded directions. Von " + from + " nach " + to +".", null);
				double distance = result.getDistance().inMeters();
				SingleDistance singleDistance = new SingleDistance();
				singleDistance.setFrom(from);
				singleDistance.setTo(to);
				singleDistance.setRoad(true);
				singleDistance.setTriedRoad(true);
				singleDistance.setDistance(distance);

				allDistances.add(singleDistance);
				showTable();
				//	        	updateDistance(distance,zutatSpec);

			}
		});


	}

	void getDistanceEstimateToCurrent(JsArray<Placemark> locations, JsArray<Placemark> locations1, String to, Boolean tried) {

		Placemark place = locations.get(0);
		double distance = locations1.get(0).getPoint().distanceFrom(place.getPoint());

		SingleDistance singleDistance = new SingleDistance();
		singleDistance.setFrom(place.getAddress());
		singleDistance.setTo(to);
		singleDistance.setRoad(false);
		singleDistance.setTriedRoad(tried);
		singleDistance.setDistance(distance);

		allDistances.add(singleDistance);
		showTable();
		//	updateDistance(distance,zutatSpec);
	}

	
	  @UiHandler("kitchenNameTextBox")
	  void onNameChange(KeyUpEvent event) {
		  if(kitchenNameTextBox.getText().length()>1){
			  this.kitchenName = kitchenNameTextBox.getText();
			  setText(this.kitchenName);
		  }
	  }
	  
	  @UiHandler("addDevice")
	  void onAddDevicePress(ClickEvent event) {
		  
    	  int rowCell = cellTable.getRowCount();
    	  List<Device> newDevice = Arrays.asList(
    			      new Device("","",0.0,  Arrays.asList(1l,5l,10l,20l), 10l));
    	  cellTable.setRowData(rowCell, newDevice);
//		  
//		  TextBox name = new TextBox();
//		  name.setText("Name");
//		  TextBox consumption = new TextBox();
//		  consumption.setText("200");
//		  TextBox stdMinuten = new TextBox();
//		  stdMinuten.setText("5,10,20");
//		  

//		  deviceTable.setWidget(row,0,name);
//		  deviceTable.setWidget(row,1,consumption);
//		  deviceTable.setWidget(row,2,stdMinuten);
//		  
		  
		  final Button removeDevice = new Button("x");
		  removeDevice.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					deviceTable.removeRow(getWidgetRow(removeDevice,deviceTable));
				}
			});	
		  
		  int row = deviceTable.getRowCount();
		  deviceTable.setWidget(row,3,removeDevice);
		  
	  }
	  
	  @UiHandler("addPerson")
	  void onAddPersonPress(ClickEvent event) {
//		  TextBox name = new TextBox();
//		  name.setText("Name");
		  TextBox email = new TextBox();
		  email.setText("email");
		  
		  int row = personTable.getRowCount();
//		  personTable.setWidget(row,0,name);
		  personTable.setWidget(row,1,email);
		  
		  final Button removePerson = new Button("x");
		  removePerson.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					
					personTable.removeRow(getWidgetRow(removePerson,personTable));
				}
			});	
		  personTable.setWidget(row,2,removePerson);
	  }
	  
		private static int getWidgetRow(Widget widget, FlexTable table) {
			for (int row = 0; row < table.getRowCount(); row++) {
				for (int col = 0; col < table.getCellCount(row); col++) {
					Widget w = table.getWidget(row, col);
					if (w == widget) {
						return row;
					}
				}
			}
			throw new RuntimeException("Unable to determine widget row");
		}
		
}

