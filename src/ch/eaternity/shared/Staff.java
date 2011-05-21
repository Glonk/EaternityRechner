package ch.eaternity.shared;


import java.io.Serializable;

import javax.persistence.Id;


public class Staff implements Serializable {

	/** 
	 * 
	 */
	private static final long serialVersionUID = 3172640409035191698L;
	
//    @PrimaryKey
//    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
//    @Extension(vendorName="datanucleus", key="gae.encoded-pk", value="true")
	
	@Id 
	public Long id;
   
	public String userName;
	

	public String userEmail;
	
	
	
// this is for the many-to-many relationship of kitchens and the staff
	
	public Long[] kitchensIds;
	
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
