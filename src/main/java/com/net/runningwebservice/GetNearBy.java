
package com.net.runningwebservice;

import com.net.running_web_service.GetNearByRequest;
import com.net.running_web_service.GetNearByResponse;
import com.net.running_web_service.GetNearByResponse.TravelPlace;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.RDF;

public class GetNearBy {

    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6378.1; // Radius of the Earth in kilometers

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon1 - lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c * 1000; // Distance in meters
    }

    public static GetNearByResponse run(GetNearByRequest request) {
        GetNearByResponse response = new GetNearByResponse();
        System.out.println("Processing GetNearByRequest");

        String runningEventNameReg = request.getRunningEventName();
        double radius = Double.parseDouble(request.getRadius());
        String NS = SharedConstants.NS;
        String ontologyPath = "file:RunningEventOntologyFinal2.rdf";

        // Load RDF model using RDFDataMgr
        Model model = RDFDataMgr.loadModel(ontologyPath);

        try {
            // Find the selected RunningEvent by name
            StmtIterator iterator = model.listStatements(null, RDF.type, model.createResource(NS + "RunningEvent"));
            Resource selectedEventResource = null;

            while (iterator.hasNext()) {
                Statement stmt = iterator.nextStatement();
                Resource runningEventResource = stmt.getSubject();

                String name = runningEventResource.getProperty(model.createProperty(NS + "RunningEventName")).getString();
                if (name.equalsIgnoreCase(runningEventNameReg)) {
                    selectedEventResource = runningEventResource;
                    break;
                }
            }

            if (selectedEventResource == null) {
                System.err.println("No RunningEvent found with the name: " + runningEventNameReg);
                return response;
            }

            double eventLatitude = Double.parseDouble(selectedEventResource.getProperty(model.createProperty(NS + "Latitude")).getString());
            double eventLongitude = Double.parseDouble(selectedEventResource.getProperty(model.createProperty(NS + "Longitude")).getString());


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


                double distance = haversine(eventLatitude, eventLongitude, travelPlaceLatitude, travelPlaceLongitude);
                if (distance <= radius) {
                    TravelPlace travelPlace = new TravelPlace();
                    travelPlace.setTravelPlaceName(travelPlaceName);

                    if (travelPlaceType != null) {
                        travelPlace.setTravelPlaceType(travelPlaceType);
                    }
                    travelPlace.setDistance(String.valueOf(distance));
                    travelPlace.setDistrict(district);
                    travelPlace.setLatitude(String.valueOf(travelPlaceLatitude));
                    travelPlace.setLongitude(String.valueOf(travelPlaceLongitude));

                    response.getTravelPlace().add(travelPlace);
                }
            }
            if (response.getTravelPlace().isEmpty()) {
                System.out.println("No attractions found within the specified radius.");
            } else {
                System.out.println("Nearby attractions retrieved successfully.");
            }
        } catch (Exception e) {
            System.err.println("Error occurred while processing the request: " + e.getMessage());
            e.printStackTrace();
        }

        return response;
    }
}