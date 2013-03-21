package ch.eaternity.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import com.googlecode.objectify.annotation.Id;

public class RecipeInfo implements IsSerializable {
	
	private static final long serialVersionUID = -28298734672427438L;
	
	@Id private Long id;

	private Long recipeId;
	
	private String title;
	
	private String subTitle;
	
	private Double co2eValue;
	
	private UploadedImage image;
	
}
