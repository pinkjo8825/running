package com.net.runningwebservice;

import com.net.running_web_service.*;


import org.apache.jena.ontology.*;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.rulesys.GenericRuleReasonerFactory;
import org.apache.jena.util.PrintUtil;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.ReasonerVocabulary;
import org.apache.jena.riot.RDFDataMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;




@Endpoint
public class endpoint {

    String SOURCE = "http://www.semanticweb.org/guind/ontologies/runningeventontology";
    String NS = SOURCE + "#";
    String output_filename = "/Users/net/Downloads/running-web-service/src/main/resources/WriteInstance3.rdf";
    String rulesPath = "/Users/net/Downloads/running-web-service/src/main/resources/testrules1.rules";
    String runURI = "http://www.semanticweb.org/guind/ontologies/runningeventontology#";
    String ontologyPath = "/Users/net/Downloads/running-web-service/src/main/resources/RunningEventOntologyFinal2.rdf";

    Model data = RDFDataMgr.loadModel("file:" + output_filename);
    Model dataOnto = RDFDataMgr.loadModel("file:" + ontologyPath);


    private static final String NAMESPACE_URI = "http://net.com/running-web-service";


    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getRecommendEventRequest")
    @ResponsePayload
    public GetRecommendEventResponse getRecommendEvent(@RequestPayload GetRecommendEventRequest request) {
        System.out.println("getRecommendEvent");
        GetRecommendEventResponse response = new GetRecommendEventResponse();

        OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        OntDocumentManager dm = m.getDocumentManager();
        dm.addAltEntry("http://www.semanticweb.org/guind/ontologies/runningeventontology",
                "file:" + ontologyPath);
        m.read("http://www.semanticweb.org/guind/ontologies/runningeventontology", "RDF/XML");
        OntClass userClass = m.getOntClass(NS + "User");
        OntProperty userActivityArea = m.getDatatypeProperty(NS + "ActivityAreaInterest");
        OntProperty userStartPeriod = m.getDatatypeProperty(NS + "StartPeriodInterest");
        OntProperty userReward = m.getDatatypeProperty(NS + "RewardInterest");
        OntProperty hasRacetype = m.getObjectProperty(NS + "hasRaceTypeInterest");
        OntProperty hasOrganization = m.getObjectProperty(NS + "hasOrganizationInterest");
        OntProperty userLocation = m.getDatatypeProperty(NS + "LocationInterest");
        OntProperty userTypeOfEvent = m.getDatatypeProperty(NS + "TypeOfEventInterest");
        OntProperty userEventPrice = m.getDatatypeProperty(NS + "EventPriceInterest");
        OntProperty userLevelEvent = m.getDatatypeProperty(NS + "LevelEventInterest");
        OntProperty userStandardEvent = m.getDatatypeProperty(NS + "StandardEventInterest");
        OntProperty userName = m.getDatatypeProperty(NS + "Username");

        String userProfileName = "tempUserInf";
        Resource userInstance = m.createResource(NS + userProfileName);

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

        if (!districtReg.isEmpty()) {
            userInstance.addProperty(userLocation, districtReg);
        }
        if (!raceTypeReg.isEmpty()) {
            userInstance.addProperty(hasRacetype, raceTypeReg);
        }
        if (!typeofEventReg.isEmpty()) {
            userInstance.addProperty(userTypeOfEvent, typeofEventReg);
        }
        if (!priceReg.isEmpty()) {
            userInstance.addProperty(userEventPrice, priceReg);
        }
        if (!organizationReg.isEmpty()) {
            userInstance.addProperty(hasOrganization, organizationReg);
        }
        if (!activityAreaReg.isEmpty()) {
            userInstance.addProperty(userActivityArea, activityAreaReg);
        }
        if (!standardReg.isEmpty()) {
            userInstance.addProperty(userStandardEvent, standardReg);
        }
        if (!levelReg.isEmpty()) {
            userInstance.addProperty(userLevelEvent, levelReg);
        }
        if (!startPeriodReg.isEmpty()) {
            userInstance.addProperty(userStartPeriod, startPeriodReg);
        }
        if (!rewardReg.isEmpty()) {
            userInstance.addProperty(userReward, rewardReg);
        }

        try (FileOutputStream out = new FileOutputStream(output_filename)) {
            m.write(out, "RDF/XML");
        } catch (IOException e) {
            e.printStackTrace();
        }

        PrintUtil.registerPrefix("run", runURI);
        Model dataInf = RDFDataMgr.loadModel("file:" + output_filename);

        Model rm = ModelFactory.createDefaultModel();
        Resource configuration = rm.createResource();
        configuration.addProperty(ReasonerVocabulary.PROPruleMode, "hybrid");
        configuration.addProperty(ReasonerVocabulary.PROPruleSet, rulesPath);
        Reasoner reasoner = GenericRuleReasonerFactory.theInstance().create(configuration);
        InfModel inf = ModelFactory.createInfModel(reasoner, dataInf);

        Property p = dataInf.getProperty(runURI, "hasRecommend");
        Property c = dataInf.getProperty(runURI, "confidence");
        Resource a = dataInf.getResource(runURI + userProfileName);
        Property rn = dataInf.getProperty(runURI, "RunningEventName");

        StmtIterator i1 = inf.listStatements(a, p, (RDFNode) null);

//        Set<Statement> statements = new HashSet<>();
        while (i1.hasNext()) {
            GetRecommendEventResponse.RunningEvent event = new GetRecommendEventResponse.RunningEvent();
            Statement statement = i1.nextStatement();
//            statements.add(statement);
            String statementString = statement.getObject().toString();
            System.out.println(statementString);
            Resource re = data.getResource(statementString);
            StmtIterator i2 = inf.listStatements(re, c, (RDFNode) null);
            int conf = 0;
            while (i2.hasNext()) {
                Statement statement2 = i2.nextStatement();
//                statements.add(statement2);
                String confStr = statement2.getString();
                double confValue;
                try {
                    confValue = Double.parseDouble(confStr); // แปลง String เป็น double
                } catch (NumberFormatException e) {
                    continue;
                }
                int roundedConfValue = (int) Math.round(confValue); // แปลง double เป็น int โดยใช้ Math.round()
//                System.out.println("=> " + roundedConfValue);
                if (roundedConfValue > conf) {
                    conf = roundedConfValue;
                }
            }
            event.setRunningEventName(statementString);
            event.setConfidence(String.valueOf(conf));
//            event.setDistrict("district");
//            event.setRaceType("raceType");
//            event.setTypeofEvent("typeofEvent");
//            event.setPrice("price");
//            event.setOrganization("organization");
//            event.setActivityArea("activityArea");
//            event.setStandard("standard");
//            event.setLevel("level");
//            event.setStartPeriod("startPeriod");
//            event.setReward("reward");

            response.getRunningEvent().add(event);
            System.out.println(conf);
        }

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getUserProfileRequest")
    @ResponsePayload
    public GetUserProfileResponse getUserProfile(@RequestPayload GetUserProfileRequest request) {

        GetUserProfileResponse response = new GetUserProfileResponse();

        OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        OntDocumentManager dm = m.getDocumentManager();
        dm.addAltEntry("http://www.semanticweb.org/guind/ontologies/runningeventontology",
                "file:" + ontologyPath);
        m.read("http://www.semanticweb.org/guind/ontologies/runningeventontology", "RDF/XML");
        OntClass userClass = m.getOntClass(NS + "User");
        String username = request.getUsername();

        String userProfileName = "tempUserInf";
        Resource userInstance = m.createResource(NS + userProfileName);
        userInstance.addProperty(RDF.type, userClass);

        // Find the user resource by username
        Property usernameProperty = dataOnto.createProperty(NS + "Username");
        ResIterator users = dataOnto.listResourcesWithProperty(usernameProperty, username);

        if (users.hasNext()) {
            Resource user = users.nextResource();
            // Iterate over the properties of the user
            StmtIterator properties = user.listProperties();
            while (properties.hasNext()) {
                Statement stmt = properties.nextStatement();
                Property property = stmt.getPredicate();
                RDFNode value = stmt.getObject();
                // Add the property and its value to the userInstance
                if (value.isLiteral()) {
                    // If the value is a literal, add it as a literal
                    userInstance.addLiteral(property, value.asLiteral());
                } else if (value.isResource()) {
                    // If the value is a resource, add it as a resource
                    userInstance.addProperty(property, value.asResource());
                }
            }

        } else {
            System.out.println("User not found");
        }

        try (FileOutputStream out = new FileOutputStream(output_filename)) {
            m.write(out, "RDF/XML");
        } catch (IOException e) {
            e.printStackTrace();
        }

        PrintUtil.registerPrefix("run", runURI);
        Model dataInf = RDFDataMgr.loadModel("file:" + output_filename);

        Model rm = ModelFactory.createDefaultModel();
        Resource configuration = rm.createResource();
        configuration.addProperty(ReasonerVocabulary.PROPruleMode, "hybrid");
        configuration.addProperty(ReasonerVocabulary.PROPruleSet, rulesPath);
        Reasoner reasoner = GenericRuleReasonerFactory.theInstance().create(configuration);
        InfModel inf = ModelFactory.createInfModel(reasoner, dataInf);

        Property p = dataInf.getProperty(runURI, "hasRecommend");
        Property c = dataInf.getProperty(runURI, "confidence");
        Resource a = dataInf.getResource(runURI + userProfileName);
        Property rn = dataInf.getProperty(runURI, "RunningEventName");

        StmtIterator i1 = inf.listStatements(a, p, (RDFNode) null);

        while (i1.hasNext()) {
            GetUserProfileResponse.RunningEvent event = new GetUserProfileResponse.RunningEvent();
            Statement statement = i1.nextStatement();

            String statementString = statement.getObject().toString();
            System.out.println(statementString);
            Resource re = data.getResource(statementString);
            StmtIterator i2 = inf.listStatements(re, c, (RDFNode) null);
            int conf = 0;
            while (i2.hasNext()) {
                Statement statement2 = i2.nextStatement();

                String confStr = statement2.getString();
                double confValue;
                try {
                    confValue = Double.parseDouble(confStr);
                } catch (NumberFormatException e) {
                    continue;
                }
                int roundedConfValue = (int) Math.round(confValue);
                System.out.println("=> " + roundedConfValue);
                if (roundedConfValue > conf) {
                    conf = roundedConfValue;
                }
            }
            event.setRunningEventName(statementString);
            event.setConfidence(String.valueOf(conf));


            response.getRunningEvent().add(event);
            System.out.println(conf);
        }


        return response;

    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "setUserProfileRequest")
    @ResponsePayload
    public SetUserProfileResponse setUserProfile(@RequestPayload SetUserProfileRequest request) {
        SetUserProfileResponse response = new SetUserProfileResponse();

        OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        OntDocumentManager dm = m.getDocumentManager();
        dm.addAltEntry("http://www.semanticweb.org/guind/ontologies/runningeventontology",
                "file:" + ontologyPath);
        m.read("http://www.semanticweb.org/guind/ontologies/runningeventontology", "RDF/XML");
        OntClass userClass = m.getOntClass(NS + "User");
        OntProperty userActivityArea = m.getDatatypeProperty(NS + "ActivityAreaInterest");
        OntProperty userStartPeriod = m.getDatatypeProperty(NS + "StartPeriodInterest");
        OntProperty userReward = m.getDatatypeProperty(NS + "RewardInterest");
        OntProperty hasRacetype = m.getObjectProperty(NS + "hasRaceTypeInterest");
        OntProperty hasOrganization = m.getObjectProperty(NS + "hasOrganizationInterest");
        OntProperty userLocation = m.getDatatypeProperty(NS + "LocationInterest");
        OntProperty userTypeOfEvent = m.getDatatypeProperty(NS + "TypeOfEventInterest");
        OntProperty userEventPrice = m.getDatatypeProperty(NS + "EventPriceInterest");
        OntProperty userLevelEvent = m.getDatatypeProperty(NS + "LevelEventInterest");
        OntProperty userStandardEvent = m.getDatatypeProperty(NS + "StandardEventInterest");
        OntProperty userAge = m.getDatatypeProperty(NS + "UserAge");
        OntProperty userName = m.getDatatypeProperty(NS + "Username");
        OntProperty userNationality = m.getDatatypeProperty(NS + "UserNationality");
        OntProperty userSex = m.getDatatypeProperty(NS + "UserSex");

        System.out.println("Number of statements in OntModel: " + m.size());

        String userProfileName = request.getUsername();
        Resource userInstance = m.createResource(NS + userProfileName);

        Byte ageReg = request.getAge();
        String nationalityReg = request.getNationality();
        String genderReg = request.getGender();
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

        userInstance.addProperty(RDF.type, userClass);
        userInstance.addProperty(userName, userProfileName);
        userInstance.addProperty(userAge, String.valueOf(ageReg));
        userInstance.addProperty(userNationality, nationalityReg);
        userInstance.addProperty(userSex, genderReg);
        userInstance.addProperty(userLocation, districtReg);
        userInstance.addProperty(hasRacetype, raceTypeReg);
        userInstance.addProperty(userTypeOfEvent, typeofEventReg);
        userInstance.addProperty(userEventPrice, priceReg);
        userInstance.addProperty(hasOrganization, organizationReg);
        userInstance.addProperty(userActivityArea, activityAreaReg);
        userInstance.addProperty(userStandardEvent, standardReg);
        userInstance.addProperty(userLevelEvent, levelReg);
        userInstance.addProperty(userStartPeriod, startPeriodReg);
        userInstance.addProperty(userReward, rewardReg);

        try (FileOutputStream out = new FileOutputStream(ontologyPath)) {
            m.write(out, "RDF/XML");
            System.out.println(userInstance);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Number of statements in OntModel: " + m.size());
        response.setStatus("success");

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getEventRequest")
    @ResponsePayload
    public GetEventResponse getEventRequest(@RequestPayload GetEventRequest request) {
        GetEventResponse response = new GetEventResponse();

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

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAllEventsRequest")
    @ResponsePayload
    public GetAllEventsResponse getAllEventsRequest(@RequestPayload GetAllEventsRequest request) {
        GetAllEventsResponse response = new GetAllEventsResponse();

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
