package ch.eaternity.client.ui.widgets;


import java.util.Arrays;
import java.util.List;

import ch.eaternity.client.DataController;
import ch.eaternity.client.DataServiceAsync;
import ch.eaternity.client.activity.RechnerActivity;
import ch.eaternity.shared.Device;
import ch.eaternity.shared.Kitchen;
import ch.eaternity.shared.UserInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;


public class KitchenDialog extends DialogBox{
	interface Binder extends UiBinder<Widget, KitchenDialog> { }
	private static final Binder binder = GWT.create(Binder.class);

	private DataServiceAsync dataService;
	
	@UiField ScrollPanel scrollPanel;
	@UiField static ListBox kitchenList;
	
	@UiField TextBox kitchenNameTextBox;
	@UiField Label kitchenId;
	
	@UiField InlineLabel locationLabel;
	@UiField Button locationButton;
	
	@UiField static CellTable<Device> devidesCellTable  = new CellTable<Device>();
	@UiField Button addDevice;
	
	@UiField static CellTable<UserInfo> personsCellTable  = new CellTable<UserInfo>();
	@UiField Button addPerson;
	
	@UiField static TextBox energyMixName;
	@UiField static TextBox energyMixCO2;

	@UiField Button newKitchenButton;
	@UiField Button deleteKitchenButton;
	@UiField Button exitButton;
	
	@UiField static TextErrorStyle textErrorStyle;
	
	private RechnerActivity presenter;
	private DataController dco;
	
	private static List<Device> devices = Arrays.asList(new Device());
	private static List<UserInfo> kitchenStaff; // = Arrays.asList(new UserInfo("Name","email"));
	
	/**
	 * The current location setted in outside of any kitchen - for adding new kitchen
	 */
	String currentLocation;
	String kitchenName;
	
	List<Kitchen> userKitchens;
	Kitchen currentKitchen;

	private UserInfo userInfo;
	
	interface TextErrorStyle extends CssResource {
		String redTextError();
	}
	
	
	// ---------------  public Methods--------------- 
	
	public KitchenDialog() {
		setWidget(binder.createAndBindUi(this));
	}
	
	public void setPresenter(RechnerActivity presenter){
		this.presenter = presenter;
		this.dco = presenter.getDCO();
		this.dataService = presenter.getDataService();
		this.userInfo = dco.getUserInfo();
		
		if (userInfo.isAdmin()) {
			newKitchenButton.setVisible(true);
		}
		else {
			newKitchenButton.setVisible(true);
		}

		userKitchens = dco.getKitchens();
		
		currentKitchen = dco.getCurrentKitchen();
		if (currentKitchen != null) {
			currentLocation = currentKitchen.getProcessedLocation();
			devices = currentKitchen.getDevices();
			kitchenStaff = currentKitchen.getUserInfos();
			updateKitchenParameters();
		}
		else if (userKitchens.size() > 0) {
			currentKitchen = userKitchens.get(0);
			updateKitchenParameters();
		}
		else {
			currentLocation = dco.getCurrentLocation();
			scrollPanel.setVisible(false);
		}
		
		updateKitchenList();
		
		//initCellTable();
		
		openDialog();
	}
	
	private void updateKitchenParameters() {
		changeKitchenName(currentKitchen.getSymbol());
		
		locationLabel.setText("Ort der Kueche: " + currentLocation);
		
		if (currentKitchen.getId() != null)
				kitchenId.setText("Id: " + currentKitchen.getId());
		 else
			  kitchenId.setText("Id: nicht gesetzt. Zuerst Speichern.");

		if (currentKitchen.getEnergyMix() != null)
		{
			energyMixName.setText(currentKitchen.getEnergyMix().Name);
			energyMixCO2.setText(currentKitchen.getEnergyMix().Co2PerKWh.toString());
		}
		
	}
	
	private void openDialog() {
		setAnimationEnabled(true);
		setGlassEnabled(true);
		show();
		scrollPanel.setHeight("420px");
		center();	
	}
	
	private void updateKitchenList() {
		kitchenList.clear();
		for (Kitchen kitchen : userKitchens) {
			kitchenList.addItem(kitchen.getSymbol());
		}
		// TODO select the current kitchen ...
	}
	
	private void changeKitchenName(String name) {
		  setText(name);
		  kitchenList.setItemText(kitchenList.getSelectedIndex(), this.kitchenName);
		  currentKitchen.setSymbol(name);
	}
	
	// -------------------------- UI Handlers ---------------------------

	@UiHandler("kitchenList")
	void onKitchenChange(ChangeEvent event) {
		currentKitchen = userKitchens.get(kitchenList.getSelectedIndex());
		switchKitchen();
	}

	@UiHandler("addDevice")
	void onAddDevicePress(ClickEvent event) {
		devices.add(new Device());
		devidesCellTable.setRowCount(devices.size(), true);
		devidesCellTable.setRowData(0, devices);

	}

	@UiHandler("addPerson")
	void onAddPersonPress(ClickEvent event) {
		//kitchenStaff.add(new UserInfo("Name", "Email"));
		personsCellTable.setRowCount(kitchenStaff.size(), true);
		personsCellTable.setRowData(0, kitchenStaff);
	}
	  
	  @UiHandler("locationButton")
	  void onClick(ClickEvent event) {
		  DistancesDialog ddlg = new DistancesDialog(); 
		  ddlg.setPresenter(presenter);
	  }
		  
	
	@UiHandler("kitchenNameTextBox")
	void onNameChange(KeyUpEvent event) {
		if (kitchenNameTextBox.getText().length() > 1) {
			changeKitchenName(kitchenNameTextBox.getText());
		}
	}

	@UiHandler("energyMixName")
	void onEnergyMixChange(KeyUpEvent event) {
		if (energyMixName.getText().length() > 1) {
			currentKitchen.getEnergyMix().Name = energyMixName.getText();
		}
	}

	@UiHandler("energyMixCO2")
	void onEnergyMixco2Change(KeyUpEvent event) {
		String errorStyle = textErrorStyle.redTextError();
		Double energyMix = 0.0;
		String text = energyMixCO2.getText();
		boolean success = false;

		try {
			if ("".equals(text)) {
				energyMixCO2.removeStyleName(errorStyle);
			} 
			else {
				energyMix = Double.parseDouble(energyMixCO2.getText().trim());
				if (energyMix > 0) {
					success = true;
					energyMixCO2.removeStyleName(errorStyle);
				}
			}
		} 
		catch (Exception e) {}

		if (success) {
			currentKitchen.getEnergyMix().Co2PerKWh = energyMix;
		} 
		else {
			energyMixCO2.addStyleName(errorStyle);
		}
	}

	  
	  @UiHandler("newKitchenButton")
	  public void onNewKitchenClick(ClickEvent event) {
		  currentKitchen = new Kitchen();
		  currentKitchen.setProcessedLocation(currentLocation);
		  userKitchens.add(currentKitchen);
		  kitchenList.addItem(currentKitchen.getSymbol());
		  kitchenList.setSelectedIndex(kitchenList.getItemCount()-1);
		  switchKitchen();
	  }

	  
		@UiHandler("exitButton")
		void onOkayClicked(ClickEvent event) {
			dco.changeCurrentKitchen(currentKitchen);
		    saveAndCloseDialog();
		}
		
	  
		//REFACTOR: call deleteKitchen in DataController
	  @UiHandler("deleteKitchenButton")
	  void onDeleteKitchenPress(ClickEvent event) {
		  dco.deleteKitchen(currentKitchen);
	  }



	public void saveLastKitchen(final Long id) {
		dataService.setCurrentKitchen(id, new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable error) {
				Window.alert("Fehler : "+ error.getMessage());
			}
			@Override
			public void onSuccess(Boolean okay) {
				dco.getUserInfo().setCurrentKitchen(id);
			}
		});
	}

//	//REFACTOR: just call DataController saveKitchen
	private void saveAndCloseDialog() {
//		for (PendingChange<?> pendingChange : pendingChanges) {
//	          pendingChange.commit();
//	        }
//        pendingChanges.clear();
//		
//        for (PendingPersonChange<?> pendingPersonChange : pendingPersonChanges) {
//        	pendingPersonChange.commit();
//	        }
//        pendingPersonChanges.clear();
//        
//		
//		// this shoots to many rpc calls... (one should be enough)
//		// shouldn't shoot any, if there is no change...
//		// which exactly for this purpose the requestFactory in GWT 2.1 was developed...
//		if(dco.getCurrentKitchen() != null){
//			// this is a hacK:
//			dco.getCurrentKitchen().hasChanged = false;
//			
//			dataService.addKitchen(dco.getCurrentKitchen(), new AsyncCallback<Long>() {
//				@Override
//				public void onFailure(Throwable error) {
//					Window.alert("Fehler : "+ error.getMessage());
//				}
//				@Override
//				public void onSuccess(Long kitchenID) {
//					// this adds a new kitchen, yet must not be the selected one:
//					presenter.getDCO().changeKitchenRecipes(kitchenID);
//	//				Search.clientData.kitchens.add(kitchen);
//	//				kitchens.addItem(kitchen.getSymbol());
//					saveLastKitchen(kitchenID);
//					
//				}
//			});
//		}
//		
//		// The other kitchens need also to be saved...
//		
//		for(final Kitchen kitchen: userKitchens){
//			// save all kitchens at once
//			if(kitchen != null && kitchen.hasChanged){ // has changed still needs to be set false	
//				dataService.addKitchen(kitchen, new AsyncCallback<Long>() {
//					@Override
//					public void onFailure(Throwable error) {
//						Window.alert("Fehler : "+ error.getMessage());
//					}
//					@Override
//					public void onSuccess(Long kitchenID) {
//						// this adds a new kitchen, yet must not be the selected one:
//						kitchen.id = kitchenID;
//		//				Search.clientData.kitchens.add(kitchen);
//		//				kitchens.addItem(kitchen.getSymbol());
//					}
//				});
//			}
//		}	
//		
//		hide();
	}


	private void switchKitchen() {
		updateKitchenParameters();
		
//		/*
//		devices = currentKitchen.getDevices;
//		devidesCellTable.setRowCount(devices.size(), true);
//		devidesCellTable.setRowData(0, devices);
//		devidesCellTable.redraw();
//
//		// also persons
//		kitchenStaff = currentKitchen.personal;
//		personsCellTable.setRowCount(kitchenStaff.size(), true);
//		personsCellTable.setRowData(0, kitchenStaff);
//		personsCellTable.redraw();
//		 */
	}
	
}
//
//	private void initCellTable() {
//		devidesCellTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
//		editableCells = new ArrayList<AbstractEditableCell<?, ?>>();
//		
//		personsCellTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
//		
//
//	    // Add a text column to show the name.
//		addColumn(new EditTextCell(), "Gerät", new GetValue<String>() {
//	      @Override
//		public String getValue(Device contact) {
//	        return contact.deviceName;
//	      }
//	    }, new FieldUpdater<Device, String>() {
//	      @Override
//		public void update(int index, Device object, String value) {
//	        pendingChanges.add(new DeviceNameChange(object, value));
//	      }
//	    });
//		
//	    // Add a text column to show the name.
//		addColumn(new EditTextCell(), "Spezifikation", new GetValue<String>() {
//	      @Override
//		public String getValue(Device contact) {
//	        return contact.deviceSpec;
//	      }
//	    }, new FieldUpdater<Device, String>() {
//	      @Override
//		public void update(int index, Device object, String value) {
//	        pendingChanges.add(new DeviceSpecChange(object, value));
//	      }
//	    });
//	    
//		addPersonColumn(new EditTextCell(),
//		        "Benutzer", new GetValueString<String>() {
//		          @Override
//				@SuppressWarnings("deprecation")
//		          public String getValue(UserInfo object) {
//		        	  return object.getNickname();
//		          }
//		        },  new FieldUpdater<UserInfo, String>() {
//		  	      @Override
//				public void update(int index, UserInfo object, String value) {
//		  	        pendingPersonChanges.add(new PersonNameChange(object, value));
//		  	      }
//		  	    });
//		
//		addPersonColumn(new EditTextCell(),
//		        "Email-Adresse Benutzer", new GetValueString<String>() {
//		          @Override
//				@SuppressWarnings("deprecation")
//		          public String getValue(UserInfo object) {
//		        	  return object.getEmailAddress();
//		          }
//		        },  new FieldUpdater<UserInfo, String>() {
//		  	      @Override
//				public void update(int index, UserInfo object, String value) {
//		  	        pendingPersonChanges.add(new PersonEmailChange(object, value));
//		  	      }
//		  	    });
//	 
//
//	    // Cell for kWConsumption
//		addColumn(new EditTextCell(),
//		        "Verbrauch (kWh/h)", new GetValue<String>() {
//		          @Override
//				@SuppressWarnings("deprecation")
//		          public String getValue(Device object) {
//		        	  return object.kWConsumption.toString();
//		          }
//		        },  new FieldUpdater<Device, String>() {
//		  	      @Override
//				public void update(int index, Device object, String value) {
//		  	        pendingChanges.add(new DeviceKwChange(object, value));
//		  	      }
//		  	    });
////	    cellTable.addColumn(numberColumn, "Energieverbrauch in kWh/h");
//	    
//	    // Cell for durations
//	    addColumn(new EditTextCell(),
//	        "Laufzeiten", new GetValue<String>() {
//	          @Override
//			@SuppressWarnings("deprecation")
//	          public String getValue(Device object) {
//	        	  return Arrays.asList(object.durations).toString().replace(']', ' ').replace('[', ' ');
//	          }
//	        },  new FieldUpdater<Device, String>() {
//		  	      @Override
//				public void update(int index, Device object, String value) {
//			  	        pendingChanges.add(new DeviceDurationsChange(object, value));
//			  	        
////			  	    List<Long> categories = object.durations;
////			  	    List<String> options = new ArrayList<String>();
////				    for (Long category : categories) {
////				      options.add(category.toString());
////				    }
////			  	        
//			  	      }
//			  	    });
//	   
//	    
//	    
//	    addColumn(new EditTextCell(),
//		        "Std-Laufzeit", new GetValue<String>() {
//		          @Override
//				@SuppressWarnings("deprecation")
//		          public String getValue(Device object) {
//		        	  return object.stdDuration.toString();
//		          }
//		        }, new FieldUpdater<Device, String>() {
//			  	      @Override
//					public void update(int index, Device object, String value) {
//				  	        pendingChanges.add(new DeviceStdDurationChange(object, value));
//				  	      }
//				  	    });
////	    cellTable.addColumn(stdDurationColumn, "Std-Laufzeit");
//	    
//
//
//	    // Add a selection model to handle user selection.
//	    final SingleSelectionModel<Device> selectionModel = new SingleSelectionModel<Device>();
//	    devidesCellTable.setSelectionModel(selectionModel);
//	    selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
//	      @Override
//		public void onSelectionChange(SelectionChangeEvent event) {
//	    	  Device selected = selectionModel.getSelectedObject();
//	        if (selected != null) {
////	          Window.alert("You selected: " + selected.deviceName);
//	        }
//	      }
//	    });
//	    
//	    // Add a selection model to handle user selection.
//	    final SingleSelectionModel<UserInfo> selectionPersonModel = new SingleSelectionModel<UserInfo>();
//	    personsCellTable.setSelectionModel(selectionPersonModel);
//	    selectionPersonModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
//	      @Override
//		public void onSelectionChange(SelectionChangeEvent event) {
//	    	  UserInfo selected = selectionPersonModel.getSelectedObject();
//	        if (selected != null) {
////	          Window.alert("You selected: " + selected.deviceName);
//	        }
//	      }
//	    });
//	    
//
//	    // DeleteButtonCell.
//	    addColumn(new ButtonCell(), "entfernen", new GetValue<String>() {
//	      @Override
//		public String getValue(Device contact) {
//	        return "x" ;
//	      }
//	    }, new FieldUpdater<Device, String>() {
//	      @Override
//		public void update(int index, Device object, String value) {
//	    	  
////	        Window.alert("You clicked " + object.deviceName);
////              dataProvider.getList().remove(selected);
//	    	  devices.remove(object);
//	    	  devidesCellTable.setRowCount(devices.size(), true);
////	    	  cellTable.redraw()
//	    	  devidesCellTable.setRowData(0, devices);
//	    	  devidesCellTable.redraw();
//	      }
//	    });
//	    
//	    addPersonColumn(new ButtonCell(), "entfernen", new GetValueString<String>() {
//		      @Override
//			public String getValue(UserInfo contact) {
//		        return "x" ;
//		      }
//		    }, new FieldUpdater<UserInfo, String>() {
//		      @Override
//			public void update(int index, UserInfo object, String value) {
//		    	  
////		        Window.alert("You clicked " + object.deviceName);
////	              dataProvider.getList().remove(selected);
//		    	  kitchenStaff.remove(object);
//		    	  personsCellTable.setRowCount(kitchenStaff.size(), true);
////		    	  cellTable.redraw()
//		    	  personsCellTable.setRowData(0, kitchenStaff);
//		    	  personsCellTable.redraw();
//		      }
//		    });
//	    
//
//	    
//	    // Set the total row count. This isn't strictly necessary, but it affects
//	    // paging calculations, so its good habit to keep the row count up to date.
//	    devidesCellTable.setRowCount(devices.size(), true);
//
//	    // Push the data into the widget.
//	    devidesCellTable.setRowData(0, devices);
//	    
//	    
//	    // Set the total row count. This isn't strictly necessary, but it affects
//	    // paging calculations, so its good habit to keep the row count up to date.
//	    personsCellTable.setRowCount(kitchenStaff.size(), true);
//
//	    // Push the data into the widget.
//	    personsCellTable.setRowData(0, kitchenStaff);
//	}
//
//	/**
//	   * Add a column with a header.
//	   *
//	   * @param <C> the cell type
//	   * @param cell the cell used to render the column
//	   * @param headerText the header string
//	   * @param getter the value getter for the cell
//	   */
//	  private <C> Column<Device, C> addColumn(Cell<C> cell, String headerText,
//	      final GetValue<C> getter, FieldUpdater<Device, C> fieldUpdater) {
//	    Column<Device, C> column = new Column<Device, C>(cell) {
//	      @Override
//	      public C getValue(Device object) {
//	        return getter.getValue(object);
//	      }
//	    };
//	    column.setFieldUpdater(fieldUpdater);
//	    if (cell instanceof AbstractEditableCell<?, ?>) {
//	      editableCells.add((AbstractEditableCell<?, ?>) cell);
//	    }
//	    devidesCellTable.addColumn(column, headerText);
//	    return column;
//	  }
//	  private static interface GetValue<C> {
//		    C getValue(Device contact);
//		  }
//
//	  
//	  private <C> Column<UserInfo, C> addPersonColumn(Cell<C> cell, String headerText,
//		      final GetValueString<C> getter, FieldUpdater<UserInfo, C> fieldUpdater) {
//		    Column<UserInfo, C> column = new Column<UserInfo, C>(cell) {
//		      @Override
//		      public C getValue(UserInfo object) {
//		        return getter.getValue(object);
//		      }
//		    };
//		    column.setFieldUpdater(fieldUpdater);
//		    if (cell instanceof AbstractEditableCell<?, ?>) {
//		      editableCells.add((AbstractEditableCell<?, ?>) cell);
//		    }
//		    personsCellTable.addColumn(column, headerText);
//		    return column;
//		  }
//		  private static interface GetValueString<C> {
//			    C getValue(UserInfo contact);
//			  }
//	  
//	  /**
//	   * The list of cells that are editable.
//	   */
//	  private List<AbstractEditableCell<?, ?>> editableCells;
//
//	  /**
//	   * The list of pending changes.
//	   */
//	  private List<PendingChange<?>> pendingChanges = new ArrayList<
//	      PendingChange<?>>();
//	  
//	  private List<PendingPersonChange<?>> pendingPersonChanges = new ArrayList<
//	  PendingPersonChange<?>>();
//	  
//	  private abstract static class PendingChange<T> {
//		    private final Device contact;
//		    private final T value;
//
//		    public PendingChange(Device contact, T value) {
//		      this.contact = contact;
//		      this.value = value;
//		    }
//		    
//
//		    /**
//		     * Commit the change to the contact.
//		     */
//		    public void commit() {
//		    	// my question... should here be standing anything with the eventbus or the requestfactory?
//		      doCommit(contact, value);
//		    }
//
//		    /**
//		     * Update the appropriate field in the {@link ContactInfo}.
//		     *
//		     * @param contact the contact to update
//		     * @param value the new value
//		     */
//		    protected abstract void doCommit(Device contact, T value);
//		  }
//	  
//	  
//	  private abstract static class PendingPersonChange<T> {
//		    private final UserInfo contact;
//		    private final T value;
//
//		    public PendingPersonChange(UserInfo contact, T value) {
//		      this.contact = contact;
//		      this.value = value;
//		    }
//		    
//
//		    /**
//		     * Commit the change to the contact.
//		     */
//		    public void commit() {
//		      doCommit(contact, value);
//		    }
//
//		    /**
//		     * Update the appropriate field in the {@link ContactInfo}.
//		     *
//		     * @param contact the contact to update
//		     * @param value the new value
//		     */
//		    protected abstract void doCommit(UserInfo contact, T value);
//		  }
//	  
//	  
//	  /**
//	   * Updates the device name.
//	   */
//	  private static class DeviceNameChange extends PendingChange<String> {
//
//	    public DeviceNameChange(Device contact, String value) {
//	      super(contact, value);
//	      
////	      if (value.length() > 1){
////	    	  int row = cellTable.getRowCount();
////	    	  
////	    	  contact.
////	    	  cellTable.setRowData(row, CONTACTS);
////	      }
//	    }
//
//	    @Override
//	    protected void doCommit(Device contact, String value) {
//	      contact.deviceName = value;
//	    }
//	  }
//	  
//	  
//	  /**
//	   * Updates the persons name.
//	   */
//	  private static class PersonEmailChange extends PendingPersonChange<String> {
//
//	    public PersonEmailChange(UserInfo contact, String value) {
//	      super(contact, value);
//	      
////	      if (value.length() > 1){
////	    	  int row = cellTable.getRowCount();
////	    	  
////	    	  contact.
////	    	  cellTable.setRowData(row, CONTACTS);
////	      }
//	    }
//
//	    @Override
//	    protected void doCommit(UserInfo contact, String value) {
//	      contact.setEmailAddress(value);
//	    }
//	  }
//	  
//	  private static class PersonNameChange extends PendingPersonChange<String> {
//
//		    public PersonNameChange(UserInfo contact, String value) {
//		      super(contact, value);
//		      
////		      if (value.length() > 1){
////		    	  int row = cellTable.getRowCount();
////		    	  
////		    	  contact.
////		    	  cellTable.setRowData(row, CONTACTS);
////		      }
//		    }
//
//		    @Override
//		    protected void doCommit(UserInfo contact, String value) {
//		      contact.setNickname(value);
//		    }
//		  }
//	  /**
//	   * Updates the category.
//	   */
////	  private static class CategoryChange extends PendingChange<Category> {
////
////	    public CategoryChange(Device contact, Category value) {
////	      super(contact, value);
////	    }
////
////	    @Override
////	    protected void doCommit(Device contact, Category value) {
////	      contact.stdDuration = Long.parseLong(value.getDisplayName());
////	    }
////	  }
//
//	  /**
//	   * A contact category.
//	   */
//	  public static class Category {
//
//	    private final String displayName;
//
//	    private Category(String displayName) {
//	      this.displayName = displayName;
//	    }
//
//	    public String getDisplayName() {
//	      return displayName;
//	    }
//	  }
//	  
//	  /**
//	   * Updates the specifiation name.
//	   */
//	  private static class DeviceKwChange extends PendingChange<String> {
//
//	    public DeviceKwChange(Device contact, String value) {
//	      super(contact, value);
//	    }
//
//	    @Override
//	    protected void doCommit(Device contact, String value) {
//	      contact.kWConsumption = Double.valueOf(value);
//	    }
//	  }
//	  /**
//	   * Updates the specifiation name.
//	   */
//	  private static class DeviceSpecChange extends PendingChange<String> {
//
//	    public DeviceSpecChange(Device contact, String value) {
//	      super(contact, value);
//	    }
//
//	    @Override
//	    protected void doCommit(Device contact, String value) {
//	      contact.deviceSpec = value;
//	    }
//	  }
//	  /**
//	   * Updates the Durations List.
//	   */
//	  private static class DeviceDurationsChange extends PendingChange<String> {
//
//	    public DeviceDurationsChange(Device contact, String value) {
//	      super(contact, value);
//	    }
//
//	    @Override
//	    protected void doCommit(Device contact, String value) {
//	    	contact.durations = parsePropsToList(value,",");
//	    }
//	  }
//	  
//	  private static class DeviceStdDurationChange extends PendingChange<String> {
//
//		    public DeviceStdDurationChange(Device contact, String value) {
//		      super(contact, value);
//		    }
//
//		    @Override
//		    protected void doCommit(Device device, String value) {
//		    	device.stdDuration = Long.parseLong( value.trim());
//		    	boolean doAdd = true;
//		    	for(Long duration: device.durations){
//		    		if(duration == device.stdDuration){
//		    			doAdd = false;
//		    			break;
//		    		}
//		    		
//		    	}
//		    	if(doAdd){
//		    		device.durations[device.durations.length] = device.stdDuration;
//		    	}
//		    }
//		  }
//	  
//	  
//	  public static Long[] parsePropsToList( String propName, String delim){
//
//		  Long[] listToFill = {};
//		  
//		   List<String> stringList = getSplitList(propName, delim);
//		   for(String s : stringList){
////		       listToFill.add((T)s.trim());
//			   Long addLong = Long.parseLong( s.trim());
//			   if(addLong != null){
//			   listToFill[listToFill.length] = addLong;
//			   }
//		   }
//		   return listToFill;
//		}
//	  
//	private static List<String> getSplitList(String propName, String delim) {
//		List<String> myarray = null;
//		String[] parts = propName.split(delim);
//		myarray = Arrays.asList(parts);
//
//		return myarray;
//	}
//
//		
//}
