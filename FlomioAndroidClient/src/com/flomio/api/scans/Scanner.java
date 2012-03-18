package com.flomio.api.scans;

import com.flomio.api.rest.Account;
import com.flomio.api.rest.App;
import com.flomio.api.rest.Response;

public class Scanner 
{
	private int accountId;
	private int appId;
	private String appKey;
	
	public Scanner(int accountId, int appId, String appKey)
	{
		this.accountId = accountId;
		this.appId = appId;
		this.appKey = appKey;
		
	}
	//TODO: what should this return? I feel a bit icky returning the Response from the other namespace
	public Response scan(String tagUuid, String readerUuid)
	{
		Account acct = new Account(accountId);
		App app = acct.getApp(appId, appKey);
		
		return app.scans().create(tagUuid, readerUuid);
	}

}
