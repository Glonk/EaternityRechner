package ch.eaternity.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import ch.eaternity.client.events.GalleryUpdatedEvent;
import ch.eaternity.client.events.GalleryUpdatedEventHandler;
import ch.eaternity.client.RezeptView;
import ch.eaternity.client.UserImageService;
import ch.eaternity.client.UserImageServiceAsync;
import ch.eaternity.shared.LoginInfo;
import ch.eaternity.shared.Rezept;
import ch.eaternity.shared.UploadedImage;

public class UploadPhoto extends Composite implements HasHandlers {

	private static UploadPhotoUiBinder uiBinder = GWT
			.create(UploadPhotoUiBinder.class);

	UserImageServiceAsync userImageService = GWT.create(UserImageService.class);

	private HandlerManager handlerManager;

	interface UploadPhotoUiBinder extends UiBinder<Widget, UploadPhoto> {
	}

	@UiField
	Button uploadButton;

	@UiField
	FormPanel uploadForm;

	@UiField
	FileUpload uploadField;

	LoginInfo loginInfo;
	
	Rezept rezept;

	public UploadPhoto(final LoginInfo loginInfo,final RezeptView rezeptView) {
		handlerManager = new HandlerManager(this);
		
		this.rezept = rezept;
		this.loginInfo = loginInfo;

		initWidget(uiBinder.createAndBindUi(this));

		uploadButton.setText("Upload");
		uploadButton.setText("Loading...");
		uploadButton.setEnabled(false);

		uploadField.setName("image");

		startNewBlobstoreSession();

		uploadForm
				.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {

					@Override
					public void onSubmitComplete(SubmitCompleteEvent event) {
						uploadForm.reset();
						startNewBlobstoreSession();

						String key = event.getResults();

						userImageService.get(key,
								new AsyncCallback<UploadedImage>() {

									@Override
									public void onFailure(Throwable caught) {
										// TODO Auto-generated method stub

									}

									@Override
									public void onSuccess(UploadedImage result) {
										
										rezeptView.getRezept().image = result;
									    
									    	Image showImage = new Image();
									    	showImage.setUrl(result.getServingUrl()+"=s160-c");
//									    	setHTML("<img src='" +GWT.getModuleBaseURL()+ rezept.image.getServingUrl() + "' />"+rezept.getCookInstruction());
//									    	rezeptView.imageUploaderHP.add(showImage);
									    	
									    	rezeptView.menuDecoInfo.add(showImage);
									    
										
										ImageOverlay overlay = new ImageOverlay(
												result, loginInfo);
										GalleryUpdatedEvent event = new GalleryUpdatedEvent();
										fireEvent(event);

										// TODO: Add something here that says,
										// hey, upload succeeded

										final PopupPanel imagePopup = new PopupPanel(
												true);
										imagePopup.setAnimationEnabled(true);
										imagePopup.setWidget(overlay);
//										imagePopup.setGlassEnabled(true);
										imagePopup.setAutoHideEnabled(true);

										imagePopup.center();
										imagePopup.setPopupPosition(10, 10);

									}
								});

					}
				});
	}

	private void startNewBlobstoreSession() {
		userImageService.getBlobstoreUploadUrl(new AsyncCallback<String>() {

			@Override
			public void onSuccess(String result) {
				uploadForm.setAction(result);
				uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
				uploadForm.setMethod(FormPanel.METHOD_POST);

				uploadButton.setText("Upload");
				uploadButton.setEnabled(true);

			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				GWT.log("error: ", caught);

			}
		});
	}

	@UiHandler("uploadButton")
	void onSubmit(ClickEvent e) {
		uploadForm.submit();
	}

	@Override
	public void fireEvent(GwtEvent<?> event) {
		handlerManager.fireEvent(event);
	}

	public HandlerRegistration addGalleryUpdatedEventHandler(
			GalleryUpdatedEventHandler handler) {
		return handlerManager.addHandler(GalleryUpdatedEvent.TYPE, handler);
	}
}
