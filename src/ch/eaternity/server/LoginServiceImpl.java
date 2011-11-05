package ch.eaternity.server;

import ch.eaternity.client.LoginService;
import ch.eaternity.shared.LoginInfo;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.NotFoundException;

public class LoginServiceImpl extends RemoteServiceServlet implements
    LoginService {

  /**
	 * 
	 */
	private static final long serialVersionUID = 844419227883060335L;

public LoginInfo login(String requestUri) {
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    DAO dao = new DAO();
    LoginInfo loginInfo = new LoginInfo();

    if (user != null) {
    	
	    try {
	    	loginInfo = dao.ofy().get(LoginInfo.class, user.getUserId());
	    } catch (NotFoundException e) {
	    	
	    }

      loginInfo.setId(user.getUserId());
      loginInfo.setLoggedIn(true);
      loginInfo.setEmailAddress(user.getEmail());
      loginInfo.setNickname(user.getNickname());
      loginInfo.setLogoutUrl(userService.createLogoutURL(requestUri));
      loginInfo.setAdmin(userService.isUserAdmin());
      
     
      dao.ofy().put(loginInfo);
      
    } else {
      loginInfo.setLoggedIn(false);
      loginInfo.setLoginUrl(userService.createLoginURL(requestUri));
    }
    return loginInfo;
  }

}