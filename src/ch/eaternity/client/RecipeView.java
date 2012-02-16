package ch.eaternity.client;

import gwtupload.client.IUploader;
import gwtupload.client.PreloadedImage;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.PreloadedImage.OnLoadPreloadedImageHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;



import ch.eaternity.client.comparators.ComparatorComparator;
import ch.eaternity.client.comparators.ComparatorObject;
import ch.eaternity.client.comparators.ComparatorRecipe;
import ch.eaternity.client.ui.EaternityRechnerView;
import ch.eaternity.client.ui.EaternityRechnerView.Presenter;
import ch.eaternity.client.widgets.PhotoGallery;
import ch.eaternity.client.widgets.UploadPhoto;
import ch.eaternity.shared.Converter;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.IngredientSpecification;




import com.google.api.gwt.services.urlshortener.shared.Urlshortener.UrlContext;
import com.google.api.gwt.services.urlshortener.shared.model.Url;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

public class RecipeView<T> extends Composite {
	interface Binder extends UiBinder<Widget, RecipeView> { }
	private static Binder uiBinder = GWT.create(Binder.class);
	
	
	@UiField AbsolutePanel dragArea;
	@UiField HTMLPanel SaveRezeptPanel;

	// add Rezept here
	@UiField Button saveRecipeButton;
	@UiField Button reportButton;

	@UiField
	public TextBox RezeptName;
	@UiField
	public CheckBox makeNotPublic;
	
	@UiField
	public CheckBox makePublic;

	@UiField Button removeRezeptButton;
	@UiField HTMLPanel htmlRezept;
	@UiField HTMLPanel rezeptTitle;

	@UiField HTML topIndikator;
	@UiField HTML bottomIndikator;
	
//	@UiField HorizontalPanel imageUploaderHP;


	@UiField
	public HTML openHTML;
	@UiField public HTML savedHTML;

	@UiField HTML codeImage;
	@UiField HTML co2ValueLabel;
	
	@UiField
	public TextBox rezeptDetails;
	
	@UiField
	public TextBox amountPersons;
	
//	private FlowPanel panelImages = new FlowPanel();
//	private PhotoGallery galleryWidget;
//	public UploadPhoto uploadWidget;
	public HandlerRegistration imagePopUpHandler = null;
	public static int overlap = 0;
	
	HTML htmlCooking;
	public Boolean askForLess;
	public Boolean askForLess2;
	public Image showImageRezept = new Image();
	public Anchor bildEntfernen;
	HandlerRegistration klicky;
	public HandlerRegistration showImageHandler = null;
	public FlexTableRowDragController tableRowDragController = null;
	public FlexTableRowDropController flexTableRowDropController = null;
	
	public boolean saved;

	public boolean isSelected;
	
	
	int  selectedRow = 0;
	int  selectedRezept = -1;
	public Recipe recipe;

	private Presenter<T> presenter;
	public void setPresenter(Presenter<T> presenter){
		this.presenter = presenter;
		
	    if(presenter.getTopPanel().leftKitchen){
//	    	PrepareButton.setVisible(false);
	    }
	    
		if (!presenter.getLoginInfo().isLoggedIn()) {
			SaveRezeptPanel.setVisible(false);
		}
	}
	
	private final EaternityRechnerView superDisplay;

	
	public RecipeView(Recipe recipe,EaternityRechnerView superDisplay) {
		this.superDisplay = superDisplay;
	    // does this need to be here?
	    initWidget(uiBinder.createAndBindUi(this));

	    setRezept(recipe);
	    
	    // this is a new recipe, so nothing to be saved:
	    setRecipeSavedMode(true);

	    
	    // we could still consider changing the image in this interface
//	    galleryWidget = new PhotoGallery(this);
////	    addInfoPanel.insert(galleryWidget,0);
//	    menuDecoInfo.add(galleryWidget);
	    
//			no more edit here
//			uploadWidget = new UploadPhoto(EaternityRechner.loginInfo, this);
//			uploadWidget.setStyleName("notInline");
			
			// Bind it to event so uploadWidget can refresh the gallery
//			uploadWidget.addGalleryUpdatedEventHandler(galleryWidget);
//			addInfoPanel.insert(uploadWidget,0);
//			menuDecoInfo.add(uploadWidget);

	    
//	    imageUploaderHP.add(panelImages);
//	    MultiUploader defaultUploader = new MultiUploader();
//	    imageUploaderHP.add(defaultUploader);
//	    defaultUploader.avoidRepeatFiles(true);
//	    defaultUploader.setValidExtensions(new String[] { "jpg", "jpeg", "png", "gif" });
//	    defaultUploader.setMaximumFiles(1);
//	    defaultUploader.addOnFinishUploadHandler(onFinishUploaderHandler);


		
	  }


	public void setRecipeSavedMode(boolean isSaved) {
		
		// the is saved should only respond if indeed something changed...
		saved = isSaved;
		reportButton.setEnabled(isSaved);
		
		makePublic.setVisible(true);

		reportButton.setVisible(isSaved);
		reportButton.setEnabled(isSaved);
		
		// the button switches -> faded out until some change happened
		saveRecipeButton.setEnabled(!isSaved);
		
		if(!isSaved){
//			savedHTML.setHTML("nicht gespeichert");
			saveRecipeButton.setText("Rezept speichern");
			
			if((recipe.openRequested != null && recipe.openRequested) || (recipe.open!=null && recipe.open)){
				// veröffentlichung angefragt
				makeNotPublic.setVisible(true);
				makePublic.setVisible(false);
			} else {
				// veröffentlichung nicht gewünscht
				makeNotPublic.setVisible(false);
				makePublic.setVisible(true);
			}
			
		} else {
//			savedHTML.setHTML("gespeichert");
			saveRecipeButton.setText("Rezept ist gespeichert");
			
			if((recipe.openRequested != null && recipe.openRequested) || (recipe.open!=null && recipe.open)){
				// veröffentlichung angefragt
				makeNotPublic.setVisible(false);
				makePublic.setVisible(false);
			} else {
				// veröffentlichung nicht gewünscht

			}
			
			
			
			if(recipe.open!=null && recipe.open){
				openHTML.setVisible(true);
				openHTML.setHTML("Rezept ist öffentlich zugänglich.");
			}
			if((recipe.openRequested != null && recipe.openRequested)){
				openHTML.setVisible(true);
				openHTML.setHTML("Veröffentlichung des Rezepts wurde angefragt.");
			}
			
		}
		

	}
	
	
	
	void shortenAndSave() {
		final RecipeView rezeptView = this;
		presenter.addRezept(recipe,rezeptView);
	    // show the report Button
	    
	  }
	
	
	@UiHandler("reportButton")
	void onReportClick(ClickEvent event) {
        Date date = new Date();
        long iTimeStamp = (long) (date.getTime() * .00003);
        
        // if there is no way to save, it should not be created a new one...
        
        long code = recipe.getId()*iTimeStamp;

		String clear = Converter.toString(code,34);
		String url = GWT.getHostPageBaseURL()+ "convert?ids=" + clear;
		Window.open(url, "Menu Klima-Bilanz", "menubar=no,location=no,resizable=yes,scrollbars=yes,status=yes");
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
				updateSuggestion();
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



	@UiHandler("RezeptName")
	void onEdit(KeyUpEvent event) {
		if(RezeptName.getText() != ""){

			// only do this, if this is the recipe that is getting edited
			
			// the case this recipe is also open
			if(isSelected){
				RecipeEditView rezeptViewEdit = (RecipeEditView) superDisplay.getRezeptEditList().getWidget(0, 1);
				rezeptViewEdit.RezeptName.setText(RezeptName.getText());
				rezeptViewEdit.recipe.setSymbol(RezeptName.getText());
			}
			
			recipe.setSymbol(RezeptName.getText());
			
		}
	}
	
	@UiHandler("rezeptDetails")
	void onEditSub(KeyUpEvent event) {
		if(rezeptDetails.getText() != ""){
//			rezeptSubTitleTop.setText(rezeptDetails.getText());
			recipe.setSubTitle(rezeptDetails.getText());
			
			
			// only do this, if this is the recipe that is getting edited
			
			// the case this recipe is also open
			if(isSelected){
				RecipeEditView rezeptViewEdit = (RecipeEditView) superDisplay.getRezeptEditList().getWidget(0, 1);
				rezeptViewEdit.rezeptDetails.setText(rezeptDetails.getText());
				rezeptViewEdit.recipe.setSubTitle(rezeptDetails.getText());
			}
			

		}
	}
	

	@UiHandler("removeRezeptButton")
	void onRemoveClicked(ClickEvent event) {
		
		final RecipeView test = this;
		if(saved){
			int row = getWidgetRow(test , superDisplay.getRezeptList());
			
			superDisplay.getRezeptList().remove(test);
			superDisplay.getRezeptList().removeRow(row);

			if(superDisplay.getSelectedRezept() == row){
				superDisplay.setSelectedRezept(-1);
				superDisplay.getSuggestionPanel().clear();
				
				if(isSelected){
					
	//				close also the Editview! ... or respectively the topview
					//TODO this fails on the top view...
					superDisplay.closeRecipeEditView();
					
				}
			}
			

			
		} else {
			
		String saveText = "Zusammenstellungen ist noch nicht gespeichert!";
		if(!presenter.getLoginInfo().isLoggedIn()){
			saveText = "Sie verlieren alle Änderungen!";
		}
		final ConfirmDialog dlg = new ConfirmDialog(saveText);
		dlg.statusLabel.setText("Zusammenstellung trotzdem ausblenden?");
		// TODO recheck user if he really want to do this...
		
		dlg.executeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				int row = getWidgetRow(test , superDisplay.getRezeptList());
				
				
				superDisplay.getRezeptList().remove(test);
				superDisplay.getRezeptList().removeRow(row);
				
				
				// well this doesn't work, as it is selected already (due to the time difference...)
				if(superDisplay.getSelectedRezept() == row){
					superDisplay.setSelectedRezept(-1);
					superDisplay.getSuggestionPanel().clear();
				
					if(isSelected){
	//					close also the Editview! ... or respectively the topview
						superDisplay.closeRecipeEditView();
						
					}
				}
//				
				dlg.hide();
			}
		});
		dlg.show();
		dlg.center();
		}
	}
	


	
	public void setRezept(Recipe recipe){
		this.recipe = recipe;
	}

	public Recipe getRezept(){
		return this.recipe;
	}	
	
	public void showRezept(final Recipe recipe) { 		
		// this is now getting called by the EaternityRechnerViewImpl:

			if(recipe.getPersons() != null){
				amountPersons.setText(recipe.getPersons().toString());
			} else {
				amountPersons.setText("4");
				Long persons = Long.parseLong(amountPersons.getText());
				recipe.setPersons(persons);
			}
			
		    savedHTML.setVisible(false);
		    openHTML.setVisible(false);
		    makePublic.setVisible(false);
		    
			updateSuggestion();

			if(recipe.image !=null){
				codeImage.setHTML("<img src='" + recipe.image.getServingUrl() + "=s80-c' />");
			} else {
				codeImage.setHTML("<img src='http://placehold.it/80x80' />");
			}
			
			if(klicky != null){
				klicky.removeHandler();
			}
			
			// add Save Recipe Button
			klicky = saveRecipeButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					//TODO if the recipe is already in your personal data-store, don't create a new one.
					
					if(RezeptName.getText() != ""){
						// TODO warn that it wasn't saved in the other case

						recipe.setSymbol(RezeptName.getText());
						recipe.openRequested = !makeNotPublic.getValue();
						recipe.open = false;

						shortenAndSave();
					}
				}
			});
		
	}




	void updateSuggestion() {

		
		recipe.setCO2Value();
		String formattedCO2Value = NumberFormat.getFormat("##").format(recipe.getCO2Value());
		co2ValueLabel.setHTML(formattedCO2Value+" g<sup>&#10031;</sup>");
		
		// this is right now not relevant, yet gets executed
//		updtTopSuggestion();
		// should be in recipe edit view!
		
		
		
	}


	public void updtTopSuggestion() {
		// TODO Algorithm for the Top Suggestions
		// Von jedem Gericht gibt es einen CO2 Wert für 4Personen (mit oder ohne Herkunft? oder aus der nächsten Distanz?), 
		// so wie sie gepeichert wurde.
		// Es werden bei der Anzeige Rezepte berücksichtigt, die: 
		// min 20% identische Zutaten ( Zutat*(Menge im Recipe)/StdMenge ) und das pro Zutat, und davon min 20%identisch
		// min +50% Zutaten die in den alternativen Vorkommen
		// hierbei wird die 2 passendsten Rezepte jeweils aus den nicht durch das markierte Recipe belegten Bereich angezeigt
		// Bereich sind 0-20%	20%-50%		50%-100%
		
		// Rezepte sollten sich bewerten lassen, und deren Popularität gemessen werden. ( Über die Zeit?)
		// 

		// diese Filter sollten in der Reihenfolge ausgeführt werden, in der sie am wenigsten Berechnungen benötigen:
		
		// TODO alle Rezepte für 4 Personen, sonst macht der Vergleich keinen Sinn
		
		// get Comparator
		ArrayList<ComparatorObject> comparator = comparator(recipe);
		// comparator returns for each ingedient:
		// (zutatSpec.getMengeGramm()/zutat.stdAmountGramm)/recipe.getPersons();
		
		Double maxScore = 0.0;
		for(ComparatorObject comparatorObject : comparator){
			maxScore = maxScore+comparatorObject.value;
		}
		// this is the sum over all those ingredient values
		// which equals about the number of different ingredients in the recipe...
		
		
		// all Recipes
		List<Recipe> allRecipes = new ArrayList<Recipe>();
		allRecipes.clear();
		// display context information...
		Recipe compare = recipe;
		compare.setSelected(true);
		if(recipe.getSymbol() == null){
			compare.setSymbol("Ihr Menu");
		}
		if(recipe.getSubTitle() == null){
			compare.setSubTitle("Menu Beschreibung");
		}
		
		// add your specific recipe to the others in the database
		allRecipes.add(compare);
		// and all the others also
		allRecipes.addAll( presenter.getClientData().getPublicRezepte());
		if(presenter.getClientData().getYourRezepte() != null){
			allRecipes.addAll(presenter.getClientData().getYourRezepte());
		}

		
		
		// zuerst der Filter über die tatsächlichen Zutaten
		ArrayList<ComparatorRecipe> scoreMap = new ArrayList<ComparatorRecipe>();
		scoreMap.clear();
		
		// Init first boundaries, for indicator
		Double MaxValueRezept = 0.0;
		Double MinValueRezept = 10000000.0;
		//  go over the Recipes in the Workspace
		for(Widget widget : superDisplay.getRezeptList()){
			RecipeView rezeptView = (RecipeView) widget;
			rezeptView.recipe.setCO2Value();
			if(rezeptView.recipe.getCO2Value()>MaxValueRezept){
				MaxValueRezept = rezeptView.recipe.getCO2Value();
			} 
			if(rezeptView.recipe.getCO2Value()<MinValueRezept){
				MinValueRezept = rezeptView.recipe.getCO2Value();
			}
		}
		
		// go over the recipes in the database ( here our special one is already included...)
		for( Recipe compareRecipe : allRecipes){
			
			// this is just to get the min and max values for the indicator
			compareRecipe.setCO2Value();
			if(compareRecipe.getCO2Value()>MaxValueRezept){
				MaxValueRezept = compareRecipe.getCO2Value();
			} 
			if(compareRecipe.getCO2Value()<MinValueRezept){
				MinValueRezept = compareRecipe.getCO2Value();
			}
			
			ComparatorRecipe comparatorRecipe = new ComparatorRecipe();
			comparatorRecipe.key = compareRecipe.getId();
			comparatorRecipe.recipe = compareRecipe;
			// get the direct comparison score... the bigger the worse...
			comparatorRecipe.comparator = getExactScore(comparator,comparator(compareRecipe));
			
			// error is the max score of both added together, minus twice the disjunct region
			
			Double error = 0.0;
			Double errorNeg = 0.0;
			for(ComparatorObject comparatorObject : comparatorRecipe.comparator){
				error = error+Math.abs(comparatorObject.value);
				if(comparatorObject.value<0){
					errorNeg = errorNeg+Math.abs(comparatorObject.value);
				}
			}
			comparatorRecipe.value = error;
			comparatorRecipe.valueNeg = errorNeg;
			scoreMap.add(comparatorRecipe);
		}
		
		// dann der gröbere über die definierten Alternativen der Zutaten
		ArrayList<ComparatorRecipe> scoreMap2 = new ArrayList<ComparatorRecipe>();
		scoreMap2.clear();
		for(ComparatorRecipe compRecipe: scoreMap){
			// this is cool now!
			if((compRecipe.value/(maxScore+compRecipe.valueNeg))<0.8){ // this is min. 20% identical _____==overlap==-----
//			if((compRecipe.value)<0.8){ // this is min. 20% identical
				Recipe compareRecipe = compRecipe.recipe;
				ComparatorRecipe comparatorRecipe = new ComparatorRecipe();
				comparatorRecipe.recipe = compareRecipe;
				comparatorRecipe.key = compareRecipe.getId();
				// TODO here we got some error...
				comparatorRecipe.value = getAltScore(compRecipe,maxScore);
				Double errorNeg = 0.0;
				for(ComparatorObject comparatorObject : compRecipe.comparator){
					if(comparatorObject.value<0){
						errorNeg = errorNeg+Math.abs(comparatorObject.value);
					}
				}
				comparatorRecipe.valueNeg = errorNeg;
				
				scoreMap2.add(comparatorRecipe);
			}
		}

		
		// alles was jetzt noch da ist (alle errors  <0.6), werden verglichen, das heisst die Statistik ausgerechnet
		ArrayList<ComparatorRecipe> scoreMapFinal = new ArrayList<ComparatorRecipe>();
		scoreMapFinal.clear();
		for(ComparatorRecipe compRecipe: scoreMap2){
			// TODO 0.6 IS JUST A GUESS
//			if((compRecipe.value/maxScore)<0.6){ // this is min. 20% identical and min. 60% alternative Identity
			if((compRecipe.value/(maxScore+compRecipe.valueNeg))<0.6){ // this is min. 20% identical and min. 60% alternative Identity
				
				compRecipe.recipe.setCO2Value();
				scoreMapFinal.add(compRecipe);
			}
		}
		
		Collections.sort(scoreMapFinal,new ComparatorComparator());

		// this is just a test
		if(scoreMapFinal.size()>0){
			superDisplay.getSuggestionPanel().clear();

			displayTops(scoreMapFinal, 0.8, 1.0);
			displayTops(scoreMapFinal, 0.5, 0.8);
			displayTops(scoreMapFinal, 0.0, 0.5);
			
		}
		
////		if(scoreMapFinal.size()>2){
//			recipe.setCO2Value();
//			double indikator = recipe.getCO2Value();
////			double stop = scoreMapFinal.get(0).recipe.getCO2Value();
//			double stop = MaxValueRezept;
////			double start = scoreMapFinal.get(scoreMapFinal.size()-1).recipe.getCO2Value();
//			double start = MinValueRezept;
//			
//			Long indikatorLeft = Math.round(800/(stop-start)*(indikator-start));
//			String indikatorHTML = "<div style='padding-left:"+indikatorLeft.toString()+"px'>für 1ne Person: "+NumberFormat.getFormat("##").format(recipe.getCO2Value())+"g CO2</div>";
//			topIndikator.setHTML(indikatorHTML);
//			bottomIndikator.setHTML(indikatorHTML);
			
//		}
		

		//		double stop = scoreMapFinal.get(0).recipe.getCO2Value();
		double stop = MaxValueRezept;
		//		double start = scoreMapFinal.get(scoreMapFinal.size()-1).recipe.getCO2Value();
		double start = MinValueRezept;


		// update all widgets bars!
		for(Widget widget : superDisplay.getRezeptList()){
			RecipeView rezeptView = (RecipeView) widget;
			rezeptView.recipe.setCO2Value();
			Long indikatorLeft = new Long(Math.round(580/(stop-start)*(rezeptView.recipe.getCO2Value()-start)));
			String indikatorHTMLoben = new String("<div style='padding-left: 30px;display:inline;background:#000;background-image:url(eckeoben.png);margin-left:"+indikatorLeft.toString()+"px'>"+NumberFormat.getFormat("##").format(rezeptView.recipe.getCO2Value())+" g* (pro Person)</div>");
			String indikatorHTMLunten = new String("<div style='padding-left: 30px;display:inline;background:#000;background-image:url(eckeunten.png);margin-left:"+indikatorLeft.toString()+"px'>"+NumberFormat.getFormat("##").format(rezeptView.recipe.getCO2Value())+" g* (pro Person)</div>");
			rezeptView.topIndikator.setHTML(indikatorHTMLoben);
			rezeptView.bottomIndikator.setHTML(indikatorHTMLunten);
			
		}

			
		
		// und die 2 Rezepte mit den höchsten Scores aus den entspr. Bereichen selektiert und angezeigt.
	}


	private void displayTops(ArrayList<ComparatorRecipe> scoreMapFinal,
			Double startDouble, Double stopDouble) {
		// maybe it shouldn't be 20% of the best rezepte, but a another scaling...
		int beginRange = (int) Math.floor((scoreMapFinal.size())*startDouble);
		int stopRange = (int) Math.floor((scoreMapFinal.size())*stopDouble);

		List<ComparatorRecipe> selectionList =  scoreMapFinal.subList(beginRange, stopRange);
		if(stopRange-beginRange != 0){
			Double minValue = 100000.0;
			Recipe selectedMax = null;
			Iterator<ComparatorRecipe> iterator = selectionList.iterator();
			while(iterator.hasNext()){
				ComparatorRecipe takeMax = iterator.next();
				if(takeMax.value<minValue){
					selectedMax = takeMax.recipe;
					minValue = takeMax.value;
				}
			}
			final Recipe takeThisOne = selectedMax;
			
			selectedMax.setCO2Value();
			Double MenuLabelWert = selectedMax.getCO2Value();
			HTML suggestText = new HTML("<div style='cursor: pointer;cursor: hand;height:60px;width:230px;background:#F9C88C;margin-right:30px;border-radius: 3px;border: solid 2px #F48F28;'><div style='height:40px;width:200px;background:#323533;color:#fff;padding-left:5px;border-bottom-right-radius: 3px;border-top-right-radius: 3px;'><b>" + selectedMax.getSymbol() + "</b><br/>"+ selectedMax.getSubTitle() +"</div>CO2-Äq ca: <b>" + NumberFormat.getFormat("##").format(MenuLabelWert)  +" g</b></div>");
			HandlerRegistration handler = suggestText.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					// add receipe to the Worksheet Panel
					superDisplay.showRecipeClone(takeThisOne);
				}
			});
			
			if(selectedMax.getSelected() != null){
				if(selectedMax.getSelected()){
					handler.removeHandler();
					suggestText = new HTML("<div style='height:60px;width:230px;background:#F48F28;margin-right:30px;border-radius: 3px;border: solid 2px #F48F28;'><div style='height:40px;width:200px;background:#323533;color:#fff;padding-left:5px;border-bottom-right-radius: 3px;border-top-right-radius: 3px;'><b>" + selectedMax.getSymbol() + "</b><br/>"+ selectedMax.getSubTitle() +"</div>CO2-Äq ca: <b>" + NumberFormat.getFormat("##").format(MenuLabelWert)  +" g</b></div>");
				}
			}
			superDisplay.getSuggestionPanel().add(suggestText);
		}
	}
	
	
	
	private Double getAltScore(ComparatorRecipe compRecipe, Double maxScore) {
		// check for all the stuff thats negative: that means we had to much in the compareRecipe
		// and check for all the alternatives of the negative one, if there is something left in the positive
		ArrayList<ComparatorObject> resultComparator = null;

		boolean changed = true;
		while(changed){
			resultComparator = compRecipe.comparator;
			changed = false;
			for(ComparatorObject compObj: resultComparator){
				if(compObj.ingredient.getAlternatives() != null){
					if((Math.abs(compObj.value)/(maxScore+compRecipe.valueNeg))>0.1){
						// we take 10% as a tolerance value
						for(Long altIngredientId :compObj.ingredient.getAlternatives()){
							for(ComparatorObject compObj2: resultComparator){
								if(altIngredientId.equals(compObj2.key)){
									if(Math.abs(compObj2.value)>0 && Math.abs(compObj.value+compObj2.value)<0.1){
										// this means we have min. 10% improvement ( to have a converging situation)

										int subtractHere = compRecipe.comparator.indexOf(compObj2);
										int addHere = compRecipe.comparator.indexOf(compObj);
										compObj2.value = compObj2.value+compObj.value;
										compObj.value = compObj2.value;
										resultComparator.set(subtractHere, compObj2);						
										resultComparator.set(addHere, compObj);	

										changed = true;
										break;
									}
								}
							}
						}

					}
				}
			}
			
		}
		
		
		Double errorOrig = compRecipe.value;
//		for(ComparatorObject comparatorObject : compRecipe.comparator){
//			errorOrig = errorOrig+Math.abs(comparatorObject.value);
//		}
//		
		Double errorHere = 0.0;
		Double errorNeg = 0.0;
		for(ComparatorObject comparatorObject : resultComparator){
			errorHere = errorHere+Math.abs(comparatorObject.value);
		}
		

		// return value should be the absolut values but 1/3 of the score...
		// TODO 1/3 IS JUST A GUESS
		return errorHere;
	}


	private ArrayList<ComparatorObject> getExactScore(ArrayList<ComparatorObject> recipeOrigin, ArrayList<ComparatorObject> recipeComparator) {
		
		ArrayList<ComparatorObject> resultComparator = new ArrayList<ComparatorObject>();
		resultComparator.clear();
		
		// takes this Recipe from this RezeptView
		for(ComparatorObject comparatorObjectOrigin : recipeOrigin){
			// and compares every ingredient

			
			ComparatorObject comparatorResultObject = new ComparatorObject();
			// this is the positive error
			// if we have something in the origin, but not in the compare
			// we start with the maximum error
			double newValue = comparatorObjectOrigin.value;
			comparatorResultObject.key = comparatorObjectOrigin.key;
			comparatorResultObject.ingredient  = comparatorObjectOrigin.ingredient;
			
			// with the one from the database
			for(ComparatorObject comparatorObject :recipeComparator){
				
				// on match - we substract this match...
				if(comparatorObject.key.equals(comparatorObjectOrigin.key)){	
					// calculate the error value
					newValue = comparatorObjectOrigin.value-comparatorObject.value;
					// if this is negative, we had too much of this in the compare
					
					// break for speed up ( as the map is injective)
					break;
				}
			}
			
			// store the error value
			comparatorResultObject.value = newValue;
			resultComparator.add(comparatorResultObject);

		}
		
		// this is the case that we have more ingredients, then in the origin one.
		// they will be added as negative error... ( as it was also too much...)
		for(ComparatorObject comparatorObject :recipeComparator){
			boolean notFound = true;
			for(ComparatorObject resultCompObj : resultComparator){
				if(comparatorObject.key.equals(resultCompObj.key)){
					notFound = false;
				}
			}
			if(notFound){
				ComparatorObject comparatorResultObject = new ComparatorObject();
				comparatorResultObject.key = comparatorObject.key;
				comparatorResultObject.value = -comparatorObject.value;
				comparatorResultObject.ingredient  = comparatorObject.ingredient;
				resultComparator.add(comparatorResultObject);
			}
		
		}
		
		
		return resultComparator;
	}


	private ArrayList<ComparatorObject> comparator(Recipe recipe){
		// wtf is up with the Map() ???
//		Map<Long,Double> recipeComparator = Collections.emptyMap();
		// everything would have been so easy!!
		
		 ArrayList<ComparatorObject> recipeComparator = new  ArrayList<ComparatorObject>();
		 recipeComparator.clear();
		
		for(IngredientSpecification zutatSpec : recipe.Zutaten){
			Ingredient zutat = presenter.getClientData().getIngredientByID(zutatSpec.getZutat_id());
//			amount of Persons needs to be assigned always!
			Double amount = (1.0*zutatSpec.getMengeGramm()/zutat.stdAmountGramm)/recipe.getPersons();
			Double alreadyAmount = 0.0;
			int index = -1;
//			check if the indgredient is already in there...
			for(ComparatorObject comparatorObject :recipeComparator){
				if(comparatorObject.key.equals(zutat.getId())){
					alreadyAmount =  comparatorObject.value;
					index = recipeComparator.indexOf(comparatorObject);
					// if so take the found index
					break;
				}
			}
			
			ComparatorObject comparatorObject = new ComparatorObject();
			comparatorObject.ingredient = zutat;
			comparatorObject.key = zutat.getId();
			comparatorObject.value = amount+alreadyAmount;
			
			if(index!=-1){
				recipeComparator.set(index, comparatorObject);
			} else {
				recipeComparator.add(comparatorObject);
			}

		}

		return recipeComparator;
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


	
	// here comes the Image Uploader:

   /*
    * Do this later (if considered useful...)
 
	private IUploader.OnFinishUploaderHandler onFinishUploaderHandler = new IUploader.OnFinishUploaderHandler() {
	    public void onFinish(IUploader uploader) {
	      if (uploader.getStatus() == Status.SUCCESS) {
	    	 
//	    	 recipe.imageId = uploader.
	    	 GWT.log("Successfully uploaded image: "+  uploader.fileUrl(), null);
	        new PreloadedImage(uploader.fileUrl(), showImage);
	        
	        // The server can send information to the client.
	        // You can parse this information using XML or JSON libraries
//	        Document doc = XMLParser.parse(uploader.getServerResponse());
//	        String size = Utils.getXmlNodeValue(doc, "file-1-size");
//	        String type = Utils.getXmlNodeValue(doc, "file-1-type");
//	        System.out.println(size + " " + type);
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

