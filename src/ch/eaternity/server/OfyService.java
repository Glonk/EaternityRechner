package ch.eaternity.server;

import ch.eaternity.shared.Commitment;
import ch.eaternity.shared.FoodProduct;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.Kitchen;
import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.UserInfo;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

public class OfyService {
    static {
    	factory().register(FoodProduct.class);
    	factory().register(Ingredient.class);
    	factory().register(Recipe.class);
    	factory().register(ImageBlob.class);
    	factory().register(Kitchen.class);
    	factory().register(UserInfo.class);
    	factory().register(Commitment.class);
    }

    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}