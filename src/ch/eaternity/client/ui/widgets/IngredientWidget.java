package ch.eaternity.client.ui.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;
/*
public class IngredientWidget extends Composite{
	
	Button removeZutat = new Button("x");
	
	removeZutat.addClickHandler(new ClickHandler() {
		public void onClick(ClickEvent event) {
			
			// this list is kept in sync with the table...
			int removedIndex = recipe.ingredients.indexOf(zutat);
			
			// by button press both get deleted
			recipe.ingredients.remove(removedIndex);
			MenuTable.removeRow(removedIndex);
	//		EaternityRechner.MenuTable.removeRow(removedIndex);
			
			// does this work to prevent the error? which error?
			// if ingredientsDialog is open, yet item gets removed... remove also IngredientsDialog
			styleRow(removedIndex, false);
			
			if(selectedRow == removedIndex){
				if(addInfoPanel.getWidgetCount() ==2){
					addInfoPanel.remove(1);
				}
			} else {
				if(selectedRow > removedIndex){
					selectedRow = selectedRow-1;
					selectRow(selectedRow);
				}
			}
			
			// set the colors in the right order...
			String style = evenStyleRow.evenRow();
			for(Integer rowIndex = 0; rowIndex<MenuTable.getRowCount(); rowIndex++){
				if ((rowIndex % 2) == 1) {
					MenuTable.getRowFormatter().addStyleName(rowIndex, style);
				} else {
					MenuTable.getRowFormatter().removeStyleName(rowIndex, style);
				}
			}
			
			// what is this for?
			if(askForLess != null){
				
					if(showImageHandler != null){
						showImageHandler.removeHandler();
						showImageHandler = null;
					}
					if(askForLess){
				if(detailText != null){
					overlap = Math.max(1,showImageRezept.getHeight() -  addInfoPanel.getOffsetHeight() +40 );
	
					//				rezeptView.detailText.setHeight(height)
					detailText.setHTML("<img src='pixel.png' style='float:right' width=360 height="+ Integer.toString(overlap)+" />"+recipe.getCookInstruction());
				}
					}
			}
			
			// update all values, that change as there is one ingredient less...
			updateSuggestion();
	//		updateSuggestion(EaternityRechner.SuggestTable, EaternityRechner.MenuTable);
		}
	});
	
	//HasHorizontalAlignment.ALIGN_RIGHT
	final TextBox MengeZutat = new TextBox();
	MengeZutat.setAlignment(TextAlignment.RIGHT);
	MengeZutat.setText(Integer.toString(zutat.getWeight()));
	MengeZutat.setWidth("36px");
	
	MengeZutat.addKeyUpHandler( new KeyUpHandler() {
		public void onKeyUp(KeyUpEvent event) {
			int keyCode = event.getNativeKeyCode();
			if ((!Character.isDigit((char) keyCode)) && (keyCode != KeyCodes.KEY_TAB)
					&& (keyCode != KeyCodes.KEY_BACKSPACE)
					&& (keyCode != KeyCodes.KEY_DELETE) && (keyCode != KeyCodes.KEY_ENTER) 
					&& (keyCode != KeyCodes.KEY_HOME) && (keyCode != KeyCodes.KEY_END)
					&& (keyCode != KeyCodes.KEY_LEFT) && (keyCode != KeyCodes.KEY_UP)
					&& (keyCode != KeyCodes.KEY_RIGHT) && (keyCode != KeyCodes.KEY_DOWN)) {
				// TextBox.cancelKey() suppresses the current keyboard event.
				MengeZutat.cancelKey();
			} else {
				String MengeZutatWert;
				int rowhere = getWidgetRow(MengeZutat,MenuTable);
				if(!MengeZutat.getText().equalsIgnoreCase("")){
					MengeZutatWert = MengeZutat.getText().trim();
					try {
						zutat.setWeight(Double.valueOf(MengeZutatWert).intValue());
					}
					catch (NumberFormatException nfe) {
						MengeZutat.setText("");
					}
							
				} else {
					MengeZutatWert = "";
				}
				
				updateTable(rowhere,zutat);
			}
	
	
		}
	
	
	});
	
	//Name
	if ((row % 2) == 1) {
		String style = evenStyleRow.evenRow();
		MenuTable.getRowFormatter().addStyleName(row, style);
	}
	MenuTable.setWidget(row, 1, MengeZutat);
	
	changeIcons(row, zutat);
	
	// Remove Button
	MenuTable.setWidget(row, 6, removeZutat);
	
	// drag Handler
	HTML handle = new HTML("<div class='dragMe'><img src='pixel.png' width=10 height=20 /></div>");
	tableRowDragController.makeDraggable(handle);
	MenuTable.setWidget(row, 0, handle);
	
	updateTable(row,zutat);
	row = row+1;
	}
	
}
*/
