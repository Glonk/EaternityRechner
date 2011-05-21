package ch.eaternity.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import ch.eaternity.client.widgets.ImageOverlay;
import ch.eaternity.shared.Device;
import ch.eaternity.shared.DeviceSpecification;
import ch.eaternity.shared.IngredientCondition;
import ch.eaternity.shared.Extraction;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.Kitchen;
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

public class InfoPreparationDialog extends Composite {
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
	RezeptView rezeptviewParent;
	private int selectedRow;
	private FlexTable menuTable;
	@UiField
	FlexTable specificationTable;
	private Recipe recipe;
	private FlexTable suggestTable;
	FlowPanel flowTransport = null;
	
	private HTML kmText = new HTML();
	private Kitchen kitchen;
	
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
		
	}

	public InfoPreparationDialog(FlexTable menuTable, Recipe recipe, FlexTable suggestTable, RezeptView rezeptview) {
		initWidget(uiBinder.createAndBindUi(this));
		
		specificationTable.setCellSpacing(0);

		this.rezeptviewParent = rezeptview;
		this.setRezept(recipe);
		this.menuTable = menuTable;
		this.suggestTable = suggestTable;
		
//		ArrayList<Kitchen> kitchens = (ArrayList<Kitchen>) Search.getClientData().kitchens;
//		if(kitchens.size()>0){
//			this.kitchen = kitchens.get(KitchenDialog.kitchens.getSelectedIndex());
		if(TopPanel.selectedKitchen != null){
			this.kitchen = TopPanel.selectedKitchen;
			recipe.energyMix = TopPanel.selectedKitchen.energyMix;
			
		} 
//		}

	}

	@UiHandler("addDevice")
	void onTableClicked(ClickEvent event) {
		addNewDevice();
	}
	
	public void addNewDevice(){
		
		final ListBox devicesBox = new ListBox();
		for(int i = 0;i <this.kitchen.devices.size();i++){
			devicesBox.addItem(this.kitchen.devices.get(i).deviceName);
		}
		devicesBox.addItem("anderes Gerät");
		devicesBox.setItemSelected(0, true);
		final ListBox minutesBox = new ListBox();
		setMinutes(minutesBox,0);
			
		
		final ChangeHandler onDeviceChange = new ChangeHandler(){
			public void onChange(ChangeEvent event){
				// update device in List
				
				setMinutes(minutesBox,devicesBox.getSelectedIndex());
				updateDeviceEmissions();
			}

		};
		
		Device currentDevice = this.kitchen.devices.get(devicesBox.getSelectedIndex());
		final DeviceSpecification currentDevSpec = new DeviceSpecification(currentDevice.deviceName, currentDevice.deviceSpec, currentDevice.kWConsumption, Long.parseLong(minutesBox.getItemText(minutesBox.getSelectedIndex())));
		recipe.deviceSpecifications.add(currentDevSpec);
		
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
		hinweisDetails.setText("CO2-Äquivalent durch Zubereitung: "+formatted+ " g *");
	}
	
	private void setMinutes(ListBox minutesBox, int index) {
		int itemsCount =  minutesBox.getItemCount();
		for(int i = 0; i<itemsCount; i++){
			minutesBox.removeItem(i);
		}
		if(!(this.kitchen.devices.size()<index)){
		for(int i = 0; i< this.kitchen.devices.get(index).durations.length; i++){
			minutesBox.addItem(this.kitchen.devices.get(index).durations[i].toString());
		}} else {
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
