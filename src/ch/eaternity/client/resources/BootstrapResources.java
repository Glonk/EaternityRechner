package ch.eaternity.client.resources;

import com.google.gwt.resources.client.TextResource;
import com.github.gwtbootstrap.client.ui.resources.Resources;

public interface BootstrapResources extends Resources {
	
    @Source("bootstrap.min.css")
    TextResource bootstrapCss();

    
    @Source("bootstrap.min.js")
    TextResource bootstrapJs();
    /*
    @Source("bootstrap-responsive.min.css")
    TextResource bootstrapResponsiveCss();*/
}