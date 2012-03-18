package com.flomio.api.rest;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Readers extends Resource {
	private static final String TAG = Readers.class.getSimpleName();

	private Account mAccount;
	private App mApp;

	// Note: these badboys can take (app_id (implied in uri) & uuid | id)  as
	// identifier
	
	private String mDuckId;

	public Readers(App app, String duckId)
	{
		this(app);
		mDuckId = duckId;
	}

	public Readers(App app){
		mApp = app;
		mAccount = app.getAccount();
	}

	@Override
	protected String getPath() {

		String assPath = String.format("%s/readers/", mApp.getPath() );
		return  mDuckId == null ? assPath : String.format("%s%s/",assPath , mDuckId);
	}

	// The following three methods are class methods?
	public Response get(String duckId)
	{
		Readers readers = new Readers(mApp, duckId);
		return FlomioApiClient.get(mAccount.key, readers.getPath());
	}

	public Response list()
	{
		Readers readers = new Readers(mApp );
		return FlomioApiClient.get(mAccount.key, readers.getPath());
	}

	public Response delete(String duckId)
	{
		Readers readers = new Readers(mApp, duckId);
		return FlomioApiClient.delete(mAccount.key, readers.getPath());
	}

	public Response create(String readerId)
	{
		Readers readerAssignment = new Readers(mApp);
		

		JSONObject jsonPayload = new JSONObject();
		try {
			
			ReaderDevices readerDevice = new ReaderDevices(mAccount, readerId);
			// app is implied in the uri, so just give the uri to the hardware
			
			//tell the reader assignment the path to the reader that we want to associate
			jsonPayload.put("reader_device", readerDevice.getPath());
		} catch (JSONException e) {
			Log.d(TAG, "Trouble creating json payload", e);
		}
		return FlomioApiClient.post(mAccount.key, readerAssignment.getPath(),
				jsonPayload.toString());
	}

}
