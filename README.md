# Flomio Android Helper Library

## Flomio API

Flomio makes it easy for you to develop NFC enabled applications. We take care of the heavy lifting of managing tags, readers, scans, and leave you to focus on delighting your customers. For more click [here](http://flomio.com/ "Flomio") 

## Android <3's NFC
Flomio sits atop the hardware NFC adapter bundled with the Android SDK. The Android OS reads the tag information and passes it to the Flomio helper library for processing. You can read more on Android's NFC support [here](http://developer.android.com/guide/topics/nfc/index.html "Near Field Communication | Android Developers").

## Install
This repo contains an Eclipse library project. To get started you need to import the project and add it to the buildpath of your existing project. 
1. Clone this repo using `git clone git@github.com:flomio/flomio-android`
2. Import it using "File > Import" then "General -> Existing Project". 
3. Add it to your project's build path by using "File > Properties" and clicking "Add" under the library section of the "Android" menu item. 

## Flomio Resources
Flomio organizes NFC activity into four resources:

* *Readers*
A NFC enabled hardware device (e.g. Galaxy Nexus)

* *Tags*
Any uniquely identified NFC tag (NFC Type 1, NFC Type 2, MiFare Ultralight, etc)

* *Scans*
A recorded instance of a tag entering the read range of a reader.

* *Apps*
A container for your readers, tags, and scans.


## Examples
Here are some examples:


	// Get the main account 
	Account account = new Account(ACCOUNT_ID, ACCOUNT_KEY);

	// Create a reader
	account.readerDevices().create("0x0000000000000F");

	// Create a tag
	account.tagDevices().create("0xFFFFFFFFFFFFF0");


	// Get an app helper that corresponds to the account
	App app = account.getApp(APP_ID, APP_KEY);

	// Add the tag and reader and assign meta data
	app.readers().addField(key, value).create("0x0000000000000F");
	app.tags().addField(key, value).create("0xFFFFFFFFFFFFF0");

	// Record a scan
	app.scans().create("0xFFFFFFFFFFFFF0", "0xFFFFFFFFFFFFF0");

	// Grab all scans on this app
	Response resp = app.scans().list();

