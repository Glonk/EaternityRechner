package ch.eaternity.client.ui.cells;

//import java.text.DecimalFormat;

import ch.eaternity.shared.FoodProductInfo;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

/**
 * A custom {@link Cell} used to render a FoodProductInfo
 */
public class ProductCell extends AbstractCell<FoodProductInfo> {

	
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

    /**
     * Create a singleton instance of the templates used to render the cell.
     */
   // private static ImgTemplate imgTemplate = GWT.create(ImgTemplate.class);
    
    private static ProductTemplate productTemplate = GWT.create(ProductTemplate.class);

    @Override
    public void render(Cell.Context context, FoodProductInfo productInfo, SafeHtmlBuilder sb) {
	  /*
	   * Always do a null check on the value. Cell widgets can pass null to
	   * cells if the underlying data contains a null, or if the data arrives
	   * out of order.
	   */
	  if (productInfo == null) {
	    return;
	  }
	  
	 // DecimalFormat formatter = new DecimalFormat("##");
	
	  SafeHtml name = SafeHtmlUtils.fromTrustedString(productInfo.getName());
	  SafeHtml co2value = SafeHtmlUtils.fromTrustedString(productInfo.getCo2eValue().toString());
	
	  SafeHtml rendered = productTemplate.cell(name, co2value);
	  
	  //SafeStyles styles = SafeStylesUtils.forBorderStyle(BorderStyle.DOTTED);
	  //SafeHtml rendered = imgTemplate.cell(styles, safeValue);
	  
	  sb.append(rendered);
    }
   
}
