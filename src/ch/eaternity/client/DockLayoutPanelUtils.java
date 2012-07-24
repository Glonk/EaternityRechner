package ch.eaternity.client;

import com.google.gwt.dom.client.Style.Unit; 
import com.google.gwt.user.client.ui.DockLayoutPanel; 
import com.google.gwt.user.client.ui.Widget; 
public class DockLayoutPanelUtils extends DockLayoutPanel { 
        private DockLayoutPanelUtils() { 
                super(Unit.PX); 
        } 
        public Double getWidgetSize(Widget widget) { 
                return ((LayoutData) widget.getLayoutData()).size; 
        } 
        public void setWidgetSize(Widget widget, double size) { 
                ((LayoutData) widget.getLayoutData()).size = size; 
        } 
} 