package ch.eaternity.client;

import ch.eaternity.client.ui.GoodbyeView;
import ch.eaternity.client.ui.GoodbyeViewImpl;
import ch.eaternity.client.ui.HelloView;
import ch.eaternity.client.ui.HelloViewImpl;
import ch.eaternity.client.ui.KlimaZmittagView;
import ch.eaternity.client.ui.KlimaZmittagViewImpl;
import ch.eaternity.client.ui.MenuPreviewView;
import ch.eaternity.client.ui.RechnerView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.PlaceController;

public class ClientFactoryImpl implements ClientFactory
{
	private static final EventBus eventBus = new SimpleEventBus();
	private static final PlaceController placeController = new PlaceController(eventBus);
	private static final HelloView helloView = new HelloViewImpl();
	private static final KlimaZmittagView klimaZmittagView = new KlimaZmittagViewImpl();
	private static final RechnerView RechnerView = new RechnerView();
	private static final GoodbyeView goodbyeView = new GoodbyeViewImpl();
	private static final MenuPreviewView menuPreviewView = new MenuPreviewView();
	private final DataController dao = new DataController(this);
	private final DataServiceAsync dataRpcService = GWT.create(DataService.class);
	

	@Override
	public EventBus getEventBus()
	{
		return eventBus;
	}

	@Override
	public HelloView getHelloView()
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
