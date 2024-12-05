package com.net.runningwebservice;

import com.net.running_web_service.GetPlaceResponse;
import com.net.running_web_service.GetPlaceRequest;
import com.net.running_web_service.GetPlaceResponse.TravelPlace;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.RDF;

public class GetPlace {
    public static GetPlaceResponse run(GetPlaceRequest request) {
        GetPlaceResponse response = new GetPlaceResponse();
        System.out.println("GetPlaceRequest");
        String districtReg = request.getDistrict();

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
                    : "unknown";

            double travelPlaceLatitude = travelPlaceResource.hasProperty(model.createProperty(NS + "Latitude"))
                    ? Double.parseDouble(travelPlaceResource.getProperty(model.createProperty(NS + "Latitude")).getString())
                    : 0.0;

            double travelPlaceLongitude = travelPlaceResource.hasProperty(model.createProperty(NS + "Longitude"))
                    ? Double.parseDouble(travelPlaceResource.getProperty(model.createProperty(NS + "Longitude")).getString())
                    : 0.0;

            String hotScore = travelPlaceResource.hasProperty(model.createProperty(NS + "HotScore"))
                    ? travelPlaceResource.getProperty(model.createProperty(NS + "HotScore")).getString()
                    : null;

            if (district.equalsIgnoreCase(districtReg)) {
                TravelPlace travelPlace = new TravelPlace();
                travelPlace.setTravelPlaceName(travelPlaceName);

                if (travelPlaceType != null) {
                    travelPlace.setTravelPlaceType(travelPlaceType);
                }
                travelPlace.setDistrict(district);
                travelPlace.setHotScore(hotScore);
                travelPlace.setLatitude(String.valueOf(travelPlaceLatitude));
                travelPlace.setLongitude(String.valueOf(travelPlaceLongitude));

                response.getTravelPlace().add(travelPlace);
            }
        }

        return response;
    }
}
