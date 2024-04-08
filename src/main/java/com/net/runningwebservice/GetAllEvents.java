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
                PREFIX event: <http://www.semanticweb.org/guind/ontologies/runningeventontology#>
                                
                SELECT ?eventName
                WHERE {
                  ?event event:RunningEventName ?eventName .
                }
                                
                """;

        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, dataOnto);
        ResultSet resultSet = qexec.execSelect();

        while (resultSet.hasNext()) {
            QuerySolution solution = resultSet.nextSolution();
            System.out.println("solution " + solution);
            String runningEventName = solution.contains("eventName") ? solution.getLiteral("eventName").getString() : "Unknown Event";
            System.out.println("runningEventName " + runningEventName);

            GetAllEventsResponse.RunningEvent event = new GetAllEventsResponse.RunningEvent();
            event.setRunningEventName(runningEventName);
            event.setDistrict("a");
            event.setRaceType("b");
            event.setTypeofEvent("c");
            event.setPrice("d");
            event.setOrganization("e");
            event.setActivityArea("f");
            event.setStandard("g");
            event.setLevel("h");
            event.setStartPeriod("i");
            event.setReward("j");

            response.getRunningEvent().add(event);
        }

        qexec.close();
        return response;
    }
}
