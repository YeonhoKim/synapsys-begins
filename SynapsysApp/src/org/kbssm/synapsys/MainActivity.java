package org.kbssm.synapsys;

import org.kbssm.synapsys.usb.UsbConnectReceiver;
import org.kbssm.synapsys.usb.UsbConnectReceiver.OnUsbConnectionStateListener;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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
	
	private AlertDialog mUSBConnectDialog;

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
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		// USB 연결 이벤트 발생시, 처리할 로직 Interface를 등록한다.
		UsbConnectReceiver receiver = UsbConnectReceiver.getInstance();
		if (receiver != null) {
			receiver.setOnUsbConnectionStateListener(new OnUsbConnectionStateListener() {
				@Override
				public void onDisconnected() {
					Toast.makeText(getBaseContext(), R.string.USBConnectDialog_detached_usb, Toast.LENGTH_SHORT).show();
					mNavigationDrawerFragment.performItemClick(0);
				}
				
				@Override
				public void onConnected() {
					mUSBConnectDialog.show();
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
