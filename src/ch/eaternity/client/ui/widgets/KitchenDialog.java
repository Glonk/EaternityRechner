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
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
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
	
	@Override
    protected void onPreviewNativeEvent(NativePreviewEvent event) {
        super.onPreviewNativeEvent(event);
        switch (event.getTypeInt()) {
            case Event.ONKEYDOWN:
                if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE) {
                    hide();
                }
                break;
        }
    }
	
}

