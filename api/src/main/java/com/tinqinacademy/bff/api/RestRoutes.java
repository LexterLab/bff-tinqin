package com.tinqinacademy.bff.api;

public class RestRoutes {
    public static final String ROOT = "/api/v1";
    public static final String HOTEL = ROOT + "/hotel";
    public static final String SYSTEM = ROOT + "/system";
    public static final String GET_ROOM_COMMENTS = HOTEL +  "/{roomId}/comment";
    public static final String EDIT_COMMENT = HOTEL +  "/comment/{commentId}";
    public static final String LEAVE_COMMENT = HOTEL +  "/{roomId}/comment";
    public static final String EDIT_USER_COMMENT = SYSTEM +  "/comment/{commentId}";
}
