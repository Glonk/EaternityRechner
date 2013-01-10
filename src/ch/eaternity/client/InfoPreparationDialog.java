package ch.eaternity.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


import ch.eaternity.client.ui.EaternityRechnerView.Presenter;
import ch.eaternity.client.ui.widgets.ImageOverlay;
import ch.eaternity.shared.Device;
import ch.eaternity.shared.DeviceSpecification;
import ch.eaternity.shared.IngredientCondition;
import ch.eaternity.shared.Extraction;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.Workgroup;
import ch.eaternity.shared.MoTransportation;
import ch.eaternity.shared.Production;
import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.SingleDistance;
import ch.eaternity.shared.IngredientSpecification;


import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLTable.Cell;

public class InfoPreparationDialog<T> extends Composite {
	interface Binder extends UiBinder<Widget, InfoPreparationDialog> { }
	private static Binder uiBinder = GWT.create(Binder.class);
	@UiField HTML zutatName;
	@UiField PassedStyle passedStyle;

	@UiField Label hinweisDetails;
	@UiField Label closeLabel;
	
	static double distance = 0;

	private final static Geocoder geocoder = new Geocoder();
	final TextBox newExtractionBox = new TextBox();
	ClickHandler clickerHandler = null;
	Boolean handlerNotAdded = true;
	
	@UiField SelectionStyle selectionStyle;
	IngredientSpecification zutatSpec;
	Ingredient stdIngredient;
	RecipeEditView rezeptviewParent;
	private int selectedRow;
	private FlexTable menuTable;
	@UiField
	FlexTable specificationTable;
	private Recipe recipe;
	private FlexTable suggestTable;
	FlowPanel flowTransport = null;
	
	private HTML kmText = new HTML();
	private Workgroup kitchen;
	
	private Presenter<T> presenter;
	public void setPresenter(Presenter<T> presenter){
		this.presenter = presenter;
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
//			hinweisPanel.addStyleName(style);
			hinweisDetails.setStyleName("black");
		} else {
//			hinweisPanel.removeStyleName(style);
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
		rezeptviewParent.addInfoPanel.getWidget(2).setVisible(false);
		rezeptviewParent.menuDecoInfo.setVisible(true);
		rezeptviewParent.styleRow(selectedRow, false);
//		rezeptviewParent.addInfoPanel.insert(new HTML("test"), 1);
		rezeptviewParent.PrepareButton.setVisible(true);
		
		
		
	}

	public InfoPreparationDialog(FlexTable menuTable, Recipe recipe, FlexTable suggestTable, RecipeEditView editRecipeView) {
		initWidget(uiBinder.createAndBindUi(this));
		
		specificationTable.setCellSpacing(0);

		editRecipeView.PrepareButton.setVisible(false);
		
		this.rezeptviewParent = editRecipeView;
		this.setRezept(recipe);
		this.menuTable = menuTable;
		this.suggestTable = suggestTable;
		
		if(presenter.getTopPanel().selectedKitchen != null){
			this.kitchen = presenter.getTopPanel().selectedKitchen;
			recipe.energyMix = presenter.getTopPanel().selectedKitchen.energyMix;
			
		} 

		
		if(!recipe.deviceSpecifications.isEmpty()){
			rezeptviewParent.PrepareButton.setText("Zubereitung bearbeiten");
			// add those to the list!
			
			for(DeviceSpecification devSpec : recipe.deviceSpecifications){
				
				
				// find the according Device in the available list, if not there include it
				boolean found = false;
				int i = 0;
				for(Device device: kitchen.devices){
					
					if(device.deviceName.equals(devSpec.deviceName)){
						found = true;
						// found in the list
						
						// get minutes selection
						int j = 0;
						if(kitchen.devices.size()>(i)){
							for(long durationHere: device.durations){
								if(durationHere >= devSpec.duration){
									break;
								}
							j++;
							}
						}
						
						addDevice(i,j,devSpec);
					}
					i++;
				}
				if(!found){
					//add to list
					Long[] durationsHere = new Long[1];
					durationsHere[0] = devSpec.duration;
					kitchen.devices.add(new Device(devSpec.deviceName,devSpec.deviceSpec, devSpec.kWConsumption, durationsHere , devSpec.duration));
					addDevice(kitchen.devices.size()-1,0,devSpec);
				}
				
				
		}
		
		// update the status info
		updateDeviceEmissions();
	}
		
		

	}

	@UiHandler("addDevice")
	void onTableClicked(ClickEvent event) {
		rezeptviewParent.PrepareButton.setText("Zubereitung bearbeiten");
		// if there is no device yet:
		if(specificationTable.getRowCount() == 0){
			suggestTable.setText(0,0,"Zubereitung");
		}
			
		addDevice(0,0,null);
	}
	
	public void addDevice(int deviceSelected, int minutesSelected,  DeviceSpecification newDevSpec){
		
		final ListBox devicesBox = new ListBox();
		for(int i = 0;i <this.kitchen.devices.size();i++){
			devicesBox.addItem(this.kitchen.devices.get(i).deviceName);
		}
		devicesBox.addItem("anderes Gerät");
		
		// this is relevant
		devicesBox.setItemSelected(deviceSelected, true);
		
		final ListBox minutesBox = new ListBox();
		
			
		
		setMinutes(minutesBox,deviceSelected);
		minutesBox.setSelectedIndex(minutesSelected);
		
		final Device currentDevice = kitchen.devices.get(devicesBox.getSelectedIndex());
		if(newDevSpec == null){
			newDevSpec = new DeviceSpecification(currentDevice.deviceName, currentDevice.deviceSpec, currentDevice.kWConsumption, Long.parseLong(minutesBox.getItemText(minutesBox.getSelectedIndex())));
			recipe.deviceSpecifications.add(newDevSpec);
		}
		final DeviceSpecification currentDevSpec = newDevSpec;
		
		
		
		final ChangeHandler onDeviceChange = new ChangeHandler(){
			public void onChange(ChangeEvent event){
				// update device in List
				int index = devicesBox.getSelectedIndex();
				int countDevices = kitchen.devices.size();
				if(countDevices>(index)){
					Device deviceNow = kitchen.devices.get(devicesBox.getSelectedIndex());
					currentDevSpec.deviceName = deviceNow.deviceName;
					currentDevSpec.deviceSpec = deviceNow.deviceSpec;
					currentDevSpec.kWConsumption = deviceNow.kWConsumption;
					currentDevSpec.duration = kitchen.devices.get(devicesBox.getSelectedIndex()).stdDuration;
				} else {
					currentDevSpec.duration= 0l;
				}
				
				setMinutes(minutesBox,index);
				int i = 0;
				if(countDevices>(index)){
				for(long durationHere: kitchen.devices.get(devicesBox.getSelectedIndex()).durations){
					
					if(durationHere >= currentDevSpec.duration){
						break;
					}
					i++;
				}
				}
				
				minutesBox.setSelectedIndex(i);
				updateDeviceEmissions();
			}

		};
		
		devicesBox.addChangeHandler(onDeviceChange);
	    
		
	
		final ChangeHandler onMinutesChange = new ChangeHandler(){
			public void onChange(ChangeEvent event){
				currentDevSpec.duration = Long.parseLong( minutesBox.getItemText(minutesBox.getSelectedIndex()));
				
				updateDeviceEmissions();
				
			}

		};
		minutesBox.addChangeHandler(onMinutesChange);
		
		int row = specificationTable.getRowCount();
			
		specificationTable.setWidget(row,0,devicesBox);
		specificationTable.setWidget(row,1,minutesBox);
		
		
		final Anchor deleteAnchor = new Anchor("x");
//		removeZutat.addStyleName("style.gwt-Button");
//		removeZutat.addStyleDependentName("gwt-Button");
		deleteAnchor.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				recipe.deviceSpecifications.remove(currentDevSpec);
				int row = getWidgetRow(deleteAnchor, specificationTable);
				specificationTable.removeRow(row);
				updateDeviceEmissions();
			}
		});
		
		specificationTable.setWidget(row,2,deleteAnchor);
		
		updateDeviceEmissions();
			
	}
	private void updateDeviceEmissions() {
		String formatted = NumberFormat.getFormat("##").format( recipe.getDeviceCo2Value() );
		hinweisDetails.setText("CO₂-Äquivalent durch Zubereitung: "+formatted+ " g*");
		suggestTable.setHTML(0,1,"ca <b>"+formatted+"g</b> *");
	}
	
	private void setMinutes(ListBox minutesBox, int index) {

		// remove all previous items
		int itemsCount =  minutesBox.getItemCount();
		for(int i = 0; i<itemsCount; i++){
			// 0 as items reorder!
			minutesBox.removeItem(0);
		}
		
		
		// case that there is actually a device selected:
		if(this.kitchen.devices.size()>(index)){
			//
			for(int i = 0; i< this.kitchen.devices.get(index).durations.length; i++){
				minutesBox.addItem(this.kitchen.devices.get(index).durations[i].toString());
			}
		} else {
				// handle new entries...
		}

		
		minutesBox.addItem("genauer");
	}
		

	
	//TODO here comes all the CO2 Logic
	public void updateZutatCO2(){
		

		
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
