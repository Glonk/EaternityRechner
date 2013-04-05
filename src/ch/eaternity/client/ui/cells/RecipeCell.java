package ch.eaternity.client.ui.cells;

import ch.eaternity.client.DataController;
import ch.eaternity.shared.RecipeInfo;
import ch.eaternity.shared.Unit;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

/**
 * A custom {@link Cell} used to render a RecipeInfo
 */
public class RecipeCell extends AbstractCell<RecipeInfo> {

	/**
	 * The HTML templates used to render the cell.
	 */
	interface ImgTemplate extends SafeHtmlTemplates {
		/**
		 * The template for this Cell, which includes styles and a value.
		 * 
		 * @param styles
		 *            the styles to include in the style attribute of the div
		 * @param value
		 *            the safe value. Since the value type is {@link SafeHtml},
		 *            it will not be escaped before including it in the
		 *            template. Alternatively, you could make the value type
		 *            String, in which case the value would be escaped.
		 * @return a {@link SafeHtml} instance
		 */
		// @SafeHtmlTemplates.Template("<img class=\"{0}\" src=\"{1}\" />")
		// SafeHtml cell(String classes, SafeHtml src);
	}

	interface ProductTemplate extends SafeHtmlTemplates {
		@SafeHtmlTemplates.Template("<div class=\"productInfo\">{0}&nbsp;&nbsp;&nbsp;{1}</div>")
		SafeHtml cell(SafeHtml name, SafeHtml value);
	}
	
	private DataController dco;
	
	public RecipeCell(DataController dco) {
		this.dco = dco;
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
	  SafeHtml co2value = SafeHtmlUtils.fromTrustedString(formatter.format(recipeInfo.getCo2eValue()));
	  SafeHtml image;
	
	  if(recipeInfo.getImage() !=null){
		  	image = SafeHtmlUtils.fromTrustedString("<img src='" + recipeInfo.getImage().getServingUrl() + "=s80-c' />");
		} else {
			image = SafeHtmlUtils.fromTrustedString("<img src='http://placehold.it/80x80' />");
	}
	  
	  sb.appendHtmlConstant("<div class='recipeInfo'><table><tr><td width='80'>");
	  sb.append(image);
	  sb.appendHtmlConstant("</td>");
	  
	  sb.appendHtmlConstant("<td width='450'><span style='font-size:20px; font-weight:bold;'>");
	  sb.append(title);
	  sb.appendHtmlConstant("</span><br /><span style='font-size:14px;'>");
	  sb.append(subtitle);
	  sb.appendHtmlConstant("</span></td>");
	  
	  sb.appendHtmlConstant("<td width='40'>");
	  if (recipeInfo.getCo2eValue() < 700) {
		  sb.appendHtmlConstant("<img src='/images/rating_bars.png' />");
	  }
	  else if (recipeInfo.getCo2eValue() > 700 && recipeInfo.getCo2eValue() < 1500) {
		  sb.appendHtmlConstant("<img src='/images/rating_bars.png' />");
	  }
	  else {
		  sb.appendHtmlConstant("<img src='/images/rating_bars.png' />");
	  }
	  sb.appendHtmlConstant("</td>");
	  
	  sb.appendHtmlConstant("<td width='140'><span style='font-size:30px; font-weight:bold;'>");
	  sb.append(co2value);
	  sb.appendHtmlConstant("</span>&nbsp;&nbsp;&nbsp;g/p.P.</td>");
	  
	  sb.appendHtmlConstant("<td width='20'>");
	  sb.appendHtmlConstant("<a style='cursor:pointer;'><img src='delete.png' /></a>");
	  sb.appendHtmlConstant("</td>");
	  
	  sb.appendHtmlConstant("</tr></table></div>");
	
    }
	
	/**
	 * handle the delete click
	 * @param context
	 * @param parent
	 * @param value
	 * @param event
	 * @param valueUpdater
	 */
	@Override
	public void onBrowserEvent(Context context, Element parent, RecipeInfo value, NativeEvent event,
	        ValueUpdater<RecipeInfo> valueUpdater) {
	    super.onBrowserEvent(context, parent, value, event, valueUpdater);
	    if ("click".equals(event.getType())) {
	        EventTarget eventTarget = event.getEventTarget();
	        if (!Element.is(eventTarget)) {
	            return;
	        }
	        if (parent.getElementsByTagName("a").getItem(0).isOrHasChild(Element.as(eventTarget))) {
	            dco.deleteRecipe(value.getId());
	        }
	    }
	}


}
