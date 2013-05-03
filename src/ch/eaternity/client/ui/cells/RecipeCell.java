package ch.eaternity.client.ui.cells;

import ch.eaternity.client.DataController;
import ch.eaternity.client.activity.RechnerActivity;
import ch.eaternity.client.place.RechnerRecipeEditPlace;
import ch.eaternity.client.resources.Resources;
import ch.eaternity.shared.RecipeInfo;
import ch.eaternity.shared.Util;
import ch.eaternity.shared.Util.RecipeScope; 

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

/**
 * A custom {@link Cell} used to render a RecipeInfo
 */
public class RecipeCell extends AbstractCell<RecipeInfo> {
	
	private DataController dco;
	private RechnerActivity presenter;
	
	public RecipeCell(RechnerActivity presenter) {
		super("click");
		this.presenter = presenter;
		this.dco = presenter.getDCO();
		// tell AbstractCell which events to forward to RecipeCell
	}

	/**
	 * Create a singleton instance of the templates used to render the cell.
	 */
	// private static ImgTemplate imgTemplate = GWT.create(ImgTemplate.class);

	// private static ProductTemplate productTemplate =
	// GWT.create(ProductTemplate.class);

	@Override
    public void render(Cell.Context context, RecipeInfo recipeInfo, SafeHtmlBuilder sb) {
	  /*
	   * Always do a null check on the value. Cell widgets can pass null to
	   * cells if the underlying data contains a null, or if the data arrives
	   * out of order.
	   */
	  if (recipeInfo == null) {
	    return;
	  }
	  
	  NumberFormat formatter = NumberFormat.getFormat("##");
	
	  SafeHtml title = SafeHtmlUtils.fromString(recipeInfo.getTitle());
	  SafeHtml subtitle = SafeHtmlUtils.fromString(recipeInfo.getSubTitle());
	  SafeHtml co2value = SafeHtmlUtils.fromTrustedString(formatter.format(recipeInfo.getCo2eValue()) + " g*");
	  SafeHtml image;
	
	  if(recipeInfo.getImage() !=null){
		  	image = SafeHtmlUtils.fromTrustedString("<img style='max-width:70px; max-height:70px;' src='" + recipeInfo.getImage().getUrl() + "' />");
		} else {
			image = SafeHtmlUtils.fromTrustedString("<img src='" + Resources.INSTANCE.recipeImageDefaultSmall().getURL() + "' />");
	}
	  
	  sb.appendHtmlConstant("<div class='" + Resources.INSTANCE.style().recipeInfo() + "'><table><tr><td width='80'>");
	  sb.append(image);
	  sb.appendHtmlConstant("</td>");
	  
	  sb.appendHtmlConstant("<td width='370' style='padding-left:10px;'><span style='font-size:20px; font-weight:600;'>");
	  sb.append(title);
	  sb.appendHtmlConstant("</span><br /><span style='font-size:14px;'>");
	  sb.append(subtitle);
	  sb.appendHtmlConstant("</span></td>");
	  
	  sb.appendHtmlConstant("<td width='40'>");
	  sb.appendHtmlConstant("<img src='" + Util.getRecipeRatingBarUrl(recipeInfo.getCo2eValue()) + "' />");
	  sb.appendHtmlConstant("</td>");
	  
	  sb.appendHtmlConstant("<td width='150' align='right'><span style='font-size:30px; font-weight:bold;'>");
	  sb.append(co2value);
	  sb.appendHtmlConstant("</span></td>");
	  sb.appendHtmlConstant("<td width='90'><div style='margin-left:7px; text-align:left;'>CO2<br />pro Person</div></td>");
	  
	  sb.appendHtmlConstant("<td width='40'>");
	  sb.appendHtmlConstant("<a style='cursor:pointer;'><img src='" + Resources.INSTANCE.deleteLarge().getURL() + "' /></a>");
	  sb.appendHtmlConstant("</td>");
	  
	  sb.appendHtmlConstant("</tr></table></div>");
	
    }
	
	/**
	 * handle the delete click
	 * @param context
	 * @param parent
	 * @param recipeInfo
	 * @param event
	 * @param valueUpdater
	 */
	@Override
	public void onBrowserEvent(Context context, Element parent, RecipeInfo recipeInfo, NativeEvent event,
	        ValueUpdater<RecipeInfo> valueUpdater) {
	    super.onBrowserEvent(context, parent, recipeInfo, event, valueUpdater);
	    if ("click".equals(event.getType())) {
	        EventTarget eventTarget = event.getEventTarget();
	        if (!Element.is(eventTarget)) {
	            return;
	        }
	        if (parent.getElementsByTagName("a").getItem(0).isOrHasChild(Element.as(eventTarget))) {
	            dco.deleteRecipe(recipeInfo.getId());
	        }
	        else {
	        	if (recipeInfo.isPublished() && dco.getRecipeScope() == RecipeScope.PUBLIC) {
	        		dco.cloneRecipe(recipeInfo);
	        		presenter.goTo(new RechnerRecipeEditPlace("clone"));
	        	}
	        	else
	        		presenter.goTo(new RechnerRecipeEditPlace(recipeInfo.getId().toString()));
	        }
	    }
	}


}
