package com.net.runningwebservice;

import com.net.running_web_service.GetAllEventsResponse;
import com.net.running_web_service.GetEventRequest;
import com.net.running_web_service.GetEventResponse;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;

import java.util.*;

public class GetEvent {

    public static GetEventResponse run(GetEventRequest request) {
        GetEventResponse response = new GetEventResponse();

        Model dataOnto = RDFDataMgr.loadModel("file:" + SharedConstants.ontologyPath);

        String districtReg = request.getDistrict();
        String raceTypeReg = request.getRaceType();
        String typeofEventReg = request.getTypeofEvent();
        String priceReg = request.getPrice();
        String organizationReg = request.getOrganization();
        String activityAreaReg = request.getActivityArea();
        String standardReg = request.getStandard();
        String levelReg = request.getLevel();
        String startPeriodReg = request.getStartPeriod();
        String rewardReg = request.getReward();

        StringBuilder queryStringBuilder = new StringBuilder("""
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
        """);

        if (districtReg != null) {
            queryStringBuilder.append(String.format("FILTER(?district = \"%s\") .\n", districtReg));
        }
        if (typeofEventReg != null) {
            queryStringBuilder.append(String.format("FILTER(?typeOfEvent = \"%s\") .\n", typeofEventReg));
        }
        if (raceTypeReg != null) {
            queryStringBuilder.append(String.format("FILTER(?raceTypeName = \"%s\") .\n", raceTypeReg));
        }
        if (priceReg != null) {
            queryStringBuilder.append(String.format("FILTER(?price = \"%s\") .\n", priceReg));
        }
        if (organizationReg != null) {
            queryStringBuilder.append(String.format("FILTER(?organizationName = \"%s\") .\n", organizationReg));
        }
        if (activityAreaReg != null) {
            queryStringBuilder.append(String.format("FILTER(?activityArea = \"%s\") .\n", activityAreaReg));
        }
        if (standardReg != null) {
            queryStringBuilder.append(String.format("FILTER(?standardOfEvent = \"%s\") .\n", standardReg));
        }
        if (levelReg != null) {
            queryStringBuilder.append(String.format("FILTER(?levelOfEvent = \"%s\") .\n", levelReg));
        }
        if (startPeriodReg != null) {
            queryStringBuilder.append(String.format("FILTER(?startPeriod = \"%s\") .\n", startPeriodReg));
        }
        if (rewardReg != null) {
            queryStringBuilder.append(String.format("FILTER(?reward = \"%s\") .\n", rewardReg));
        }


        queryStringBuilder.append("}");
        String queryString = queryStringBuilder.toString();
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, dataOnto);
        ResultSet resultSet = qexec.execSelect();

        System.out.println(queryString);

        try {
            Map<String, GetEventResponse.RunningEvent> eventsMap = new HashMap<>();
            int solutionCount = 0;

            while (resultSet.hasNext()) {
                QuerySolution solution = resultSet.nextSolution();
                solutionCount++;

                String eventName = solution.getLiteral("eventName").getString().trim();
                GetEventResponse.RunningEvent event = eventsMap.computeIfAbsent(eventName, k -> {
                    GetEventResponse.RunningEvent newEvent = new GetEventResponse.RunningEvent();
                    newEvent.setRunningEventName(eventName);
                    newEvent.setDistrict(solution.getLiteral("district").getString().trim());
                    newEvent.setTypeofEvent(solution.getLiteral("typeOfEvent").getString().trim());
                    newEvent.setOrganization(solution.getLiteral("organizationName").getString().trim());
                    newEvent.setActivityArea(solution.getLiteral("activityArea").getString().trim());
                    newEvent.setStandard(solution.getLiteral("standardOfEvent").getString().trim());
                    newEvent.setLevel(solution.getLiteral("levelOfEvent").getString().trim());
                    newEvent.setStartPeriod(solution.getLiteral("startPeriod").getString().trim());
                    newEvent.setPrices(new GetEventResponse.RunningEvent.Prices());
                    newEvent.getPrices().getPrice().clear(); // Initialize the list
                    newEvent.setRaceTypes(new GetEventResponse.RunningEvent.RaceTypes());
                    newEvent.getRaceTypes().getRaceType().clear(); // Initialize the list
                    newEvent.setRewards(new GetEventResponse.RunningEvent.Rewards());
                    newEvent.getRewards().getReward().clear(); // Initialize the list
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

            System.out.println("Total solutions processed: " + solutionCount);
        } finally {
            qexec.close();
        }

        return response;
    }
}
