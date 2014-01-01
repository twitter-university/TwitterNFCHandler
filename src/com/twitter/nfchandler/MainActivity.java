package com.twitter.nfchandler;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.util.Log;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	private static final ComponentName TWITTER_URI_HANDLER = new ComponentName(
			"com.twitter.android", "com.twitter.applib.UrlInterpreterActivity");
	private static final String MATCH_HOST = "twitter.com";

	private Intent buildIntent(Uri uri) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(uri);
		intent.setComponent(TWITTER_URI_HANDLER);
		return intent;
	}

	public void onResume() {
		super.onResume();
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
			Parcelable[] rawMsgs = getIntent().getParcelableArrayExtra(
					NfcAdapter.EXTRA_NDEF_MESSAGES);
			if (rawMsgs != null) {
				for (Parcelable rawMsg : rawMsgs) {
					for (NdefRecord record : ((NdefMessage) rawMsg)
							.getRecords()) {
						Uri uri = record.toUri();
						if (uri != null) {
							Log.d(TAG, "Handling URI: " + uri);
							if (MATCH_HOST.equals(uri.getHost())) {
								try {
									super.startActivity(buildIntent(uri));
									super.finish();
									return;
								} catch (ActivityNotFoundException e) {
									Log.w(TAG,
											"Twitter activity not found. Skipping.",
											e);
								}
							}
						}
					}
				}
			}
		}
	}
}
