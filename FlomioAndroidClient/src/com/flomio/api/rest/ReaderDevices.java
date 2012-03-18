package com.flomio.api.rest;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class ReaderDevices extends Resource {

	private static final String TAG = ReaderDevices.class.getSimpleName();
	private Account mAccount;
	private String mUuid = null;

	public ReaderDevices(Account account) {
		mAccount = account;
	}

	public ReaderDevices(Account account, String uuid) {
		this(account);
		mUuid = uuid;

		// TODO: the uuid MUST be formatted as `0xFFFFFFFFFFFFFF` Not so much
		// for these ReaderDevice guys but the `Reader` app assignments can take
		// {uuid|id} for idents. For assignment resources the `0x`
		// differentiates between a uuid/app_id pair or identity column. This is
		// a conwenience so you can get assignments with one request.
	}

	// @Override
	// protected String getPath() {
	// 	return getPath(false);
	// }
	
	protected String getPath() {
		if (mUuid == null) {
			return String.format("%s/reader_devices/", mAccount.getPath());
		} else {
			return String.format("%s/reader_devices/%s/", mAccount.getPath(), mUuid);
		}
	}

	public Response list() {
		ReaderDevices createdReaderDev = new ReaderDevices(mAccount);

		return FlomioApiClient.get(mAccount.key, createdReaderDev.getPath());
	}
	
	public Response get(String readerId) {
		// In general need some way to GET a resource_uri returned in the json
		// response body. 

		// Likewise for PUT/DELETE and any other `detail` endpoints.
		
		ReaderDevices createdReaderDev = new ReaderDevices(mAccount, readerId);

		return FlomioApiClient.get(mAccount.key, createdReaderDev.getPath());
	}
	
	public Response delete(String readerId) {
		ReaderDevices createdReaderDev = new ReaderDevices(mAccount, readerId);

		return FlomioApiClient.delete(mAccount.key, createdReaderDev.getPath());
	}

	public Response create(Context context) 
	{
		//Per Grundy, default uuid to device ANDROID_ID
		//http://developer.android.com/reference/android/provider/Settings.Secure.html#ANDROID_ID
		return create(getDeviceUuid(context));
	}
	public static String getDeviceUuid(Context ctx) 
	{
		return "0x" + android.provider.Settings.Secure.getString(ctx.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
	}
	
	public Response create(String uuid) 
	{

		ReaderDevices createdReaderDev = new ReaderDevices(mAccount);

		JSONObject jsonPayload = new JSONObject();
		try {
			//NOTE: do not include account creation either
			// Yep, account id is implied in the uri
			jsonPayload.put("type", 1);
			jsonPayload.put("uuid", uuid);
		} catch (JSONException e) {
			Log.d(TAG, "Trouble creating json payload", e);
		}
		return FlomioApiClient.post(mAccount.key, createdReaderDev.getPath(),
				jsonPayload.toString());
	}
}
