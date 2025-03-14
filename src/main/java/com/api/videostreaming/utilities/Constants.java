package com.api.videostreaming.utilities;

public class Constants {
    private Constants(){}
    
    /*
     * Generic Constants
    */
    public static final int SUCCESS_CODE = 200; 
    public static final int NO_CONTENT_CODE = 204; 
    public static final int UNAUTHORIZED_CODE = 401;
    public static final int EXPIRED_JWT_CODE = 403;
    public static final int BAD_REQUEST_CODE = 400;
    public static final int FORBIDDEN_CODE = 403;
    public static final int NOT_FOUND_CODE = 404;
    public static final int CONFLICT_CODE = 409;
    public static final int TOO_MANY_REQUESTS_CODE = 429;
    public static final int INTERNAL_SERVER_ERROR_CODE = 500;
    public static final int BAD_GATEWAY_ERROR_CODE = 502;

    public static final String SUCCESSFUL = "successful";
    public static final String UNSUCCESSFUL = "unsuccessful";
    
    public static final String JWT_TOKEN_EXPIRED = "JWT_TOKEN_EXPIRED";
    public static final String MISSING_AUTHORIZATION_HEADER = "MISSING_AUTHORIZATION_HEADER";
    public static final String BAD_REQUEST = "BAD_REQUEST";
    public static final String INVALID_JWT_REQUEST = "INVALID_JWT_REQUEST";
    public static final String INVALID_JWT_TOKEN = "INVALID_JWT_TOKEN";
    public static final String UNAUTHORIZED_REQUEST = "UNAUTHORIZED_REQUEST";
    public static final String SERVER_ERROR = "SERVER_ERROR";
    public static final String BAD_GATEWAY_ERROR = "BAD_GATEWAY_ERROR";
    public static final String INVALID_PARAMETERS = "INVALID_PARAMETERS";
    public static final String PARAMETERS_MISSING = "PARAMETERS_MISSING";
    public static final String TOO_MANY_REQUESTS = "TOO_MANY_REQUESTS";
    public static final String INVALID_USER_TYPE = "INVALID_USER_TYPE";
    public static final String NO_CONTENT = "NO_CONTENT";

    /*
     * Code Base Constats
     */
    public static final String REQUEST = "## Request: ";
    public static final String RESPONSE = "-- Response: ";
    public static final String TOKEN_TYPE = "tokenType"; 
    public static final String TYPE_REFRESH_TOKEN = "refresh";
    public static final String TYPE_AUTH_TOKEN = "auth";

    /*
     * ADMIN Constats
    */
    public static final String BAD_CREDENTIALS = "Invalid credentials, user not found";
    public static final String TOKEN_GENERATED = "token generated";

    /*
     * VIDEO Constats
    */
    public static final String VIDEO_META_DATA_FETCH = "meta data fetched";
    public static final String VIDEO_PUBLISHED = "video published";
    public static final String INVALID_VIDEO_CONTENT_ID = "INVALID_VIDEO_CONTENT_ID";
    public static final String META_DATA_UPDATED_SUCCESSFULLY = "META_DATA_UPDATED_SUCCESSFULLY";
    public static final String VIDEO_DELETED = "video deleted";
    public static final String VIDEO_CONTENT_DATA_FETCH = "VIDEO_CONTENT_DATA_FETCH";

    /*
     * Engagment strategy
    */
    public static final String ENGAGEMENT_REGISTER = "ENGAGEMENT_REGISTER";
}

