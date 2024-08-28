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
                String output_filename = SharedConstants.output_filename;
                String rulesPath = SharedConstants.rulesPath;
                String runURI = SharedConstants.runURI;
                String ontologyPath = SharedConstants.ontologyPath;
                String backup_filename = SharedConstants.backup_filename;

                if(method == 3) {
                    try {
                        Files.copy(Paths.get(backup_filename), Paths.get(output_filename), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                Model data = RDFDataMgr.loadModel("file:" + output_filename);


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
                    m.write(new PrintWriter(new FileOutputStream(output_filename)), "RDF/XML");
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
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
                Resource user = dataInf.getResource(runURI + userProfileName);
                Property rn = data.getProperty(runURI, "RunningEventName");


                StmtIterator i1 = inf.listStatements(user, p, (RDFNode) null);



        ArrayList<String> formattedEventNames = new ArrayList<String>();
        ArrayList<String> confList = new ArrayList<String>();

//        while (i1.hasNext()) {
//            Statement statement = i1.nextStatement();
//            String resultName = PrintUtil.print(statement.getProperty(rn).getString());
////            System.out.println("411: statement : " + statement.toString());
//            String statementString = statement.getObject().toString();
//            System.out.println("418: statementURI" + statementString);
//            int maxConf = Integer.MAX_VALUE;
////            System.out.println("maxConf " + maxConf);
//            Resource re = data.getResource(statementString);
//            StmtIterator i2 = inf.listStatements(re, c, (RDFNode) null);
//            while (i2.hasNext()) {
//                Statement statement2 = i2.nextStatement();
////                System.out.println("Number of confidence statements found: " + i2.toList().size());
//                String conf = statement2.getString();
//                confList.add(conf);
//                System.out.println("428: conf  = " + conf);
//            }
//            for (int i = 0; i < confList.size(); i++) {
//                String confValueStr = confList.get(i);
//                System.out.println("confList: " + confValueStr);
//                double confValue;
//                try {
//                    confValue = Double.parseDouble(confValueStr);
//                } catch (NumberFormatException e) {
//                    continue;
//                }
//
//                int roundedConfValue = (int) Math.round(confValue);
//                if (roundedConfValue < maxConf) {
//                    maxConf = roundedConfValue;
//                }
//
//            }
//
//                System.out.println(maxConf);
//                formattedEventNames.add(resultName);
//                confList.clear();
//        }



        while (i1.hasNext()) {
                Statement statement = i1.nextStatement();
                String resultName = PrintUtil.print(statement.getProperty(rn).getString());

                String statementString = statement.getObject().toString();
                System.out.println(statementString);
                Resource re = data.getResource(statementString);
                StmtIterator i2 = inf.listStatements(re, c, (RDFNode) null);
                while (i2.hasNext()) {
                    Statement statement2 = i2.nextStatement();
                    System.out.println("Number of confidence statements found: " + i2.toList().size());
                    String conf = statement2.getString();
                    System.out.println(conf);
                    confList.add(conf);
                }
                formattedEventNames.add(resultName);
        }

        if (formattedEventNames.isEmpty()) {
                    response.setStatus("empty");
                    return response;

                }

                String filterClause = formattedEventNames.stream()
                        .map(eventName -> "?eventName = \"" + eventName + "\"")
                        .collect(Collectors.joining(" || ", "FILTER (", ") ."));

                Model dataOnto = RDFDataMgr.loadModel("file:" + SharedConstants.ontologyPath);

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
                    data.write(new PrintWriter(new FileOutputStream(output_filename)), "RDF/XML");
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
                }


                return response;
//            }



        }
}


//                        GetRecommendEventResponse.RunningEvent event = eventsMap.computeIfAbsent(eventName, k -> {

//                            GetRecommendEventResponse.RunningEvent newEvent = new GetRecommendEventResponse.RunningEvent();
//                            newEvent.setRunningEventName(eventName);
//                            newEvent.setConfidence(confList.get(counter.get()));
//                            newEvent.setDistrict(solution.getLiteral("district").getString().trim());
//                            newEvent.setTypeofEvent(solution.getLiteral("typeOfEvent").getString().trim());
//                            newEvent.setOrganization(solution.getLiteral("organizationName").getString().trim());
//                            newEvent.setActivityArea(solution.getLiteral("activityArea").getString().trim());
//                            newEvent.setStandard(solution.getLiteral("standardOfEvent").getString().trim());
//                            newEvent.setLevel(solution.getLiteral("levelOfEvent").getString().trim());
//                            newEvent.setStartPeriod(solution.getLiteral("startPeriod").getString().trim());
//                            newEvent.setPrices(new GetRecommendEventResponse.RunningEvent.Prices());
//                            newEvent.getPrices().getPrice().clear();
//                            newEvent.setRaceTypes(new GetRecommendEventResponse.RunningEvent.RaceTypes());
//                            newEvent.getRaceTypes().getRaceType().clear();
//                            newEvent.setRewards(new GetRecommendEventResponse.RunningEvent.Rewards());
//                            newEvent.getRewards().getReward().clear();
//                            return newEvent;
//                        });