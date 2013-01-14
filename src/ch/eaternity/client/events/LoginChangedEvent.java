package ch.eaternity.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class LoginChangedEvent extends GwtEvent<LoginChangedEventHandler> {
  public static Type<LoginChangedEventHandler> TYPE = new Type<LoginChangedEventHandler>();
  
  public Long id;
  
  public LoginChangedEvent(Long id) {
	  this.id = id;
  }
  
  @Override
  public Type<LoginChangedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(LoginChangedEventHandler handler) {
    handler.onEvent(this);
  }
}
