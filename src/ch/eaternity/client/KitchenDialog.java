package ch.eaternity.client;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import ch.eaternity.client.ui.EaternityRechnerView;
import ch.eaternity.client.ui.EaternityRechnerView.Presenter;
import ch.eaternity.shared.Device;
import ch.eaternity.shared.EnergyMix;
import ch.eaternity.shared.Extraction;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.Workgroup;
import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.SingleDistance;
import ch.eaternity.shared.IngredientSpecification;
import ch.eaternity.shared.Staff;

import com.google.gwt.cell.client.AbstractEditableCell;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
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
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;


public class KitchenDialog<T> extends DialogBox{
	
		static Long[] longList ={1l,5l,10l,20l};
	 private static List<Device> devicesHere = Arrays.asList(
		      new Device("","",0.0,  longList, 10l));
	 
	 private static List<Staff> personsHere = Arrays.asList(new Staff("Name","email"));
	 
	 
	
	interface Binder extends UiBinder<Widget, KitchenDialog> { }
	private static final Binder binder = GWT.create(Binder.class);

	private final static Geocoder geocoder = new Geocoder();
	private final DataServiceAsync dataService = GWT.create(DataService.class);

	@UiField Button executeButton;
	@UiField FlexTable mapsTable;
	@UiField ScrollPanel scrollPanel;
	@UiField FlexTable summaryTable;
	@UiField FlexTable deviceTable;
	@UiField FlexTable personTable;
	@UiField Button locationButton;
	@UiField Button addDevice;
	@UiField Button addPerson;
	@UiField Button deleteKitchen;
	@UiField TextBox clientLocationDialog;
	@UiField TextBox kitchenNameTextBox;
	@UiField static TextBox energyMix;
	@UiField static TextBox energyMixco2;
	@UiField static ListBox kitchens;
	@UiField Anchor leaveKitchen;
	@UiField Anchor newKitchen;
	@UiField HTMLPanel kitchen;
	
//	@UiField Button commitButton;
	@UiField
	static CellTable<Device> devidesCellTable  = new CellTable<Device>();
	
	@UiField
	static CellTable<Staff> personsCellTable  = new CellTable<Staff>();
	
//	@UiField SimplePager pager;
	
	ArrayList<SingleDistance> allDistances = new ArrayList<SingleDistance>();
	String currentLocation;
	String kitchenName;
	
	List<Workgroup> availableKitchens;
	Workgroup selectedKitchen;
	
	private Presenter<T> presenter;
	public void setPresenter(Presenter<T> presenter){
		this.presenter = presenter;
		
		availableKitchens = presenter.getClientData().kitchens;
		
		if(availableKitchens == null || availableKitchens.size() == 0){
			availableKitchens = new ArrayList<Workgroup>(1);
			Workgroup newKitchen = new Workgroup("neue Küche",currentLocation);
			newKitchen.energyMix = new EnergyMix("ewz.naturpower",0.01345);
			newKitchen.hasChanged = true;
			availableKitchens.add(newKitchen);
			
		}

		selectedKitchen = availableKitchens.get(0);
		this.kitchenName = selectedKitchen.getSymbol();
		currentLocation = selectedKitchen.location;
		devicesHere = selectedKitchen.devices;
		personsHere = selectedKitchen.personal;
		

		openDialog();
		
	}
	
	private EaternityRechnerView superDisplay;

	
	Integer timeToWaitForGeocode = 1; // in seconds ( to not flood, and be blocked)

	public KitchenDialog(String location, EaternityRechnerView superDisplay) {
		this.superDisplay = superDisplay;
//		String kitchenName = kitchens.getItemText(kitchens.getSelectedIndex());
//		String kitchenName = "neue Küche";
		processAddress(location,true);
		
		// this is only necessary for the first time...
		currentLocation = location;
		

	
	}


	private void addKitchenNamesToList(List<Workgroup> availableKitchens) {
		for(Workgroup kitchen : availableKitchens){
			if(kitchen != null){
				kitchens.addItem(kitchen.getSymbol(),Integer.toString(availableKitchens.indexOf(kitchen))); // +kitchen.location  kitchen.id.toString()+ kitchen.getSymbol() 
			}
	
		}

	}


	private void openDialog() {
		setWidget(binder.createAndBindUi(this));
		setAnimationEnabled(true);
		setGlassEnabled(true);
		show();
		scrollPanel.setHeight("420px");
		center();
		clientLocationDialog.setText(currentLocation);
		
		kitchenNameTextBox.setText(kitchenName);
		setText(kitchenName);

		if (selectedKitchen.energyMix != null)
		{
			energyMix.setText(selectedKitchen.energyMix.Name);
			energyMixco2.setText(selectedKitchen.energyMix.Co2PerKWh.toString());
		}
		
		
		addKitchenNamesToList(availableKitchens);
		
		if(!presenter.getLoginInfo().isAdmin() && availableKitchens.size() < 2){
			kitchen.setVisible(false);
		} else if(!presenter.getLoginInfo().isAdmin()) {
			newKitchen.setVisible(false);
		}
		
		initCellTable();
	    
	}
	
	


	private void initCellTable() {
		

		
		devidesCellTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		editableCells = new ArrayList<AbstractEditableCell<?, ?>>();
		
		personsCellTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		

	    // Add a text column to show the name.
		addColumn(new EditTextCell(), "Gerät", new GetValue<String>() {
	      @Override
		public String getValue(Device contact) {
	        return contact.deviceName;
	      }
	    }, new FieldUpdater<Device, String>() {
	      @Override
		public void update(int index, Device object, String value) {
	        pendingChanges.add(new DeviceNameChange(object, value));
	      }
	    });
		
	    // Add a text column to show the name.
		addColumn(new EditTextCell(), "Spezifikation", new GetValue<String>() {
	      @Override
		public String getValue(Device contact) {
	        return contact.deviceSpec;
	      }
	    }, new FieldUpdater<Device, String>() {
	      @Override
		public void update(int index, Device object, String value) {
	        pendingChanges.add(new DeviceSpecChange(object, value));
	      }
	    });
	    
		addPersonColumn(new EditTextCell(),
		        "Benutzer", new GetValueString<String>() {
		          @Override
				@SuppressWarnings("deprecation")
		          public String getValue(Staff object) {
		        	  return object.userName;
		          }
		        },  new FieldUpdater<Staff, String>() {
		  	      @Override
				public void update(int index, Staff object, String value) {
		  	        pendingPersonChanges.add(new PersonNameChange(object, value));
		  	      }
		  	    });
		
		addPersonColumn(new EditTextCell(),
		        "Email-Adresse Benutzer", new GetValueString<String>() {
		          @Override
				@SuppressWarnings("deprecation")
		          public String getValue(Staff object) {
		        	  return object.userEmail;
		          }
		        },  new FieldUpdater<Staff, String>() {
		  	      @Override
				public void update(int index, Staff object, String value) {
		  	        pendingPersonChanges.add(new PersonEmailChange(object, value));
		  	      }
		  	    });
	 

	    // Cell for kWConsumption
		addColumn(new EditTextCell(),
		        "Verbrauch (kWh/h)", new GetValue<String>() {
		          @Override
				@SuppressWarnings("deprecation")
		          public String getValue(Device object) {
		        	  return object.kWConsumption.toString();
		          }
		        },  new FieldUpdater<Device, String>() {
		  	      @Override
				public void update(int index, Device object, String value) {
		  	        pendingChanges.add(new DeviceKwChange(object, value));
		  	      }
		  	    });
//	    cellTable.addColumn(numberColumn, "Energieverbrauch in kWh/h");
	    
	    // Cell for durations
	    addColumn(new EditTextCell(),
	        "Laufzeiten", new GetValue<String>() {
	          @Override
			@SuppressWarnings("deprecation")
	          public String getValue(Device object) {
	        	  return Arrays.asList(object.durations).toString().replace(']', ' ').replace('[', ' ');
	          }
	        },  new FieldUpdater<Device, String>() {
		  	      @Override
				public void update(int index, Device object, String value) {
			  	        pendingChanges.add(new DeviceDurationsChange(object, value));
			  	        
//			  	    List<Long> categories = object.durations;
//			  	    List<String> options = new ArrayList<String>();
//				    for (Long category : categories) {
//				      options.add(category.toString());
//				    }
//			  	        
			  	      }
			  	    });
	    
//	    cellTable.addColumn(durationsColumn, "Laufzeiten");
	    
	    
	    // SelectionCell.
//	    final Category[] categories;
//	    categories = new Category[6];
//	    for (int i = 0; i < 6; i++) {
//	        categories[i] = new Category("bla");
//	      }
//	    List<String> options = new ArrayList<String>();
//	    for (Category category : categories) {
//	      options.add(category.getDisplayName());
//	    }
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
	    
//	    CONTACTS.get(index)
	    
	    
	    //options is yet not dynamic, so just  leyve this for now as is
	    // http://stackoverflow.com/questions/4565790/how-to-dynamically-update-the-choices-in-a-selectioncell-using-gwt
//	    addColumn(new SelectionCell(options), "Std-Laufzeit", new GetValue<String>() {
//	      public String getValue(Device contact) {
//	        return contact.stdDuration.toString();
//	      }
//	    }, new FieldUpdater<Device, String>() {
//	      public void update(int index, Device object, String value) {
//	        for (Category category : categories) {
//	          if (category.getDisplayName().equals(value)) {
//	            pendingChanges.add(new CategoryChange(object, category));
//	            break;
//	          }
//	        }
//	      }
//	    });
	    
	    
	    addColumn(new EditTextCell(),
		        "Std-Laufzeit", new GetValue<String>() {
		          @Override
				@SuppressWarnings("deprecation")
		          public String getValue(Device object) {
		        	  return object.stdDuration.toString();
		          }
		        }, new FieldUpdater<Device, String>() {
			  	      @Override
					public void update(int index, Device object, String value) {
				  	        pendingChanges.add(new DeviceStdDurationChange(object, value));
				  	      }
				  	    });
//	    cellTable.addColumn(stdDurationColumn, "Std-Laufzeit");
	    


	    // Add a selection model to handle user selection.
	    final SingleSelectionModel<Device> selectionModel = new SingleSelectionModel<Device>();
	    devidesCellTable.setSelectionModel(selectionModel);
	    selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
	      @Override
		public void onSelectionChange(SelectionChangeEvent event) {
	    	  Device selected = selectionModel.getSelectedObject();
	        if (selected != null) {
//	          Window.alert("You selected: " + selected.deviceName);
	        }
	      }
	    });
	    
	    // Add a selection model to handle user selection.
	    final SingleSelectionModel<Staff> selectionPersonModel = new SingleSelectionModel<Staff>();
	    personsCellTable.setSelectionModel(selectionPersonModel);
	    selectionPersonModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
	      @Override
		public void onSelectionChange(SelectionChangeEvent event) {
	    	  Staff selected = selectionPersonModel.getSelectedObject();
	        if (selected != null) {
//	          Window.alert("You selected: " + selected.deviceName);
	        }
	      }
	    });
	    

	    // DeleteButtonCell.
	    addColumn(new ButtonCell(), "entfernen", new GetValue<String>() {
	      @Override
		public String getValue(Device contact) {
	        return "x" ;
	      }
	    }, new FieldUpdater<Device, String>() {
	      @Override
		public void update(int index, Device object, String value) {
	    	  
//	        Window.alert("You clicked " + object.deviceName);
//              dataProvider.getList().remove(selected);
	    	  devicesHere.remove(object);
	    	  devidesCellTable.setRowCount(devicesHere.size(), true);
//	    	  cellTable.redraw()
	    	  devidesCellTable.setRowData(0, devicesHere);
	    	  devidesCellTable.redraw();
	      }
	    });
	    
	    addPersonColumn(new ButtonCell(), "entfernen", new GetValueString<String>() {
		      @Override
			public String getValue(Staff contact) {
		        return "x" ;
		      }
		    }, new FieldUpdater<Staff, String>() {
		      @Override
			public void update(int index, Staff object, String value) {
		    	  
//		        Window.alert("You clicked " + object.deviceName);
//	              dataProvider.getList().remove(selected);
		    	  personsHere.remove(object);
		    	  personsCellTable.setRowCount(personsHere.size(), true);
//		    	  cellTable.redraw()
		    	  personsCellTable.setRowData(0, personsHere);
		    	  personsCellTable.redraw();
		      }
		    });
	    

	    
	    // Set the total row count. This isn't strictly necessary, but it affects
	    // paging calculations, so its good habit to keep the row count up to date.
	    devidesCellTable.setRowCount(devicesHere.size(), true);

	    // Push the data into the widget.
	    devidesCellTable.setRowData(0, devicesHere);
	    
	    
	    // Set the total row count. This isn't strictly necessary, but it affects
	    // paging calculations, so its good habit to keep the row count up to date.
	    personsCellTable.setRowCount(personsHere.size(), true);

	    // Push the data into the widget.
	    personsCellTable.setRowData(0, personsHere);
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
	    devidesCellTable.addColumn(column, headerText);
	    return column;
	  }
	  private static interface GetValue<C> {
		    C getValue(Device contact);
		  }

	  
	  private <C> Column<Staff, C> addPersonColumn(Cell<C> cell, String headerText,
		      final GetValueString<C> getter, FieldUpdater<Staff, C> fieldUpdater) {
		    Column<Staff, C> column = new Column<Staff, C>(cell) {
		      @Override
		      public C getValue(Staff object) {
		        return getter.getValue(object);
		      }
		    };
		    column.setFieldUpdater(fieldUpdater);
		    if (cell instanceof AbstractEditableCell<?, ?>) {
		      editableCells.add((AbstractEditableCell<?, ?>) cell);
		    }
		    personsCellTable.addColumn(column, headerText);
		    return column;
		  }
		  private static interface GetValueString<C> {
			    C getValue(Staff contact);
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
	  
	  private List<PendingPersonChange<?>> pendingPersonChanges = new ArrayList<
	  PendingPersonChange<?>>();
	  
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
		    	// my question... should here be standing anything with the eventbus or the requestfactory?
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
	  
	  
	  private abstract static class PendingPersonChange<T> {
		    private final Staff contact;
		    private final T value;

		    public PendingPersonChange(Staff contact, T value) {
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
		    protected abstract void doCommit(Staff contact, T value);
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
	   * Updates the persons name.
	   */
	  private static class PersonEmailChange extends PendingPersonChange<String> {

	    public PersonEmailChange(Staff contact, String value) {
	      super(contact, value);
	      
//	      if (value.length() > 1){
//	    	  int row = cellTable.getRowCount();
//	    	  
//	    	  contact.
//	    	  cellTable.setRowData(row, CONTACTS);
//	      }
	    }

	    @Override
	    protected void doCommit(Staff contact, String value) {
	      contact.userEmail = value;
	    }
	  }
	  
	  private static class PersonNameChange extends PendingPersonChange<String> {

		    public PersonNameChange(Staff contact, String value) {
		      super(contact, value);
		      
//		      if (value.length() > 1){
//		    	  int row = cellTable.getRowCount();
//		    	  
//		    	  contact.
//		    	  cellTable.setRowData(row, CONTACTS);
//		      }
		    }

		    @Override
		    protected void doCommit(Staff contact, String value) {
		      contact.userName = value;
		    }
		  }
	  /**
	   * Updates the category.
	   */
//	  private static class CategoryChange extends PendingChange<Category> {
//
//	    public CategoryChange(Device contact, Category value) {
//	      super(contact, value);
//	    }
//
//	    @Override
//	    protected void doCommit(Device contact, Category value) {
//	      contact.stdDuration = Long.parseLong(value.getDisplayName());
//	    }
//	  }

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
	  private static class DeviceKwChange extends PendingChange<String> {

	    public DeviceKwChange(Device contact, String value) {
	      super(contact, value);
	    }

	    @Override
	    protected void doCommit(Device contact, String value) {
	      contact.kWConsumption = Double.valueOf(value);
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
	  
	  private static class DeviceStdDurationChange extends PendingChange<String> {

		    public DeviceStdDurationChange(Device contact, String value) {
		      super(contact, value);
		    }

		    @Override
		    protected void doCommit(Device device, String value) {
		    	device.stdDuration = Long.parseLong( value.trim());
		    	boolean doAdd = true;
		    	for(Long duration: device.durations){
		    		if(duration == device.stdDuration){
		    			doAdd = false;
		    			break;
		    		}
		    		
		    	}
		    	if(doAdd){
		    		device.durations[device.durations.length] = device.stdDuration;
		    	}
		    }
		  }
	  
	  
	  public static Long[] parsePropsToList(
			  String propName,
			  String delim){
		  
//		  for(int i = 1;i <= listToFill.size(); i++){
//			  listToFill.remove(i);
//		  }
//		  List<Long> listToFill = Arrays.asList({});
		  Long[] listToFill = {};
		  
		   //This is paired down for convenience - assume getSplitList correctly parses to List<String>
		   List<String> stringList = getSplitList(propName, delim);
		   for(String s : stringList){
//		       listToFill.add((T)s.trim());
			   Long addLong = Long.parseLong( s.trim());
			   if(addLong != null){
			   listToFill[listToFill.length] = addLong;
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
		@Override
		public void onSuccess(LatLng latlng)
		{

			//			herkunft.add(latlng.getLatitude());
			//			herkunft.add(latlng.getLongitude());
			Window.alert("- " + latlng.getLongitude() + " -");

		}

		@Override
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
			dataService.addDistances(allDistances, new AsyncCallback<Integer>() {
				@Override
				public void onFailure(Throwable error) {
					Window.alert("Fehler : "+ error.getMessage());
				}
				@Override
				public void onSuccess(Integer ignore) {
					presenter.getClientData().getDistances().addAll(allDistances);
					updateAllZutaten(); 
					//				Window.alert(Integer.toString(ignore) + " Distanzen gespeichert.");
				}
			});
		}

		hide();
	}

	private  void calculateDistances(String string, boolean firstTime) {
		ArrayList<SingleDistance> distances = (ArrayList<SingleDistance>) presenter.getClientData().getDistances();
		ArrayList<String> distancesRequested = new ArrayList<String>();
		boolean notFound;
		List<Ingredient> zutaten = presenter.getClientData().getIngredients();
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
		
		// why was this here?:
//			openDialog();
		
//		} else {
//			updateAllZutaten();
//		}
	}


	private void updateAllZutaten() {
		for(Widget widget : superDisplay.getRezeptList()){
			RecipeView rezeptView = ((RecipeView) widget);
			List<IngredientSpecification> zutaten = new ArrayList<IngredientSpecification>();
			zutaten.addAll(rezeptView.getRezept().Zutaten);
			for(IngredientSpecification zutatSpec : zutaten ){
				int index = rezeptView.getRezept().Zutaten.indexOf(zutatSpec);
				for(SingleDistance singleDistance : presenter.getClientData().getDistances()){
					if(singleDistance.getFrom().contentEquals(TopPanel.currentHerkunft) && 
							singleDistance.getTo().contentEquals(zutatSpec.getHerkunft().symbol)){

						zutatSpec.setDistance(singleDistance.getDistance());
						rezeptView.getRezept().Zutaten.set(index, zutatSpec);

						break;
					}

				}
			}
			rezeptView.updateSuggestion();

//			if(rezeptView.addInfoPanel.getWidgetCount() ==2){
//				rezeptView.addInfoPanel.remove(1);
//			}
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
		// this button finalizes the decision to enter the kitchen
		presenter.getTopPanel().location.setVisible(false);
		presenter.getTopPanel().isNotInKitchen = false;
		presenter.getTopPanel().isCustomerLabel.setText(" Sie befinden sich in der Küche: "+kitchenName+" ");
		presenter.getTopPanel().selectedKitchen = selectedKitchen;
	
        saveAndCloseDialog();
	}




	public void saveLastKitchen(final Long id) {
		dataService.setYourLastKitchen(id, new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable error) {
				Window.alert("Fehler : "+ error.getMessage());
			}
			@Override
			public void onSuccess(Boolean okay) {
				presenter.getClientData().lastKitchen = id;
				presenter.getLoginInfo().setLastKitchen(id);
				presenter.getLoginInfo().setUsedLastKitchen(true);
			}
		});
	}


	private void saveAndCloseDialog() {
		for (PendingChange<?> pendingChange : pendingChanges) {
	          pendingChange.commit();
	        }
        pendingChanges.clear();
		
        for (PendingPersonChange<?> pendingPersonChange : pendingPersonChanges) {
        	pendingPersonChange.commit();
	        }
        pendingPersonChanges.clear();
        
		saveDistances();
		
		// clear workspace (including prompts for saving) to avoid copying recipies from one kitchen to another)
		presenter.removeAllRecipesFromWorkplace();
		
		
		// this shoots to many rpc calls... (one should be enough)
		// shouldn't shoot any, if there is no change...
		// which exactly for this purpose the requestFactory in GWT 2.1 was developed...
		if(presenter.getTopPanel().selectedKitchen != null){
			// this is a hacK:
			presenter.getTopPanel().selectedKitchen.hasChanged = false;
			
			dataService.addKitchen(presenter.getTopPanel().selectedKitchen, new AsyncCallback<Long>() {
				@Override
				public void onFailure(Throwable error) {
					Window.alert("Fehler : "+ error.getMessage());
				}
				@Override
				public void onSuccess(Long kitchenID) {
					// this adds a new kitchen, yet must not be the selected one:
					presenter.getTopPanel().selectedKitchen.id = kitchenID;
					presenter.getSearchPanel().updateKitchenRecipesForSearch(kitchenID);
					presenter.getSearchPanel().updateResults(Search.SearchInput.getText());
					Search.yourRecipesText.setHTML("in Rezepten von: " + kitchenName );
	//				Search.clientData.kitchens.add(kitchen);
	//				kitchens.addItem(kitchen.getSymbol());
					saveLastKitchen(kitchenID);
					
				}
			});
		}
		
		// The other kitchens need also to be saved...
		
		for(final Workgroup kitchen: availableKitchens){
			// save all kitchens at once
			if(kitchen != null && kitchen.hasChanged){ // has changed still needs to be set false	
				dataService.addKitchen(kitchen, new AsyncCallback<Long>() {
					@Override
					public void onFailure(Throwable error) {
						Window.alert("Fehler : "+ error.getMessage());
					}
					@Override
					public void onSuccess(Long kitchenID) {
						// this adds a new kitchen, yet must not be the selected one:
						kitchen.id = kitchenID;
		//				Search.clientData.kitchens.add(kitchen);
		//				kitchens.addItem(kitchen.getSymbol());
					}
				});
			}
		}
		

		// update search... this should also be done when loaded regular
		
		presenter.getSearchPanel().SearchInput.setText("");
		presenter.getSearchPanel().updateResults(" ");
		
		
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
				@Override
				public void onFailure(int statusCode) {
					presenter.getTopPanel().locationLabel.setText("Wir können diese Adresse nicht finden: ");
				}

				@Override
				public void onSuccess(JsArray<Placemark> locations) {
					Placemark place = locations.get(0);
					presenter.getTopPanel().locationLabel.setText("Sie befinden sich in: " +place.getAddress() +"  ");
					currentLocation = place.getAddress();
					selectedKitchen.location = currentLocation;
					setText("Berechne alle Routen von: " + place.getAddress());
					TopPanel.currentHerkunft = place.getAddress();

					calculateDistances(place.getAddress(),firstTime);

				}
			});
		} else {
			presenter.getTopPanel().locationLabel.setText("Bitte geben Sie hier Ihre Adresse ein: ");
		}

	}







	void getDistance(final String from, final String to ) {

		Timer t = new Timer() {
			@Override
			public void run() {



				geocoder.getLocations(to, new LocationCallback() {
					@Override
					public void onFailure(int statusCode) {
						Window.alert("Diese Zutat hat einen falsche Herkunft: "+ to);
					}

					@Override
					public void onSuccess(final JsArray<Placemark> locationsTo) {

						geocoder.getLocations(from, new LocationCallback() {
							@Override
							public void onFailure(int statusCode) {
								Window.alert("Wir können Ihre Adresse nicht zuordnen: " + from);
							}

							@Override
							public void onSuccess(JsArray<Placemark> locationsFrom) {

								simpleDirectionsDemo(locationsFrom,locationsTo);
							}
						});
					}
				});
				timeToWaitForGeocode = timeToWaitForGeocode - 1000;
			}
		};
		t.schedule(timeToWaitForGeocode);
		timeToWaitForGeocode = timeToWaitForGeocode + 1000;
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
			  kitchens.setItemText(kitchens.getSelectedIndex(), this.kitchenName);
			  selectedKitchen.setSymbol(this.kitchenName);
		  }
	  }
	  
	  @UiHandler("energyMix")
	  void onEnergyMixChange(KeyUpEvent event) {
		  if(energyMix.getText().length()>1){			  
			  selectedKitchen.energyMix.Name = energyMix.getText();
		  }
	  }
	  
	  @UiHandler("energyMixco2")
	  void onEnergyMixco2Change(KeyUpEvent event) {
		  if(energyMixco2.getText().length()>1){ // check of parseDouble is possible
			  selectedKitchen.energyMix.Co2PerKWh = Double.parseDouble(energyMixco2.getText());
		  }
	  }
	  
	  @UiHandler("clientLocationDialog")
	  void onLocationNameChange(KeyUpEvent event) {
		  if(clientLocationDialog.getText().length()>1){
			  selectedKitchen.location = kitchenNameTextBox.getText();
		  }
	  }
	  
	  @UiHandler("newKitchen")
	  public void onNewKitchenClick(ClickEvent event) {
		  selectedKitchen = new Workgroup("neue Küche");
		  selectedKitchen.location = "Zürich, Schweiz";
		  availableKitchens.add(selectedKitchen);
		  kitchens.addItem(selectedKitchen.getSymbol());
		  kitchens.setSelectedIndex(kitchens.getItemCount()-1);
		  
		  //update view:
		  
			  // devices
			  switchKitchen();
		  
	  }

	  
	  @UiHandler("leaveKitchen")
	  public void onLeaveKitchenClick(ClickEvent event) {
		  presenter.getTopPanel().location.setVisible(true);
		  presenter.getTopPanel().isNotInKitchen = true;
		  presenter.getTopPanel().isCustomerLabel.setText("Nichtkommerzielle Nutzung ");
		  presenter.getTopPanel().selectedKitchen = null;
		  
		  Search.yourRecipesText.setHTML(" in eigenen Rezepten");
		  
		  saveLastKitchen(0L);
	
		  saveAndCloseDialog();
	  }


	private void switchKitchen() {
		
		//set Name
		this.kitchenName = selectedKitchen.getSymbol();
			setText(this.kitchenName);
		  kitchenNameTextBox.setText(this.kitchenName);
		
		  devicesHere = selectedKitchen.devices;
		  devidesCellTable.setRowCount(devicesHere.size(), true);
		  devidesCellTable.setRowData(0, devicesHere);
		  devidesCellTable.redraw();
		  
		  // also persons
		  personsHere = selectedKitchen.personal;
		  personsCellTable.setRowCount(personsHere.size(), true);
		  personsCellTable.setRowData(0, personsHere);
		  personsCellTable.redraw();
		  
		  // the rest: energy mix, and location
		  currentLocation = selectedKitchen.location;
		  clientLocationDialog.setText(currentLocation);
	}
	  
	  @UiHandler("kitchens")
	  void onKitchenChange(ChangeEvent event) {
//		  int selectedIndex = kitchens.getSelectedIndex();
//		  if (selectedIndex > 0)
//		   Window.alert("Something got selected " + kitchens.getValue(selectedIndex));
		   selectedKitchen = availableKitchens.get(kitchens.getSelectedIndex());
		   switchKitchen();
	  }

	  
	  @UiHandler("addDevice")
	  void onAddDevicePress(ClickEvent event) {
		  
//    	  int rowCell = cellTable.getRowCount();
//    	  List<Device> newDevice = Arrays.asList(
//    			      new Device("","",0.0,  Arrays.asList(1l,5l,10l,20l), 10l));
//    	  cellTable.setRowData(rowCell, newDevice);
		  Long[] longList ={1l,5l,10l,20l};
    	  devicesHere.add(new Device("","",0.0, longList , 10l));
    	  devidesCellTable.setRowCount(devicesHere.size(), true);
    	  
    	  devidesCellTable.setRowData(0, devicesHere);
    	  
//    	  cellTable.redraw();
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
		  
//		  final Button removeDevice = new Button("x");
//		  removeDevice.addClickHandler(new ClickHandler() {
//				public void onClick(ClickEvent event) {
//					deviceTable.removeRow(getWidgetRow(removeDevice,deviceTable));
//				}
//			});	
//		  
//		  int row = deviceTable.getRowCount();
//		  deviceTable.setWidget(row,3,removeDevice);
		  
	  }
	  
	  @UiHandler("addPerson")
	  void onAddPersonPress(ClickEvent event) {
		  
		  
    	  personsHere.add(new Staff("Name","Email"));
    	  personsCellTable.setRowCount(personsHere.size(), true);
    	  
    	  personsCellTable.setRowData(0, personsHere);
//		  
////		  TextBox name = new TextBox();
////		  name.setText("Name");
//		  TextBox email = new TextBox();
//		  email.setText("email");
//		  
//		  int row = personTable.getRowCount();
////		  personTable.setWidget(row,0,name);
//		  personTable.setWidget(row,1,email);
//		  
//		  final Button removePerson = new Button("x");
//		  removePerson.addClickHandler(new ClickHandler() {
//				public void onClick(ClickEvent event) {
//					
//					personTable.removeRow(getWidgetRow(removePerson,personTable));
//				}
//			});	
//		  personTable.setWidget(row,2,removePerson);
	  }
	  
//		private static int getWidgetRow(Widget widget, FlexTable table) {
//			for (int row = 0; row < table.getRowCount(); row++) {
//				for (int col = 0; col < table.getCellCount(row); col++) {
//					Widget w = table.getWidget(row, col);
//					if (w == widget) {
//						return row;
//					}
//				}
//			}
//			throw new RuntimeException("Unable to determine widget row");
//		}
	  
	  @UiHandler("deleteKitchen")
	  void onDeleteKitchenPress(ClickEvent event) {
		  // clear workspace if kitchen to delete matches kitchen which was worked on
		  //presenter.removeAllRecipesFromWorkplace();
		  
		  // alle rezepte der Kueche loeschen
		  
		  
		  // Kueche loeschen
		  
		  
		  // kitchendialog neu laden
		  
		  //
	  }
	  

		
}
