package ch.eaternity.client.ui.widgets;


import java.util.ArrayList;
import java.util.List;

import ch.eaternity.client.DataController;
import ch.eaternity.client.activity.RechnerActivity;
import ch.eaternity.shared.Kitchen;
import ch.eaternity.shared.KitchenUser;
import ch.eaternity.shared.UserInfo;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextInputCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;


public class KitchenDialog extends DialogBox{
	interface Binder extends UiBinder<Widget, KitchenDialog> { }
	private static final Binder binder = GWT.create(Binder.class);
	
	/**
	 * The key provider that allows us to identify Contacts even if a field
	 * changes. We identify contacts by their unique ID.
	 */
	private final ProvidesKey<KitchenUser> KEY_PROVIDER = new ProvidesKey<KitchenUser>() {
		@Override
		public Object getKey(KitchenUser item) {
			return (item == null) ? null : item.getId();
		}
	};
	
	@UiField ScrollPanel scrollPanel;
	@UiField ListBox kitchenList;
	
	@UiField TextBox kitchenNameTextBox;
	@UiField Label kitchenId;
	
	@UiField InlineLabel locationLabel;
	@UiField Button locationButton;
	/*
	@UiField static CellTable<Device> devidesCellTable  = new CellTable<Device>();
	@UiField Button addDevice;
	*/
	
	@UiField CellTable<KitchenUser> usersCellTable = new CellTable<KitchenUser>(KEY_PROVIDER);
	@UiField Button addPerson;
	
	@UiField TextBox energyMixName;
	@UiField TextBox energyMixCO2;

	@UiField Button newKitchenButton;
	@UiField Button deleteKitchenButton;
	@UiField Button exitButton;
	
	@UiField static TextErrorStyle textErrorStyle;
	
	interface TextErrorStyle extends CssResource {
		String redTextError();
	}
	
	private RechnerActivity presenter;
	private DataController dco;
	
	//private static List<Device> devices = Arrays.asList(new Device());
	private List<KitchenUser> kitchenStaff = new ArrayList<KitchenUser>();// = Arrays.asList(new KitchenUser("Name","email"));
	
	private ListDataProvider<KitchenUser> usersDataProvider = new ListDataProvider<KitchenUser>();
	
	/**
	 * The current location setted in outside of any kitchen - for adding new kitchen
	 */
	private String currentLocation;
	
	private List<Kitchen> userKitchens;
	private Kitchen currentKitchen;
	
	private UserInfo userInfo;
	
	
	// ---------------  public Methods--------------- 
	
	public KitchenDialog() {
		
	}
	
	public void setPresenter(RechnerActivity presenter){
		setWidget(binder.createAndBindUi(this));
		this.presenter = presenter;
		this.dco = presenter.getDCO();
		this.userInfo = dco.getUserInfo();		
		
		kitchenStaff.add(new KitchenUser("Aurelian", "auja@gmx.ch"));
		kitchenStaff.add(new KitchenUser("Jorim", "joirm@gmx.ch"));
		kitchenStaff.add(new KitchenUser("Test", "test@gmx.ch"));
		
		if (userInfo.isAdmin()) 
			newKitchenButton.setVisible(true);
		else
			newKitchenButton.setVisible(true);

		userKitchens = dco.getKitchens();
		 
		currentKitchen = dco.getCurrentKitchen();
		if (currentKitchen != null) {
			//currentLocation = currentKitchen.getProcessedLocation();
			usersDataProvider.setList(currentKitchen.getKitchenUsers());
			updateKitchenList();
			updateKitchenParameters();
			changeKitchenName(currentKitchen.getSymbol());
		}
		else if (userKitchens.size () > 0) {
			currentKitchen = userKitchens.get(0);
			usersDataProvider.setList(currentKitchen.getKitchenUsers());
			updateKitchenList();
			updateKitchenParameters();
			changeKitchenName(currentKitchen.getSymbol());
		}
		else {
			currentLocation = dco.getCurrentLocation();
			scrollPanel.setVisible(false);
		}
		initPersonCellTable();
		
		openDialog();
	}
	
	private void updateKitchenParameters() {
		
		if (currentKitchen != null){
			scrollPanel.setVisible(true);
		
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
		kitchenList.setSelectedIndex(userKitchens.indexOf(currentKitchen));
	}
	
	/**
	 * updateKitchenList must be called before to ensure correct selection index
	 * @param name
	 */
	private void changeKitchenName(String name) {
		  setText(name);  
		  kitchenList.setItemText(kitchenList.getSelectedIndex(), name);
		  kitchenNameTextBox.setText(name);
		  currentKitchen.setSymbol(name);
	}
	
	// -------------------------- UI Handlers ---------------------------

	@UiHandler("kitchenList")
	void onKitchenChange(ChangeEvent event) {
		currentKitchen = userKitchens.get(kitchenList.getSelectedIndex());
		switchKitchen();
	}
	/*
	@UiHandler("addDevice")
	void onAddDevicePress(ClickEvent event) {
		devices.add(new Device());
		devidesCellTable.setRowCount(devices.size(), true);
		devidesCellTable.setRowData(0, devices);
		currentKitchen.setChanged(true);
	}
	*/

	@UiHandler("addPerson")
	void onAddPersonPress(ClickEvent event) {
		currentKitchen.getKitchenUsers().add(new KitchenUser("Name", "Email"));
		usersDataProvider.refresh();
		currentKitchen.setChanged(true);
	}

	@UiHandler("locationButton")
	void onClick(ClickEvent event) {
		DistancesDialog ddlg = new DistancesDialog();
		ddlg.setPresenter(presenter);
	}

	@UiHandler("kitchenNameTextBox")
	void onNameChange(KeyUpEvent event) {
		changeKitchenName(kitchenNameTextBox.getText());
		currentKitchen.setChanged(true);
	}

	@UiHandler("energyMixName")
	void onEnergyMixChange(KeyUpEvent event) {
		if (energyMixName.getText().length() > 1) {
			currentKitchen.getEnergyMix().Name = energyMixName.getText();
		}
		currentKitchen.setChanged(true);
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
			} else {
				energyMix = Double.parseDouble(energyMixCO2.getText().trim());
				if (energyMix > 0) {
					success = true;
					energyMixCO2.removeStyleName(errorStyle);
				}
			}
		} catch (Exception e) {
		}

		if (success) {
			currentKitchen.getEnergyMix().Co2PerKWh = energyMix;
			currentKitchen.setChanged(true);
		} else {
			energyMixCO2.addStyleName(errorStyle);
		}
	}

	@UiHandler("newKitchenButton")
	public void onNewKitchenClick(ClickEvent event) {
		currentKitchen = new Kitchen();
		currentKitchen.setProcessedLocation(currentLocation);
		userKitchens.add(currentKitchen);
		switchKitchen();
	}

	@UiHandler("exitButton")
	void onOkayClicked(ClickEvent event) {
		dco.changeCurrentKitchen(currentKitchen);
		saveAndCloseDialog();
	}

	// REFACTOR: call deleteKitchen in DataController
	@UiHandler("deleteKitchenButton")
	public void onDeleteKitchenPress(ClickEvent event) {
		dco.deleteKitchen(currentKitchen);
		userKitchens.remove(currentKitchen);
		if (userKitchens.size() > 0)
			currentKitchen = userKitchens.get(0);
		else
			scrollPanel.setVisible(false);
			
		updateKitchenList();
		updateKitchenParameters();
		changeKitchenName(currentKitchen.getSymbol());
	}

	private void saveAndCloseDialog() {

		for (Kitchen kitchen : userKitchens) {
			if (kitchen.hasChanged())
				dco.saveKitchen(kitchen);
		}
		if (currentKitchen != null)
			dco.changeCurrentKitchen(currentKitchen);
		
		hide();
	}

	/**
	 * set the current kitchen before calling this method accordingly. this changes all the parameters
	 * 
	 */
	private void switchKitchen() {
		updateKitchenList();
		updateKitchenParameters();
		changeKitchenName(currentKitchen.getSymbol());

		usersDataProvider.setList(currentKitchen.getKitchenUsers());
	}

	public void initPersonCellTable() {
		
		usersCellTable.setWidth("70%", true);
		usersCellTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);

		Column<KitchenUser, String> nameColumn = new Column<KitchenUser, String>(new TextInputCell()) {
			@Override
			public String getValue(KitchenUser kitchenUser) {
				return kitchenUser.getNickname();
			}
		};

		Column<KitchenUser, String> mailColumn = new Column<KitchenUser, String>(new TextInputCell()) {
			@Override
			public String getValue(KitchenUser kitchenUser) {
				return kitchenUser.getEmailAddress();
			}
		};
		
		ActionCell.Delegate<KitchenUser> actionDelegate = new ActionCell.Delegate<KitchenUser>() {
			@Override
			public void execute(KitchenUser kitchenUser) {
				for (int i = 0; i < currentKitchen.getKitchenUsers().size(); i++) {
					KitchenUser iterateUser = currentKitchen.getKitchenUsers().get(i);
					if (iterateUser == kitchenUser)
						currentKitchen.getKitchenUsers().remove(i);
				}
				// currentKitchen.getKitchenUsers().remove(kitchenUser);
				//usersDataProvider.setList(currentKitchen.getKitchenUsers());
				usersDataProvider.refresh();
				usersCellTable.redraw();
			}
		};
		
		Column<KitchenUser, KitchenUser> removeColumn = new Column<KitchenUser, KitchenUser>(new ActionCell<KitchenUser>("x", actionDelegate)) {
			@Override
			public KitchenUser getValue(KitchenUser kitchenUser) {
				return kitchenUser;
			}
		};;

		// Add a field updater to be notified when the user enters a new name.
		nameColumn.setFieldUpdater(new FieldUpdater<KitchenUser, String>() {
			@Override
			public void update(int index, KitchenUser kitchenUser, String value) {
				kitchenUser.setNickname(value);
			}
		});

		mailColumn.setFieldUpdater(new FieldUpdater<KitchenUser, String>() {
			@Override
			public void update(int index, KitchenUser kitchenUser, String value) {
				kitchenUser.setEmailAddress(value);
			}
		});

		usersCellTable.addColumn(nameColumn, "Name");
		usersCellTable.addColumn(mailColumn, "Email Adresse");
		usersCellTable.addColumn(removeColumn, "entfernen");

		usersDataProvider.addDataDisplay(usersCellTable);
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
