package org.kbssm.synapsys;

import org.kbssm.synapsys.usb.USBConnectReceiver;
import org.kbssm.synapsys.usb.USBConnectReceiver.OnUsbConnectionStateListener;
import org.kbssm.synapsys.usb.USBConnectionFragment;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;

/**
 * The main activity that starts "Synapsys" operations.
 * 
 * @author Yeonho.Kim
 *
 */
public class MainActivity extends Activity implements NavigationDrawerCallbacks {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
										.findFragmentById(R.id.main_navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp( R.id.main_navigation_drawer,
										(DrawerLayout) findViewById(R.id.main_drawer_layout) );
		
		
		init();
	}

	void init () {
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		USBConnectReceiver receiver = USBConnectReceiver.getInstance();
		if (receiver != null) {
			receiver.setOnUsbConnectionStateListener(new OnUsbConnectionStateListener() {
				
				@Override
				public void onDisconnected() {
					mNavigationDrawerFragment.performItemClick(0);
				}
				
				@Override
				public void onConnected() {
					mNavigationDrawerFragment.performItemClick(1);
				}
			});
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		getFragmentManager().beginTransaction()
							.replace(	R.id.main_container,
										(Fragment) ContentFragmentHolder.getInstance(position))
							.commit();
	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 0:
			mTitle = getString(R.string.app_name);
			break;
		case 1:
			mTitle = getString(R.string.title_usb_section);
			break;
		case 2:
			mTitle = getString(R.string.title_wireless_section);
			break;
		case 3:
			mTitle = getString(R.string.title_setting_section);
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		
		}
		
		return super.onOptionsItemSelected(item);
	}

	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	public NavigationDrawerFragment getNavigationDrawerFragment() {
		return mNavigationDrawerFragment;
	}
	
}
