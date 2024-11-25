package com.net.runningwebservice;

import com.net.running_web_service.GetRecommendEventRequest;
import com.net.running_web_service.GetRecommendEventResponse;
import org.apache.jena.ontology.*;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.rulesys.GenericRuleReasonerFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.util.PrintUtil;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.ReasonerVocabulary;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class GetRecommend {

    public static synchronized GetRecommendEventResponse run(GetRecommendEventRequest request) {
//        public static  GetRecommendEventResponse run(GetRecommendEventRequest request) {
//            synchronized (SharedConstants.lock) {

                int method = SharedConstants.methods;

                GetRecommendEventResponse response = new GetRecommendEventResponse();
                System.out.println("GetRecommendEventResponse");

                String NS = SharedConstants.NS;
                String runURI = SharedConstants.runURI;
                String ontologyPath = "file:RunningEventOntologyFinal2.rdf";
//                String ontologyPath = "file:Running_modify_4.rdf";
                String output_filename = "file:WriteInstance3.rdf";
                String rulesPath = "file:testrules1.rules";

//                String travelPlaceRule = "file:travelPlaceRule.rules";
//                String ontologyPath = "RunningEventOntologyFinal2.rdf";
//                String output_filename = "WriteInstance3.rdf";
//                String rulesPath = "testrules1.rules";

                String backup_filename = "WriteInstance3-backup.rdf";

                if(method == 3) {
                    try {
//                        Files.copy(Paths.get(backup_filename), Paths.get(output_filename), StandardCopyOption.REPLACE_EXISTING);
                        Files.copy(Paths.get(backup_filename), Paths.get("WriteInstance3.rdf"), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        System.out.println("1");
                        e.printStackTrace();
                    }
                }

                Model data = RDFDataMgr.loadModel(output_filename);


                OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
                OntDocumentManager dm = m.getDocumentManager();
                dm.addAltEntry("http://www.semanticweb.org/guind/ontologies/runningeventontology", ontologyPath);
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
                OntProperty placeType = m.getDatatypeProperty(NS + "TravelPlaceTypeInterest");
//        hasTravelPlaceTypeInterest

                OntClass minimarathonClass = m.getOntClass(NS + "MiniMarathon");
                OntClass marathonClass = (OntClass) m.getOntClass(NS + "Marathon");
                OntClass halfmarathonClass = (OntClass) m.getOntClass(NS + "HalfMarathon");
                OntClass funrunClass = (OntClass) m.getOntClass(NS + "FunRun");

                OntClass organizationClass = (OntClass) m.getOntClass(NS + "Organization");
                OntProperty organizationName = m.getDatatypeProperty(NS + "OrganizationName");


                String userProfileName = "tempUserInf";
                String userURI = NS + userProfileName;
                Resource userInstance = m.createResource(userURI);

                userInstance.addProperty(RDF.type, userClass);
                userInstance.addProperty(userName, userProfileName);

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

//                Beach
//                Natural
//                TownAndCity
//                Cultural
//                ShoppingAndDinning
//                EntertainmentAndNightLife
//                HealthAndWellness
                userInstance.addProperty(placeType, "Natural");


                if (districtReg != null) {
                    userInstance.addProperty(userLocation, districtReg);
                }
                if (raceTypeReg != null) {
                    switch (raceTypeReg) {
                        case "Mini Marathon":
                            userInstance.addProperty(hasRacetype, minimarathonClass);
                            break;
                        case "Marathon":
                            userInstance.addProperty(hasRacetype, marathonClass);
                            break;
                        case "Half Marathon":
                            userInstance.addProperty(hasRacetype, halfmarathonClass);
                            break;
                        case "Fun run":
                            userInstance.addProperty(hasRacetype, funrunClass);
                            break;
                        default:
                            break;
                    }
                }
                if (typeofEventReg != null) {
                    userInstance.addProperty(userTypeOfEvent, typeofEventReg);
                }
                if (priceReg != null) {
                    userInstance.addProperty(userEventPrice, priceReg);
                }
                if (organizationReg != null) {
                    ExtendedIterator instances = organizationClass.listInstances();
                    while (instances.hasNext()) {
                        Individual thisInstance = (Individual) instances.next();

                        if (organizationReg.equals(thisInstance.getProperty(organizationName).getString())) {

                            userInstance.addProperty(hasOrganization, thisInstance);
//                        System.out.println(userInstance.getProperty(hasOrganization).toString());

                        }
                    }
                }
                if (activityAreaReg != null) {
                    userInstance.addProperty(userActivityArea, activityAreaReg);
                }
                if (standardReg != null) {
                    userInstance.addProperty(userStandardEvent, standardReg);
                }
                if (levelReg != null) {
                    userInstance.addProperty(userLevelEvent, levelReg);
                }
                if (startPeriodReg != null) {
                    userInstance.addProperty(userStartPeriod, startPeriodReg);
                }
                if (rewardReg != null) {
                    userInstance.addProperty(userReward, rewardReg);
                }
//            System.out.println(userInstance);

                try {
                    m.write(new PrintWriter(new FileOutputStream("WriteInstance3.rdf")), "RDF/XML");
                } catch (FileNotFoundException ex) {
                    System.out.println("2");
                    ex.printStackTrace();
                }

                PrintUtil.registerPrefix("run", runURI);
                Model dataInf = RDFDataMgr.loadModel(output_filename);

                Model rm = ModelFactory.createDefaultModel();
                Resource configuration = rm.createResource();
                configuration.addProperty(ReasonerVocabulary.PROPruleMode, "hybrid");
                configuration.addProperty(ReasonerVocabulary.PROPruleSet, rulesPath);
                Reasoner reasoner = GenericRuleReasonerFactory.theInstance().create(configuration);
                InfModel inf = ModelFactory.createInfModel(reasoner, dataInf);

                Resource user = dataInf.getResource(runURI + userProfileName);
                Property p = dataInf.getProperty(runURI, "hasRecommend");
                Property c = dataInf.getProperty(runURI, "confidence");
                Property rn = data.getProperty(runURI, "RunningEventName");
                Property venueProperty = dataInf.getProperty(runURI, "hasEventVenue");
                Property districtProperty = dataInf.getProperty(runURI, "District");

//                StmtIterator i1 = inf.listStatements(user, p, (RDFNode) null);

//                try {
//                    inf.write(new PrintWriter(new FileOutputStream("WriteInstance3.rdf")), "RDF/XML");
//                } catch (FileNotFoundException ex) {
//                    ex.printStackTrace();
//                }

                Model rm2 = ModelFactory.createDefaultModel();
                Resource config2 = rm2.createResource();
                config2.addProperty(ReasonerVocabulary.PROPruleMode, "hybrid");
                config2.addProperty(ReasonerVocabulary.PROPruleSet, "file:travelPlaceRule.rules");
                Reasoner reasoner2 = GenericRuleReasonerFactory.theInstance().create(config2);
                InfModel inf2 = ModelFactory.createInfModel(reasoner2, inf);

                StmtIterator i1 = inf2.listStatements(user, p, (RDFNode) null);

        ArrayList<String> formattedEventNames = new ArrayList<String>();
        ArrayList<String> confList = new ArrayList<String>();
        ArrayList<String> districtList = new ArrayList<String>();

        while (i1.hasNext()) {
                Statement statement = i1.nextStatement();
                String resultName = PrintUtil.print(statement.getProperty(rn).getString());
                String statementString = statement.getObject().toString();
                Resource re = data.getResource(statementString);
                StmtIterator i2 = inf.listStatements(re, c, (RDFNode) null);

            Resource venue = re.getPropertyResourceValue(venueProperty);
            if (venue != null) {
                Statement districtStatement = venue.getProperty(districtProperty);
                if (districtStatement != null) {
                    String district = districtStatement.getString();
                    districtList.add(district);
                }
            }
                while (i2.hasNext()) {
                    Statement statement2 = i2.nextStatement();
                    String conf = statement2.getString();
                    confList.add(conf);
                }

                formattedEventNames.add(resultName);
        }

        try {
            inf2.write(new PrintWriter(new FileOutputStream("WriteInstance3.rdf")), "RDF/XML");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
//        System.out.println(districtList);

        Model model = RDFDataMgr.loadModel(output_filename);
//        Resource user = dataInf.getResource(runURI + userProfileName);
        Property hasTravelPlaceRecommend = model.getProperty(runURI, "hasTravelPlaceRecommend");
        StmtIterator iterator = model.listStatements(user, hasTravelPlaceRecommend, (RDFNode) null);

        while (iterator.hasNext()) {
            Statement stmt = iterator.nextStatement();
            Resource travelPlaceResource = stmt.getObject().asResource();

            String travelPlaceName = travelPlaceResource.hasProperty(model.createProperty(NS + "TravelPlaceName"))
                    ? travelPlaceResource.getProperty(model.createProperty(NS + "TravelPlaceName")).getString()
                    : null;
            String latitude = travelPlaceResource.hasProperty(model.createProperty(NS + "Latitude"))
                    ? travelPlaceResource.getProperty(model.createProperty(NS + "Latitude")).getString()
                    : null;
            String longitude = travelPlaceResource.hasProperty(model.createProperty(NS + "Longitude"))
                    ? travelPlaceResource.getProperty(model.createProperty(NS + "Longitude")).getString()
                    : null;
            String travelPlaceType = travelPlaceResource.hasProperty(model.createProperty(NS + "TravelPlaceType"))
                    ? travelPlaceResource.getProperty(model.createProperty(NS + "TravelPlaceType")).getString()
                    : null;
            String district = travelPlaceResource.hasProperty(model.createProperty(NS + "District"))
                    ? travelPlaceResource.getProperty(model.createProperty(NS + "District")).getString()
                    : null;

            GetRecommendEventResponse.TravelPlace travelPlace = new GetRecommendEventResponse.TravelPlace();
            travelPlace.setTravelPlaceName(travelPlaceName);
            travelPlace.setLatitude(latitude);
            travelPlace.setLongitude(longitude);
            travelPlace.setDistrict(district);
            if (travelPlaceType != null) {
                travelPlace.setTravelPlaceType(travelPlaceType);
            }

//            response.setTravelPlace(travelPlace);
            response.getTravelPlace().add(travelPlace);

        }



        if (formattedEventNames.isEmpty()) {
                    response.setStatus("empty");
                    return response;

                }

                String filterClause = formattedEventNames.stream()
                        .map(eventName -> "?eventName = \"" + eventName + "\"")
                        .collect(Collectors.joining(" || ", "FILTER (", ") ."));

//                Model dataOnto = RDFDataMgr.loadModel("file:" + SharedConstants.ontologyPath);
                    Model dataOnto = RDFDataMgr.loadModel("RunningEventOntologyFinal2.rdf");
                    Model infData = RDFDataMgr.loadModel("WriteInstance3.rdf");

                String queryString = """
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

                        """ + filterClause + "}";
//            System.out.println(queryString);
                Query query = QueryFactory.create(queryString);
                QueryExecution qexec = QueryExecutionFactory.create(query, dataOnto);
                ResultSet resultSet = qexec.execSelect();
                try {
                    Map<String, GetRecommendEventResponse.RunningEvent> eventsMap = new HashMap<>();

                    while (resultSet.hasNext()) {
                        QuerySolution solution = resultSet.nextSolution();

                        String eventName = solution.getLiteral("eventName").getString().trim();

                        GetRecommendEventResponse.RunningEvent event = eventsMap.get(eventName);
                        int index =  formattedEventNames.indexOf(eventName);

                        if (event == null) {
                            event = new GetRecommendEventResponse.RunningEvent();
                            event.setRunningEventName(eventName);
                            event.setConfidence(confList.get(index));
                            event.setDistrict(solution.getLiteral("district").getString().trim());
                            event.setTypeofEvent(solution.getLiteral("typeOfEvent").getString().trim());
                            event.setOrganization(solution.getLiteral("organizationName").getString().trim());
                            event.setActivityArea(solution.getLiteral("activityArea").getString().trim());
                            event.setStandard(solution.getLiteral("standardOfEvent").getString().trim());
                            event.setLevel(solution.getLiteral("levelOfEvent").getString().trim());
                            event.setStartPeriod(solution.getLiteral("startPeriod").getString().trim());

                            event.setPrices(new GetRecommendEventResponse.RunningEvent.Prices());
                            event.getPrices().getPrice().clear();
                            event.setRaceTypes(new GetRecommendEventResponse.RunningEvent.RaceTypes());
                            event.getRaceTypes().getRaceType().clear();
                            event.setRewards(new GetRecommendEventResponse.RunningEvent.Rewards());
                            event.getRewards().getReward().clear();

                            eventsMap.put(eventName, event);
                        }

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

                } finally {
                    qexec.close();
                }

                //replace
                if(method == 1) {
                Resource userResource = data.getResource(userURI);
                data.removeAll(userResource, null, (RDFNode) null);
                try {
                    data.write(new PrintWriter(new FileOutputStream("WriteInstance3.rdf")), "RDF/XML");
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
                }


                return response;
//            }



        }
}

