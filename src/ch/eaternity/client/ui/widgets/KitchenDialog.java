package ch.eaternity.client.ui.widgets;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.eaternity.client.DataController;
import ch.eaternity.client.DataService;
import ch.eaternity.client.DataServiceAsync;
import ch.eaternity.client.ui.EaternityRechnerView.Presenter;
import ch.eaternity.shared.Device;
import ch.eaternity.shared.EnergyMix;
import ch.eaternity.shared.SingleDistance;
import ch.eaternity.shared.Staff;
import ch.eaternity.shared.Workgroup;

import com.google.gwt.cell.client.AbstractEditableCell;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.maps.client.geocode.Geocoder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.sun.corba.se.pept.transport.ContactInfo;


public class KitchenDialog<T> extends DialogBox{
	interface Binder extends UiBinder<Widget, KitchenDialog> { }
	private static final Binder binder = GWT.create(Binder.class);

	private final DataServiceAsync dataService = GWT.create(DataService.class);

	@UiField Button executeButton;
	@UiField ScrollPanel scrollPanel;
	@UiField FlexTable deviceTable;
	@UiField FlexTable personTable;
	@UiField InlineLabel locationLabel;
	@UiField Button locationButton;
	@UiField Button addDevice;
	@UiField Button addPerson;
	@UiField Button deleteKitchen;
	@UiField TextBox kitchenNameTextBox;
	@UiField Label kitchenId;
	@UiField static TextBox energyMix;
	@UiField static TextBox energyMixco2;
	@UiField static ListBox kitchens;
	@UiField Anchor leaveKitchen;
	@UiField Anchor newKitchen;
	@UiField HTMLPanel kitchen;
	
	@UiField static CellTable<Device> devidesCellTable  = new CellTable<Device>();
	@UiField static CellTable<Staff> personsCellTable  = new CellTable<Staff>();
	
	static Long[] longList ={1l,5l,10l,20l};
	private static List<Device> devicesHere = Arrays.asList(new Device("","",0.0,  longList, 10l));
	private static List<Staff> personsHere = Arrays.asList(new Staff("Name","email"));
	
	ArrayList<SingleDistance> allDistances = new ArrayList<SingleDistance>();
	String currentLocation;
	String kitchenName;
	
	List<Workgroup> availableKitchens;
	Workgroup selectedKitchen;
	
	private Presenter<T> presenter;
	private DataController dco;
	
	
	// ---------------  public Methods--------------- 
	
	public KitchenDialog() {
		currentLocation = dco.getCurrentLocation();
	}
	
	public void setPresenter(Presenter<T> presenter){
		this.presenter = presenter;
		this.dco = presenter.getDCO();
		
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
	
	private void openDialog() {
		setWidget(binder.createAndBindUi(this));
		setAnimationEnabled(true);
		setGlassEnabled(true);
		show();
		scrollPanel.setHeight("420px");
		center();
		
		kitchenNameTextBox.setText(kitchenName);
		setText(kitchenName);
		
		locationLabel.setText("Ort der Kueche: " + currentLocation);
		
		if (selectedKitchen.id != null)
				kitchenId.setText("Id: " + selectedKitchen.id);
		 else
			  kitchenId.setText("Id: nicht gesetzt. Zuerst Speichern.");

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

	  @UiHandler("kitchens")
	  void onKitchenChange(ChangeEvent event) {
		   selectedKitchen = availableKitchens.get(kitchens.getSelectedIndex());
		   switchKitchen();
	  }
	
	  
	  @UiHandler("addDevice")
	  void onAddDevicePress(ClickEvent event) {
	
		  Long[] longList ={1l,5l,10l,20l};
	  	  devicesHere.add(new Device("","",0.0, longList , 10l));
	  devidesCellTable.setRowCount(devicesHere.size(), true);
	  
	  devidesCellTable.setRowData(0, devicesHere);
		  
	  }
	  
	  @UiHandler("addPerson")
	  void onAddPersonPress(ClickEvent event) {
		  
		  
	  personsHere.add(new Staff("Name","Email"));
	  	  personsCellTable.setRowCount(personsHere.size(), true);
	  	  
	  	  personsCellTable.setRowData(0, personsHere);
	  }
	  
	  @UiHandler("locationButton")
	  void onClick(ClickEvent event) {
		  DistancesDialog ddlg = new DistancesDialog(); 
		  ddlg.setPresenter(presenter);
	  }
		  
	@UiHandler("executeButton")
	void onOkayClicked(ClickEvent event) {
		presenter.getDCO().changeKitchen(selectedKitchen);
	    saveAndCloseDialog();
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

	  
	  @UiHandler("newKitchen")
	  public void onNewKitchenClick(ClickEvent event) {
		  selectedKitchen = new Workgroup("neue Küche");
		  selectedKitchen.location = "Zürich, Schweiz";
		  availableKitchens.add(selectedKitchen);
		  kitchens.addItem(selectedKitchen.getSymbol());
		  kitchens.setSelectedIndex(kitchens.getItemCount()-1);
		  switchKitchen();
	  }

	  
	  @UiHandler("leaveKitchen")
	  public void onLeaveKitchenClick(ClickEvent event) {
		  presenter.getTopPanel().location.setVisible(true);
		  presenter.getDCO().isInKitchen = false;
		  presenter.getTopPanel().isCustomerLabel.setText("Nichtkommerzielle Nutzung ");
		  presenter.getTopPanel().selectedKitchen = null;
		  
		  Search.yourRecipesText.setHTML(" in eigenen Rezepten");
		  
		  saveLastKitchen(0L);
	
		  saveAndCloseDialog();
	  }
	  
		//REFACTOR: call deleteKitchen in DataController
	  @UiHandler("deleteKitchen")
	  void onDeleteKitchenPress(ClickEvent event) {
		  // clear workspace if kitchen to delete matches kitchen which was worked on
		  //presenter.removeAllRecipesFromWorkplace();
		  
		  // alle rezepte der Kueche loeschen
		  
		  
		  // Kueche loeschen
		  
		  
		  // kitchendialog neu laden
		  
		  //
	  }
	
	
	private void addKitchenNamesToList(List<Workgroup> availableKitchens) {
		for(Workgroup kitchen : availableKitchens){
			if(kitchen != null){
				kitchens.addItem(kitchen.getSymbol(),Integer.toString(availableKitchens.indexOf(kitchen))); // +kitchen.location  kitchen.id.toString()+ kitchen.getSymbol() 
			}
		}
	}


	public void saveLastKitchen(final Long id) {
		dataService.setYourLastKitchen(id, new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable error) {
				Window.alert("Fehler : "+ error.getMessage());
			}
			@Override
			public void onSuccess(Boolean okay) {
				presenter.getClientData().cdata.lastKitchen = id;
				presenter.getLoginInfo().setLastKitchen(id);
				presenter.getLoginInfo().setIsInKitchen(true);
			}
		});
	}

	//REFACTOR: just call DataController saveKitchen
	private void saveAndCloseDialog() {
		for (PendingChange<?> pendingChange : pendingChanges) {
	          pendingChange.commit();
	        }
        pendingChanges.clear();
		
        for (PendingPersonChange<?> pendingPersonChange : pendingPersonChanges) {
        	pendingPersonChange.commit();
	        }
        pendingPersonChanges.clear();
        
		
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
					presenter.getDCO().changeKitchenRecipes(kitchenID);
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


	private void switchKitchen() {
		//set Name
		this.kitchenName = selectedKitchen.getSymbol();
			setText(this.kitchenName);
		  kitchenNameTextBox.setText(this.kitchenName);
		  if (selectedKitchen.id != null)
			  kitchenId.setText("Id: " + selectedKitchen.id);
		  else
			  kitchenId.setText("Id: nicht gesetzt. Zuerst Speichern.");
		
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
		  locationLabel.setText("Ort der Kueche: " + currentLocation);
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
	  
	  
	  public static Long[] parsePropsToList( String propName, String delim){

		  Long[] listToFill = {};
		  
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

		return myarray;
	}

		
}
