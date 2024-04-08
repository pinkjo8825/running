package com.net.runningwebservice;

import com.net.running_web_service.AuthRequest;
import com.net.running_web_service.AuthResponse;


public class Auth {

    public static AuthResponse run(AuthRequest request) {
        AuthResponse response = new AuthResponse();

        response.setStatus("test");
        return response;
    }
}
