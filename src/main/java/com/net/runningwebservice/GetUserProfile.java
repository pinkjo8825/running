package com.net.runningwebservice;

import com.net.running_web_service.GetRecommendEventResponse;
import com.net.running_web_service.GetUserProfileRequest;
import com.net.running_web_service.GetUserProfileResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
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

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

public class GetUserProfile {

    public static String addSpaceBeforeCapital(String str) {
        return str.replaceAll("([a-zA-Z])([A-Z0-9])", "$1 $2").replaceAll("(\\d)([A-Za-z])", "$1 $2");
    }

    public static void hisRecommend(OntModel model, Resource userInstance, Individual eventIndividual, String latestRaceType) {
        System.out.println("Entering hisRecommend");

        ArrayList<String> hisFactor = new ArrayList<>();
        ArrayList<String> REList = new ArrayList<>();
        ArrayList<String> REFactor = new ArrayList<>();
        int hiscount = 0;

        String NS = SharedConstants.NS;

        OntClass RunningEventClass = model.getOntClass(NS + "RunningEvent");
        OntProperty rehasRacetype = model.getObjectProperty(NS + "hasRaceType");
        OntProperty reRaceTypeName = model.getDatatypeProperty(NS + "RaceTypeName");
        OntProperty reLevelofEvent = model.getDatatypeProperty(NS + "LevelOfEvent");
        OntProperty reTypeofEvent = model.getDatatypeProperty(NS + "TypeOfEvent");
        OntProperty reStandardofEvent = model.getDatatypeProperty(NS + "StandardOfEvent");
        OntProperty hasRecommend = model.getObjectProperty(NS + "hasRecommend");

        ExtendedIterator<? extends OntResource> REInstances = RunningEventClass.listInstances();
        while (REInstances.hasNext()) {
            OntResource resource = REInstances.next();
            if (resource.canAs(Individual.class)) {
                Individual thisInstance = resource.as(Individual.class);
                REList.add(thisInstance.getURI());
            }
        }

        StmtIterator iter = eventIndividual.listProperties();
        while (iter.hasNext()) {
            Statement stmt = iter.next();
            Property predicate = stmt.getPredicate();
            RDFNode object = stmt.getObject();
            if (object instanceof Resource) {
                if (predicate.equals(rehasRacetype)) {
                    Individual rtName = model.getIndividual(object.toString());
                    if (rtName != null && rtName.getProperty(reRaceTypeName) != null &&
                            rtName.getProperty(reRaceTypeName).getString().equals(latestRaceType)) {
                        StmtIterator raceTypeProperties = rtName.listProperties();
                        while (raceTypeProperties.hasNext()) {
                            Statement raceTypeStmt = raceTypeProperties.next();
                            RDFNode raceTypeObject = raceTypeStmt.getObject();
                            hisFactor.add(raceTypeObject.toString());
                            hiscount++;
                        }
                    }
                }
            } else {
                if (predicate.equals(reLevelofEvent) || predicate.equals(reTypeofEvent) || predicate.equals(reStandardofEvent)) {
                    hisFactor.add(object.toString());
                    hiscount++;
                }
            }
        }

        for (String uri : REList) {
            Individual rel = model.getIndividual(uri);
            List<Statement> REProperties = rel.listProperties().toList(); // Avoid modifying during iteration

            for (Statement stmt : REProperties) {
                RDFNode object = stmt.getObject();
                if (object instanceof Resource) {
                    REFactor.add(object.toString());
                } else {
                    REFactor.add(object.toString());
                }
            }

            int count = 0;
            for (String his : hisFactor) {
                for (String ref : REFactor) {
                    if (his.equals(ref)) {
                        count++;
                        if (count == hiscount) {
                            model.add(userInstance, hasRecommend, rel);
                            model.add(eventIndividual, hasRecommend, rel); // Optional: track recommendation in eventIndividual
                            System.out.println("Added recommendation: " + rel.getURI());
                            break;
                        }
                    }
                }
            }
            REFactor.clear();
        }

        System.out.println("Recommendations from Running History:");
        StmtIterator historyRecommendations = userInstance.listProperties(hasRecommend);
        while (historyRecommendations.hasNext()) {
            Statement stmt = historyRecommendations.next();
            System.out.println("- " + stmt.getObject().asResource().getURI());
        }

    }

    public static synchronized GetUserProfileResponse run(GetUserProfileRequest request) {

        GetUserProfileResponse response = new GetUserProfileResponse();
        System.out.println("GetUserProfileResponse");

        ArrayList<String> formattedEventNames = new ArrayList<String>();
        ArrayList<String> hisRecEventNames = new ArrayList<String>();
        ArrayList<String> confList = new ArrayList<String>();

        String NS = SharedConstants.NS;
        String runURI = SharedConstants.runURI;

        String output_filename = "file:WriteInstance3-2.rdf";
        String rulesPath = "file:testrules1.rules";
        String ontologyPath = "file:RunningEventOntologyFinal2.rdf";
        String backup_filename = "WriteInstance3-backup.rdf";

        try {
            Files.copy(Paths.get(backup_filename), Paths.get("WriteInstance3-2.rdf"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String username = request.getUsername();
        String token = request.getToken();

        boolean result = false;
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(SharedConstants.key)
                    .build()
                    .parseClaimsJws(token);

            String subject = claims.getBody().getSubject();
            if (subject.equals(username)) {
                result = true;
                response.setStatus("Success");
            } else {
                response.setStatus("Fail");
            }
        } catch (JwtException e) {
            response.setStatus("Fail");
            System.out.println("JWT validation failed: " + e.getMessage());
        }

        if (result) {
            // Load the model from the updated file
            OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
            // Read base ontology
            m.read("http://www.semanticweb.org/guind/ontologies/runningeventontology", "RDF/XML");
            // Read instance data from WriteInstance3-2.rdf
            m.read("WriteInstance3-2.rdf");

            Model dataOnto = RDFDataMgr.loadModel(ontologyPath);

            OntClass userClass = m.getOntClass(NS + "User");

            String userProfileName = username;
            String userURI = NS + userProfileName;
            Resource userInstance = m.createResource(userURI);
            userInstance.addProperty(RDF.type, userClass);

            Property usernameProperty = dataOnto.createProperty(NS + "Username");
            ResIterator users = dataOnto.listResourcesWithProperty(usernameProperty, username);

            OntClass RunningEventClass = (OntClass) m.getOntClass(NS + "RunningEvent");
            OntProperty RunningEventName = m.getDatatypeProperty(NS + "RunningEventName");
            boolean hasHisRe = false;

            List<String> rehis = response.getRehis();
            if (users.hasNext()) {

                Resource user = users.nextResource();
                StmtIterator properties = user.listProperties();
                StmtIterator properties2 = user.listProperties();
                while (properties.hasNext()) {
                    Statement stmt = properties.nextStatement();
                    Property property = stmt.getPredicate();
                    RDFNode value = stmt.getObject();

                    if (property.getURI().equals(NS + "TravelPlaceTypeInterest")) {
                        response.setTravelPlaceType(value.asLiteral().getString());
                        System.out.println(value.asLiteral().getString());
                    }
                    if (property.getURI().equals(NS + "LocationInterest")) {
                        response.setDistrict(value.asLiteral().getString());
                    }
                    if (property.getURI().equals(NS + "TypeOfEventInterest")) {
                        response.setTypeofEvent(value.asLiteral().getString());
                    }
                    if (property.getURI().equals(NS + "ActivityAreaInterest")) {
                        response.setActivityArea(value.asLiteral().getString());
                    }
                    if (property.getURI().equals(NS + "StandardEventInterest")) {
                        response.setStandard(value.asLiteral().getString());
                    }
                    if (property.getURI().equals(NS + "LevelEventInterest")) {
                        response.setLevel(value.asLiteral().getString());
                    }
                    if (property.getURI().equals(NS + "StartPeriodInterest")) {
                        response.setStartPeriod(value.asLiteral().getString());
                    }
                    if (property.getURI().equals(NS + "RewardInterest")) {
                        response.setReward(value.asLiteral().getString());
                    }
                    if (property.getURI().equals(NS + "EventPriceInterest")) {
                        response.setPrice(value.asLiteral().getString());
                    }

                    if (property.getURI().equals(NS + "hasRunningEventHistory")) {
                        String eventHistory = value.asResource().getURI().substring(value.asResource().getURI().lastIndexOf("#") + 1);
                        rehis.add(addSpaceBeforeCapital(eventHistory));
                    }
                    if (property.getURI().equals(NS + "hasLatestRaceType")) {
                        response.setLatestRT(value.asResource().getURI().substring(value.asResource().getURI().lastIndexOf("#") + 1));
                    }

                    if (property.getURI().equals(NS + "hasOrganizationInterest")) {
                        Resource organizationResource = value.asResource();
                        Statement stmtOrganization = organizationResource.getProperty(m.createProperty(NS + "OrganizationName"));

                        if (stmtOrganization != null) {
                            String organizationName = stmtOrganization.getString();
                            response.setOrganization(organizationName);
                        } else {
                            String organizationName = organizationResource.getURI().substring(organizationResource.getURI().lastIndexOf("#") + 1);
                            response.setOrganization(organizationName);
                        }
                    }

                    if (property.getURI().equals(NS + "hasRaceTypeInterest")) {
                        String raceType = value.asResource().getURI().substring(value.asResource().getURI().lastIndexOf("#") + 1);
                        response.setRaceType(addSpaceBeforeCapital(raceType));
                    }

                    if (value.isLiteral()) {
                        userInstance.addLiteral(property, value.asLiteral());
                    }
                    if (value.isResource()) {
                        userInstance.addProperty(property, value.asResource());
                    }

                    if (property.getURI().equals(NS + "reHisOne")) {
                        String rehisValue = value.asLiteral().getString();
                        ExtendedIterator rehisInstances = RunningEventClass.listInstances();
                        while (rehisInstances.hasNext()) {
                            Individual thisInstance = (Individual) rehisInstances.next();
                            String instanceRunningEventName = thisInstance.getProperty(RunningEventName).getString();
                            if (rehisValue.equals(instanceRunningEventName)) {
                                String latestRT = "";
                                while (properties2.hasNext()) {
                                    Statement stmt2 = properties2.nextStatement();
                                    Property property2 = stmt2.getPredicate();
                                    RDFNode value2 = stmt.getObject();
                                    if (property2.getURI().equals(NS + "hasLatestRaceType")) {
                                        hasHisRe = true;
                                        latestRT = value2.asLiteral().getString();
                                    }
                                }
                                if (user.canAs(Individual.class)) {
                                    Individual userIndividual = user.as(Individual.class);
                                    if (hasHisRe && !latestRT.isEmpty()) {
                                        hisRecommend(m, userIndividual, thisInstance, latestRT);
                                    } else {
                                        hisRecommend(m, userIndividual, thisInstance, "null");
                                    }
                                } else {
                                    System.out.println("Warning: User instance cannot be cast to Individual. Using Resource instead.");
                                    if (hasHisRe && !latestRT.isEmpty()) {
                                        hisRecommend(m, user, thisInstance, latestRT);
                                    } else {
                                        hisRecommend(m, user, thisInstance, "null");
                                    }
                                }
                            }
                        }
                    }
                }

            } else {
                System.out.println("User not found");
            }

            // After all history-based recommendations are done, write the updated model
            try {
                m.write(new PrintWriter(new FileOutputStream("WriteInstance3-2.rdf")), "RDF/XML");
            } catch (FileNotFoundException ex) {
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

            Property p = dataInf.getProperty(runURI, "hasRecommend");
            Property c = dataInf.getProperty(runURI, "confidence");
            Resource a = dataInf.getResource(runURI + userProfileName);
            Property rn = dataInf.getProperty(runURI, "RunningEventName");

            Model rm2 = ModelFactory.createDefaultModel();
            Resource config2 = rm2.createResource();
            config2.addProperty(ReasonerVocabulary.PROPruleMode, "hybrid");
            config2.addProperty(ReasonerVocabulary.PROPruleSet, "file:travelPlaceRule.rules");
            Reasoner reasoner2 = GenericRuleReasonerFactory.theInstance().create(config2);
            InfModel inf2 = ModelFactory.createInfModel(reasoner2, inf);

            StmtIterator i1 = inf2.listStatements(a, p, (RDFNode) null);

            while (i1.hasNext()) {
                Statement statement = i1.nextStatement();
                String resultName = PrintUtil.print(statement.getProperty(rn).getString());
                String statementString = statement.getObject().toString();
                Resource re = dataInf.getResource(statementString);
                StmtIterator i2 = inf.listStatements(re, c, (RDFNode) null);
                while (i2.hasNext()) {
                    Statement statement2 = i2.nextStatement();
                    String conf = statement2.getString();
                    confList.add(conf);
                }
                formattedEventNames.add(resultName);
            }

            try {
                inf2.write(new PrintWriter(new FileOutputStream("WriteInstance3-2.rdf")), "RDF/XML");
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }

            Model model = RDFDataMgr.loadModel(output_filename);
            Property hasRecommend = model.getProperty(runURI, "hasRecommend");
            StmtIterator iteratorRunningEvent = model.listStatements(a, hasRecommend, (RDFNode) null);

            ArrayList<String> events = new ArrayList<>(formattedEventNames);

            while (iteratorRunningEvent.hasNext()) {
                Statement stmt = iteratorRunningEvent.nextStatement();
                Resource runningEventResource = stmt.getObject().asResource();

                GetUserProfileResponse.RunningEvent runningEvent = new GetUserProfileResponse.RunningEvent();

                String confidence = runningEventResource.hasProperty(model.createProperty(NS + "confidence"))
                        ? runningEventResource.getProperty(model.createProperty(NS + "confidence")).getString()
                        : null;
                runningEvent.setConfidence(confidence);

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

                Resource venue = runningEventResource.getPropertyResourceValue(model.createProperty(NS + "hasEventVenue"));
                String district = venue != null && venue.hasProperty(model.createProperty(NS + "District"))
                        ? venue.getProperty(model.createProperty(NS + "District")).getString()
                        : null;
                runningEvent.setDistrict(district);

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

                runningEvent.setRaceTypes(new GetUserProfileResponse.RunningEvent.RaceTypes());
                raceTypes.forEach(runningEvent.getRaceTypes().getRaceType()::add);

                runningEvent.setPrices(new GetUserProfileResponse.RunningEvent.Prices());
                prices.forEach(runningEvent.getPrices().getPrice()::add);

                runningEvent.setRewards(new GetUserProfileResponse.RunningEvent.Rewards());
                rewards.forEach(runningEvent.getRewards().getReward()::add);

                Resource organization = runningEventResource.getPropertyResourceValue(model.createProperty(NS + "isOrganizedBy"));
                String organizationName = organization != null && organization.hasProperty(model.createProperty(NS + "OrganizationName"))
                        ? organization.getProperty(model.createProperty(NS + "OrganizationName")).getString()
                        : null;
                runningEvent.setOrganization(organizationName);

                Property hasTravelPlaceRecommend = model.getProperty(runURI, "hasTravelPlaceRecommend");
                StmtIterator travelPlaceIterator = model.listStatements(runningEventResource, hasTravelPlaceRecommend, (RDFNode) null);

                while (travelPlaceIterator.hasNext()) {
                    Resource travelPlaceResource = travelPlaceIterator.nextStatement().getObject().asResource();
                    GetUserProfileResponse.RunningEvent.TravelPlaces travelPlace = new GetUserProfileResponse.RunningEvent.TravelPlaces();
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
                    travelPlace.setHotScore(travelPlaceResource.hasProperty(model.createProperty(NS + "HotScore"))
                            ? travelPlaceResource.getProperty(model.createProperty(NS + "HotScore")).getString()
                            : null);

                    runningEvent.getTravelPlaces().add(travelPlace);
                }

                response.getRunningEvent().add(runningEvent);

            }
            System.out.println("Recommendations based on User Preferences:");
            for (GetUserProfileResponse.RunningEvent event : response.getRunningEvent()) {
                System.out.println("- " + event.getRunningEventName());
            }

            iteratorRunningEvent.close();
        }

        return response;
    }
}