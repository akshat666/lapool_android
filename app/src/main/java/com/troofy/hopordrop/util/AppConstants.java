package com.troofy.hopordrop.util;

/**
 * Created by akshat
 */
public class AppConstants {




    private AppConstants(){}

    public static final int STATUS_AUTH_USER_ACTIVE = 50;
    public static final int STATUS_AUTH_USER_INACTIVE = 51;

    public static final int CACHE_PICKREQ_DURATION = 2000;
    public static final int CACHE_GEOSEARCH_KEY = 2001;

    public static final int HTTP_CODE_INTERNAL_ERROR_500 = 500;

    // Channel status constants
    public static final int STATUS_USERCHANNEL_ACTIVE = 0;

    public static final int STATUS_PICKREQ_NEW = 20;
    public static final int STATUS_PICKREQ_ACCEPTED = 21;
    public static final int STATUS_PICKREQ_CANCELLED = 22;

    public static final int STATUS_TRIP_CREATED = 50;
    public static final int STATUS_TRIP_ENDED_BY_DROPPER = 52;
    public static final int STATUS_TRIP_ENDED_BY_HOPPER = 53;


    public static final int PUSH_TYPE_PICKREQ_CREATED = 499;
    public static final int PUSH_TYPE_PICKREQ_ACCEPTED = 500;
    public static final int PUSH_TYPE_TRIP_ENDED = 501;
    public static final int PUSH_TYPE_CHAT = 502;

    public static final int PROVIDER_TYPE_FACEBOOK = 100;

    public static final int PICKUPREQ_EXPIRE_TIME_INTERVAL = 900000;

    public static final long alarmInterval = 600000;

    public static final int LOCATION_ALARM_ID = 100;
    public static final int TRIP_LOC_ALARM_ID = 101;
    public static final int ALERT_ALARM_ID = 102;

    //2mins
    public static long tripPollingInterval = 120000;
    //5mins
    public static long sosPollingInterval = 300000;

    public static final int  NETWORK_WORK = 1;
    public static final int  NETWORK_FACEBOOK = 2;


}
