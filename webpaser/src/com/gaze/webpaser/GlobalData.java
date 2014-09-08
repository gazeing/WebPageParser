package com.gaze.webpaser;

public class GlobalData {

	//baseUrl of our website
	public static final String baseUrl = "http://www.rebonline.com.au/";
	
//	//the REG id used by GCM
//	public static final String PROPERTY_REG_ID = "AIzaSyDl--1M1ZI-9r-5c_0p8ajhwT7N3RucKxk";
//	
//	
//    // Defines a custom Intent action
//    public static final String BROADCAST_ACTION =
//        "com.sterlingpublishing.BROADCAST";
//   
//    // Defines the key for the status "extra" in an Intent
//    public static final String NOTIFICATION_MESSAGE =
//        "noti_msg";
//    
//    public static final String NOTIFICATION_URL =
//            "noti_url";
//    
//    public static final String JSON_PARSING_FINISH =
//            "json_finish";
    
    public static final String MAIN_LIST_URL = "main_list_url";
    
    public static final String MAIN_LIST_ITEM_SERIALABLE = "main_list_item_serialable";
    

    
    
    // google project id
//    public static String SENDER_ID = "744563661502";
//    public static final String PROPERTY_APP_VERSION = "1.0";
    /** Your ad unit id. Replace with your actual ad unit id. */
//    public static final String AD_UNIT_ID = "/50807330/REB_LaunchAd";
    
    public static final String PRELOAD_LIST_URL ="http://www.rebonline.com.au/?option=com_ajax&format=json&plugin=latestajaxarticlesfromcategory&cat_id=1";
    public static final String BLOG_LIST_URL = "http://www.rebonline.com.au/?option=com_ajax&format=json&plugin=latestajaxarticlesfromcategory&cat_id=21";
    public static final String FEATURES_LIST_URL = "http://www.rebonline.com.au/?option=com_ajax&format=json&plugin=latestajaxarticlesfromcategory&cat_id=51";
    
//    public static final String MENU_LIST_URL = "http://www.rebonline.com.au/steven/json/side_menu.json";
    
    
    public static ImageThreadLoader m_Imageloader = new ImageThreadLoader();
    
    
    
	
//	//the url and title webview is showing
//	public static String showingTitle = "";
//	public static String showingUrl = "";
//	
//	//the intent from getIntent() of RebMainActivity
//	public static Intent m_Intent = null;
//	//when come from browsable or notification, set this to true to avoid show adview and tutorial
//	public static boolean ifShowContentDirectly = false;
//	
//	//an instance of main webview
//	public static WebView m_webview = null;
//	
//	//detect if the app has been brought foreground
//	public static boolean m_IsRebForeground = false;
//	
//	//store url to load when get notification
////	public static String m_dirtyUrl = null;
//	
//	//save current url when orientation changed
//	public static String savedUrl = null;
//	
//	
////	public static Bitmap bluredScreen =null;
//	
//
//    
////    //define a loading finish listener for start page
////    public static TaskCompleteListener m_loadingFinishListener = null;
//
//    
//    //if we show the ad
//    public static boolean ifShowAd = true;
//
//
//	public static void reset() {
//		ifShowAd = true;
////		m_loadingFinishListener = null;
//		savedUrl = null;
////		m_dirtyUrl = null;
//		m_IsRebForeground = false;
//		m_webview = null;
//		m_Intent = null;
//		showingUrl = "";
//		showingTitle = "";
//		ifShowContentDirectly = false;
//		
//	}
}
