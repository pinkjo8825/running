package com.net.runningwebservice;

import com.net.running_web_service.GetEventRequest;
import com.net.running_web_service.GetEventResponse;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;

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

//      price, activityArea, StartPeriod, Reward aren't work
        StringBuilder queryStringBuilder = new StringBuilder();
        queryStringBuilder.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n")
                .append("PREFIX ro: <http://www.semanticweb.org/guind/ontologies/runningeventontology#>\n\n")
                .append("SELECT ?event ?eventName\nWHERE {\n")
                .append("  ?event rdf:type ro:RunningEvent .\n")
                .append("  ?event ro:RunningEventName ?eventName .\n");

        if (districtReg != null && !districtReg.isEmpty()) {
            queryStringBuilder.append("  ?event ro:hasEventVenue ?venue . ?venue ro:District \"").append(districtReg).append("\" .\n");
        }

        if (raceTypeReg != null && !raceTypeReg.isEmpty()) {
            queryStringBuilder.append("  ?event ro:hasRaceType ?raceType . ?raceType ro:RaceTypeName \"").append(raceTypeReg).append("\" .\n");
        }

        if (typeofEventReg != null && !typeofEventReg.isEmpty()) {
            queryStringBuilder.append("  ?event ro:TypeOfEvent \"").append(typeofEventReg).append("\" .\n");
        }

        if (organizationReg != null && !organizationReg.isEmpty()) {
            queryStringBuilder.append("  ?event ro:isOrganizedBy ?org . ?org ro:OrganizationName \"").append(organizationReg).append("\" .\n");
        }

        if (standardReg != null && !standardReg.isEmpty()) {
            queryStringBuilder.append("  ?event ro:StandardOfEvent \"").append(standardReg).append("\" .\n");
        }

        if (levelReg != null && !levelReg.isEmpty()) {
            queryStringBuilder.append("  ?event ro:LevelOfEvent \"").append(levelReg).append("\" .\n");
        }

        queryStringBuilder.append("}");

        String queryString = queryStringBuilder.toString();
        System.out.println(queryString);


        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, dataOnto);
        ResultSet resultSet = qexec.execSelect();
        System.out.println("resultSet " + resultSet);

        while (resultSet.hasNext()) {
            QuerySolution solution = resultSet.nextSolution();
            System.out.println("solution " + solution);
            String runningEventName = solution.contains("eventName") ? solution.getLiteral("eventName").getString() : "Unknown Event";
            System.out.println("runningEventName " + runningEventName);

            GetEventResponse.RunningEvent event = new GetEventResponse.RunningEvent();
            event.setRunningEventName(runningEventName);

            response.getRunningEvent().add(event);
        }
        qexec.close();
        return response;
    }
}
