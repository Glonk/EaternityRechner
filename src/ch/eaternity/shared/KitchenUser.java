package ch.eaternity.shared;

import java.io.Serializable;

import com.googlecode.objectify.annotation.Id;

public class KitchenUser implements Serializable {
	
	private static final long serialVersionUID = 8798746252345138L;

	@Id private Long id;
	
	private String nickname;
	private String emailAddress;
	
	public KitchenUser() {}

	public KitchenUser(String name, String mail) {
		this.nickname = name;
		this.emailAddress = mail;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	/*
	@Override
	public boolean equals(KitchenUser other) {
		return this.id.equals(other.)
	}
	*/
}
