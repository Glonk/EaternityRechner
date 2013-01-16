package ch.eaternity.client.ui.widgets;


import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ConfirmDialog extends DialogBox{
	interface Binder extends UiBinder<Widget, ConfirmDialog> { }
	private static final Binder binder = GWT.create(Binder.class);

	@UiField public Button noButton;
	@UiField public Button yesButton;
	@UiField public Label statusLabel;

	public ConfirmDialog(String title) {

		// Use this opportunity to set the dialog's caption.
		setText(title);
		setWidget(binder.createAndBindUi(this));
		
		setAnimationEnabled(true);
		setGlassEnabled(true);
	}



	@Override
	protected void onPreviewNativeEvent(NativePreviewEvent preview) {
		super.onPreviewNativeEvent(preview);

		NativeEvent evt = preview.getNativeEvent();
		if (evt.getType().equals("keydown")) {
			// Use the popup's key preview hooks to close the dialog when either
			// enter or escape is pressed.
			switch (evt.getKeyCode()) {
			case KeyCodes.KEY_ESCAPE:
				hide();
				break;
			}
		}
	}
	
	// REFACTOR: add Click Handlers here, not in Methods.... 
}

