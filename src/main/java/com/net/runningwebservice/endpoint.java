package com.net.runningwebservice;

import com.net.running_web_service.*;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class endpoint {

    private static final String NAMESPACE_URI = "http://net.com/running-web-service";

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getRecommendEventRequest")
    @ResponsePayload
    public GetRecommendEventResponse getRecommendEvent(@RequestPayload GetRecommendEventRequest request) {
        return GetRecommend.run(request);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getUserProfileRequest")
    @ResponsePayload
    public GetUserProfileResponse getUserProfile(@RequestPayload GetUserProfileRequest request) {
        return GetUserProfile.run(request);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "setUserProfileRequest")
    @ResponsePayload
    public SetUserProfileResponse setUserProfile(@RequestPayload SetUserProfileRequest request) {
        return SetUserProfile.run(request);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getEventRequest")
    @ResponsePayload
    public GetEventResponse getEventRequest(@RequestPayload GetEventRequest request) {
        return GetEvent.run(request);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAllEventsRequest")
    @ResponsePayload
    public GetAllEventsResponse getAllEventsRequest(@RequestPayload GetAllEventsRequest request) {
        return GetAllEvents.run(request);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getNearByRequest")
    @ResponsePayload
    public GetNearByResponse getNearByRequest(@RequestPayload GetNearByRequest request) {
        return GetNearBy.run(request);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "authRequest")
    @ResponsePayload
    public AuthResponse authRequest(@RequestPayload AuthRequest request) {
        return Auth.run(request);
    }
}
