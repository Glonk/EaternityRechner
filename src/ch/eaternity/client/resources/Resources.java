package ch.eaternity.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;

public interface Resources extends ClientBundle {
	public static final Resources INSTANCE = GWT.create(Resources.class);

	@Source("global.css")
	public CssResource globalCss();

	@Source("menu-label-1.png")
	public ImageResource ratingBar1();

	@Source("menu-label-2.png")
	public ImageResource ratingBar2();

	@Source("menu-label-3.png")
	public ImageResource ratingBar3();

	@Source("saisonal.png")
	public ImageResource season();

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

	
}
