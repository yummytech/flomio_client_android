package com.flomio.api.rest;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class TagDevices extends Resource {
	private static final String TAG = TagDevices.class.getSimpleName();
	
	private Account mAccount;
	private String mUuid;
	
	public TagDevices(Account account, String uuid) 
	{
		this(account);
		mUuid = uuid;
		
		// TODO: the uuid MUST be formatted as `0xFFFFFFFFFFFFFF` Not so much
		// for these ReaderDevice guys but the Reader resources can take
		// {uuid|id} for idents. For Assignment resources the `0x`
		// differentiates between a uuid/app_id pair or integer identity column. This is
		// a conwenience so you can get assignments with one request.
	}
	
	public TagDevices(Account account){
		mAccount = account;
	}
	
	@Override
	protected String getPath() {
		
		String tagsBasePath = String.format("%s/tag_devices/",
				mAccount.getPath()  );
		return  mUuid == null ? tagsBasePath : String.format("%s%s/",tagsBasePath , mUuid );
	}
	
	public Response get(String uuid)
	{
		TagDevices tagDevices = new TagDevices(mAccount,uuid );
		return FlomioApiClient.get(mAccount.key, tagDevices.getPath());
	}
	
	public Response list()
	{
		TagDevices tagDevices = new TagDevices(mAccount );
		return FlomioApiClient.get(mAccount.key, tagDevices.getPath());
	}
	
	public Response delete(String uuid)
	{
		TagDevices tagDevices = new TagDevices(mAccount,uuid );
		return FlomioApiClient.delete(mAccount.key, tagDevices.getPath());
	}
	
	public Response create(String uuid) 
	{

		TagDevices createdTag = new TagDevices(mAccount);

		JSONObject jsonPayload = new JSONObject();
		try {
			jsonPayload.put("type", 0);
			jsonPayload.put("uuid", uuid);
		} catch (JSONException e) {
			Log.d(TAG, "Trouble creating json payload", e);
		}
		return FlomioApiClient.post(mAccount.key, createdTag.getPath(),
				jsonPayload.toString());
	}

}
