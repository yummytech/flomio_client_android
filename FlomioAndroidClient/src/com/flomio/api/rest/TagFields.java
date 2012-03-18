package com.flomio.api.rest;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class TagFields extends Resource {
	
	// This guy will need to be parented to a Tags `instance` or
	// however this java shit works.  

	// LIST ENDPOINT
	//  /accounts/{id}/apps/{id}/tags/{uuid|id}/fields/
	
	// DETAIL ENDPOINT
	//  /accounts/{id}/apps/{id}/tags/{uuid|id}/fields/{key}/
	
	
	/// TODO    V  Everything below :/
	
	private static final String TAG = TagFields.class.getSimpleName();

	private Tags mTagAss;
	private String mKey; // The key in the {"key" : "my_field", "value" : "FFFFFF!"}
	
	public TagFields(Tags tagAss)
	{
		mTagAss = tagAss;
	}

	public TagFields(Tags tagAss, String key){
		mTagAss = tagAss;
		mKey = key;
	}
	@Override
	protected String getPath() {

		String assPath = String.format("%sfields/", mTagAss.getPath() );
		return  mKey == null ? assPath : String.format("%s%s/",assPath , mKey);
	}

	// The following three methods are class methods?
	public Response get()
	{
		return FlomioApiClient.get(mTagAss.mAccount.key, getPath());
	}
	
	public Response delete(String key)
	{
		TagFields field = new TagFields(mTagAss, key);
		return FlomioApiClient.delete(mTagAss.mAccount.key, field.getPath());
	}
/*    public Response list()
	{
		TagFields field = new TagFields(mAccount );
		return FlomioApiClient.get(mAccount.key, field.getPath());
	}

	public Response delete(String key)
	{
		TagFields field = new TagFields(mAccount, key);
		return FlomioApiClient.delete(mAccount.key, field.getPath());
	}
*/
	
	public Response change( String value)
	{
//    	TagFields tagFields = new TagFields(mTagAss, key);
		JSONObject jsonPayload = new JSONObject();
		try {
			// app is implied in the uri, so just give the uri to the hardware
			jsonPayload.put("value", value);
		} catch (JSONException e) {
			Log.d(TAG, "Trouble creating json payload", e);
		}
		return FlomioApiClient.put(mTagAss.mAccount.key, 
				getPath(),
				jsonPayload.toString());
	}

}
