package ch.eaternity.client.ui.cells;


import ch.eaternity.client.place.RechnerRecipeEditPlace;
import ch.eaternity.client.resources.Resources;
import ch.eaternity.client.ui.SearchIngredients;
import ch.eaternity.shared.FoodProductInfo;
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
 * A custom {@link Cell} used to render a FoodProductInfo
 */
public class ProductCell extends AbstractCell<FoodProductInfo> {

	
	private SearchIngredients searchIngredients;
	
	
    /**
     * The HTML templates used to render the cell.
     */
    interface ImgTemplate extends SafeHtmlTemplates {
      /**
       * The template for this Cell, which includes styles and a value.
       * 
       * @param styles the styles to include in the style attribute of the div
       * @param value the safe value. Since the value type is {@link SafeHtml},
       *          it will not be escaped before including it in the template.
       *          Alternatively, you could make the value type String, in which
       *          case the value would be escaped.
       * @return a {@link SafeHtml} instance
       */
      //@SafeHtmlTemplates.Template("<img class=\"{0}\" src=\"{1}\" />")
     // SafeHtml cell(String classes, SafeHtml src);
    }
    
    interface ProductTemplate extends SafeHtmlTemplates {
          @SafeHtmlTemplates.Template("<div class=\"productInfo\">{0}&nbsp;&nbsp;&nbsp;{1}</div>")
          SafeHtml cell(SafeHtml name, SafeHtml value);
      }

    
    public ProductCell(SearchIngredients searchIngredients) {
    	super("click");
    	this.searchIngredients = searchIngredients;
    }
    /**
     * Create a singleton instance of the templates used to render the cell.
     */
   // private static ImgTemplate imgTemplate = GWT.create(ImgTemplate.class);
    
    //private static ProductTemplate productTemplate = GWT.create(ProductTemplate.class);

	@Override
	public void render(Cell.Context context, FoodProductInfo productInfo,
			SafeHtmlBuilder sb) {
		/*
		 * Always do a null check on the value. Cell widgets can pass null to
		 * cells if the underlying data contains a null, or if the data arrives
		 * out of order.
		 */
		if (productInfo == null) {
			return;
		}

		NumberFormat formatter = NumberFormat.getFormat("##");

		SafeHtml name = SafeHtmlUtils.fromTrustedString(productInfo.getName());
		SafeHtml co2value = SafeHtmlUtils.fromTrustedString(formatter
				.format(productInfo.getCo2eValue()));

		// SafeHtml rendered = productTemplate.cell(name, co2value);

		sb.appendHtmlConstant("<div style='display:inline;'>");
			sb.append(name);
		sb.appendHtmlConstant("</div>");

		sb.appendHtmlConstant("<div style='float:right; display:inline;'>");
			sb.appendHtmlConstant("<div class='inlineProductInfoItem' style='text-align:right; width:40px;'>");
			sb.append(co2value);
			sb.appendHtmlConstant("&nbsp;g*</div>");
			
			sb.appendHtmlConstant("<div class='inlineProductInfoItem'><img src='" + Util.getIngredientRatingBarUrl(productInfo.getCo2eValue()) + "' /></div>");
		
			if (productInfo.isInSeason())
				sb.appendHtmlConstant("<div class='inlineProductInfoItem'><img src='"+ Resources.INSTANCE.season().getURL() + "'  /></div>");
			
		sb.appendHtmlConstant("</div>");

		// SafeStyles styles =
		// SafeStylesUtils.forBorderStyle(BorderStyle.DOTTED);
		// SafeHtml rendered = imgTemplate.cell(styles, safeValue);

		// sb.append(rendered);
	}
    
    // future: override for mouseOver effect, keyboardselection effect...
    @Override
    public void onBrowserEvent(Cell.Context context, Element parent, FoodProductInfo value, NativeEvent event, ValueUpdater<FoodProductInfo> valueUpdater) {
    	super.onBrowserEvent(context, parent, value, event, valueUpdater);
 	    if ("click".equals(event.getType())) {
 	        EventTarget eventTarget = event.getEventTarget();
 	        if (!Element.is(eventTarget)) {
 	            return;
 	        }
 	        searchIngredients.addFoodProduct(value);
 	    }
    	 // Handle the click event.
         if ("onmoseover".equals(event.getType())) {
           // Ignore clicks that occur outside of the outermost element.
        
         }
    	 
    }
    
   
}
