package ch.eaternity.shared;


import java.io.Serializable;
import java.util.List;

import javax.persistence.Id;


public class Staff implements Serializable {

	/** 
	 * Exists because adding people to kitchens is not related to the google accounts yet necessarly...
	 * 
	 * LoginInfo with UserEmail is later in timeline
	 */
	private static final long serialVersionUID = 3172640409035191698L;
	
	// this object should be matched with the login-info, don't you think...
	
	@Id 
	public Long id;
   
	public String userName;
	public String userEmail;
	
	// this is for the many-to-many relationship of kitchens and the staff
	public List<Long> kitchensIds;
	
	public Staff() {

	}
	
	public Staff(String Name) {
		this.userName = Name;
	}

	public Staff(String Name,String email) {
		this.userName = Name;
		this.userEmail = email;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}


	

	  

}
