package ch.eaternity.client.ui.widgets;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class SimpleWidgetPanel extends LayoutPanel implements AcceptsOneWidget {
 
     private Widget widget;
 
     @Override
     public void setWidget(IsWidget w) {
         setOneWidget(asWidgetOrNull(w));
     }
 
     private void setOneWidget(Widget w) {
         // validate
         if (w == widget) {
             return;
         }
 
         // remove the old widget
         if (widget != null) {
             super.remove(widget);
         }
 
         // logical attach
         widget = w;
 
         if (w != null) {
             super.add(w);
         }
     }
 }