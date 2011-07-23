/*
 * Copyright 2007 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package ch.eaternity.client;



import java.util.ArrayList;
import java.util.Date;

import ch.eaternity.shared.Kitchen;
import ch.eaternity.shared.SingleDistance;

import com.google.gwt.core.client.GWT;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.maps.client.geocode.Placemark;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;

import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Label;



/**
 * The top panel, which contains the 'welcome' message and various links.
 */
public class TopPanel extends Composite {

  interface Binder extends UiBinder<Widget, TopPanel> { }
  private static final Binder binder = GWT.create(Binder.class);
  static String currentHerkunft = "Zürich, Schweiz";

  @UiField Button locationButton;
  @UiField Anchor signOutLink;
  @UiField Anchor signInLink;
//  @UiField Anchor stepIn;
//  @UiField Anchor stepOut;
  @UiField Anchor ingredientLink;
  @UiField
static Anchor editKitchen;
  @UiField InlineLabel loginLabel;
  @UiField
static ListBox Monate;
//  @UiField
//static ListBox kitchens;
  @UiField 
  static HTMLPanel location;
//  @UiField HTMLPanel kitchen;
  @UiField static Label locationLabel;
//  @UiField Label loadingLabel;
  @UiField
static TextBox clientLocation;
  @UiField
static HTMLPanel isCustomer;
  @UiField
static InlineLabel isCustomerLabel;
  
  public static Placemark currentLocation;
  public static DistancesDialog ddlg;
  public static KitchenDialog kDlg;

  
 // here should be all the distances stored 
protected static ArrayList<SingleDistance> allDistances = new ArrayList<SingleDistance>();
public static boolean leftKitchen = true;
public static Kitchen selectedKitchen;


  
  public TopPanel() {
    initWidget(binder.createAndBindUi(this));
    locationButton.setEnabled(false);
    ingredientLink.setVisible(false);
    signOutLink.setVisible(false);
//    stepOut.setVisible(false);
//    stepIn.setVisible(false);
    editKitchen.setVisible(false);
    
	Monate.addItem("Januar");
	Monate.addItem("Februar");
	Monate.addItem("März");
	Monate.addItem("April");
	Monate.addItem("Mai");
	Monate.addItem("Juni");
	Monate.addItem("Juli");
	Monate.addItem("August");
	Monate.addItem("September");
	Monate.addItem("Oktober");
	Monate.addItem("November");
	Monate.addItem("Dezember");
	
	
	Date date = new Date();
	Monate.setSelectedIndex(date.getMonth());
	

	
	
	//.parse( Integer.toString( TopPanel.Monate.getSelectedIndex() ));
	

  }
  

  @UiHandler("Monate")
  void onChange(ChangeEvent event) {
	  EaternityRechner.updateSaisonAndMore();
	  Search.updateResults(Search.SearchInput.getText());
	  // TODO aktualisiere die Saisonalität aller Rezepte... dieser Prozess muss gethreaded sein!
	  // TODO close the InfozutatDialog when doing this...
  }

  @UiHandler("locationButton")
  public void onClick(ClickEvent event) {
	  ddlg = new DistancesDialog(clientLocation.getText()); 
  }
  
  
//  @UiHandler("stepIn")
//  public void onStepInClick(ClickEvent event) {
//		stepIn.setVisible(false);
//		stepOut.setVisible(true);
//		location.setVisible(false);
//		kitchen.setVisible(true);
//  }
//  
//  @UiHandler("stepOut")
//  public void onStepOutClick(ClickEvent event) {
//		stepIn.setVisible(true);
//		stepOut.setVisible(false);	
//		location.setVisible(true);
//		kitchen.setVisible(false);
//	  
//  }

  @UiHandler("editKitchen")
  public void onEditKitchenClick(ClickEvent event) {
	  kDlg = new KitchenDialog(clientLocation.getText()); 
  }
  
}
