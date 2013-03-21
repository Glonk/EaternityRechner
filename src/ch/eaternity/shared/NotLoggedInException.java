package ch.eaternity.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class NotLoggedInException extends Exception implements IsSerializable {

  /**
	 * 
	 */
	private static final long serialVersionUID = 234987662346234L;

public NotLoggedInException() {
    super();
  }

  public NotLoggedInException(String message) {
    super(message);
  }

}