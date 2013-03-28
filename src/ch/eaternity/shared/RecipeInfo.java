package ch.eaternity.shared;



import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

import com.googlecode.objectify.annotation.Id;

public class RecipeInfo implements IsSerializable {
	
	private static final long serialVersionUID = -28298734672427438L;
	
	@Id private Long id;
	
	private String title;
	
	private String subTitle;
	
	private Double co2eValue;
	
	private UploadedImage image;

	private RecipeInfo() {}
	
	public RecipeInfo(Recipe recipe) {
		this.id = recipe.getId();
		this.title = recipe.getTitle();
		this.subTitle = recipe.getSubTitle();
		this.co2eValue = recipe.getCachedCO2Value();
		this.image = recipe.getImage();
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public Double getCo2eValue() {
		return co2eValue;
	}

	public void setCo2eValue(Double co2eValue) {
		this.co2eValue = co2eValue;
	}

	public UploadedImage getImage() {
		return image;
	}

	public void setImage(UploadedImage image) {
		this.image = image;
	}
	
}
