package com.jbrunnen.yourwaypizza.app;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;

public class JBrunnenTesting {
    // this class contains methods for testing code
    // test results are logged in logcat using log.d
    // to remove testing change the testMode boolean to false
    // leave all of the testing code in the main project so that later if regression testing
    // is required then the testing can be switched back on
    static final boolean testMode = false;
    // identify the tests so they can be filtered from the log file later if needed
    static final String testTAG = "jb testing";
    static public void testDataLog(String testId, String testActivity, String testData, String testComment) {
        String testLogOutput = "";
        if(testMode){
            testLogOutput = "|TestId: " + testId + "|Activity: " + testActivity + "|Testdata: " + testData + "|Comment: " + testComment;
            Log.d(testTAG, testLogOutput);
        }
    }

    // test for tablet device when program running
    // it gets the height and width from the current device
    static public boolean isDeviceTablet() {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float yInches= metrics.heightPixels/metrics.ydpi;
        float xInches= metrics.widthPixels/metrics.xdpi;
        double diagonalInches = Math.sqrt(xInches*xInches + yInches*yInches);

        JBrunnenTesting.testDataLog("ScreenSizeTest","jbrunnentesting","diagonal inches: " + diagonalInches, "");
        // if the device as greater than or equal to 7 inches then this screen is big enough for two columns
        return diagonalInches >= 7.0;
    }

}
