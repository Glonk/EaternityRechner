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


import ch.eaternity.shared.Zutat;
import ch.eaternity.shared.Zutat.Herkuenfte;
import ch.eaternity.shared.Zutat.Zustaende;
import ch.eaternity.shared.Zutat.Produktionen;
import ch.eaternity.shared.Zutat.Transportmittel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.DOMException;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.XMLParser;
import com.google.gwt.xml.client.NodeList;


/**
 * A simple example of an 'about' dialog box.
 */
public class AdminDialog extends DialogBox {

	interface Binder extends UiBinder<Widget, AdminDialog> { }
	private static final Binder binder = GWT.create(Binder.class);

	@UiField Button closeButton;
	@UiField Button executeButton;
	@UiField TextArea adminText;
	@UiField Label statusLabel;

	
	private final DataServiceAsync zutatService = GWT.create(DataService.class);

	public AdminDialog() {
		
		// Use this opportunity to set the dialog's caption.
		setText("eaternity rechner admin interface");
		setWidget(binder.createAndBindUi(this));

		setAnimationEnabled(true);
		setGlassEnabled(true);
		adminText.setCharacterWidth(40);
		adminText.setVisibleLines(10);

		executeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				String file = adminText.getText();
				if (file.length() == 0) {
					Window.alert("no text");
				} else {
					processFile(file);
				}
			}


		});
	}

	private void processFile(String messageXml) {
		try {
			// parse the XML document into a DOM
			Document messageDom = XMLParser.parse(messageXml);
//			Window.alert("Root element " + messageDom.getDocumentElement().getNodeName());
			NodeList zutatenLst = messageDom.getElementsByTagName("zutat");
			// Window.alert( Integer.toString(zutatenLst.getLength()) );
			for (int s = 0; s < zutatenLst.getLength(); s++) {
				
				Zutat newZutat = new Zutat();
				
				Node zutat = zutatenLst.item(s);

				if (zutat.getNodeType() == Node.ELEMENT_NODE) {

					Element zutatElmnt = (Element) zutat;
					
					// id
					NodeList zutatIdElmntLst = zutatElmnt.getElementsByTagName("id");
					Element zutatIdElmnt = (Element) zutatIdElmntLst.item(0);
					NodeList zutatId = zutatIdElmnt.getChildNodes();
//					Window.alert("Zutat Id : "  + ((Node) zutatId.item(0)).getNodeValue());
					newZutat.setId( Long.parseLong(((Node) zutatId.item(0)).getNodeValue().trim()) );
					
					// symbol
					NodeList symbolElmntLst = zutatElmnt.getElementsByTagName("symbol");
					Element symbolElmnt = (Element) symbolElmntLst.item(0);
					NodeList symbol = symbolElmnt.getChildNodes();
//					Window.alert("Zutat Name : "  + ((Node) symbol.item(0)).getNodeValue());
					newZutat.setSymbol( ((Node) symbol.item(0)).getNodeValue() );
					
					// CO2eWert
					NodeList CO2eWertElmntLst = zutatElmnt.getElementsByTagName("CO2eWert");
					Element CO2eWertElmnt = (Element) CO2eWertElmntLst.item(0);
					NodeList CO2eWert = CO2eWertElmnt.getChildNodes();
//					Window.alert("CO2eWert : "  + ((Node) CO2eWert.item(0)).getNodeValue());
					newZutat.setCO2eWert( Integer.parseInt( ((Node) CO2eWert.item(0)).getNodeValue() ) );


					NodeList alternativen = zutatElmnt.getElementsByTagName("alternativen");
					Element alternativeElement = (Element) alternativen.item(0);

					if (alternativeElement.getNodeType() == Node.ELEMENT_NODE) {
						ArrayList<Long> newAlternativen = new ArrayList<Long>();
						NodeList alternativeElmntLst = alternativeElement.getElementsByTagName("zutat_id");
						for(int i=0; i<alternativeElmntLst.getLength(); i++){
							Node alternativenId = alternativeElmntLst.item(i);
							if (alternativenId.getNodeType() == Node.ELEMENT_NODE) {
								
								Element alternativenIdElement = (Element) alternativenId;
								NodeList alternativeId = alternativenIdElement.getChildNodes();
								
//								Window.alert("Alterntive Id : "  + ((Node) alternativeId.item(0)).getNodeValue());
								newAlternativen.add( Long.parseLong(((Node) alternativeId.item(0)).getNodeValue().trim()) );
							}

						}
						newZutat.setAlternativen(newAlternativen);
					}
					
					NodeList stdSpecification = zutatElmnt.getElementsByTagName("stdSpecification");
					Element stdSpecificationElement = (Element) stdSpecification.item(0);
					if (stdSpecificationElement.getNodeType() == Node.ELEMENT_NODE) {
						
						
						// std mengeGramm
						NodeList mengeGrammElmntLst = stdSpecificationElement.getElementsByTagName("mengeGramm");
						Element mengeGrammElmnt = (Element) mengeGrammElmntLst.item(0);
						NodeList mengeGramm = mengeGrammElmnt.getChildNodes();
//						Window.alert("std menge Gramm : "  + ((Node) mengeGramm.item(0)).getNodeValue());
						newZutat.setStdMengeGramm( Integer.parseInt( ((Node) mengeGramm.item(0)).getNodeValue() ) );
						
						// std herkunft
						NodeList herkunftElmntLst = stdSpecificationElement.getElementsByTagName("herkunft");
						Element herkunftElmnt = (Element) herkunftElmntLst.item(0);
						NodeList herkunft = herkunftElmnt.getChildNodes();
//						Window.alert("std herkunft : "  + ((Node) herkunft.item(0)).getNodeValue());
						//
						Herkuenfte herkunftStd =  Herkuenfte.valueOf(( (Node) herkunft.item(0)).getNodeValue().trim());
						newZutat.setStdHerkunft( herkunftStd  );
//						ZutatVarianten.showAddress( ( (Node) herkunft.item(0)).getNodeValue().trim() );
						
						// std zustand
						NodeList zustandElmntLst = stdSpecificationElement.getElementsByTagName("zustand");
						Element zustandElmnt = (Element) zustandElmntLst.item(0);
						NodeList zustand = zustandElmnt.getChildNodes();
//						Window.alert("std zustand : "  + ((Node) zustand.item(0)).getNodeValue());
						newZutat.setStdZustand( Zustaende.valueOf( ((Node) zustand.item(0)).getNodeValue() ));
						
						// std produktion
						NodeList produktionElmntLst = stdSpecificationElement.getElementsByTagName("produktion");
						Element produktionElmnt = (Element) produktionElmntLst.item(0);
						NodeList produktion = produktionElmnt.getChildNodes();
//						Window.alert("std produktion : "  + ((Node) produktion.item(0)).getNodeValue());
						newZutat.setStdProduktion( Produktionen.valueOf( ((Node) produktion.item(0)).getNodeValue() ));
						
						// std transportmittel
						NodeList transportmittelElmntLst = stdSpecificationElement.getElementsByTagName("transportmittel");
						Element transportmittelElmnt = (Element) transportmittelElmntLst.item(0);
						NodeList transportmittel = transportmittelElmnt.getChildNodes();
//						Window.alert("std transportmittel : "  + ((Node) transportmittel.item(0)).getNodeValue());
						newZutat.setStdTransportmittel( Transportmittel.valueOf( ((Node) transportmittel.item(0)).getNodeValue() ));
						
						
						// std startSeason
						NodeList startSeasonElmntLst = stdSpecificationElement.getElementsByTagName("startSeason");
						Element startSeasonElmnt = (Element) startSeasonElmntLst.item(0);
						NodeList startSeason = startSeasonElmnt.getChildNodes();
//						Window.alert("std startSeason : "  + ((Node) startSeason.item(0)).getNodeValue());
						newZutat.setStdStartSeason(  ((Node) startSeason.item(0)).getNodeValue() );
						
						
						// std stopSeason
						NodeList stopSeasonElmntLst = stdSpecificationElement.getElementsByTagName("stopSeason");
						Element stopSeasonElmnt = (Element) stopSeasonElmntLst.item(0);
						NodeList stopSeason = stopSeasonElmnt.getChildNodes();
//						Window.alert("std stopSeason : "  + ((Node) stopSeason.item(0)).getNodeValue());
						newZutat.setStdStopSeason(	((Node)  stopSeason.item(0)).getNodeValue() );
						
						
						
						NodeList labels = zutatElmnt.getElementsByTagName("Labels");
						Element labelsElement = (Element) labels.item(0);

						if (labelsElement.getNodeType() == Node.ELEMENT_NODE) {
							ArrayList<Long> newLabels = new ArrayList<Long>();
							NodeList labelIdElmntLst = labelsElement.getElementsByTagName("label_id");
							for(int i=0; i<labelIdElmntLst.getLength(); i++){
								Node labelsId = labelIdElmntLst.item(i);
								if (labelsId.getNodeType() == Node.ELEMENT_NODE) {
									
									Element labelIdElement = (Element) labelsId;
									NodeList labelId = labelIdElement.getChildNodes();
									
//									Window.alert("Label Id : "  + ((Node) labelId.item(0)).getNodeValue());
									newLabels.add(Long.parseLong(((Node) labelId.item(0)).getNodeValue().trim()));
									
								}

							}
							newZutat.setStdLabels(newLabels);
						}
						
						
						
						NodeList herkuenfte = zutatElmnt.getElementsByTagName("Herkuenfte");
						Element herkuenfteElement = (Element) herkuenfte.item(0);

						if (herkuenfteElement.getNodeType() == Node.ELEMENT_NODE) {
							ArrayList<Herkuenfte> newHerkuenfte = new ArrayList<Herkuenfte>();
							NodeList herkunftIdElmntLst = herkuenfteElement.getElementsByTagName("herkunft");
							for(int i=0; i<herkunftIdElmntLst.getLength(); i++){
								Node herkuenfte_name = herkunftIdElmntLst.item(i);
								if (herkuenfte_name.getNodeType() == Node.ELEMENT_NODE) {
									
									Element herkunft_nameElement = (Element) herkuenfte_name;
									NodeList herkuenft_name = herkunft_nameElement.getChildNodes();
									
//									Window.alert("Label Id : "  + ((Node) labelId.item(0)).getNodeValue());
									newHerkuenfte.add( Herkuenfte.valueOf( ((Node) herkuenft_name.item(0)).getNodeValue().trim() ));
									
								}

							}
							newZutat.setHerkuenfte(newHerkuenfte);
						}
						
						
					}
					
					
					
				}
				addZutat(newZutat);

			}

			//		    Node fromNode = messageDom.getElementsByTagName("from").item(0);
			//		    String from = ((Element)fromNode).getAttribute("displayName");


			
		} catch (DOMException e) {
			Window.alert(e.getMessage());
		}
	}
	
	private void addZutat(final Zutat zutat) {
		 zutatService.addZutat(zutat, new AsyncCallback<String>() {
			public void onFailure(Throwable error) {
				Window.alert(error.getMessage());
			}
			public void onSuccess(String ignore) {
				Window.alert(zutat.getSymbol());
			}
		});
	}
	
	

	@Override
	protected void onPreviewNativeEvent(NativePreviewEvent preview) {
		super.onPreviewNativeEvent(preview);

		NativeEvent evt = preview.getNativeEvent();
		if (evt.getType().equals("keydown")) {
			// Use the popup's key preview hooks to close the dialog when either
			// enter or escape is pressed.
			switch (evt.getKeyCode()) {
			case KeyCodes.KEY_ENTER:
				String file = adminText.getText();
				if (file.length() != 0) {
					processFile(file);
				}
			case KeyCodes.KEY_ESCAPE:
				hide();
				break;
			}
		}
	}

	@UiHandler("closeButton")
	void onSignOutClicked(ClickEvent event) {
		hide();
	}
}
