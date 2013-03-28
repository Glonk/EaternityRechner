package ch.eaternity.client.events;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.GwtEvent;

public class AlertEvent extends GwtEvent<AlertEventHandler> {
  public static Type<AlertEventHandler> TYPE = new Type<AlertEventHandler>();
  
  public enum Destination {
	  VIEW, EDIT, BOTH
  }
  public Alert alert;
  
  public Destination destination;
  
  public Integer timeDisplayed = null;;
  
  public AlertEvent(String message, AlertType type, Destination dest) {
	  alert = new Alert(message);
	  alert.setAnimation(true);
	  alert.setType(type);
	  alert.setClose(true);
	  destination = dest;
  }
  public AlertEvent(String message, AlertType type, Destination dest, int time) {
	  this(message, type, dest);
	  timeDisplayed = time;
  }
  
  @Override
  public Type<AlertEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(AlertEventHandler handler) {
    handler.onEvent(this);
  }
}
