package org.kbssm.synapsys;

import org.kbssm.synapsys.usb.USBConnectReceiver;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
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
		mNavigationDrawerFragment.setUp(
				R.id.main_navigation_drawer,
				(DrawerLayout) findViewById(R.id.main_drawer_layout) );
		
		// Register USB EventReceiver.
		USBConnectReceiver.register(this);
		
		init();
	}


	private static final String ACTION_USB_PERMISSION =
		    "com.android.example.USB_PERMISSION";
	private final BroadcastReceiver mUsbReceiver =new BroadcastReceiver(){
	    public void onReceive(Context context,Intent intent){
	        String action = intent.getAction();
	        if(ACTION_USB_PERMISSION.equals(action)){
	            synchronized(this){
	                UsbAccessory accessory =(UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
	                if(intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED,false)){
	                    if(accessory !=null){
	                        //액세서와 통신하기 위한 설정 코드
	                    }
	                }
	                else{
	                    Log.d("","permission denied for accessory "+ accessory);
	                }
	            }
	        }
	    }
	};
	void init () {
		UsbManager mUsbManager =(UsbManager) getSystemService(Context.USB_SERVICE);
	
		PendingIntent mPermissionIntent = PendingIntent.getBroadcast(this,0,new Intent(ACTION_USB_PERMISSION),0);
		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		registerReceiver(mUsbReceiver, filter);
		
		UsbAccessory accessory =(UsbAccessory)getIntent().getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
		mUsbManager.requestPermission(accessory, mPermissionIntent);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		// Unregister USB EventReceiver.
		USBConnectReceiver.unregister(this);
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

}
