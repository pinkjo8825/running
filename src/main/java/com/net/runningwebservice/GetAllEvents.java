package com.net.runningwebservice;

import com.net.running_web_service.GetAllEventsRequest;
import com.net.running_web_service.GetAllEventsResponse;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;

import java.util.List;

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

        try {
            GetAllEventsResponse.RunningEvent currentEvent = null;
            String currentEventName = "";

            while (resultSet.hasNext()) {
                QuerySolution solution = resultSet.nextSolution();
                String eventName = solution.getLiteral("eventName").getString();

                if (!eventName.equals(currentEventName)) {

                    if (currentEvent != null) {
                        response.getRunningEvent().add(currentEvent);
                    }

                    currentEvent = new GetAllEventsResponse.RunningEvent();
                    currentEventName = eventName;
                    currentEvent.setRunningEventName(eventName);
                    currentEvent.setDistrict(solution.getLiteral("district").getString());
                    currentEvent.setTypeofEvent(solution.getLiteral("typeOfEvent").getString());
                    currentEvent.setOrganization(solution.getLiteral("organizationName").getString());
                    currentEvent.setActivityArea(solution.getLiteral("activityArea").getString());
                    currentEvent.setStandard(solution.getLiteral("standardOfEvent").getString());
                    currentEvent.setLevel(solution.getLiteral("levelOfEvent").getString());
                    currentEvent.setStartPeriod(solution.getLiteral("startPeriod").getString());

                    currentEvent.setPrices(new GetAllEventsResponse.RunningEvent.Prices());
                    currentEvent.setRaceTypes(new GetAllEventsResponse.RunningEvent.RaceTypes());
                    currentEvent.setRewards(new GetAllEventsResponse.RunningEvent.Rewards());
                }

                String raceTypeName = solution.getLiteral("raceTypeName").getString();
                if (!currentEvent.getRaceTypes().getRaceType().contains(raceTypeName)) {
                    currentEvent.getRaceTypes().getRaceType().add(raceTypeName);
                }
                String price = solution.getLiteral("price").getString();
                if (!currentEvent.getPrices().getPrice().contains(price)) {
                    currentEvent.getPrices().getPrice().add(price);
                }
                String rewardName = solution.getLiteral("reward").getString();
                if (!currentEvent.getRewards().getReward().contains(rewardName)) {
                    currentEvent.getRewards().getReward().add(rewardName);
                }

            }

            if (currentEvent != null) {
                response.getRunningEvent().add(currentEvent);
            }
        } finally {
            qexec.close();
        }

        return response;
    }
}
