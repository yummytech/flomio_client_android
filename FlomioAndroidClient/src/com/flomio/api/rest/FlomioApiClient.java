package com.flomio.api.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;


public class FlomioApiClient {
	private static final String TAG = FlomioApiClient.class.getSimpleName();

	static private String API_HOST = "https://api.flomio.com";

	static private String AUTH_HEADER_NAME = "X-Request-Signature";

	public static Response get(String key, String path) 
	{
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(API_HOST + path);
		Response flomioResponse = new Response();
		Log.d(TAG, "get: " + API_HOST + path);

		/*for GET/DELETE, auth header should be a hash of:
		 The path, and if there is a query segment, the query prefixed with '?',
		 concatenated together. Note this does *not* include the scheme/address parts of
		 a request url.*/

		get.setHeader(AUTH_HEADER_NAME, createSignature(key, path));

		HttpResponse response;
		try {
			response = client.execute(get);
			
			Log.d(TAG, response.getStatusLine().toString());
			flomioResponse.success = (HttpStatus.SC_OK==response.getStatusLine().getStatusCode());

			flomioResponse.responseData = readResponse(response);
		}
		catch (IOException e) {
			Log.e(TAG, "get failed", e);
		}
		return flomioResponse;
	}
	
	public static Response put(String key, String path, String payload){
		HttpClient client = new DefaultHttpClient();
		HttpPut post = new HttpPut(API_HOST + path);
		Log.d(TAG, "put: " + API_HOST + path);
		Response flomioResponse = new Response();
		try {

			StringEntity se = new StringEntity(payload);
			post.setEntity(se); 
			/*for POST/PUT  requests with a payload, auth header is a hash of:
			 * The source for the payload is determined by the request METHOD. 
			 * Where there is a request body, this is used, otherwise a procedure the request 
			 * path and query string.
			 * */
			post.setHeader(AUTH_HEADER_NAME,  createSignature(key, payload));
			post.setHeader("Content-type", "application/json");
			post.setHeader("Accept", "application/json");

			HttpResponse response = client.execute(post);

			Log.d(TAG, response.getStatusLine().toString());
			int statusCode = response.getStatusLine().getStatusCode();
			//since this is a REST service and we are PUTing, we should get back a 204 or 202
			
			// Actually, it's possible to get a 201 here. Our API framework, for
			// better or worse, will return a 201 if you PUT to a detail uri of
			// an inexisting object.
			
			// It's arguable that it should 404 but that's the way it is This is
			// of note to us as we are using the /fields/{key}/ detail uri.
			
			// It's convenient for this to just use PUT all the time, without
			// knowing whether or not the `.../fields/badge_number/` is already
			// set.
			flomioResponse.success = (HttpStatus.SC_NO_CONTENT==statusCode || HttpStatus.SC_ACCEPTED == statusCode);

			if(response.getEntity() != null){
				flomioResponse.responseData = readResponse(response);
			}

		} catch (IOException e) {
			Log.e(TAG, "put failed", e);
		}
		return flomioResponse;
	}
	
	public static Response delete(String key, String path) 
	{
		HttpClient client = new DefaultHttpClient();
		HttpDelete delete = new HttpDelete(API_HOST + path);
		Response flomioResponse = new Response();
		Log.d(TAG, "delete: " +  API_HOST + path);

		/*for GET/DELETE, auth header should be a hash of:
		 The path, and if there is a query segment, the query prefixed with '?',
		 concatenated together. Note this does *not* include the scheme/address parts of
		 a request url.*/

		delete.setHeader(AUTH_HEADER_NAME, createSignature(key, path));

		HttpResponse response;
		try {
			response = client.execute(delete);
			
			Log.d(TAG, response.getStatusLine().toString());
			flomioResponse.success = (HttpStatus.SC_NO_CONTENT==response.getStatusLine().getStatusCode());
			if(response.getEntity() != null){
				flomioResponse.responseData = readResponse(response);
			}
		}
		catch (IOException e) {
			Log.e(TAG, "get failed", e);
		}
		return flomioResponse;
	}

	public static Response post(String key, String path, String payload){
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(API_HOST + path);
		Log.d(TAG, "post: " + API_HOST + path);
		Response flomioResponse = new Response();
		try {

			StringEntity se = new StringEntity(payload);
			post.setEntity(se); 
			/*for POST/PUT  requests with a payload, auth header is a hash of:
			 * The source for the payload is determined by the request METHOD. 
			 * Where there is a request body, this is used, otherwise a procedure the request 
			 * path and query string.
			 * */
			post.setHeader(AUTH_HEADER_NAME,  createSignature(key, payload));
			post.setHeader("Content-type", "application/json");
			post.setHeader("Accept", "application/json");

			HttpResponse response = client.execute(post);

			Log.d(TAG, response.getStatusLine().toString());
			//since this is a REST service and we are POSTing, we should get back a 201
			flomioResponse.success = (HttpStatus.SC_CREATED==response.getStatusLine().getStatusCode());

			flomioResponse.responseData = readResponse(response);


		} catch (IOException e) {
			Log.e(TAG, "post failed", e);
		}
		return flomioResponse;
	}

	private static JSONObject readResponse(HttpResponse response) throws IOException {
		if(response == null || response.getEntity() == null) return null;
		
		BufferedReader rd = new BufferedReader(new InputStreamReader(
				response.getEntity().getContent()));


		// pull out the response body one line at a time
		StringBuilder responseBody = new StringBuilder();
		for(String line = rd.readLine(); line != null; line = rd.readLine() ){
			Log.d(TAG, "response line: " + line);
			responseBody.append(line);
		}
		Log.d(TAG, "done reading");

		if(responseBody.length() < 1) return null;
		
		// convert the response body into a JSON object
		try {
			return new JSONObject(responseBody.toString());

		} catch (JSONException ex) {
			Log.d(TAG, "Trouble parsing json response ", ex);
			return null;
		}
	}

	private static String createSignature(String Key, String payload) {
		SecretKeySpec signingKey = null;
		try {
			signingKey = new SecretKeySpec(Key.getBytes("ASCII"), "HmacSHA1");
		} catch (UnsupportedEncodingException ex) {
			Log.w(TAG, "Trouble creating SecretKeySpec", ex);
		}

		try {
			//initialize the hash algortihm
			Mac mac = Mac.getInstance("HmacSHA1");    
			mac.init(signingKey);

			StringBuffer data = new StringBuffer();        
			data.append(payload);

			Log.d(TAG, "payload: " + data.toString());
			Log.d(TAG, "key: "+ Key);
			Log.d(TAG, "key ascii: " + bytesToHexString(Key.getBytes("ASCII")));
			//compute the hmac on input data bytes
			byte[] rawHmac = mac.doFinal(payload.getBytes());

			//base64-encode the hmac
			String signature = bytesToHexString(rawHmac); //new String(rawHmac);

			Log.d(TAG, signature);

			return signature;
		} catch (NoSuchAlgorithmException e) {
			Log.d(TAG, "", e);
			return "lol";
		} catch (InvalidKeyException e) {
			Log.d(TAG, "", e);
			return "lol";
		} catch (UnsupportedEncodingException e)
		{
			Log.d(TAG, "", e);
			return "lol";
		}	
	}

	/** 
	 * Convert bytes into their String representations
	 * 
	 */
	private static String bytesToHexString(byte[] bytes) {  
		StringBuilder sb = new StringBuilder(bytes.length * 2);  

		Formatter formatter = new Formatter(sb);  
		for (byte b : bytes) {  
			formatter.format("%02x", b);  
		}  

		return sb.toString();  
	}

}
