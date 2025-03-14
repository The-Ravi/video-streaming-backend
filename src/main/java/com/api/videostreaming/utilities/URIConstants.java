package com.api.videostreaming.utilities;

public class URIConstants {
    private URIConstants() {}

    public static final String API_VERSION = "/v1";

    public static final String GET = "/get";
    public static final String POST = "/post";
    public static final String GET_ALL = "/getAll";
    
    /*
     * AUTH URIS
     */
    public static final String AUTH_BASE_URL = "/auth";
    public static final String GET_TOKEN = "/generate-token";
    public static final String REFRESH_TOKEN = "/token-refresh";


        /*
     * VIDEO URIS
    */
    public static final String VIDEO_BASE_URL = "/api/video";
    public static final String PUBLISH_VIDEO = "/{videoId}/publish";
    public static final String ADD_OR_EDIT_META_DATA = "/metadata/{videoId}";
    public static final String VIDEO_ID = "/{videoId}";
    public static final String LOAD_VIDEO = "/{videoId}/load";
    public static final String PLAY_VIDEO = "/{videoId}/play";
    public static final String TRACK_ENGAGEMENT = "/{videoId}/track";
    public static final String SEARCH = "/search";


    /*
     * ENGAGEMENT URIS
    */
    public static final String ES_BASE_URL = "/engagement";

}