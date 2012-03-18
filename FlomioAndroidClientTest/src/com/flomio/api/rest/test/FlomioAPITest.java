package com.flomio.api.rest.test;

import org.json.JSONException;

import android.test.AndroidTestCase;
import android.util.Log;

import com.flomio.api.rest.Account;
import com.flomio.api.rest.App;
import com.flomio.api.rest.Response;
import com.flomio.api.scans.Scanner;

public class FlomioAPITest extends AndroidTestCase
{
	// ############################# FIXTURES ############################### //
	/*
	 * Modify these fixtures with your Flomio resource information. 
	 */

	private static final int mAppId = 12345678;
	private static final String mAppKey = "XXXXXXXXXX";

	// used for ensuring adding reader devices and tag devices to readers and tags work
	private static final int sAssignmentAppId = 12345678;
	private static final String sAssignmentAppKey = "XXXXXXXXXX";

	private static final int mAccountId = 12345678;
	private static final String mAccountKey = "XXXXXXXXXX";

	// Assignments are made for these too
	String mTagDeviceUuid = "0x00000000000001";
	String mReaderDeviceUuid = "0xFFFFFFFFFFFF";

	// ########################## END FIXTURES ############################## //
	
	String mNonExistentReaderUuid_toBeCreated = "0xFFDD000000D18";
	String mNonExistentTagUuid_toBeCreated = "0xFFDD000000D21";
	String mNonExistentReaderUuid_toBeCreated_forAssignment = "0xFFDD000000D19";
	String mNonExistentTagUuid_toBeCreated_forAssignment = "0xFFDD000000D22";

	final String TAG = FlomioAPITest.class.getSimpleName();

	Account mAccount;
	App mApp;


	protected void setUp() throws Exception
	{
		super.setUp();
		//acct id: 22
		//acct key: V4oy47J7RW8ykf79Knm9BgLsG38PVGHM

		//app id: 24
		//app key: UBi1T9r2wFX8DfhYyri2LChYgPA5dzdy


		//Get the root helper
		 mAccount = new Account(mAccountId, mAccountKey);

		//Get an app helper that corresponds to the account
		mApp = mAccount.getApp(mAppId, mAppKey);
	}

	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	public void testScanCreate() throws JSONException
	{

		try{
		// Readers and Tags for the tag device and reader device must first
		// exist before creating a scan. Failures will result otherwise
		Response response = mApp.readers().create(mReaderDeviceUuid);
		response = mApp.tags().create(mTagDeviceUuid);
		
		
		//Make a scan
		//NOTE: There was an issue with the tags not being hex values
		Response resp = mApp.scans().create(mTagDeviceUuid, mReaderDeviceUuid);
		assertTrue(resp.success);

		Log.d(TAG, "response: " + resp.responseData);
//		assertEquals(resp.responseData.getString("tag"), "TEST_ANDROID_TAG");
//		assertEquals(resp.responseData.getString("reader"), "TEST_ANDROID_READER");
		}finally{
			//clean up 
			mApp.readers().delete(mReaderDeviceUuid);
			mApp.tags().delete(mTagDeviceUuid);
		}
	}

	public void testReaderDeviceGet(){
		Response resp = mAccount.readerDevices().get( mReaderDeviceUuid);

		assertTrue(resp.success);
	}

	public void testReaderDeviceList(){
		Response resp = mAccount.readerDevices().list( );

		assertTrue(resp.success);
	}

//	public void testScanDetail() throws JSONException
//	{
//
//		//Make a scan
//		//NOTE: There was an issue with the tags not being hex values
//		Response resp = app.scans.get(tagUuid, readerUuid);
//
//		assertTrue(resp.success);
//
//		Log.d(TAG, "response: " + resp.responseData);
//		assertEquals(resp.responseData.getString("tag"), tagUuid);
//		assertEquals(resp.responseData.getString("reader"), readerUuid);
//
//	}

	public void testScanList() throws JSONException
	{
		//Make a scan
		//NOTE: There was an issue with the tags not being hex values
		Response resp = mApp.scans().list();

		assertTrue(resp.success);

//		assertTrue(resp.responseData.tagUuid);
//		assertEquals(resp.responseData.getString("reader"), readerUuid);

	}

	public void testReaderDevicesCreateAndDelete()
	{
		Response response = mAccount.readerDevices().create(mNonExistentReaderUuid_toBeCreated);
		assertTrue(response.success);

		response = mAccount.readerDevices().delete(mNonExistentReaderUuid_toBeCreated);
		assertTrue(response.success);
	}

	public void testReaderDevicesDefaultUuidCreateAndDelete()
	{
		Response response = mAccount.readerDevices().create(this.mContext);
		assertTrue(response.success);
		Log.d("test", "created reader");
		String uuid = mAccount.readerDevices().getDeviceUuid(this.mContext);
		Log.d("test", uuid);
		response = mAccount.readerDevices().delete(uuid);
		assertTrue(response.success);
	}

	public void testTagDeviceGet() throws JSONException
	{
		Response response = mAccount.tagDevices().get(mTagDeviceUuid);
		assertTrue(response.success);
//		Log.d
//		assertTrue(response.responseData.getString("description").startsWith("2nd"));
	}

	public void testTagDeviceList()
	{
		Response response = mAccount.tagDevices().list();
		assertTrue(response.success);
	}

	public void testTagDeviceCreateAndDelete()
	{
		Response response = mAccount.tagDevices().create(mNonExistentTagUuid_toBeCreated);
		assertTrue(response.success);

		response = mAccount.tagDevices().delete(mNonExistentTagUuid_toBeCreated);
		assertTrue(response.success);
	}

	// TagAssignment becomes Tag
	public void testTagCreate() 
	{
		String key = "color";
		String value = "green";
		try{
			//remove the tag if it already exists, just in case
			Response response = mAccount.tagDevices().delete(mNonExistentTagUuid_toBeCreated_forAssignment);
			//assertTrue(response.success);

			response = mAccount.tagDevices().create(mNonExistentTagUuid_toBeCreated_forAssignment);
			assertTrue(response.success);

			// now that the tag is created, assign it to an app
			Response tagResponse = mAccount.getApp(sAssignmentAppId, sAssignmentAppKey).
					tags().addField(key, value)
					.create(mNonExistentTagUuid_toBeCreated_forAssignment);
			assertTrue(tagResponse.success);

			// get back the tag field we added in the
			tagResponse = mAccount.getApp(sAssignmentAppId, sAssignmentAppKey).
					tags(mNonExistentTagUuid_toBeCreated_forAssignment).fields(key)
					.get();
			assertTrue(tagResponse.success);

			// change the tag field
			response = mAccount.getApp(sAssignmentAppId, sAssignmentAppKey).tags(mNonExistentTagUuid_toBeCreated_forAssignment).fields(key).change("red");
			assertTrue(response.success);


			// remove the tag
			tagResponse = mAccount.getApp(sAssignmentAppId, sAssignmentAppKey).tags().delete(mNonExistentTagUuid_toBeCreated_forAssignment);
			assertTrue(tagResponse.success);
		}finally{
			//clean up
			Response response = mAccount.tagDevices().delete(mNonExistentTagUuid_toBeCreated_forAssignment);
			// remove the tag  again for good measure
			response = mAccount.getApp(sAssignmentAppId, sAssignmentAppKey).tags().delete(mNonExistentTagUuid_toBeCreated_forAssignment);
		}
	}

	public void testReaderCreate(){
		try{
			//remove the reader if it already exists, just in case
			Response response = mAccount.readerDevices().delete(mNonExistentReaderUuid_toBeCreated_forAssignment);

			// create the reader device
			response = mAccount.readerDevices().create(mNonExistentReaderUuid_toBeCreated_forAssignment);
			assertTrue(response.success);

			// now that the reader device is created, assign it to an app
			Response readerResponse = mAccount.getApp(sAssignmentAppId, sAssignmentAppKey).readers().create(mNonExistentReaderUuid_toBeCreated_forAssignment);
			assertTrue(readerResponse.success);

			// remove the reader
			readerResponse = mAccount.getApp(sAssignmentAppId, sAssignmentAppKey).readers().delete(mNonExistentReaderUuid_toBeCreated_forAssignment);
			assertTrue(readerResponse.success);
		}finally{

			//clean up
			Response response = mAccount.readerDevices().delete(mNonExistentReaderUuid_toBeCreated_forAssignment);
			// one more time for good measure
			response = mAccount.getApp(sAssignmentAppId, sAssignmentAppKey).readers().delete(mNonExistentReaderUuid_toBeCreated_forAssignment);

		}
	}

	public void testScanCreateViaLightweightScannerAPI() throws JSONException
	{
		try{
			//make sure we have readers and tags before creating scan
			Response response = mApp.readers().create(mReaderDeviceUuid);
			response = mApp.tags().create(mTagDeviceUuid);
			
			//Make a scan
			//NOTE: There was an issue with the tags not being hex values
			Scanner scanner = new Scanner(mAccountId, mAppId, mAppKey);
			Response resp = scanner.scan(mTagDeviceUuid, mReaderDeviceUuid);
	
			assertTrue(resp.success);
	
			Log.d(TAG, "response: " + resp.responseData);
		}finally{
			//clean up 
			mApp.readers().delete(mReaderDeviceUuid);
			mApp.tags().delete(mTagDeviceUuid);
		}
	}


}
