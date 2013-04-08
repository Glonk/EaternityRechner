package ch.eaternity.client;

import ch.eaternity.client.ui.GoodbyeView;
import ch.eaternity.client.ui.GoodbyeViewImpl;
import ch.eaternity.client.ui.KlimaZmittagView;
import ch.eaternity.client.ui.KlimaZmittagViewImpl;
import ch.eaternity.client.ui.LoginView;
import ch.eaternity.client.ui.MenuPreviewView;
import ch.eaternity.client.ui.RechnerView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.PlaceController;

public class ClientFactoryImpl implements ClientFactory
{
	private final EventBus eventBus = new SimpleEventBus();
	private final PlaceController placeController = new PlaceController(eventBus);
	private final LoginView helloView = new LoginView();
	private final KlimaZmittagView klimaZmittagView = new KlimaZmittagViewImpl();
	private final RechnerView RechnerView = new RechnerView();
	private final GoodbyeView goodbyeView = new GoodbyeViewImpl();
	private final MenuPreviewView menuPreviewView = new MenuPreviewView();
	private final DataController dao = new DataController();
	private final DataServiceAsync dataRpcService = GWT.create(DataService.class);
	

	@Override
	public EventBus getEventBus()
	{
		return eventBus;
	}

	@Override
	public LoginView getLoginView()
	{
		return helloView;
	}
	
	@Override
	public RechnerView getRechnerView()
	{
		return RechnerView;
	}

	@Override
	public PlaceController getPlaceController()
	{
		return placeController;
	}

	@Override
	public GoodbyeView getGoodbyeView()
	{
		return goodbyeView;
	}
	
	@Override
	public MenuPreviewView getMenuPreviewView()
	{
		return menuPreviewView;
	}

	@Override
	public DataServiceAsync getDataServiceRPC() {
		return dataRpcService;
	}

	@Override
	public KlimaZmittagView getKlimaZmittagView() {
		return klimaZmittagView;
	}

	@Override
	public DataController getDataController() {
		return dao;
	}

}
