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

import ch.eaternity.client.ui.EaternityRechnerView;
import ch.eaternity.client.ui.EaternityRechnerView.Presenter;
import ch.eaternity.shared.Workgroup;
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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;

import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Label;



/**
 * The top panel, which contains the 'welcome' message and various links.
 */
public class TopPanel<T> extends Composite {

  interface Binder extends UiBinder<Widget, TopPanel> { }
  private static final Binder binder = GWT.create(Binder.class);
  public static String currentHerkunft = "Zürich, Schweiz";

  @UiField
public Button locationButton;
  @UiField
public Anchor signOutLink;
  @UiField
public Anchor signInLink;
  @UiField
public Anchor ingredientLink;
  @UiField
public
 Anchor editKitchen;
  @UiField
public InlineLabel loginLabel;
  @UiField
public
 ListBox Monate;
  @UiField
public HTMLPanel location;
  @UiField Label locationLabel;
  @UiField
 TextBox clientLocation;
  @UiField
public
 HTMLPanel isCustomer;
  @UiField
public
 InlineLabel isCustomerLabel;
  
  @UiField HTML pinHTML;
  @UiField HTML calHTML;
  
  public  Placemark currentLocation;
  public  DistancesDialog ddlg;
  public  KitchenDialog kDlg;
  private EaternityRechnerView superDisplay;
  
 // here should be all the distances stored 
protected  ArrayList<SingleDistance> allDistances = new ArrayList<SingleDistance>();
public  boolean leftKitchen = true;
public  Workgroup selectedKitchen;

private Presenter<T> presenter;
public void setPresenter(Presenter<T> presenter){
	this.presenter = presenter;
}

public void setSuperDisplay(EaternityRechnerView superDisplay){
	this.superDisplay = superDisplay;
}
  
  public TopPanel() {
		
		
    initWidget(binder.createAndBindUi(this));
    locationButton.setEnabled(false);
    ingredientLink.setVisible(false);
    signOutLink.setVisible(false);
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
	
	
	calHTML.addMouseListener(
			new TooltipListener(
					"Der Monat in dem Sie kochen.", 5000 /* timeout in milliseconds*/,"toolTipDown",-130,10));
	
	
	pinHTML.addMouseListener(
			new TooltipListener(
					"Der Ort in dem Sie kochen.", 5000 /* timeout in milliseconds*/,"toolTipDown",-110,10));
	
	//.parse( Integer.toString( TopPanel.Monate.getSelectedIndex() ));
	

  }
  

  @UiHandler("Monate")
  void onChange(ChangeEvent event) {
	  superDisplay.updateSaisonAndMore();
	  presenter.getSearchPanel().updateResults(Search.SearchInput.getText());
	  // TODO aktualisiere die Saisonalität aller Rezepte... dieser Prozess muss gethreaded sein!
	  // TODO close the InfozutatDialog when doing this...
  }

  @UiHandler("locationButton")
  public void onClick(ClickEvent event) {
	  ddlg = new DistancesDialog(clientLocation.getText(),superDisplay); 
	  ddlg.setPresenter(presenter);
  }
  

  @UiHandler("editKitchen")
  public void onEditKitchenClick(ClickEvent event) {
	  kDlg = new KitchenDialog(clientLocation.getText(),superDisplay); 
	  kDlg.setPresenter(presenter);
  }
  
}
