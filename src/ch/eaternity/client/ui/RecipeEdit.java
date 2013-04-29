
package ch.eaternity.client.ui;

import java.util.Date;
import java.util.List;

import org.eaticious.common.QuantityImpl;

import ch.eaternity.client.DataController;
import ch.eaternity.client.activity.RechnerActivity;
import ch.eaternity.client.events.AlertEvent;
import ch.eaternity.client.events.AlertEventHandler;
import ch.eaternity.client.events.IngredientAddedEvent;
import ch.eaternity.client.events.IngredientAddedEventHandler;
import ch.eaternity.client.events.LoginChangedEvent;
import ch.eaternity.client.events.LoginChangedEventHandler;
import ch.eaternity.client.events.RecipeLoadedEvent;
import ch.eaternity.client.events.RecipeLoadedEventHandler;
import ch.eaternity.client.place.RechnerRecipeEditPlace;
import ch.eaternity.client.place.RechnerRecipeViewPlace;
import ch.eaternity.client.resources.Resources;
import ch.eaternity.client.ui.cells.ImageActionCell;
import ch.eaternity.client.ui.widgets.IngredientSpecificationWidget;
import ch.eaternity.client.ui.widgets.UploadPhotoWidget;
import ch.eaternity.shared.FoodProductInfo;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.SavingPotential;
import ch.eaternity.shared.Season;
import ch.eaternity.shared.SeasonDate;
import ch.eaternity.shared.Util;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Close;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.TextInputCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.CellPreviewEvent.Handler;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * 
 * @author aurelianjaggi
 *
 */
public class RecipeEdit extends Composite {
	interface Binder extends UiBinder<Widget, RecipeEdit> { }
	private static Binder uiBinder = GWT.create(Binder.class);
	
	/**
	 * The key provider that allows us to identify Contacts even if a field
	 * changes. We identify contacts by their unique ID.
	 */
	private final ProvidesKey<Ingredient> KEY_PROVIDER = new ProvidesKey<Ingredient>() {
		@Override
		public Object getKey(Ingredient ingredient) {
			return (ingredient == null) ? null : ingredient.getId();
		}
	};
	
	public interface CellTableResource extends CellTable.Resources
	{
	   public interface CellTableStyle extends CellTable.Style {};

	   @Source({"ingredientCellTable.css"})
	   CellTableStyle cellTableStyle();
	};
	
	CellTableResource cellTableResource = GWT.create(CellTableResource.class);
	
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
	
	
	@UiField(provided = true)
	CellTable<Ingredient> ingredientCellTable = new CellTable<Ingredient>(100, cellTableResource);
	
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
	
	private UploadPhotoWidget uploadWidget;
	private ListDataProvider<Ingredient> ingredientDataProvider = new ListDataProvider<Ingredient>();
	private final SingleSelectionModel<Ingredient> selectionModel = new SingleSelectionModel<Ingredient>();
	
	private boolean saved = false;
	private int numberofComments = 0;
	private Recipe recipe;
	private List<Ingredient> ingredients;
	
	
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
		
	    saveButton.setEnabled(false);
	    generatePDFButton.setVisible(false);
		generatePDFButton.setEnabled(false);
		publishButton.setEnabled(false);
		duplicateButton.setEnabled(false);
		saveButton.setEnabled(false);
		
		imageUploadWidgetPanel.setVisible(false);
		
		deleteImage.setUrl("/images/delete.png");
	
		initIngredientTable();
	}
	
	public void setPresenter(RechnerActivity presenter) {
		this.presenter = presenter;
		this.dco = presenter.getDCO();
		this.setHeight("1500px");
		
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
							ingredientDataProvider.refresh();
							updateCo2Value();
							changeSaveStatus(false);
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
						ingredients = recipe.getIngredients();
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
	
	/**
	 * @return true if recipe has changed since last save, false otherwise
	 */
	public boolean isSaved() {
		return saved;
	}
	

	
	public void updateIngredientValue(Ingredient ingSpec) {
		ingredientDataProvider.refresh();
		updateCo2Value();
		changeSaveStatus(false);
	}
	
	
	public void updateIngredients() {
		ingredientDataProvider.setList(recipe.getIngredients());
		updateCo2Value();
	}

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
			recipe.setCookingDate(date);
			ingredientCellTable.redraw();
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

	
	// ---------------------- private Methods ---------------------


	private void changeSaveStatus(boolean saved) {
		this.saved = saved;
		saveButton.setEnabled(!saved);
	}
	
	private void removeIngredient(Ingredient ingredient) {
		for (int i = 0; i < ingredients.size(); i++) {
			Ingredient ingredientIt = ingredients.get(i);
			if (ingredientIt == ingredient)
				ingredients.remove(i);
		}

		ingredientDataProvider.setList(ingredients);
		
		ingSpecWidget.setVisible(false);
		
		updateCo2Value();
		changeSaveStatus(false);
	}

	
	
	
	private void initIngredientTable() {
		ingredientCellTable.setWidth("400px", true);
		ingredientCellTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);

		// Add a selection model to handle user selection.
		ingredientCellTable.setSelectionModel(selectionModel);
	    
	    selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
	    	public void onSelectionChange(SelectionChangeEvent event) {
	    		Ingredient selected = selectionModel.getSelectedObject();
	    		if (selected != null) {
					if (!ingSpecWidget.isPresenterSetted()){
						ingSpecWidget.setPresenter(presenter, selected, recipe.getVerifiedLocation());
					}
					else{
						ingSpecWidget.setIngredient(selected, recipe.getVerifiedLocation());
					}
	    		}
	    	}
	    });
	    
	    
	    // ----------- Weight -----------
		Column<Ingredient, String> weightInputColumn = new Column<Ingredient, String>(new TextInputCell()) {
			@Override
			public String getValue(Ingredient ingredient) {
				return Integer.toString(ingredient.getWeight().getAmount().intValue());
			}
			@Override 
	        public String getCellStyleNames(Context context, Ingredient ingredient)
	        {
	            return "weightTextInputCell";
	        }
		};

		// ----------- Name -----------
		Column<Ingredient, String> nameColumn = new Column<Ingredient, String>(new TextCell()) {
			@Override
			public String getValue(Ingredient ingredient) {
				return " g     " + ingredient.getFoodProduct().getName();
			}
		};
		
		// ----------- Organic? -----------
		Column<Ingredient, ImageResource> bioColumn = new Column<Ingredient, ImageResource>(new ImageResourceCell()) {
			@Override
			public ImageResource getValue(Ingredient ingredient) {
				if(ingredient.getProduction() != null && ingredient.getProduction().getSymbol().equalsIgnoreCase("bio")){
					return Resources.INSTANCE.bio();
				}
				return null;
			}
		};
		
		// ----------- Seasonal? -----------
		Column<Ingredient, ImageResource> seasonColumn = new Column<Ingredient, ImageResource>(new ImageResourceCell()) {
			@Override
			public ImageResource getValue(Ingredient ingredient) {

				Season season = ingredient.getFoodProduct().getSeason();
				if(season != null){

					SeasonDate date = new SeasonDate(recipe.getCookingDate());
					
					if( !ingredient.getProduction().getSymbol().equalsIgnoreCase("GH") && 
						ingredient.getCondition().getSymbol().equalsIgnoreCase("frisch") && 
						date.after(season.getBeginning()) && date.before(season.getEnd()) )
						return Resources.INSTANCE.season();
					else return null;
				}
				else return null;
			}
		};
		
		// ----------- Regional? -----------
		Column<Ingredient, ImageResource> regionalColumn = new Column<Ingredient, ImageResource>(new ImageResourceCell()) {
			@Override
			public ImageResource getValue(Ingredient ingredient) {
				if ( ingredient.getRoute().getDistanceKM().getAmount() < 100)
					return Resources.INSTANCE.region();
				else
					return null;
			}
		};
		
		// ----------- Rating -----------
		Column<Ingredient, ImageResource> ratingColumn = new Column<Ingredient, ImageResource>(new ImageResourceCell()) {
			@Override
			public ImageResource getValue(Ingredient ingredient) {
				if(ingredient.getFoodProduct().getCo2eValue() < .4)
					return Resources.INSTANCE.ingredientRatingBar1();
				else if(ingredient.getFoodProduct().getCo2eValue() < 1.2)
					return Resources.INSTANCE.ingredientRatingBar2();
				else 
					return Resources.INSTANCE.ingredientRatingBar3();
			}
		};
		
		// ----------- CO2 Value -----------
		Column<Ingredient, String> co2Column = new Column<Ingredient, String>(new TextCell()) {
			@Override
			public String getValue(Ingredient ingredient) {
				return ((int)ingredient.getCalculatedCO2Value()) +"g";
			}
			@Override 
	        public String getCellStyleNames(Context context, Ingredient ingredient)
	        {
	            return "co2TextCell";
	        }
		};
		
		
		// ----------- Delete Ingredient -----------
		ImageActionCell.Delegate<Ingredient> actionDelegate = new ImageActionCell.Delegate<Ingredient>() {
			@Override
			public void execute(Ingredient ingredient) {
				removeIngredient(ingredient);
			}
		};
		
		Column<Ingredient, Ingredient> removeColumn = new Column<Ingredient, Ingredient>(new ImageActionCell<Ingredient>(Resources.INSTANCE.deleteSmall(), actionDelegate)) {
			@Override
			public Ingredient getValue(Ingredient ingredient) {
				return ingredient;
			}
		};


		
		ingredientCellTable.setColumnWidth(weightInputColumn, 55.0, Unit.PX);
		ingredientCellTable.setColumnWidth(nameColumn, 100.0, Unit.PCT);
		ingredientCellTable.setColumnWidth(bioColumn, 25.0, Unit.PX);
		ingredientCellTable.setColumnWidth(seasonColumn, 25.0, Unit.PX);
		ingredientCellTable.setColumnWidth(regionalColumn, 25.0, Unit.PX);
		ingredientCellTable.setColumnWidth(ratingColumn, 25.0, Unit.PX);
		ingredientCellTable.setColumnWidth(co2Column, 50.0, Unit.PX);
		ingredientCellTable.setColumnWidth(removeColumn, 25.0, Unit.PX);

		ingredientCellTable.addColumn(weightInputColumn, "Menge");
		ingredientCellTable.addColumn(nameColumn, "Zutat");
		ingredientCellTable.addColumn(bioColumn);
		ingredientCellTable.addColumn(seasonColumn);
		ingredientCellTable.addColumn(regionalColumn);
		ingredientCellTable.addColumn(ratingColumn);
		ingredientCellTable.addColumn(co2Column, "CO2-Äq.");
		ingredientCellTable.addColumn(removeColumn);

		ingredientDataProvider.addDataDisplay(ingredientCellTable);

		// Add a field updater to be notified when the user enters a new name.
		weightInputColumn.setFieldUpdater(new FieldUpdater<Ingredient, String>() {
			@Override
			public void update(int index, Ingredient ingredient, String value) {
				String errorStyle = textErrorStyle.redTextError();
				Double grams = 0.0;
				boolean success = false;
				
				try { 
					if ("".equals(value)) {
						//amountPersons.removeStyleName(errorStyle);
					}
					else {
						grams = Double.parseDouble(value.trim());
						if (grams > 0) {
							success = true;
							//amountPersons.removeStyleName(errorStyle);
						}
					}
				}
				catch (IllegalArgumentException IAE) {}
				
				if (success) {
					ingredient.setWeight(new QuantityImpl(grams, org.eaticious.common.Unit.GRAM));
					updateCo2Value();
					changeSaveStatus(false);
				}
				else {
					//amountPersons.addStyleName(errorStyle);
				}
			}
		});
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






