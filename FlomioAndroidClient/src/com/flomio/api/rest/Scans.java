package com.flomio.api.rest;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Scans extends Resource {
    private static final String TAG = Resource.class.getSimpleName();
    private App mApp;
    private Account mAccount;
    
    //this object knows how to create scans, and eventually list and retrieve them
    /*
    * This bad boy only uses the mApp.mAppKey for CREATION of scans.
    * To query them requires the use of the account key.
    */
    public Scans(App app, Account account) 
    {
        mApp      = app;
        mAccount  = account;
    }

    @Override
    protected String getPath() {
        // App's uri + path to "Scan" resource
        String scanUri = String.format("%s/scans/", mApp.getPath());
        return scanUri;
    }

    /**
     * Creates a scan that associates a tag and a reader to the App.
     * 
     * @param tagId
     * @param readerId
     * @return
     */
    public Response create(String tagUuid, String readerUuid)
    {
        Tags tags = new Tags(mApp, tagUuid);
        Readers readers = new Readers(mApp, readerUuid);

        String payload = createPayload(tags, readers);
        String scanUri = getPath();

        return FlomioApiClient.post(mApp.mAppKey, scanUri, payload);
    }
    

    private String createPayload(Tags tag, Readers reader)
    {
        JSONObject jsonPayload = new JSONObject();
        try{
            jsonPayload.put("tag", tag.getPath());
            jsonPayload.put("reader", reader.getPath());
        }
        catch(JSONException ex)
        {
            Log.d(TAG, "Problem creating JSON payload", ex);
        }
        
        return jsonPayload.toString();
    }
 
    /*
     * This bad boy only uses the mApp.mAppKey for CREATION of scans.
     * To query them requires the use of the account key.
     */
    public Response list() 
    {   
        return FlomioApiClient.get(mAccount.key, this.getPath());
    }

}
