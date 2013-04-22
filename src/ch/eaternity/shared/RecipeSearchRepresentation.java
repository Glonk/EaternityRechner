package ch.eaternity.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ch.eaternity.shared.Util.RecipeScope;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This Object represents a search with arbitrary amount of filters performed
 * on a specific recipe scope. 
 * @author aurelianjaggi
 *
 */
public class RecipeSearchRepresentation implements Serializable {

	private static final long serialVersionUID = -12341125063462438L;
	
	// ---------------------- What to search for ---------------------
	
	private List<Long> productIds = new ArrayList<Long>();
		
	/**
	 * search in title and subtitle and probably comments?
	 */
	private String text = new String();
	
	// Category
	
	// Collections
	
	// ---------------------- Where to search in ---------------------
	
	private RecipeScope scope;
	
	/**
	 * usually just one, maybee more
	 */
	private Long kitchenId;

	// ---------------------- Public Methods  ---------------------
	
	public RecipeSearchRepresentation() {
		scope = RecipeScope.PUBLIC;			
	}
	
	public RecipeSearchRepresentation(String text, RecipeScope scope) {
		this();
		this.text = text;
		this.scope = scope;
	}
	
	public List<Long> getProductIds() {
		return productIds;
	}

	public void setProductIds(List<Long> productIds) {
		this.productIds = productIds;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public RecipeScope getScope() {
		return scope;
	}

	public void setScope(RecipeScope scope) {
		this.scope = scope;
	}

	public Long getKitchenId() {
		return kitchenId;
	}

	public void setKitchenId(Long kitchenId) {
		this.kitchenId = kitchenId;
	}
	
	
}
