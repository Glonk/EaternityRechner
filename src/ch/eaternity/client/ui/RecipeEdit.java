
package ch.eaternity.client.ui;

import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.PreloadedImage;
import gwtupload.client.PreloadedImage.OnLoadPreloadedImageHandler;

import java.util.Date;
import java.util.Iterator;

import ch.eaternity.client.DataController;
import ch.eaternity.client.activity.RechnerActivity;
import ch.eaternity.client.events.AlertEvent;
import ch.eaternity.client.events.AlertEventHandler;
import ch.eaternity.client.events.IngredientAddedEvent;
import ch.eaternity.client.events.IngredientAddedEventHandler;
import ch.eaternity.client.events.LoadedDataEvent;
import ch.eaternity.client.events.LoadedDataEventHandler;
import ch.eaternity.client.events.LoginChangedEvent;
import ch.eaternity.client.events.LoginChangedEventHandler;
import ch.eaternity.client.events.MonthChangedEvent;
import ch.eaternity.client.events.MonthChangedEventHandler;
import ch.eaternity.client.place.RechnerRecipeEditPlace;
import ch.eaternity.client.place.RechnerRecipeViewPlace;
import ch.eaternity.client.ui.widgets.ConfirmDialog;
import ch.eaternity.client.ui.widgets.FlexTableRowDragController;
import ch.eaternity.client.ui.widgets.FlexTableRowDropController;
import ch.eaternity.client.ui.widgets.IngredientWidget;
import ch.eaternity.client.ui.widgets.UploadPhoto;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.IngredientSpecification;
import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.RecipeComment;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Close;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.Alert;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
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
	@UiField AbsolutePanel dragArea;
	
	@UiField Close closeRecipe;
	@UiField VerticalPanel alertPanel;
	@UiField TextBox RezeptName;
	@UiField TextBox rezeptDetails;
	@UiField Label co2valueLabel;
	
	@UiField HTML codeImage;
	@UiField HorizontalPanel imageWidgetPanel;
	
	@UiField TextBox amountPersons;
	@UiField CheckBox makePublic;
	@UiField TextBox recipeDate;
	@UiField HTML recipeDateError;
	
	@UiField HorizontalPanel addInfoPanel;
	
	@UiField FlexTable SuggestTable;
	@UiField FlexTable commentTable;
	
	@UiField Anchor PreparationButton;
	@UiField CheckBox preparationFactor;
	@UiField TextArea cookingInstr;
	
	
	@UiField VerticalPanel MenuTableWrapper;
	@UiField FlexTable MenuTable;
	@UiField FlowPanel collectionPanel;
	@UiField HTML bottomIndikator;
	
	@UiField Button newRecipeButton;
	@UiField Button generatePDFButton;
	@UiField Button publishButton;
	@UiField Button duplicateButton;
	@UiField Button saveButton;
	@UiField Button deleteButton;
	
	@UiField SelectionStyleRow selectionStyleRow;
	@UiField EvenStyleRow evenStyleRow;
	
	
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
	private FlexTableRowDropController flexTableRowDropController = null;
	
	private boolean saved = false;
	private boolean infoDialogIsOpen = false;
	
	private int numberofComments = 0;
	
	private Listener listener;
	private int selectedRow = 0;
	private Recipe recipe;
	private String recipeId;
	
	
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
		
	    tableRowDragController = new FlexTableRowDragController(dragArea);
	    flexTableRowDropController = new FlexTableRowDropController(MenuTable,this);
	    tableRowDragController.registerDropController(flexTableRowDropController);

	    saveButton.setEnabled(false);
		generatePDFButton.setEnabled(false);
		publishButton.setEnabled(false);
		duplicateButton.setEnabled(false);
		saveButton.setEnabled(false);
		
		// Image
		uploadWidget = new UploadPhoto(this);
		uploadWidget.setStyleName("notInline");	
		imageWidgetPanel.insert(uploadWidget,0);
		imageWidgetPanel.setVisible(false);
	    
	    MenuTable.getColumnFormatter().setWidth(0, "300px");
	}
	
	

	private void bind() {
		// Listen to the EventBus 
		presenter.getEventBus().addHandler(IngredientAddedEvent.TYPE,
				new IngredientAddedEventHandler() {
					@Override
					public void onEvent(IngredientAddedEvent event) {
						if (recipe != null) {
							addIngredient(event.ing);
							updateIcons();
						}
					}
				});
		presenter.getEventBus().addHandler(MonthChangedEvent.TYPE,
				new MonthChangedEventHandler() {
					@Override
					public void onEvent(MonthChangedEvent event) {
						if (recipe != null)
							updateIcons();
					}
				});
		presenter.getEventBus().addHandler(LoginChangedEvent.TYPE,
				new LoginChangedEventHandler() {
					@Override
					public void onEvent(LoginChangedEvent event) {
						if (recipe != null)
							updateLoginSpecificParameters();
					}
				});
		presenter.getEventBus().addHandler(LoadedDataEvent.TYPE,
				new LoadedDataEventHandler() {
					@Override
					public void onLoadedData(LoadedDataEvent event) {
						loadRecipe(recipeId);
					}
				});
		presenter.getEventBus().addHandler(AlertEvent.TYPE,
				new AlertEventHandler() {
					@Override
					public void onEvent(final AlertEvent event) {
						if (event.destination == AlertEvent.Destination.EDIT) {
							alertPanel.insert(event.alert,0);
							Timer t = new Timer() {
								public void run() {
									event.alert.close();
								}
							};
							if (event.timeDisplayed != null)
								t.schedule(event.timeDisplayed);
						}
					}
				});
	}
	
	public void setPresenter(RechnerActivity presenter) {
		this.presenter = presenter;
		this.dco = presenter.getDCO();
		this.setHeight("1600px");
		
		bind();
	}
	
	public void updateParameters() {
		RezeptName.setText(recipe.getSymbol());
		rezeptDetails.setText(recipe.getSubTitle());
		amountPersons.setText(recipe.getPersons().toString());
		
		// Image
		if(recipe.getImage() !=null){
			codeImage.setHTML("<img src='" + recipe.getImage().getServingUrl() + "=s120-c' />");
		}
		
		//Date
		DateTimeFormat dtf = DateTimeFormat.getFormat("dd.MM.yy");
		Date date = recipe.getCookingDate();
		if(date != null)
		{
			recipeDate.setText(dtf.format(date));
		}
		
	    if(dco.getCurrentKitchen() == null){
	    	PreparationButton.setVisible(false);
	    }
		
	    // Cooking instruction
	    String cookingInstructions = recipe.getCookInstruction();
	    if(dco.getLoginInfo() != null && !dco.getLoginInfo().isLoggedIn() || dco.getLoginInfo() == null){
	    	presenter.getEventBus().fireEvent(new AlertEvent("Sie sind nicht angemeldet. Alle Änderungen am Rezept können nicht gespeichert werden.", AlertType.INFO, AlertEvent.Destination.VIEW));
    	}
		cookingInstr.setText(cookingInstructions);
		
		if (recipe.getOpen() == true)
			publishButton.setText("Veröffentlichung rückgängig");
		else
			publishButton.setText("veröffentlichen");
		
		updateLoginSpecificParameters();
		updateCo2Value();
		updateIngredients();
		
	}
	
	public void updateLoginSpecificParameters() {
		if (dco.getLoginInfo() != null && dco.getLoginInfo().isLoggedIn()) {
			codeImage.setHTML("<img src='http://placehold.it/120x120' />");
			initializeCommentingField();
			
			imageWidgetPanel.setVisible(true);
			
		    saveButton.setEnabled(true);
			generatePDFButton.setEnabled(true);
			publishButton.setEnabled(true);
			duplicateButton.setEnabled(true);
			saveButton.setEnabled(true);
		}
	}
	
	private void updateCo2Value() {
		double co2value = 0l;
		for (IngredientSpecification ingSpec : recipe.getIngredients()) {
			co2value = co2value + ingSpec.getCalculatedCO2Value();
		}
		co2valueLabel.setText("" + ((int)co2value));
	}

	// ---------------------- UI Handlers ----------------------
	
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
			Long persons = 1l;
			recipe.setPersons(persons);
			if(!amountPersons.getText().isEmpty()){
				persons = Long.parseLong(amountPersons.getText().trim());
				if(persons > 0){
					recipe.setPersons(persons);
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

	
	@UiHandler("saveButton")
	public void onSaveClicked(ClickEvent event) {
		dco.saveRecipe(recipe);
		saved = true;
		//TODO Statusmeldung
	}
	
	@UiHandler("closeRecipe")
	void onCloseClicked(ClickEvent event) {
		presenter.goTo(new RechnerRecipeViewPlace(dco.getRecipeScope().toString()));
	}
	
	@UiHandler("generatePDFButton")
	public void onGeneratePDFButtonClicked(ClickEvent event) {
		presenter.getEventBus().fireEvent(new AlertEvent("This is a veeeery Long message which should be displayd in RecipeEdit and not in RecipeView", AlertType.ERROR, AlertEvent.Destination.EDIT));
		
	}
	
	@UiHandler("deleteButton") 
	public void onDeleteClicked(ClickEvent event) {
		saved = true;
		dco.deleteRecipe(recipe);
		presenter.goTo(new RechnerRecipeViewPlace(dco.getRecipeScope().toString()));
	}
	
	@UiHandler("publishButton")
	public void onPublishClicked(ClickEvent event) {
		dco.approveRecipe(recipe, true);
		//TODO Statusmeldung
	}

	@UiHandler("newRecipeButton")
	public void onAddRecipeButtonPress(ClickEvent event) {
		presenter.goTo(new RechnerRecipeEditPlace("new"));
	}

	@UiHandler("recipeDate")
	void onBlur(BlurEvent event)  {
		Date date = getDate();
		if (date != null)
			dco.getEditRecipe().setCookingDate(date);
		 	saved = false;
	}
	
	private Date getDate() {
		String text = recipeDate.getText();
		Date date = null;
		try { 
			if ("".equals(text)) {}
			else {
				DateTimeFormat fmt = DateTimeFormat.getFormat("dd.MM.yy");
				date = fmt.parseStrict(text);	
				recipeDateError.setHTML("");
				}
		}
		catch (IllegalArgumentException IAE) {
			if(!"TT/MM/JJ".equals(text))
				recipeDateError.setHTML("'" + text + "' is not a propper formated Date.");
			else
				recipeDateError.setHTML("");
			//recipeDate.setText("");
			//recipeDate.setCursorPos(0);
		}
		return date;
	}
	
	/**
	 * @return true if recipe has changed since last save, false otherwise
	 */
	public boolean hasChanged() {
		return saved;
	}

	public void setRecipeId(String id) {
		this.recipeId = id;
		
		// if data is already loaded load recipe, otherwise wait for the LoadedDataEvent
		if (dco.dataLoaded())
			loadRecipe(id);
	}

	public void loadRecipe(String id) {
		this.recipe = dco.setEditRecipe(id);
		updateParameters();
	}
	
	public void removeIngredient(IngredientWidget ingWidget) {
		recipe.removeIngredient(ingWidget.getIngredient());
		MenuTable.remove(ingWidget);
		
		// does this work to prevent the error? which error?
		// if ingredientsDialog is open, yet item gets removed... remove also IngredientsDialog
		/*
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
		}*/
		
		// set the colors in the right order...
		String style = evenStyleRow.evenRow();
		for(Integer rowIndex = 0; rowIndex<MenuTable.getRowCount(); rowIndex++){
			MenuTable.getRowFormatter().removeStyleName(rowIndex, style);
			if ((rowIndex % 2) == 1) {
				MenuTable.getRowFormatter().addStyleName(rowIndex, style);
			} 
		}
		updateCo2Value();
	}
	
	
	public void updateIngredients() {
		MenuTable.clear();
		for (IngredientSpecification ingSpec : recipe.getIngredients()) {
			addIngredient(ingSpec);
		}
		updateCo2Value();
	}
	
	public void addIngredient(IngredientSpecification ingSpec) {
		int row = MenuTable.getRowCount();
		IngredientWidget ingWidget = new IngredientWidget(ingSpec,this, dco.getCurrentMonth());
		MenuTable.setWidget(row, 0, ingWidget);
		
		// drag Handler
		tableRowDragController.makeDraggable(ingWidget,ingWidget.getDragHandle());
		
		//Alternate Coloring
		if ((row % 2) == 1) {
			String style = evenStyleRow.evenRow();
			MenuTable.getRowFormatter().addStyleName(row, style);
		}
		updateCo2Value();
	}

	
	// ---------------------- private Methods ---------------------

	private void updateIcons() {
		Iterator<Widget> it = MenuTable.iterator();
		while (it.hasNext()) {
			((IngredientWidget)it.next()).updateIcons();
		}
	}

		private void initializeCommentingField() {
			if (recipe.getComments() != null) {
				numberofComments = recipe.getComments().size();
						
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
					fillCommentBoxes(recipe.getComments().get(i),i);
				}
				
				commentTable.setWidget(numberofComments ,1,addCommentButton);
			}
		}
		
		private void fillCommentBoxes(RecipeComment recipeComment, int thisRow) {
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
			recipe.getComments().clear();
			for (int i = 0; i < commentTable.getRowCount()-1; i++) {
				TextBox readBox = (TextBox) commentTable.getWidget(i, 1);
				if(readBox.getText() != ""){
					RecipeComment recipeComment = new RecipeComment(readBox.getText());
					try{
						TextBox readAmountBox = (TextBox) commentTable.getWidget(i, 2);
						recipeComment.amount = Integer.parseInt(readAmountBox.getText());
					} catch (ClassCastException error) {
						recipeComment.amount = 0;
					} catch (NumberFormatException error2) {}
					
					recipe.getComments().add(recipeComment);
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

		
	
	public void selectRow(int row) {

		saved = false;

		if (selectedRow != -1 && infoDialogIsOpen) {

			VerticalPanel verticalInfoPanel = (VerticalPanel) (addInfoPanel.getWidget(1));
			//InfoZutatDialog infoDialog = (InfoZutatDialog) (verticalInfoPanel.getWidget(0));

			//IngredientSpecification zutatSpec2 = infoDialog.getZutatSpec();

			//recipe.getIngredients().set(selectedRow, zutatSpec2);
		}

		IngredientSpecification zutatSpec = recipe.getIngredients().get(row);

		if (zutatSpec == null) {
			return;
		}

		Ingredient zutat = dco.getIngredientByID(zutatSpec.getZutat_id());

		//openSpecificationDialog(zutatSpec, zutat, (TextBox) MenuTable.getWidget(row, 1), MenuTable, row);

		styleRow(selectedRow, false);
		styleRow(row, true);
		selectedRow = row;

		if (listener != null) {
			listener.onItemSelected(zutatSpec);
		}
	}

/*
	private void openSpecificationDialog(IngredientSpecification zutatSpec, Ingredient zutat, TextBox amount, FlexTable MenuTable, int selectedRow) {
		// if another one was already open
		if (infoDialogIsOpen) {
			addInfoPanel.remove(1);
		} else {
			infoDialogIsOpen = true;
		}

		InfoZutatDialog infoZutat = new InfoZutatDialog(zutatSpec, zutat, amount, MenuTable, selectedRow, recipe, SuggestTable, this);
		infoZutat.setPresenter(presenter);
		addInfoPanel.insert(infoZutat, 1);
	}
		*/
		

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
/*
	// REFACTOR: listen to EventBus
	public void updateIngSpecWidgetSaison() {
		if (addInfoPanel.getWidgetCount() == 2) {
			InfoZutatDialog infoZutat = (InfoZutatDialog) addInfoPanel.getWidget(1);
			infoZutat.updateSaison();
		}
	}
	*/
	
	public void setImageUrl(String url) {
		codeImage.setHTML("<img src='" + url + "=s120-c' />");
	}

	// here comes the Image Uploader:
	private IUploader.OnFinishUploaderHandler onFinishUploaderHandler = new IUploader.OnFinishUploaderHandler() {
		public void onFinish(IUploader uploader) {
			if (uploader.getStatus() == Status.SUCCESS) {

				GWT.log("Successfully uploaded image: " + uploader.fileUrl(), null);
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

	public Recipe getRecipe() {
		return this.recipe;
	}


	public void closeRecipeEdit() {
		if (!saved) {
			String saveText = recipe.getSymbol() + " ist noch nicht gespeichert!";
			final ConfirmDialog dlg = new ConfirmDialog(saveText);
			dlg.statusLabel.setText("Speichern?");

			dlg.yesButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					dco.saveRecipe(recipe);
					dlg.hide();
				}
			});
			dlg.noButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					dlg.hide();
				}
			});

			dlg.show();
			dlg.center();
		}
		
		dco.clearEditRecipe();
		
	}




	

}






