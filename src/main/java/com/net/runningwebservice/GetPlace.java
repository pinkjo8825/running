package com.net.runningwebservice;

import com.net.running_web_service.GetPlaceResponse;
import com.net.running_web_service.GetPlaceRequest;

public class GetPlace {
    public static GetPlaceResponse run(GetPlaceRequest request) {
        GetPlaceResponse response = new GetPlaceResponse();
        String districtReg = request.getDistrict();

        return response;
    }
}
