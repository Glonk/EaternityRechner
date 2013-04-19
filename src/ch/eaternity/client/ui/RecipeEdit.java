
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
import ch.eaternity.client.events.RecipeLoadedEvent;
import ch.eaternity.client.events.RecipeLoadedEventHandler;
import ch.eaternity.client.place.RechnerRecipeEditPlace;
import ch.eaternity.client.place.RechnerRecipeViewPlace;
import ch.eaternity.client.ui.widgets.ConfirmDialog;
import ch.eaternity.client.ui.widgets.FlexTableRowDragController;
import ch.eaternity.client.ui.widgets.FlexTableRowDropController;
import ch.eaternity.client.ui.widgets.IngredientSpecificationWidget;
import ch.eaternity.client.ui.widgets.IngredientWidget;
import ch.eaternity.client.ui.widgets.UploadPhotoWidget;
import ch.eaternity.shared.FoodProduct;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.SavingPotential;
import ch.eaternity.shared.Util;

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
import com.google.gwt.user.client.ui.SimplePanel;
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
	@UiField Image co2Image;
	
	@UiField Image recipeImage;
	@UiField Image deleteImage;
	@UiField SimplePanel imageUploadWidgetPanel;
	
	@UiField TextBox amountPersons;
	@UiField TextBox recipeDate;
	@UiField HTML recipeDateError;
	
	@UiField FlexTable commentTable;
	
	//@UiField CheckBox preparationFactor;
	@UiField TextArea cookingInstr;
	
	@UiField FlexTable ingredientList;
	@UiField IngredientSpecificationWidget ingSpecWidget;
	//@UiField FlowPanel collectionPanel;
	@UiField Label sumCO2Label;
	
	@UiField Button newRecipeButton;
	@UiField Button generatePDFButton;
	@UiField Button publishButton;
	@UiField Button duplicateButton;
	@UiField Button saveButton;
	@UiField Button deleteButton;
	
	@UiField static SelectionStyleRow selectionStyleRow;
	@UiField static EvenStyleRow evenStyleRow;
	@UiField static TextErrorStyle textErrorStyle;
	
	
	// ---------------------- Class Variables ----------------------
	
	private RechnerActivity presenter;
	private DataController dco;
	
	private FlowPanel panelImages = new FlowPanel();
	private UploadPhotoWidget uploadWidget;

	private FlexTableRowDragController tableRowDragController = null;
	private FlexTableRowDropController flexTableRowDropController = null;
	
	private boolean saved = false;
	
	private int numberofComments = 0;
	
	private int selectedRow = 0;
	private Recipe recipe;
	
	
	public interface Listener {
		void onItemSelected(Ingredient item);
	}
	
	interface SelectionStyleRow extends CssResource {
		String selectedRow();
	}
	interface EvenStyleRow extends CssResource {
		String evenRow();
	}
	interface TextErrorStyle extends CssResource {
		String redTextError();
	}
	
	
	// ---------------------- public Methods -----------------------
	
	public RecipeEdit() {
		initWidget(uiBinder.createAndBindUi(this));
		this.setVisible(false);
		
	    tableRowDragController = new FlexTableRowDragController(dragArea);
	    flexTableRowDropController = new FlexTableRowDropController(ingredientList,this);
	    tableRowDragController.registerDropController(flexTableRowDropController);

	    saveButton.setEnabled(false);
	    generatePDFButton.setVisible(false);
		generatePDFButton.setEnabled(false);
		publishButton.setEnabled(false);
		duplicateButton.setEnabled(false);
		saveButton.setEnabled(false);
		
		imageUploadWidgetPanel.setVisible(false);
		
		deleteImage.setUrl("/images/delete.png");
	    
	    ingredientList.getColumnFormatter().setWidth(0, "300px");
	}
	
	public void setPresenter(RechnerActivity presenter) {
		this.presenter = presenter;
		this.dco = presenter.getDCO();
		this.setHeight("1600px");
		
		// Image
		uploadWidget = new UploadPhotoWidget(this, presenter);
		uploadWidget.setStyleName("notInline");	
		imageUploadWidgetPanel.setWidget(uploadWidget);
		
		bind();
	}

	private void bind() {
		// Listen to the EventBus 
		presenter.getEventBus().addHandler(IngredientAddedEvent.TYPE,
				new IngredientAddedEventHandler() {
					@Override
					public void onEvent(IngredientAddedEvent event) {
						if (recipe != null) {
							addIngredient(event.ing);
							changeSaveStatus(false);
							updateIcons();
						}
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
		presenter.getEventBus().addHandler(RecipeLoadedEvent.TYPE,
				new RecipeLoadedEventHandler() {
					@Override
					public void onEvent(RecipeLoadedEvent event) {
						recipe = event.recipe;
						ingSpecWidget.setVisible(false);
						setVisible(true);
						updateParameters();
						changeSaveStatus(true);
					}
				});
		presenter.getEventBus().addHandler(AlertEvent.TYPE,
				new AlertEventHandler() {
					@Override
					public void onEvent(final AlertEvent event) {
						if (event.destination == AlertEvent.Destination.EDIT || event.destination == AlertEvent.Destination.BOTH) {
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
	

	
	public void updateParameters() {
		changeSaveStatus(false);
		RezeptName.setText(recipe.getTitle());
		rezeptDetails.setText(recipe.getSubTitle());
		amountPersons.setText(recipe.getPersons().toString());
		
		// Image
		if(recipe.getImage() !=null){
			setImageUrl(recipe.getImage().getUrl(), false);
		}
		
		//Date
		DateTimeFormat dtf = DateTimeFormat.getFormat("dd.MM.yy");
		Date date = recipe.getCookingDate();
		if(date != null)
		{
			recipeDate.setText(dtf.format(date));
		}

		
	    // Cooking instruction
	    String cookingInstructions = recipe.getCookInstruction();
	    if(dco.getUserInfo() != null && !dco.getUserInfo().isLoggedIn() || dco.getUserInfo() == null){
	    	//TODO enabled that, place somewhere else? gives problem because loginInfo is not yet available...
	    	// now not of concerns because all users need to login 
	    	//presenter.getEventBus().fireEvent(new AlertEvent("Sie sind nicht angemeldet. Alle Änderungen am Rezept können nicht gespeichert werden.", AlertType.INFO, AlertEvent.Destination.EDIT));
    	}
		cookingInstr.setText(cookingInstructions);
		
		if (recipe.isPublished() == true)
			publishButton.setText("Veröffentlichung rückgängig");
		else
			publishButton.setText("veröffentlichen");
		
		updateLoginSpecificParameters();
		updateCo2Value();
		updateIngredients();
		
	}
	
	public void updateLoginSpecificParameters() {
		if (dco.getUserInfo() != null && dco.getUserInfo().isLoggedIn()) {
			setImageUrl("http://placehold.it/120x120", true);
			initializeCommentingField();
			
			imageUploadWidgetPanel.setVisible(true);
			
			generatePDFButton.setEnabled(true);
			publishButton.setEnabled(true);
			duplicateButton.setEnabled(true);
			saveButton.setEnabled(true);
		}
	}
	
	public void updateCo2Value() {
		co2valueLabel.setText("" + (recipe.getCO2ValuePerServing().intValue()));
		co2Image.setUrl(Util.getRecipeRatingBarUrl(recipe.getCO2ValuePerServing()));
		sumCO2Label.setText("" + (recipe.getCO2Value().intValue()) + "g");
	}

	// ---------------------- UI Handlers ----------------------
	
	@UiHandler("RezeptName")
	public void onEdit(KeyUpEvent event) {
		if(RezeptName.getText() != ""){
			recipe.setTitle(RezeptName.getText());
			changeSaveStatus(false);
		}
	}
	
	@UiHandler("rezeptDetails")
	public void onEditSub(KeyUpEvent event) {
		if(rezeptDetails.getText() != ""){
			recipe.setSubTitle(rezeptDetails.getText());
			changeSaveStatus(false);
		}
	}
	
	@UiHandler("cookingInstr")
	public void onEditCook(KeyUpEvent event) {
		if(cookingInstr.getText() != ""){
			recipe.setCookInstruction(cookingInstr.getText());
			changeSaveStatus(false);
		}
	}


	@UiHandler("deleteImage")
	public void onDeleteClick(ClickEvent event) {
		recipe.setImage(null);
		setImageUrl("http://placehold.it/120x120", true);
		imageUploadWidgetPanel.setVisible(true);
	}
	
	
	@UiHandler("ingredientList")
	public void onClick(ClickEvent event) {
		// Select the row that was clicked (-1 to account for header row).
		Cell cell = ingredientList.getCellForEvent(event);
		if (cell != null) {
			int row = cell.getRowIndex();
			IngredientWidget ingWidget = (IngredientWidget)ingredientList.getWidget(row,0);
			
			if (!ingSpecWidget.isPresenterSetted()){
				ingSpecWidget.setPresenter(presenter, ingWidget.getIngredient(), recipe.getVerifiedLocation());
			}
			else{
				ingSpecWidget.setIngredient(ingWidget.getIngredient(), recipe.getVerifiedLocation());
			}
			ingSpecWidget.setVisible(true);

			styleRow(selectedRow, false);
			styleRow(row, true);
			selectedRow = row;
		}
	}
	
	
	@UiHandler("amountPersons")
	public void onKeyUp(KeyUpEvent event) {
		String errorStyle = textErrorStyle.redTextError();
		String text = amountPersons.getText();
		Long persons = 4L;
		boolean success = false;
		
		try { 
			if ("".equals(text)) {
				amountPersons.removeStyleName(errorStyle);
			}
			else {
				persons = Long.parseLong(amountPersons.getText().trim());
				if (persons > 0) {
					success = true;
					amountPersons.removeStyleName(errorStyle);
				}
			}
		}
		catch (IllegalArgumentException IAE) {}
		
		if (success) {
			recipe.setPersons(persons);
			updateCo2Value();
			changeSaveStatus(false);
		}
		else {
			amountPersons.addStyleName(errorStyle);
		}
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
	
	@UiHandler("recipeDate")
	public void onBlur(BlurEvent event)  {
		Date date = getDate();
		if (date != null) {
			dco.getEditRecipe().setCookingDate(date);
			changeSaveStatus(false);
		}
	}

	
	@UiHandler("saveButton")
	public void onSaveClicked(ClickEvent event) {
		dco.saveRecipe(recipe);
		changeSaveStatus(true);
	}
	
	@UiHandler("closeRecipe")
	public void onCloseClicked(ClickEvent event) {
		presenter.goTo(new RechnerRecipeViewPlace(dco.getRecipeScope().toString()));
	}
	
	@UiHandler("generatePDFButton")
	public void onGeneratePDFButtonClicked(ClickEvent event) {
		presenter.getEventBus().fireEvent(new AlertEvent("This is a veeeery Long message which should be displayd in RecipeEdit and not in RecipeView", AlertType.ERROR, AlertEvent.Destination.EDIT));
		
	}
	
	@UiHandler("deleteButton") 
	public void onDeleteClicked(ClickEvent event) {
		saved = true;
		if (dco.getUserInfo().isLoggedIn())
			dco.deleteRecipe(recipe.getId());
		presenter.goTo(new RechnerRecipeViewPlace(dco.getRecipeScope().toString()));
	}
	
	@UiHandler("publishButton")
	public void onPublishClicked(ClickEvent event) {
		recipe.setPublished(!recipe.isPublished());
		updateParameters();
		//dco.approveRecipe(recipe, true);
	}

	@UiHandler("newRecipeButton")
	public void onAddRecipeButtonPress(ClickEvent event) {
		presenter.goTo(new RechnerRecipeEditPlace("new"));
	}


	
	
	/**
	 * @return true if recipe has changed since last save, false otherwise
	 */
	public boolean isSaved() {
		return saved;
	}
	
	private void changeSaveStatus(boolean saved) {
		this.saved = saved;
		saveButton.setEnabled(!saved);
	}
	
	public void removeIngredient(IngredientWidget ingWidget) {
		recipe.removeIngredient(ingWidget.getIngredient());
		ingredientList.remove(ingWidget);
		ingSpecWidget.setVisible(false);
		
		// set the colors in the right order...
		String style = evenStyleRow.evenRow();
		for(Integer rowIndex = 0; rowIndex<ingredientList.getRowCount(); rowIndex++){
			ingredientList.getRowFormatter().removeStyleName(rowIndex, style);
			if ((rowIndex % 2) == 1) {
				ingredientList.getRowFormatter().addStyleName(rowIndex, style);
			} 
		}
		updateCo2Value();
		changeSaveStatus(false);
	}
	
	public void updateIngredientValue(Ingredient ingSpec) {
		((IngredientWidget)ingredientList.getWidget(selectedRow,0)).updateCO2Value();
		updateCo2Value();
		changeSaveStatus(false);
	}
	
	
	public void updateIngredients() {
		ingredientList.clear();
		for (Ingredient ingSpec : recipe.getIngredients()) {
			addIngredient(ingSpec);
		}
		updateCo2Value();
	}
	
	public void addIngredient(Ingredient ingSpec) {
		int row = ingredientList.getRowCount();
		IngredientWidget ingWidget = new IngredientWidget(dco, ingSpec,this, dco.getCurrentMonth());
		ingredientList.setWidget(row, 0, ingWidget);
		
		// drag Handler
		tableRowDragController.makeDraggable(ingWidget,ingWidget.getDragHandle());
		
		//Alternate Coloring
		if ((row % 2) == 1) {
			String style = evenStyleRow.evenRow();
			ingredientList.getRowFormatter().addStyleName(row, style);
		}
		updateCo2Value();
	}

	
	// ---------------------- private Methods ---------------------

	private void updateIcons() {
		Iterator<Widget> it = ingredientList.iterator();
		while (it.hasNext()) {
			((IngredientWidget)it.next()).updateIcons();
		}
	}

		private void initializeCommentingField() {
			if (recipe.getSavingPotentials() != null) {
				numberofComments = recipe.getSavingPotentials().size();
						
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
					fillCommentBoxes(recipe.getSavingPotentials().get(i),i);
				}
				
				commentTable.setWidget(numberofComments ,1,addCommentButton);
			}
		}
		
		private void fillCommentBoxes(SavingPotential recipeComment, int thisRow) {
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
			recipe.getSavingPotentials().clear();
			for (int i = 0; i < commentTable.getRowCount()-1; i++) {
				TextBox readBox = (TextBox) commentTable.getWidget(i, 1);
				if(readBox.getText() != ""){
					SavingPotential recipeComment = new SavingPotential(readBox.getText());
					try{
						TextBox readAmountBox = (TextBox) commentTable.getWidget(i, 2);
						recipeComment.amount = Integer.parseInt(readAmountBox.getText());
					} catch (ClassCastException error) {
						recipeComment.amount = 0;
					} catch (NumberFormatException error2) {}
					
					recipe.getSavingPotentials().add(recipeComment);
				}	
			}
			changeSaveStatus(false);
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

		

	void styleRow(int row, boolean selected) {
		if (row != -1) {
			String style = selectionStyleRow.selectedRow();

			if (selected) {
				ingredientList.getRowFormatter().addStyleName(row, style);
			} else {
				ingredientList.getRowFormatter().removeStyleName(row, style);
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
	
	public void setImageUrl(String url, boolean imageWidgetVisible) {
		//recipeImage.setUrl(url);
		uploadWidget.setVisible(imageWidgetVisible);
		recipeImage.setUrlAndVisibleRect(url,0,0,670,230);
	}

	public Recipe getRecipe() {
		return this.recipe;
	}


	public void closeRecipeEdit() {
		
		if (!saved && dco.getUserInfo() != null && dco.getUserInfo().isLoggedIn() && recipe != null) {
			dco.saveRecipe(recipe);
			saved = true;
		}
		
		dco.clearEditRecipe();
		
	}




	

}






