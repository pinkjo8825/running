package com.net.runningwebservice;

import com.net.running_web_service.GetPlaceInterestRequest;
import com.net.running_web_service.GetPlaceInterestResponse;
import com.net.running_web_service.GetPlaceInterestResponse.TravelPlace;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.RDF;


public class GetPlaceInterest {
    public static GetPlaceInterestResponse run(GetPlaceInterestRequest request) {
        GetPlaceInterestResponse response = new GetPlaceInterestResponse();
        System.out.println("GetPlaceInterestRequest");
        String interestReg = request.getInterest();
//        beach
//        natural
//        townAndCity
//        cultural
//        shoppingAndDining
//        entertainmentAndNightLife
//        healthAndWellness
        String NS = SharedConstants.NS;
        String ontologyPath = "file:RunningEventOntologyFinal2.rdf";
        Model model = RDFDataMgr.loadModel(ontologyPath);

        StmtIterator travelPlaceIterator = model.listStatements(null, RDF.type, model.createResource(NS + "TravelPlace"));
        while (travelPlaceIterator.hasNext()) {
            Statement travelPlaceStmt = travelPlaceIterator.nextStatement();
            Resource travelPlaceResource = travelPlaceStmt.getSubject();

            String travelPlaceName = travelPlaceResource.hasProperty(model.createProperty(NS + "TravelPlaceName"))
                    ? travelPlaceResource.getProperty(model.createProperty(NS + "TravelPlaceName")).getString()
                    : "unknown";

            String travelPlaceType = travelPlaceResource.hasProperty(model.createProperty(NS + "TravelPlaceType"))
                    ? travelPlaceResource.getProperty(model.createProperty(NS + "TravelPlaceType")).getString()
                    : null;

            String district = travelPlaceResource.hasProperty(model.createProperty(NS + "District"))
                    ? travelPlaceResource.getProperty(model.createProperty(NS + "District")).getString()
                    : null;

            double travelPlaceLatitude = travelPlaceResource.hasProperty(model.createProperty(NS + "Latitude"))
                    ? Double.parseDouble(travelPlaceResource.getProperty(model.createProperty(NS + "Latitude")).getString())
                    : 0.0;

            double travelPlaceLongitude = travelPlaceResource.hasProperty(model.createProperty(NS + "Longitude"))
                    ? Double.parseDouble(travelPlaceResource.getProperty(model.createProperty(NS + "Longitude")).getString())
                    : 0.0;

            if (travelPlaceType != null) {
                if (travelPlaceType.equalsIgnoreCase(interestReg)) {
                    TravelPlace travelPlace = new TravelPlace();
                    travelPlace.setTravelPlaceName(travelPlaceName);
                    travelPlace.setTravelPlaceType(travelPlaceType);
                    if (district != null) {
                        travelPlace.setDistrict(district);
                    }
                    travelPlace.setLatitude(String.valueOf(travelPlaceLatitude));
                    travelPlace.setLongitude(String.valueOf(travelPlaceLongitude));

                    response.getTravelPlace().add(travelPlace);
                }
            }
        }

        return response;
    }
}
