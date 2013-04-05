package ch.eaternity.client.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Start / Stop spinning process (waiting time of ajax calls)
 * @author aurelianjaggi
 *
 */
public class SpinnerEvent extends GwtEvent<SpinnerEventHandler> {
  public static Type<SpinnerEventHandler> TYPE = new Type<SpinnerEventHandler>();
  
  public Boolean spinning;
  
  public String action;
  
  public SpinnerEvent(Boolean spinning) {
	  this.spinning = spinning;
  }
  
  public SpinnerEvent(Boolean spinning, String action) {
	  this.spinning = spinning;
	  this.action = action;
  }
  
  @Override
  public Type<SpinnerEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(SpinnerEventHandler handler) {
    handler.onEvent(this);
  }
}
