package org.kbssm.synapsys;

import org.kbssm.synapse.SynapseManager;
import org.kbssm.synapsys.global.SynapsysApplication;
import org.kbssm.synapsys.global.SynapsysListener;
import org.kbssm.synapsys.usb.UsbConnectReceiver;
import org.kbssm.synapsys.usb.UsbConnectReceiver.OnUsbConnectionStateListener;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * The main activity that starts "Synapsys" operations.
 * 
 * @author Yeonho.Kim
 *
 */
public class MainActivity extends Activity implements NavigationDrawerCallbacks {

	/******************************************************************
 		FIELDS
	 ******************************************************************/
	/**
	 * 
	 */
	private SynapsysApplication mApplication;
	
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

	private AlertDialog mUSBConnectDialog;
	
	private OnUsbConnectionStateListener mUsbConnectionStateListener;
	
	private SynapsysListener mSynapseListener;
	


	/******************************************************************
		LIFECYCLE
	 ******************************************************************/
	/** */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_main);
		
		mApplication = (SynapsysApplication) getApplication();
		
		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
										.findFragmentById(R.id.main_navigation_drawer);
		
		mNavigationDrawerFragment.setUp( R.id.main_navigation_drawer,
										(DrawerLayout) findViewById(R.id.main_drawer_layout) );
		
		init();
	}

	void init () {
		mTitle = getTitle();
		
		DialogInterface.OnClickListener mOnClickListener = new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch(which) {
				case DialogInterface.BUTTON_POSITIVE:
					mNavigationDrawerFragment.performItemClick(1);
					break;
				}
			}
		};
		
		mUSBConnectDialog = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
							.setTitle(R.string.USBConnectDialog_forward_dialog_title)
							.setMessage(R.string.USBConnectDialog_forward_dialog_content)
							.setPositiveButton(R.string.confirm, mOnClickListener)
							.setNegativeButton(R.string.cancel, mOnClickListener)
							.create();
		
		mUsbConnectionStateListener = new OnUsbConnectionStateListener() {
			@Override
			public void onDisconnected() {
				Toast.makeText(getBaseContext(), R.string.USBConnectDialog_detached_usb, Toast.LENGTH_SHORT).show();
				mNavigationDrawerFragment.performItemClick(0);
			}
			
			@Override
			public void onConnected(boolean rndisEnabled) {
				if (mNavigationDrawerFragment.getCurrentTabPosition() == 1) {
					ContentFragmentHolder.getInstance(1).refresh();
					
				} else if (mUSBConnectDialog != null)
					mUSBConnectDialog.show();
				
			}
		};
		
		mSynapseListener = new SynapsysListener(this) {
			
			@Override
			public void onDetectingStateChanged(boolean enabled) {
				super.onDetectingStateChanged(enabled);
	
				setProgressBarIndeterminateVisibility(enabled);
			}
			
			@Override
			public void onConnectedStateDetected(String address) {
				super.onConnectedStateDetected(address);
				
				ContentFragmentHolder.getInstance(mNavigationDrawerFragment.getCurrentTabPosition()).refresh();
			}
			
			@Override
			public void onDisconnectedStateDetected(String address) {
				super.onDisconnectedStateDetected(address);

				ContentFragmentHolder.getInstance(mNavigationDrawerFragment.getCurrentTabPosition()).refresh();
			}
		};
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.d("MainActivity", "onResume");

		UsbConnectReceiver receiver = UsbConnectReceiver.getInstance();
		if (receiver != null) 
			receiver.setOnUsbConnectionStateListener(mUsbConnectionStateListener);
		
		SynapseManager mManager = mApplication.getSynapseManager();
		mManager.setSynapsysListener(mSynapseListener);
		
		if (!mManager.isOnUsbTethering())
			mManager.setUsbTethering(true);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.d("MainActivity", "onPause");
		

		UsbConnectReceiver receiver = UsbConnectReceiver.getInstance();
		if (receiver != null) 
			receiver.setOnUsbConnectionStateListener(null);
		
		mApplication.getSynapseManager().setSynapsysListener(null);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d("MainActivity", "onDestroy");
		
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		getFragmentManager().beginTransaction()
							.replace(	R.id.main_container,
										(Fragment) ContentFragmentHolder.getInstance(position))
							.commit();
	}


	/******************************************************************
		METHODS
	 ******************************************************************/
	/** */
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
		case R.id.menu_refresh:
			mApplication.getSynapseManager().findConnectedAddress(true);
			break;
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
