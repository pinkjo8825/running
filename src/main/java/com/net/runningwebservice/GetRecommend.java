package com.net.runningwebservice;

import com.net.running_web_service.GetPlaceResponse;
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

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
                OntProperty organizationNameProp = m.getDatatypeProperty(NS + "OrganizationName");


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
                String travelPlaceReg = request.getTravelPlaceType();

//                Beach
//                Natural
//                TownAndCity
//                Cultural
//                ShoppingAndDinning
//                EntertainmentAndNightLife
//                HealthAndWellness

                if (travelPlaceReg != null) {
                    userInstance.addProperty(placeType, travelPlaceReg);
                }
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

                        if (organizationReg.equals(thisInstance.getProperty(organizationNameProp).getString())) {

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

            response.getTravelPlace().add(travelPlace);

        }

////////
////////
////////

        Property hasRecommend = model.getProperty(runURI, "hasRecommend");
        StmtIterator iteratorRunningEvent = model.listStatements(user, hasRecommend, (RDFNode) null);

        while (iteratorRunningEvent.hasNext()) {
            Statement stmt = iteratorRunningEvent.nextStatement();
            Resource runningEventResource = stmt.getObject().asResource();

            GetRecommendEventResponse.RunningEvent runningEvent = new GetRecommendEventResponse.RunningEvent();


            String confidence = runningEventResource.hasProperty(model.createProperty(NS + "confidence"))
                    ? runningEventResource.getProperty(model.createProperty(NS + "confidence")).getString()
                    : null;
            runningEvent.setConfidence(confidence);

            // Set Running Event details
            String eventName = runningEventResource.hasProperty(model.createProperty(NS + "RunningEventName"))
                    ? runningEventResource.getProperty(model.createProperty(NS + "RunningEventName")).getString()
                    : null;
            runningEvent.setRunningEventName(eventName);

            String typeOfEvent = runningEventResource.hasProperty(model.createProperty(NS + "TypeOfEvent"))
                    ? runningEventResource.getProperty(model.createProperty(NS + "TypeOfEvent")).getString()
                    : null;
            runningEvent.setTypeofEvent(typeOfEvent);

            String standard = runningEventResource.hasProperty(model.createProperty(NS + "StandardOfEvent"))
                    ? runningEventResource.getProperty(model.createProperty(NS + "StandardOfEvent")).getString()
                    : null;
            runningEvent.setStandard(standard);

            String level = runningEventResource.hasProperty(model.createProperty(NS + "LevelOfEvent"))
                    ? runningEventResource.getProperty(model.createProperty(NS + "LevelOfEvent")).getString()
                    : null;
            runningEvent.setLevel(level);

            // Access venue details
            Resource venue = runningEventResource.getPropertyResourceValue(model.createProperty(NS + "hasEventVenue"));
            String district = venue != null && venue.hasProperty(model.createProperty(NS + "District"))
                    ? venue.getProperty(model.createProperty(NS + "District")).getString()
                    : null;
            runningEvent.setDistrict(district);

            // Multi-value fields: RaceTypes, Prices, Rewards
            List<String> raceTypes = new ArrayList<>();
            Set<String> prices = new LinkedHashSet<>();
            Set<String> rewards = new LinkedHashSet<>();
            String activityArea = null;
            String startPeriod = null;

            StmtIterator raceTypeIterator = runningEventResource.listProperties(model.createProperty(NS + "hasRaceType"));
            while (raceTypeIterator.hasNext()) {
                Resource raceType = raceTypeIterator.nextStatement().getObject().asResource();

                String raceTypeName = raceType.hasProperty(model.createProperty(NS + "RaceTypeName"))
                        ? raceType.getProperty(model.createProperty(NS + "RaceTypeName")).getString()
                        : null;
                if (raceTypeName != null) raceTypes.add(raceTypeName);

                String price = raceType.hasProperty(model.createProperty(NS + "Price"))
                        ? raceType.getProperty(model.createProperty(NS + "Price")).getString()
                        : null;
                if (price != null) prices.add(price);

                StmtIterator rewardIterator = raceType.listProperties(model.createProperty(NS + "Reward"));
                while (rewardIterator.hasNext()) {
                    String reward = rewardIterator.nextStatement().getString();
                    if (reward != null) rewards.add(reward);
                }

                if (activityArea == null) {
                    activityArea = raceType.hasProperty(model.createProperty(NS + "ActivityArea"))
                            ? raceType.getProperty(model.createProperty(NS + "ActivityArea")).getString()
                            : null;
                }

                if (startPeriod == null) {
                    startPeriod = raceType.hasProperty(model.createProperty(NS + "StartPeriod"))
                            ? raceType.getProperty(model.createProperty(NS + "StartPeriod")).getString()
                            : null;
                }
            }
            runningEvent.setActivityArea(activityArea);
            runningEvent.setStartPeriod(startPeriod);

            // Add race types, prices, and rewards
            runningEvent.setRaceTypes(new GetRecommendEventResponse.RunningEvent.RaceTypes());
            raceTypes.forEach(runningEvent.getRaceTypes().getRaceType()::add);

            runningEvent.setPrices(new GetRecommendEventResponse.RunningEvent.Prices());
            prices.forEach(runningEvent.getPrices().getPrice()::add);

            runningEvent.setRewards(new GetRecommendEventResponse.RunningEvent.Rewards());
            rewards.forEach(runningEvent.getRewards().getReward()::add);

            // Access organization details
            Resource organization = runningEventResource.getPropertyResourceValue(model.createProperty(NS + "isOrganizedBy"));
            String organizationName = organization != null && organization.hasProperty(model.createProperty(NS + "OrganizationName"))
                    ? organization.getProperty(model.createProperty(NS + "OrganizationName")).getString()
                    : null;
            runningEvent.setOrganization(organizationName);

            // Access travel places associated with this running event
//            Property hasTravelPlaceRecommend = model.createProperty(NS + "hasTravelPlaceRecommend");
            StmtIterator travelPlaceIterator = model.listStatements(runningEventResource, hasTravelPlaceRecommend, (RDFNode) null);

            while (travelPlaceIterator.hasNext()) {
                Resource travelPlaceResource = travelPlaceIterator.nextStatement().getObject().asResource();
                GetRecommendEventResponse.RunningEvent.TravelPlaces travelPlace = new GetRecommendEventResponse.RunningEvent.TravelPlaces();
                travelPlace.setTravelPlaceName(travelPlaceResource.hasProperty(model.createProperty(NS + "TravelPlaceName"))
                        ? travelPlaceResource.getProperty(model.createProperty(NS + "TravelPlaceName")).getString()
                        : null);
                travelPlace.setTravelPlaceType(travelPlaceResource.hasProperty(model.createProperty(NS + "TravelPlaceType"))
                        ? travelPlaceResource.getProperty(model.createProperty(NS + "TravelPlaceType")).getString()
                        : null);
                travelPlace.setDistrict(travelPlaceResource.hasProperty(model.createProperty(NS + "District"))
                        ? travelPlaceResource.getProperty(model.createProperty(NS + "District")).getString()
                        : null);
                travelPlace.setLatitude(travelPlaceResource.hasProperty(model.createProperty(NS + "Latitude"))
                        ? travelPlaceResource.getProperty(model.createProperty(NS + "Latitude")).getString()
                        : null);
                travelPlace.setLongitude(travelPlaceResource.hasProperty(model.createProperty(NS + "Longitude"))
                        ? travelPlaceResource.getProperty(model.createProperty(NS + "Longitude")).getString()
                        : null);
                runningEvent.getTravelPlaces().add(travelPlace);

            }

            // Add the running event to the response
            response.getRunningEvent().add(runningEvent);
        }

        iteratorRunningEvent.close();

////////
////////
////////
                return response;
//            }



        }
}

