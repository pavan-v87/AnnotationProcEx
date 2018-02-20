package com.example;

/**
 * Created by Pavan.VijayaNar on 9/6/2017.
 */

public class LogConstants {
    public static final String CATEGORY_OTA_PLAYBACK = "ota-playback";
    public static final String OTA_PLAY_BACK_START_TIME = "ota-playback-start-time";

    public static final int START_POINT = 0;
    public static final int END_POINT = 1;

   //@IntDef({START_POINT, END_POINT})
    public @interface POINT{}

   // @StringDef({CATEGORY_OTA_PLAYBACK})
    public @interface CATEGORY{}

   // @StringDef({OTA_PLAY_BACK_START_TIME})
    public @interface EVENT{}
}
