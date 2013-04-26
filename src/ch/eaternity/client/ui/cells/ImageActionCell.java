package ch.eaternity.client.ui.cells;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;
import static com.google.gwt.dom.client.BrowserEvents.KEYDOWN;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.ImageResourceRenderer;

/**
 * A Cell which displays an image and acts upon user clicks via a delegate
 * @author aurelian jaggi
 *
 * @param <C> The object which will be acted upon
 */
public class ImageActionCell<C> extends AbstractCell<C> {

	private static ImageResourceRenderer renderer;

	private final ImageResource imageResource;
	
	private final Delegate<C> delegate;
	
	/**
	 * The delegate that will handle events from the cell.
	 * 
	 * @param <T>
	 *            the type that this delegate acts on
	 */
	public static interface Delegate<T> {
		/**
		 * Perform the desired action on the given object.
		 * 
		 * @param object
		 *            the object to be acted upon
		 */
		void execute(T object);
	}
	

	/**
	 * Construct a new ImageActionCell.
	 */
	public ImageActionCell(ImageResource imageResource, Delegate<C> delegate) {
		super(CLICK);
	    this.delegate = delegate;
		if (renderer == null) {
			renderer = new ImageResourceRenderer();
		}
		this.imageResource = imageResource;
		
	}

	@Override
	public void render(Context context, C value, SafeHtmlBuilder sb) {
		sb.append(renderer.render(imageResource));
	}

	@Override
	public void onBrowserEvent(Context context, Element parent, C value,
			NativeEvent event, ValueUpdater<C> valueUpdater) {
		super.onBrowserEvent(context, parent, value, event, valueUpdater);
		if (CLICK.equals(event.getType())) {
			EventTarget eventTarget = event.getEventTarget();
			if (!Element.is(eventTarget)) {
				return;
			}
			if (parent.getFirstChildElement().isOrHasChild(
					Element.as(eventTarget))) {
				delegate.execute(value);
			}
		}
	}

}
