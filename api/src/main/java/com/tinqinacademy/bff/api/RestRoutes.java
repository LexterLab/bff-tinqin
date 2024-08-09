package com.tinqinacademy.bff.api;

public class RestRoutes {
    public static final String ROOT = "/";
    public static final String HOTEL = ROOT + "/hotel";
    public static final String SYSTEM = ROOT + "/system";
    public static final String GET_ROOM_COMMENTS = HOTEL +  "/{roomId}/comment";
}
