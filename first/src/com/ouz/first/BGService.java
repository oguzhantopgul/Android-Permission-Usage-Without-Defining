package com.ouz.first;
import java.util.ArrayList;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.util.Log;

public class BGService extends Service {

	/**
	 * Message type: register the activity's messenger for receiving responses
	 * from Service. We assume only one activity can be registered at one time.
	 */
	public static final int MESSAGE_TYPE_REGISTER = 1;
	/**
	 * Message type: text sent Activity<->Service
	 */
	public static final int MESSAGE_TYPE_TEXT = 2;

	/**
	 * Messenger used for handling incoming messages.
	 */
	final Messenger mMessenger = new Messenger(new IncomingHandler());

	/**
	 * Messenger on Activity side, used for sending messages back to Activity
	 */
	// 2.appe geri donecegimiz cevabin messengeri
	Messenger mResponseMessenger = null;

	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_TYPE_TEXT:
				Bundle b = msg.getData();
				if (b != null) {
					int  request = Integer.parseInt (b.getString("data"));
					switch (request) {
					case 1:
						getContacts();
						break;
					case 2:
						getGPS();
						break;
					default:
						break;
					}
					//Log.d("MessengerService",
					//		"Service received message MESSAGE_TYPE_TEXT with: " + b.getCharSequence("data"));
					//sendToActivity("Who's there? You wrote: " + b.getCharSequence("data"));
					
				} else {
					Log.d("MessengerService", "Service received message MESSAGE_TYPE_TEXT with empty message");
					sendToActivity("Who's there? Speak!");
				}
				break;
			case MESSAGE_TYPE_REGISTER:
				Log.d("MessengerService", "Registered Activity's Messenger.");
				mResponseMessenger = msg.replyTo;
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("MessengerService", "Service started");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d("MessengerService", "Binding messenger...");
		return mMessenger.getBinder();
	}

	public void onCreate(){
		Log.w("First","Service Started");
		super.onCreate();
	}	

	/**
	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {

			//2.appden servise gelen istek.
			Bundle data = msg.getData();        	
			String dataString = data.getString("SecretMessage");

			//Ayni Activityye geri donmek icin replyTo'yu alip mResponseMessengera attim.
			myResponseMessenger = msg.replyTo;

			Toast.makeText(getApplicationContext(), 
					dataString, Toast.LENGTH_SHORT).show();

			//2.app'den servise gelen istege gore cagirilacak fonksiyonu belirliyoruz.
			switch (Integer.parseInt(dataString)) {
			case 1:
				getContacts();
				break;
			case 2:
				Log.w("Service", "Get GPS Data");
				break;	

			default:
				break;
			}

		}
	}**/


	public void getContacts(){

		ContentResolver cr = getContentResolver();
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
		ArrayList<String> mylist = new ArrayList<String>();

		if (cur.getCount() > 0) {
			while (cur.moveToNext()) {
				String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
				String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				if (Integer.parseInt(cur.getString(
						cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
					Cursor pCur = cr.query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",new String[]{id}, null);
					while (pCur.moveToNext()) {
						String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						Log.w("Service","ID: " + id +
								", Name: " + name + 
								", Phone No: " + phoneNo);
						//bundle.putString("start", "Ad: "+ name + " Telefon: " +phoneNo);
						mylist.add(name + ":" + phoneNo); 
						
					}
				}
			}
		}
		sendToActivity(mylist.toString());
	}

	public void getGPS(){
		Log.w("Service","Getting GPS Coordinates");
	}

	//2.appe geri donerken kullanilacak fonksiyon . 
	void sendToActivity(String text) {
		if (mResponseMessenger == null) {
			Log.d("MessengerService", "Cannot send message to activity - no activity registered to this service.");
		} else {
			Log.d("MessengerService", "Sending message to activity: " + text);
			Bundle data = new Bundle();
			data.putString("data", text);
			Message msg = Message.obtain(null, MESSAGE_TYPE_TEXT);
			msg.setData(data);
			try {
				mResponseMessenger.send(msg);
			} catch (RemoteException e) {
				// We always have to trap RemoteException (DeadObjectException
				// is thrown if the target Handler no longer exists)
				e.printStackTrace();
			}
		}
	}

}
