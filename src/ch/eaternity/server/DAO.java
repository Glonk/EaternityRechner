package ch.eaternity.server;


import java.util.ArrayList;

import ch.eaternity.shared.Condition;
import ch.eaternity.shared.Extraction;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.MoTransportation;
import ch.eaternity.shared.ProductLabel;

import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;
import com.googlecode.objectify.helper.DAOBase;

public class DAO extends DAOBase
{
    static {
        ObjectifyService.register(Ingredient.class);
        ObjectifyService.register(Extraction.class);
        ObjectifyService.register(Condition.class);
        ObjectifyService.register(MoTransportation.class);
        ObjectifyService.register(ProductLabel.class);
    }

    /** Your DAO can have your own useful methods */
    public Long updateOrCreateIngredient(Ingredient ingredient)
    {
    	Ingredient found = ofy().find(Ingredient.class, ingredient.getId());
    	
        if (found == null)
        	ofy().put(ingredient);
        else
        	ofy().delete(found);
        	ofy().put(ingredient);
        
        return ingredient.getId();
    }
    
    public ArrayList<Ingredient> getAllIngredients()
    {
//        Objectify ofy = ObjectifyService.begin();
        Query<Ingredient> found = ofy().query(Ingredient.class);
        
        
        ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>(found.countAll());

        QueryResultIterator<Ingredient> iterator = found.iterator();
        while (iterator.hasNext()) {
        	Ingredient ingredient = iterator.next();
        	ingredients.add(ingredient);
        }
        
        return ingredients;
    }
    

    public Boolean CreateIngredients(ArrayList<Ingredient> ingredients)
    {
        ofy().put(ingredients);
        return true;
    }
    
	public String getAllIngredientsXml() {
		ArrayList<Ingredient> ingredients = getAllIngredients();
		return null;

	}

    
}