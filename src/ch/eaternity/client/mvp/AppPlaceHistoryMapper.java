package ch.eaternity.client.mvp;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

import ch.eaternity.client.place.EaternityRechnerPlace;
import ch.eaternity.client.place.GoodbyePlace;
import ch.eaternity.client.place.HelloPlace;
import ch.eaternity.client.place.KlimaZmittagPlace;

/**
 * PlaceHistoryMapper interface is used to attach all places which the
 * PlaceHistoryHandler should be aware of. This is done via the @WithTokenizers
 * annotation or by extending PlaceHistoryMapperWithFactory and creating a
 * separate TokenizerFactory.
 */
@WithTokenizers( { HelloPlace.Tokenizer.class,EaternityRechnerPlace.Tokenizer.class, GoodbyePlace.Tokenizer.class, KlimaZmittagPlace.Tokenizer.class })
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {
}
