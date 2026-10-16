package com.gastonnicora.trips.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserAgentTest {

    private static final String CHROME_DESKTOP = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
            + "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
    private static final String CHROME_ANDROID = "Mozilla/5.0 (Linux; Android 13; Pixel 7) "
            + "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36";
    private static final String SAFARI_IPHONE = "Mozilla/5.0 (iPhone; CPU iPhone OS 17_0 like Mac OS X) "
            + "AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.0 Mobile/15E148 Safari/604.1";

    @Test
    void shouldReturnAndroidForDalvik() {
        assertEquals("android", UserAgent.getDevice("Dalvik/2.1.0 (Linux; U; Android 13; Pixel 7)"));
    }

    @Test
    void shouldReturnAndroidForOkHttp() {
        assertEquals("android", UserAgent.getDevice("okhttp/4.9.0"));
    }

    @Test
    void shouldReturnAndroidForRetrofit() {
        assertEquals("android", UserAgent.getDevice("Retrofit/2.9.0"));
    }

    @Test
    void shouldReturnIosForAlamofire() {
        assertEquals("ios", UserAgent.getDevice("TripsApp/1.0 (com.trips.app; build:1; iOS 17.0.0) Alamofire/5.8.0"));
    }

    @Test
    void shouldReturnIosForCfNetworkClient() {
        assertEquals("ios", UserAgent.getDevice("TripsApp/1.0 CFNetwork/1494.0.7 Darwin/23.4.0"));
    }

    @Test
    void shouldReturnWebForChromeOnAndroid() {
        assertEquals("web", UserAgent.getDevice(CHROME_ANDROID));
    }

    @Test
    void shouldReturnWebForDesktopBrowser() {
        assertEquals("web", UserAgent.getDevice(CHROME_DESKTOP));
    }

    @Test
    void shouldReturnWebForSafariOnIphone() {
        assertEquals("web", UserAgent.getDevice(SAFARI_IPHONE));
    }

    @Test
    void shouldReturnWebForUnknownUserAgent() {
        assertEquals("web", UserAgent.getDevice("JUnit-Test"));
    }

    @Test
    void shouldReturnWebWhenUserAgentIsBlank() {
        assertEquals("web", UserAgent.getDevice("   "));
    }

    @Test
    void shouldReturnWebWhenUserAgentIsNull() {
        assertEquals("web", UserAgent.getDevice(null));
    }
}