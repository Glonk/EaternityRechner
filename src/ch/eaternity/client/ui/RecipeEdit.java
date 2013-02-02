
package ch.eaternity.client.ui;

import gwtupload.client.IUploader;
import gwtupload.client.PreloadedImage.OnLoadPreloadedImageHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import ch.eaternity.client.DataController;
import ch.eaternity.client.activity.RechnerActivity;
import ch.eaternity.client.events.UpdateRecipeViewEvent;
import ch.eaternity.client.events.UpdateRecipeViewEventHandler;
import ch.eaternity.client.place.RechnerRecipeViewPlace;
import ch.eaternity.client.ui.widgets.FlexTableRowDragController;

import ch.eaternity.client.ui.widgets.UploadPhoto;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.IngredientSpecification;
import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.RecipeComment;
import ch.eaternity.shared.SeasonDate;
import ch.eaternity.shared.comparators.ComparatorComparator;
import ch.eaternity.shared.comparators.ComparatorObject;
import ch.eaternity.shared.comparators.ComparatorRecipe;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Close;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Anchor;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author aurelianjaggi
 *
 */
public class RecipeEdit extends Composite {
	interface Binder extends UiBinder<Widget, RecipeEdit> { }
	private static Binder uiBinder = GWT.create(Binder.class);
	
	// ---------------------- User Interface Elements --------------
	@UiField public AbsolutePanel dragArea;
	@UiField SelectionStyleRow selectionStyleRow;
	@UiField EvenStyleRow evenStyleRow;

	@UiField FlexTable MenuTable;
	@UiField public VerticalPanel menuDecoInfo;
	@UiField Anchor PreparationButton;
	@UiField TextBox RezeptName;
	@UiField FlexTable SuggestTable;
	@UiField FlexTable commentTable;
	@UiField HorizontalPanel addInfoPanel;
	@UiField Close removeRezeptButton;
	@UiField public HTMLPanel htmlRezept;
	@UiField HTMLPanel rezeptTitle;

	@UiField HTML bottomIndikator;
	@UiField TextArea cookingInstr;
	@UiField TextBox amountPersons;
	@UiField TextBox rezeptDetails;
	@UiField VerticalPanel MenuTableWrapper;

	@UiField HTML detailText;
	@UiField HTML codeImage;
	
	@UiField Button newRecipeButton;
	@UiField Button generatePDFButton;
	@UiField Button publishButton;
	@UiField Button duplicateButton;
	@UiField Button saveButton;
	@UiField Button deleteButton;
	
	// ---------------------- Class Variables ----------------------
	
	private RechnerActivity presenter;
	private DataController dco;
	
	private FlowPanel panelImages = new FlowPanel();
	private UploadPhoto uploadWidget;
	private HandlerRegistration imagePopUpHandler = null;
	private static int overlap = 0;
	
	private HTML htmlCooking;
	private Boolean askForLess;
	private Boolean askForLess2;
	private Image showImageRezept = new Image();
	private Anchor bildEntfernen;
	private HandlerRegistration showImageHandler = null;
	private FlexTableRowDragController tableRowDragController = null;
	//private FlexTableRowDropController flexTableRowDropController = null;
	
	private boolean saved = false;
	private boolean infoDialogIsOpen = false;
	
	private int numberofComments = 0;
	
	private Listener listener;
	private int  selectedRow = 0;
	private int  selectedRezept = -1;
	private Recipe recipe;

	private int heightOfView;
	
	public interface Listener {
		void onItemSelected(IngredientSpecification item);
	}
	
	interface SelectionStyleRow extends CssResource {
		String selectedRow();
	}
	interface EvenStyleRow extends CssResource {
		String evenRow();
	}
	
	public void setListener(Listener listener) {
		this.listener = listener;
	}
	
	
	// ---------------------- public Methods -----------------------
	
	public RecipeEdit() {
		initWidget(uiBinder.createAndBindUi(this));
		
		/*
	    tableRowDragController = new FlexTableRowDragController(dragArea);
	    flexTableRowDropController = new FlexTableRowDropController(MenuTable,this);
    
	    tableRowDragController.registerDropController(flexTableRowDropController);

	    initTable();
	    
	    // from the old setpresenter method...
	    if(dco.getCurrentKitchen() == null){
	    	PreparationButton.setVisible(false);
	    }
	    
		if (dco.getLoginInfo().isLoggedIn()) {
			uploadWidget = new UploadPhoto(dco.getLoginInfo(), this);
			uploadWidget.setStyleName("notInline");		
			menuDecoInfo.insert(uploadWidget,0);
		}
	    
		
		if(recipe.getCookInstruction() != null){
	    	cookingInstr.setText(recipe.getCookInstruction());
	    } else {
	    	String cookingIntructions = "Kochanleitung.";
	    	if(!presenter.getLoginInfo().isLoggedIn()){
	    		cookingIntructions = "Sie sind nicht angemeldet. Alle Änderungen am Rezept können nicht gespeichert werden.";
	    	}
			cookingInstr.setText(cookingIntructions);
	    }	
	    */
		
		
	}
	

	private void bind() {
		
		
		//  Listen to the EventBus 
		presenter.getEventBus().addHandler(UpdateRecipeViewEvent.TYPE,
				new UpdateRecipeViewEventHandler() {
					@Override
					public void onEvent(UpdateRecipeViewEvent event) {
					}
				});
	}
	public void setPresenter(RechnerActivity presenter) {
		this.presenter = presenter;
		this.dco = presenter.getDCO();
		this.setHeight("1600px");
		bind();
	}
	
	// ---------------------- UI Handlers ----------------------
	@UiHandler("newRecipeButton")
	public void onAddRecipeButtonPress(ClickEvent event) {
		dco.createRecipe();		
	}
	
	@UiHandler("RezeptName")
	void onEdit(KeyUpEvent event) {
		if(RezeptName.getText() != ""){
			recipe.setSymbol(RezeptName.getText());
		}
	}
	
	@UiHandler("cookingInstr")
	void onEditCook(KeyUpEvent event) {
		if(cookingInstr.getText() != ""){
			recipe.setCookInstruction(cookingInstr.getText());
		}
	}

	@UiHandler("rezeptDetails")
	void onEditSub(KeyUpEvent event) {
		if(rezeptDetails.getText() != ""){
			recipe.setSubTitle(rezeptDetails.getText());
		}
	}

	@UiHandler("PreparationButton")
	void onPrepareClicked(ClickEvent event) {
		/*
		 // we changed something -> so it isn't saved anymore
		 saved = false;
		 
		 // what is this???
		 if(selectedRow != -1 && infoDialogIsOpen){
			 VerticalPanel verticalInfoPanel = (VerticalPanel)(addInfoPanel.getWidget(1));
			 InfoZutatDialog infoDialog = (InfoZutatDialog)(verticalInfoPanel.getWidget(1));
			 IngredientSpecification zutatSpec2 = infoDialog.getZutatSpec();
			 recipe.ingredients.set(selectedRow , zutatSpec2);
		 }
		 
		 // the selected row in the recipe is not highlighted anymore
		 if (selectedRow != -1) {
			 styleRow(selectedRow, false);
		 }
		 

		// remove window
		addInfoPanel.remove(1);
		
		// cooking instructions etc...
		menuDecoInfo.setVisible(false);
		
		InfoPreparationDialog infoPrepare = new InfoPreparationDialog(MenuTable,recipe,SuggestTable,this);
		VerticalPanel verticalDummy = new VerticalPanel();
		verticalDummy.add(infoPrepare);
		addInfoPanel.insert(verticalDummy, 1);
		infoDialogIsOpen = true;
		
		// is this necessary... it should be only on change...
		updateSuggestion();

		*/
	}
	
	@UiHandler("removeRezeptButton")
	void onRemoveClicked(ClickEvent event) {
		presenter.goTo(new RechnerRecipeViewPlace(""));
	}
	
	@UiHandler("MenuTable")
	void onClick(ClickEvent event) {
		// Select the row that was clicked (-1 to account for header row).
		Cell cell = MenuTable.getCellForEvent(event);
		if (cell != null) {
			int row = cell.getRowIndex();
			//selectRow(row);
		}
	}
	
	@UiHandler("amountPersons")
	void onKeyUp(KeyUpEvent event) {
		int keyCode = event.getNativeKeyCode();
		if ((Character.isDigit((char) keyCode)) 
				|| (keyCode == KeyCodes.KEY_BACKSPACE)
				|| (keyCode == KeyCodes.KEY_DELETE) ) {
			// TextBox.cancelKey() suppresses the current keyboard event.
			Long persons = 1l;
			recipe.setPersons(persons);
			if(!amountPersons.getText().isEmpty()){
				persons = Long.parseLong(amountPersons.getText().trim());
				if(persons > 0){
					recipe.setPersons(persons);
					//updateSuggestion();
//					updateSuggestion(EaternityRechner.SuggestTable, EaternityRechner.MenuTable);
				} else {
//					amountPersons.setText("1");
				}
			} else {
//				amountPersons.setText("1");
			}
			
			
		} else {
			amountPersons.cancelKey();
		}
	}

	
	
	/**
	 * @return true if recipe has changed since last save, false otherwise
	 */
	public boolean hasChanged() {
		return saved;
	}
	
	// ---------------------- private Methods ---------------------
/*
	private void updateIngredients() {}
	private void updateParameters() {}

		// here we add the visuals for commenting
		public void addCommentingField() {
			

			
			numberofComments = recipe.comments.size();
			
			final Anchor addCommentButton = new Anchor("Einen Kommentar hinzufügen.");
			addCommentButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
					commentTable.remove(addCommentButton);
					fillCommentBoxes(null,numberofComments);
					numberofComments = numberofComments +1;
					commentTable.setWidget(numberofComments ,1,addCommentButton);

				}

			});
			
			for (int i = 0; i < numberofComments; i++) {
				fillCommentBoxes(recipe.comments.get(i),i);
			}
			
			commentTable.setWidget(numberofComments ,1,addCommentButton);
		}
		
		public void fillCommentBoxes(RecipeComment recipeComment, int thisRow) {
			
			
			final Anchor removeRowButton = new Anchor("x");
			removeRowButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					int thisRow = getWidgetRow(removeRowButton,commentTable);
					commentTable.removeRow(thisRow);
				}
			});
			
			TextBox commentBox = new TextBox();
			if(recipeComment != null){
				commentBox.setText(recipeComment.symbol);
			}
			
			commentBox.addKeyUpHandler(new KeyUpHandler() {
				public void onKeyUp(KeyUpEvent event)  {
					updateComments();
				}
				
			});
			
		
			commentBox.setWidth("273px");
			
			if(recipeComment != null && recipeComment.amount != 0){
					setAmountBox(thisRow, recipeComment.amount);
			} else {
				final Anchor addCommentAmountButton = new Anchor("+");
				
				addCommentAmountButton.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						int thisRow = getWidgetRow(addCommentAmountButton,commentTable);
						commentTable.remove(addCommentAmountButton);
						setAmountBox(thisRow, 0);
					}

					
				});
				
				commentTable.setWidget(thisRow ,2,addCommentAmountButton);
			}

			
			
			
			commentTable.setWidget(thisRow,0,removeRowButton);
			commentTable.setWidget(thisRow,1,commentBox);
			
		}

		
		private void updateComments() {
			
			recipe.comments.clear();
			for (int i = 0; i < commentTable.getRowCount()-1; i++) {
				TextBox readBox = (TextBox) commentTable.getWidget(i, 1);
				if(readBox.getText() != ""){
					RecipeComment recipeComment = new RecipeComment(readBox.getText());
					try{
						TextBox readAmountBox = (TextBox) commentTable.getWidget(i, 2);
						recipeComment.amount = Integer.parseInt(readAmountBox.getText());
					} catch (ClassCastException error) {
						recipeComment.amount = 0;
					} catch (NumberFormatException error2) {
						
					}
					
					recipe.comments.add(recipeComment);
				}
				
				
			}
			
			saved = false;
			
			
			
		}
		
		public void setAmountBox(int thisRow, int amount) {
			TextBox commentAmountBox = new TextBox();
			commentAmountBox.setText(Integer.toString(amount));
			commentAmountBox.addKeyUpHandler(new KeyUpHandler() {
				public void onKeyUp(KeyUpEvent event)  {
					int keyCode = event.getNativeKeyCode();
					if ((Character.isDigit((char) keyCode)) 
							|| (keyCode == KeyCodes.KEY_BACKSPACE)
							|| (keyCode == KeyCodes.KEY_DELETE) ) {
						updateComments();
					}
				}


				
			});
			commentAmountBox.setWidth("20px");
			commentTable.setWidget(thisRow ,2,commentAmountBox);
		}

		
		

		


		
		private void initTable() {
			MenuTable.getColumnFormatter().setWidth(0, "10px");
			MenuTable.getColumnFormatter().setWidth(1, "40px");
			MenuTable.getColumnFormatter().setWidth(2, "170px");
			MenuTable.getColumnFormatter().setWidth(4, "80px");
			MenuTable.setCellPadding(1);
		    
		}
		
		
		// REFACOTR: should not be called from outside... listen to EventBus
		public void showRezept(final Recipe recipe) {
			
			
				if(recipe.image !=null){
					codeImage.setHTML("<img src='" + recipe.image.getServingUrl() + "=s120-c' />");
				} else {
					if (dco.getLoginInfo().isLoggedIn()) {
						codeImage.setHTML("<img src='http://placehold.it/120x120' />");
					}
				}
				
				if(recipe.getPersons() != null){
					amountPersons.setText(recipe.getPersons().toString());
				} else {
					amountPersons.setText("4");
					Long persons = Long.parseLong(amountPersons.getText());
					recipe.setPersons(persons);
				}
				
		
				displayZutatImMenu(recipe.ingredients);
			
				updateSuggestion();
		
				
				RezeptName.setText(recipe.getSymbol());
				rezeptDetails.setText(recipe.getSubTitle());
				
				
				superDisplay.setTitleHTML("Sie bearbeiten soeben: "+ recipe.getSymbol());
				
				if (presenter.getLoginInfo().isLoggedIn()) {
					// only show this if the user is logged in:
					addCommentingField();
					
				}
			
			
		}

		
		
		// REFACOTR: correct here
		 public void selectRow(int row) {
			 
			 // only show the buttom if we are in a kitchen...
			 if(presenter.getTopPanel().selectedKitchen != null){
				 PreparationButton.setVisible(true);
			 }

			 saved = false;
			 
			 if(selectedRow != -1 && infoDialogIsOpen ){

				 VerticalPanel verticalInfoPanel = (VerticalPanel)(addInfoPanel.getWidget(1));
				 InfoZutatDialog infoDialog = (InfoZutatDialog)(verticalInfoPanel.getWidget(0));
					 
				IngredientSpecification zutatSpec2 = infoDialog.getZutatSpec();

				recipe.ingredients.set(selectedRow , zutatSpec2);
			 }
			 
			IngredientSpecification zutatSpec = recipe.ingredients.get(row);

			if (zutatSpec == null) {
				return;
			}
			
			Long ParentZutatId = zutatSpec.getZutat_id();
			Ingredient zutat = presenter.getClientData().getIngredientByID(ParentZutatId);
			
			openSpecificationDialog(zutatSpec,zutat, (TextBox) MenuTable.getWidget(row, 1), MenuTable,row);

			styleRow(selectedRow, false);
			
			styleRow(row, true);

			selectedRow = row;

			if (listener != null) {
				listener.onItemSelected(zutatSpec);
			}
			
			updateSuggestion();
		}

		// REFACOTR: correct
		private void openSpecificationDialog(IngredientSpecification zutatSpec, Ingredient zutat,  TextBox amount,FlexTable MenuTable,int selectedRow) {
			// if another one was already open
			if(infoDialogIsOpen){
				addInfoPanel.remove(1);
			} else {
				infoDialogIsOpen = true;
			}

			menuDecoInfo.setVisible(false);
			InfoZutatDialog infoZutat = new InfoZutatDialog(zutatSpec,zutat,amount,MenuTable,selectedRow,recipe,SuggestTable,this);
			infoZutat.setPresenter(presenter);
			VerticalPanel verticalDialog = new VerticalPanel();
			verticalDialog.add(infoZutat);
			addInfoPanel.insert(verticalDialog, 1);

			
		}
		
		

		//TODO do the same for Search BUtton Press
		void styleRow(int row, boolean selected) {
			if (row != -1) {
				String style = selectionStyleRow.selectedRow();

				if (selected) {
					MenuTable.getRowFormatter().addStyleName(row, style);
				} else {
					MenuTable.getRowFormatter().removeStyleName(row, style);
				}
			}
		}
		


		// REFACOTR: here is the BUG located (invalid Ingredient) if we move the line....
		// duplicate code in RecipeView (showRezept)
		// create own Ingredients Widget
		void displayZutatImMenu( ArrayList<IngredientSpecification> zutaten) {
			
			if(askForLess != null){

				if(showImageHandler != null){
					showImageHandler.removeHandler();
					showImageHandler = null;
				}
				if(askForLess){
					if(detailText != null){
						overlap = Math.max(1,showImageRezept.getHeight() -  addInfoPanel.getOffsetHeight() +40);
						detailText.setHTML("<img src='pixel.png' style='float:right' width=360 height="+ Integer.toString(overlap)+" />"+recipe.getCookInstruction());
					}
				}
			}
			
		// TODO here i don't want to re-initialize everything over and over again.
		MenuTable.removeAllRows();
//		EaternityRechner.MenuTable.removeAllRows();
		
		Integer row = MenuTable.getRowCount();

		for(final IngredientSpecification zutat : zutaten){
//			
		Button removeZutat = new Button("x");

		removeZutat.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				
				// this list is kept in sync with the table...
				int removedIndex = recipe.ingredients.indexOf(zutat);
				
				// by button press both get deleted
				recipe.ingredients.remove(removedIndex);
				MenuTable.removeRow(removedIndex);
//				EaternityRechner.MenuTable.removeRow(removedIndex);
				
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
//				updateSuggestion(EaternityRechner.SuggestTable, EaternityRechner.MenuTable);
			}
		});
		
//		HasHorizontalAlignment.ALIGN_RIGHT
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

		// REFACOTR: good, listen to eventbus, rename to updateIcons
		public void changeIcons(Integer row, final IngredientSpecification zutat) {
			HTML icon = new HTML();
			Boolean itsOkay = true;
			
			
			if(zutat.getCondition() != null){
				if(zutat.getCondition().symbol.equalsIgnoreCase("frisch") && zutat.getDistance() < 500000){
					if(zutat.getStartSeason() != null && zutat.getStopSeason() != null){

						SeasonDate date = new SeasonDate(presenter.getCurrentMonth(),1);
						SeasonDate dateStart = zutat.getStartSeason();		
						SeasonDate dateStop =  zutat.getStopSeason();
						
						if( date.after(dateStart) && date.before(dateStop) ){

							icon.setHTML(icon.getHTML()+"<div class='extra-icon regloc'><img src='pixel.png' height=1 width=20 /></div>");
						} else if (!zutat.getCondition().symbol.equalsIgnoreCase("frisch") && !zutat.getProduction().symbol.equalsIgnoreCase("GH") && zutat.getDistance() < 500000) {
							icon.setHTML(icon.getHTML()+"<div class='extra-icon regloc'><img src='pixel.png' height=1 width=20 /></div>");
						} else if (zutat.getProduction().symbol.equalsIgnoreCase("GH")) {
							// nothing
						} else {
							icon.setHTML(icon.getHTML()+"<div class='extra-icon smiley3'><img src='pixel.png' height=1 width=20 /></div>");
							itsOkay = false;
						}
					}
				} 
			}
			
			if (itsOkay) {
				if(zutat.getCalculatedCO2Value()/zutat.getWeight() < .4){
					icon.setHTML("<div class='extra-icon smiley1'><img src='pixel.png' height=1 width=20 /></div>"+icon.getHTML());
					icon.setHTML(" g  <div class='extra-icon smiley2'><img src='pixel.png' height=1 width=20 /></div>"+icon.getHTML());
			
				} else	if(zutat.getCalculatedCO2Value()/zutat.getWeight() < 1.2){
					icon.setHTML(" g  <div class='extra-icon smiley2'><img src='pixel.png' height=1 width=20 /></div>"+icon.getHTML());
			
				} else {
					icon.setHTML(" g  "+icon.getHTML());
				}
			} else {
				icon.setHTML(" g  "+icon.getHTML());
			}
			

			
			
			
			if(zutat.getProduction() != null && zutat.getProduction().symbol.equalsIgnoreCase("bio")){
				icon.setHTML(icon.getHTML()+"<div class='extra-icon bio'><img src='pixel.png' height=1 width=20 /></div>");
			}
			
			icon.setHTML(icon.getHTML()+zutat.getName());
			
			MenuTable.setWidget(row, 2,  icon);
		}
		
		
		// REFACTOR: into own Suggestion class... 
		//looks like this produces the black bar indicating how much co2 there is compared to other ingredients
		void updateSuggestion() {
			
			Double MenuLabelWert = 0.0;
			Double MaxMenuWert = 0.0;

			if(recipe.ingredients.isEmpty()){
				if(addInfoPanel.getWidgetCount() ==2){
					addInfoPanel.remove(1);
				}
			}
			

			for (IngredientSpecification zutatSpec : recipe.ingredients) { 
				MenuLabelWert +=zutatSpec.getCalculatedCO2Value();
				if(zutatSpec.getCalculatedCO2Value()>MaxMenuWert){
					MaxMenuWert = zutatSpec.getCalculatedCO2Value();
				}
				
			}

			for (IngredientSpecification zutatSpec : recipe.ingredients) { 
				String formatted = NumberFormat.getFormat("##").format( zutatSpec.getCalculatedCO2Value() );
				MenuTable.setText(recipe.ingredients.indexOf(zutatSpec),4,"ca "+formatted+" g*");
				MenuTable.setHTML(recipe.ingredients.indexOf(zutatSpec), 5, "<div style='background:#A3C875;width:40px;height:1.0em;margin-right:5px;'><div style='background:#323533;height:1.0em;width:".concat(Double.toString(zutatSpec.getCalculatedCO2Value()/MaxMenuWert*40).concat("px'>.</div></div>")));
			}
			
			String formatted = NumberFormat.getFormat("##").format(MenuLabelWert);
			
			SuggestTable.setCellSpacing(2);
			SuggestTable.setText(1,0,"SUMME");
			SuggestTable.getColumnFormatter().setWidth(0, "215px");
			SuggestTable.setHTML(1,1,"ca <b>"+formatted+" g</b>* CO₂-Äq.");
			SuggestTable.getColumnFormatter().setWidth(1, "140px");
			
			if(recipe.getSymbol() == null){
		          recipe.setSymbol("Ihr Menu");
		    }
		    if(recipe.getSubTitle() == null){
		        recipe.setSubTitle("Menu Beschreibung");
		    }

			// updtTopSuggestion();
		
			
			heightOfView = this.getOffsetHeight();
		
			
		}


			   
		private void updateTable(int row,IngredientSpecification zutatSpec){
			saved = false;
			updateSuggestion();

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

		// REFACTOR: listen to EventBus
		public void updateSaison() {
			if(addInfoPanel.getWidgetCount() ==2){
				InfoZutatDialog infoZutat = (InfoZutatDialog) addInfoPanel.getWidget(1);
				 infoZutat.updateSaison(infoZutat.zutatSpec);
			 }
		}
		
		
		// here comes the Image Uploader:
		private IUploader.OnFinishUploaderHandler onFinishUploaderHandler = new IUploader.OnFinishUploaderHandler() {
		    public void onFinish(IUploader uploader) {
		      if (uploader.getStatus() == Status.SUCCESS) {
		    	 
		    	 GWT.log("Successfully uploaded image: "+  uploader.fileUrl(), null);
		        new PreloadedImage(uploader.fileUrl(), showImage);
		        
		      }
		    }
		  };

		  // Attach an image to the pictures viewer
		  OnLoadPreloadedImageHandler showImage = new OnLoadPreloadedImageHandler() {
		    public void onLoad(PreloadedImage image) {
		      image.setWidth("125px");
		      panelImages.add(image);
		    }
		  };
		  */
		  
	}






