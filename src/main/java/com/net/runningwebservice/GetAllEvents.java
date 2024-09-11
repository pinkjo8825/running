package com.net.runningwebservice;

import com.net.running_web_service.GetAllEventsRequest;
import com.net.running_web_service.GetAllEventsResponse;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;


import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class GetAllEvents {


    public static GetAllEventsResponse run(GetAllEventsRequest request) {
        GetAllEventsResponse response = new GetAllEventsResponse();
        System.out.println("GetAllEventsResponse");
        String ontologyPath = "file:RunningEventOntologyFinal2.rdf";
        Model dataOnto = RDFDataMgr.loadModel(ontologyPath);

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
                  ?event re:TypeOfEvent ?typeOfEvent .
                  ?event re:hasRaceType ?raceType .
                  ?raceType re:RaceTypeName ?raceTypeName .
                  ?raceType re:ActivityArea ?activityArea .
                  ?raceType re:Price ?price .
                  ?raceType re:StartPeriod ?startPeriod .
                  ?raceType re:Reward ?reward .
                  ?event re:isOrganizedBy ?organization .
                  ?organization re:OrganizationName ?organizationName .
                  ?event re:StandardOfEvent ?standardOfEvent .
                  ?event re:LevelOfEvent ?levelOfEvent .
                }     
                """;

        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, dataOnto);
        ResultSet resultSet = qexec.execSelect();

        try {
            Map<String, GetAllEventsResponse.RunningEvent> eventsMap = new HashMap<>();
            int solutionCount = 0;

            while (resultSet.hasNext()) {
                QuerySolution solution = resultSet.nextSolution();
                solutionCount++;

                String eventName = solution.getLiteral("eventName").getString().trim();
                GetAllEventsResponse.RunningEvent event = eventsMap.computeIfAbsent(eventName, k -> {
                    GetAllEventsResponse.RunningEvent newEvent = new GetAllEventsResponse.RunningEvent();
                    newEvent.setRunningEventName(eventName);
                    newEvent.setDistrict(solution.getLiteral("district").getString().trim());
                    newEvent.setTypeofEvent(solution.getLiteral("typeOfEvent").getString().trim());
                    newEvent.setOrganization(solution.getLiteral("organizationName").getString().trim());
                    newEvent.setActivityArea(solution.getLiteral("activityArea").getString().trim());
                    newEvent.setStandard(solution.getLiteral("standardOfEvent").getString().trim());
                    newEvent.setLevel(solution.getLiteral("levelOfEvent").getString().trim());
                    newEvent.setStartPeriod(solution.getLiteral("startPeriod").getString().trim());
                    newEvent.setPrices(new GetAllEventsResponse.RunningEvent.Prices());
                    newEvent.getPrices().getPrice().clear();
                    newEvent.setRaceTypes(new GetAllEventsResponse.RunningEvent.RaceTypes());
                    newEvent.getRaceTypes().getRaceType().clear();
                    newEvent.setRewards(new GetAllEventsResponse.RunningEvent.Rewards());
                    newEvent.getRewards().getReward().clear();
                    return newEvent;
                });

                String raceType = solution.getLiteral("raceTypeName").getString().trim();
                if (!event.getRaceTypes().getRaceType().contains(raceType)) {
                    event.getRaceTypes().getRaceType().add(raceType);
                }

                String price = solution.getLiteral("price").getString().trim();
                if (!event.getPrices().getPrice().contains(price)) {
                    event.getPrices().getPrice().add(price);
                }

                String rewardName = solution.getLiteral("reward").getString().trim();
                if (!event.getRewards().getReward().contains(rewardName)) {
                    event.getRewards().getReward().add(rewardName);
                }
            }
            response.getRunningEvent().addAll(eventsMap.values());

//            System.out.println("Total solutions processed: " + solutionCount);
        } finally {
            qexec.close();
        }

        return response;
    }
}
