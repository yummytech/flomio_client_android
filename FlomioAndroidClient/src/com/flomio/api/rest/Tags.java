package com.flomio.api.rest;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Tags extends Resource {
	private static final String TAG = Tags.class.getSimpleName();

	protected Account mAccount;
	protected App mApp;
	HashMap<String, String> mFieldsMap = new HashMap<String, String>();
	
	public TagFields fields = new TagFields(this);

	// Note: these badboys can take (app_id (implied in uri) & uuid | id)  as
	// identifier
	private String mDuckId;

	public Tags(App app, String duckId)
	{
		this(app);
		mDuckId = duckId;
	}

	public Tags(App app){
		mApp = app;
		mAccount = app.getAccount();
	}

	@Override
	protected String getPath() {

		String assPath = String.format("%s/tags/", mApp.getPath() );
		return  mDuckId == null ? assPath : String.format("%s%s/",assPath , mDuckId);
	}

	// The following three methods are class methods?
	public Response get(String duckId)
	{
		Tags tags = new Tags(mApp, duckId);
		return FlomioApiClient.get(mAccount.key, tags.getPath());
	}

	public Response list()
	{
		Tags tags = new Tags(mApp);
		return FlomioApiClient.get(mAccount.key, tags.getPath());
	}

	public Response delete(String duckId)
	{
		Tags tags = new Tags(mApp, duckId);
		return FlomioApiClient.delete(mAccount.key, tags.getPath());
	}

	public Tags addField(String key, String value){
		mFieldsMap.put(key, value);
		return this;
	}
	
	public Response create(String tagId) 
	{
		Tags tagsAssignment = new Tags(mApp);
		/// fields  // JSONObject
		
		JSONObject jsonPayload = new JSONObject();
		try {
			TagDevices tag = new TagDevices(mAccount, tagId);
			
			// jsonPayload.put("app",    mApp.getPath()); not needed
			// app is implied in the uri, so just give the uri to the hardware
			
				// would kind of like it if full uris were optional instead 
				// of mandatory ...
			
			//Need to tell the tag assignment the tag uri for the tag we want
			// to associate to the app
			jsonPayload.put("tag_device", tag.getPath());
			
			// This guy can take a fields : {"a" : "b", "c" : "d", ... }
			//jsonPayload.put("fields", fields);
			if(mFieldsMap.size() > 0){
				JSONObject fieldData = new JSONObject();
				for(String field : mFieldsMap.keySet()){
					fieldData.put(field, mFieldsMap.get(field));
				}
				jsonPayload.put("fields", fieldData);
				
			}
			
		} catch (JSONException e) {
			Log.d(TAG, "Trouble creating json payload", e);
		}
		return FlomioApiClient.post(mAccount.key, tagsAssignment.getPath(),
				jsonPayload.toString());
	}
	
	public TagFields fields(String fieldKey){
		return new TagFields(this, fieldKey);
	}

}
