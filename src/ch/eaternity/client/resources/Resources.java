package ch.eaternity.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.user.cellview.client.CellTable;

public interface Resources extends ClientBundle {
	public static final Resources INSTANCE = GWT.create(Resources.class);

	interface Style extends CssResource {
		String productInfoItem();
		String recipeInfo();
		String container();
		String intro();
		String rotate90();
		String rotate180();
		String rotate270();
		String editableTextBox();
		String generalAnchor();
	}
	
	interface Constants extends CssResource {
		String edarkgreen();
		String elightgreen();
		String eoddrow();
		String etextgray();
		String etextblack();
	}
	
	
	@Source("global.css")
	public Style style();
	
	@Source("constants.css")
	public Constants constants();

	@Source("menu-label-1.png")
	public ImageResource ratingBar1();

	@Source("menu-label-2.png")
	public ImageResource ratingBar2();

	@Source("menu-label-3.png")
	public ImageResource ratingBar3();

	@Source("saisonal.png")
	public ImageResource season();
	
	@Source("regional.png")
	public ImageResource region();

	@Source("zutat-bewertung-1.png")
	public ImageResource ingredientRatingBar1();

	@Source("zutat-bewertung-2.png")
	public ImageResource ingredientRatingBar2();
	
	@Source("zutat-bewertung-3.png")
	public ImageResource ingredientRatingBar3();

	@Source("top_calendar.png")
	public ImageResource calendar();
	
	@Source("top_contact.png")
	public ImageResource contact();
	
	@Source("top_logout.png")
	public ImageResource logout();
	
	@Source("question_mark.png")
	public ImageResource questionMark();

	@Source("trash22px.png")
	public ImageResource deleteLarge();
	
	@Source("trash11px.png")
	public ImageResource deleteSmall();
	
	@Source("bio.png")
	public ImageResource bio();
	
	@Source("recipe_image_default.png")
	public ImageResource recipeImageDefault();
	
	@Source("recipe_image_default_small.png")
	public ImageResource recipeImageDefaultSmall();
	
	@Source("h_menu_active.png")
	public ImageResource hMenuActive();
	
	@Source("arrow-sort.png")
	public ImageResource arrowSort();
	
	@Source("rechner.png")
	public ImageResource rechner();
}
