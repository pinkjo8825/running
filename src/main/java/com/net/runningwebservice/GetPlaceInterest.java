package com.net.runningwebservice;

import com.net.running_web_service.GetPlaceInterestRequest;
import com.net.running_web_service.GetPlaceInterestResponse;


public class GetPlaceInterest {
    public static GetPlaceInterestResponse run(GetPlaceInterestRequest request) {
        GetPlaceInterestResponse response = new GetPlaceInterestResponse();
        String interestReg = request.getInterest();

        return response;
    }
}
