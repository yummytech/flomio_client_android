package com.flomio.api.rest;

abstract public class Resource {
	public static final String RESOURCE_API_BASE = "/api/v1";
	abstract protected String getPath();
	
}
