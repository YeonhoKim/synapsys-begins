package org.kbssm.synapsys;

import org.kbssm.synapsys.usb.UsbConnectionFragment;
import org.kbssm.synapsys.wireless.WirelessConnectionFragment;


/**
 * @author Yeonho.Kim
 *
 */
public class ContentFragmentHolder {

	private static final NavigationFragmentCallbacks[] sContentFragments;
	static {
		HomeFragment home = new HomeFragment();
		UsbConnectionFragment usb = new UsbConnectionFragment();
		WirelessConnectionFragment wireless = new WirelessConnectionFragment();
		SettingFragment setting = new SettingFragment();
		
		sContentFragments = new NavigationFragmentCallbacks[]{
			home,
			usb,
			wireless,
			setting
		};
	}
	
	public static NavigationFragmentCallbacks getInstance(int position) {
		return sContentFragments[position];
	}
}
