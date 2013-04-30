package ch.eaternity.client;


import ch.eaternity.client.mvp.AppActivityMapper;
import ch.eaternity.client.mvp.AppPlaceHistoryMapper;
import ch.eaternity.client.place.RechnerRecipeEditPlace;
import ch.eaternity.client.place.RechnerRecipeViewPlace;
import ch.eaternity.client.resources.Resources;
import ch.eaternity.client.ui.widgets.SimpleWidgetPanel;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.web.bindery.event.shared.EventBus;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class EaternityRechner implements EntryPoint {


	private Place defaultEntryPlace = new RechnerRecipeViewPlace("PUBLIC");
	private SimpleWidgetPanel appWidget = new SimpleWidgetPanel();
	interface GlobalResources extends ClientBundle {
			@NotStrict
			@Source("global.css")
			CssResource css();
		}
	
	   /**
	   * This field gets compiled out when <code>log_level=OFF</code>, or any <code>log_level</code>
	   * higher than <code>DEBUG</code>.
	   */
	  private long startTimeMillis;

	  /**
	   * Note, we defer all application initialization code to {@link #onModuleLoad2()} so that the
	   * UncaughtExceptionHandler can catch any unexpected exceptions.
	   */
	  @Override
	  public void onModuleLoad() {
	    /*
	     * Install an UncaughtExceptionHandler which will produce <code>FATAL</code> log messages
	     */
	    Log.setUncaughtExceptionHandler();

	    // use deferred command to catch initialization exceptions in onModuleLoad2
	    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
	      @Override
	      public void execute() {
	        onModuleLoad2();
	      }
	    });
	  }

	  /**
	   * Deferred initialization method, used by {@link #onModuleLoad()}.
	   */
	  private void onModuleLoad2() {
	    /*
	     * Use a <code>if (Log.isDebugEnabled()) {...}</code> guard to ensure that
	     * <code>System.currentTimeMillis()</code> is compiled out when <code>log_level=OFF</code>, or
	     * any <code>log_level</code> higher than <code>DEBUG</code>.
	     */
	    if (Log.isDebugEnabled()) {
	      startTimeMillis = System.currentTimeMillis();
	    }
	    
	    /*
	     * Again, we need a guard here, otherwise <code>log_level=OFF</code> would still produce the
	     * following useless JavaScript: <pre> var durationSeconds, endTimeMillis; endTimeMillis =
	     * currentTimeMillis_0(); durationSeconds = (endTimeMillis - this$static.startTimeMillis) /
	     * 1000.0; </pre>
	     */
	    if (Log.isDebugEnabled()) {
	      long endTimeMillis = System.currentTimeMillis();
	      float durationSeconds = (endTimeMillis - startTimeMillis) / 1000F;
	      Log.debug("Duration: " + durationSeconds + " seconds");
	    }

		ClientFactory clientFactory = GWT.create(ClientFactory.class);
		EventBus eventBus = clientFactory.getEventBus();
		PlaceController placeController = clientFactory.getPlaceController();
		
		// Inject global styles.
		Resources.INSTANCE.globalCss().ensureInjected();
		GWT.<GlobalResources>create(GlobalResources.class).css().ensureInjected();
		
		// Start ActivityManager for the main widget with our ActivityMapper
		ActivityMapper activityMapper = new AppActivityMapper(clientFactory);
		ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);
		activityManager.setDisplay(appWidget);

		// Start PlaceHistoryHandler with our PlaceHistoryMapper
		AppPlaceHistoryMapper historyMapper= GWT.create(AppPlaceHistoryMapper.class);
		PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
		historyHandler.register(placeController, eventBus, defaultEntryPlace);

		RootLayoutPanel.get().add(appWidget);
		// Goes to place represented on URL or default place
		historyHandler.handleCurrentHistory();

	}

}


