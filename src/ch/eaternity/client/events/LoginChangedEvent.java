package ch.eaternity.client.events;

import ch.eaternity.shared.LoginInfo;

import com.google.gwt.event.shared.GwtEvent;

public class LoginChangedEvent extends GwtEvent<LoginChangedEventHandler> {
  public static Type<LoginChangedEventHandler> TYPE = new Type<LoginChangedEventHandler>();
  
  public LoginInfo loginInfo;
  
  public LoginChangedEvent(LoginInfo loginInfo) {
	  this.loginInfo = loginInfo;
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
