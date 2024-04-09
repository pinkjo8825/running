package com.net.runningwebservice;

import com.net.running_web_service.GetAllEventsRequest;
import com.net.running_web_service.GetAllEventsResponse;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;

public class GetAllEvents {

    public static GetAllEventsResponse run(GetAllEventsRequest request) {
        GetAllEventsResponse response = new GetAllEventsResponse();

        Model dataOnto = RDFDataMgr.loadModel("file:" + SharedConstants.ontologyPath);

        String queryString  = """
                PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
                PREFIX owl: <http://www.w3.org/2002/07/owl#>
                PREFIX re: <http://www.semanticweb.org/guind/ontologies/runningeventontology#>

                SELECT ?eventName ?district ?raceTypeName ?typeOfEvent ?price ?organizationName ?activityArea ?standardOfEvent ?levelOfEvent ?startPeriod ?reward
                WHERE {
                  ?event rdf:type re:RunningEvent .
                  ?event re:RunningEventName ?eventName .
                  ?event re:hasEventVenue ?venue .
                  ?venue re:District ?district .
                  ?event re:TypeOfEvent ?typeOfEvent . \s
                  ?event re:hasRaceType ?raceType .
                  ?raceType re:RaceTypeName ?raceTypeName .
                  ?raceType re:ActivityArea ?activityArea .\s
                  ?raceType re:Price ?price .\s
                  ?raceType re:StartPeriod ?startPeriod .\s
                  ?raceType re:Reward ?reward .\s
                  ?event re:isOrganizedBy ?organization .\s
                  ?organization re:OrganizationName ?organizationName .\s
                  ?event re:StandardOfEvent ?standardOfEvent .\s
                  ?event re:LevelOfEvent ?levelOfEvent .\s
                }     
                """;

        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, dataOnto);
        ResultSet resultSet = qexec.execSelect();

        while (resultSet.hasNext()) {
            QuerySolution solution = resultSet.nextSolution();

            String runningEventName = solution.getLiteral("eventName") != null ? solution.getLiteral("eventName").getString() : null;
            String districtName = solution.getLiteral("district") != null ? solution.getLiteral("district").getString() : null;
            String raceTypeName = solution.getLiteral("raceTypeName") != null ? solution.getLiteral("raceTypeName").getString() : null;
            String typeofEventName = solution.getLiteral("typeOfEvent") != null ? solution.getLiteral("typeOfEvent").getString() : null;
            String priceName = solution.getLiteral("price") != null ? solution.getLiteral("price").getString() : null;
            String organizationName = solution.getLiteral("organizationName") != null ? solution.getLiteral("organizationName").getString() : null;
            String activityAreaName = solution.getLiteral("activityArea") != null ? solution.getLiteral("activityArea").getString() : null;
            String standardName = solution.getLiteral("standardOfEvent") != null ? solution.getLiteral("standardOfEvent").getString() : null;
            String levelName = solution.getLiteral("levelOfEvent") != null ? solution.getLiteral("levelOfEvent").getString() : null;
            String startPeriodName = solution.getLiteral("startPeriod") != null ? solution.getLiteral("startPeriod").getString() : null;
            String rewardName = solution.getLiteral("reward") != null ? solution.getLiteral("reward").getString() : null;


            GetAllEventsResponse.RunningEvent event = new GetAllEventsResponse.RunningEvent();
            event.setRunningEventName(runningEventName);
            event.setDistrict(districtName);
            event.setRaceType(raceTypeName);
            event.setTypeofEvent(typeofEventName);
            event.setPrice(priceName);
            event.setOrganization(organizationName);
            event.setActivityArea(activityAreaName);
            event.setStandard(standardName);
            event.setLevel(levelName);
            event.setStartPeriod(startPeriodName);
            event.setReward(rewardName);

            response.getRunningEvent().add(event);
        }

        qexec.close();
        return response;
    }
}
