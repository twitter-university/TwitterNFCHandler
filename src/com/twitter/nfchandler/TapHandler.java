package com.twitter.nfchandler;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.widget.Toast;

public class TapHandler extends BroadcastReceiver {
	private Context context;

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
			Parcelable[] rawMsgs = intent
					.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			if (rawMsgs != null) {

				NdefMessage message;
				// Loop through messages
				for (int i = 0; i < rawMsgs.length; i++) {
					message = (NdefMessage) rawMsgs[i];

					for (NdefRecord record : message.getRecords()) {
						Toast.makeText(context,
								"Uri:" + record.toUri().toString(),
								Toast.LENGTH_LONG).show();
						handleUri(record.toUri());
					}
				}

			}
		}
	}

	private static final ComponentName TWITTER_URI_HANDLER = new ComponentName(
			"com.twitter.android", "com.twitter.android.applib.UrlHandler");

	private void handleUri(Uri uri) {
		if (uri == null)
			return;
		try {
			Intent intent = new Intent("android.intent.action.VIEW");
			intent.setData(uri);
			intent.setComponent(TWITTER_URI_HANDLER);
			context.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(context, "Activity not found: ", Toast.LENGTH_LONG)
					.show();
			e.printStackTrace();
		}
	}

}
