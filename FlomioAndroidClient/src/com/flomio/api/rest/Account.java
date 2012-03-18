package com.flomio.api.rest;

public class Account extends Resource
{

	int id;
	String key;
	
	public Account(int id, String key) 
	{
		this.id = id;
		this.key = key;
	}

	public Account(int id) 
	{
		this.id = id;
	}

	public App getApp(int id, String key) 
	{
		return new App(this, id, key);
	}
	
	@Override
	protected String getPath() {
		return String.format("%s/accounts/%s", RESOURCE_API_BASE, this.id);
	}
	
	public ReaderDevices readerDevices(){
		return new ReaderDevices(this);
	}
	
	public TagDevices tagDevices(){
		return new TagDevices(this);
	}
	
}
