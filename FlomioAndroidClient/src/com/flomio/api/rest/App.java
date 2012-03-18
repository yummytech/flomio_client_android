package com.flomio.api.rest;

public class App extends Resource {

	private Account mAccount;
	private int mId;
	String mAppKey;
	
	
	public App(Account account, int id, String key) {
		mAccount = account;
		mId = id;
		mAppKey = key;
	}
	
	
	public Account getAccount() {
		return mAccount;
	}

	@Override
	protected String getPath() 
	{
		//accounts/1/apps/1 etc
		return String.format("%s/apps/%s", mAccount.getPath(), mId);
		
		// return String.format("%s/apps/%s", RESOURCE_API_BASE, mId);
	}

	public Scans scans(){
		return new Scans(this, mAccount);
	}
	
	public Tags tags(){
		return new Tags(this);
	}
	
	public Tags tags(String tagId){
		return new Tags(this, tagId);
	}
	
	public Readers readers(){
		return new Readers(this);
	}
	
}
