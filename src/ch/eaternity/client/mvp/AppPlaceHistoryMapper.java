package ch.eaternity.client.mvp;

import ch.eaternity.client.place.GoodbyePlace;
import ch.eaternity.client.place.LoginPlace;
import ch.eaternity.client.place.KlimaZmittagPlace;
import ch.eaternity.client.place.RechnerRecipeEditPlace;
import ch.eaternity.client.place.RechnerRecipeViewPlace;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

/**
 * PlaceHistoryMapper interface is used to attach all places which the
 * PlaceHistoryHandler should be aware of. This is done via the @WithTokenizers
 * annotation or by extending PlaceHistoryMapperWithFactory and creating a
 * separate TokenizerFactory.
 */
@WithTokenizers( { LoginPlace.Tokenizer.class,RechnerRecipeViewPlace.Tokenizer.class,RechnerRecipeEditPlace.Tokenizer.class, GoodbyePlace.Tokenizer.class, KlimaZmittagPlace.Tokenizer.class })
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {
}
