package ch.eaternity.resources;

import com.google.gwt.resources.client.TextResource;
import com.github.gwtbootstrap.client.ui.resources.Resources;

public interface BootstrapResources extends Resources {
	
    @Source("css/bootstrap.min.css")
    TextResource bootstrapCss();
    
    @Source("js/bootstrap.min.js")
    TextResource bootstrapJs();

    @Source("css/bootstrap-responsive.min.css")
    TextResource bootstrapResponsiveCss();
}