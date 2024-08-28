package com.net.runningwebservice;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class GetUserProfile {

    public static ArrayList<String> hisRecommend(Individual eventIndividual, String latestRaceType) {
        System.out.println("enter hisRecommend");
        ArrayList<String> hisFactor = new ArrayList<>();
        ArrayList<String> REList = new ArrayList<>();
        ArrayList<String> REFactor = new ArrayList<>();
        ArrayList<String> matchRE = new ArrayList<>();


        int hiscount = 0;

        OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        OntDocumentManager dm = m.getDocumentManager();
        String NS = SharedConstants.NS;
        String output_filename = SharedConstants.output_filename;


        dm.addAltEntry("http://www.semanticweb.org/guind/ontologies/runningeventontology", "file:" + output_filename);
        m.read("http://www.semanticweb.org/guind/ontologies/runningeventontology", "RDF/XML");

        OntClass racetypeClass = m.getOntClass(NS + "RaceType");
        OntProperty reTypeofEvent = m.getDatatypeProperty(NS + "TypeOfEvent");
        OntProperty reLevelofEvent = m.getDatatypeProperty(NS + "LevelOfEvent");
        OntProperty reStandardofEvent = m.getDatatypeProperty(NS + "StandardOfEvent");
        OntProperty rehasRacetype = m.getObjectProperty(NS + "hasRaceType");
        OntProperty reisOrganizaed = m.getObjectProperty(NS + "isOrganizedBy");
        OntProperty rehasEventvenue = m.getObjectProperty(NS + "hasEventVenue");
        OntProperty reRaceTypeName = m.getDatatypeProperty(NS + "RaceTypeName");
        OntProperty rePrice = m.getDatatypeProperty(NS + "Price");
        OntProperty reReward = m.getDatatypeProperty(NS + "Reward");
        OntProperty reStartPeriod = m.getDatatypeProperty(NS + "StartPeriod");
        OntProperty reActivityArea = m.getDatatypeProperty(NS + "ActivityArea");
        OntProperty reVenueName = m.getDatatypeProperty(NS + "VenueName");
        OntProperty reDistrict = m.getDatatypeProperty(NS + "District");
        OntClass RunningEventClass = m.getOntClass(NS + "RunningEvent");
        OntProperty RunningEventName = m.getDatatypeProperty(NS + "RunningEventName");

        ExtendedIterator REInstances = RunningEventClass.listInstances();
        while (REInstances.hasNext()) {
            Individual thisInstance = (Individual) REInstances.next();
            REList.add(thisInstance.getURI());
        }

        Individual reInstance = m.getIndividual(eventIndividual.getURI());
        StmtIterator iter = reInstance.listProperties();

//        { "Fun run", "Mini Marathon", "Half Marathon", "Marathon" }
        String latest =  latestRaceType;

        while (iter.hasNext()) {
            Statement stmt = iter.nextStatement();
            Property predicate = stmt.getPredicate();
            RDFNode object = stmt.getObject();
            if (object instanceof Resource) {
                if (predicate.getURI().equals(rehasRacetype.getURI())) {
                    Individual rtName = m.getIndividual(object.toString());
                    if (rtName.getProperty(reRaceTypeName).getString().equals(latest)) {
                        StmtIterator iter2 = rtName.listProperties();
                        while (iter2.hasNext()) {
                            Statement stmt2 = iter2.nextStatement();
                            RDFNode object2 = stmt2.getObject();
                            Property predicate2 = stmt2.getPredicate();
                            if (object2 instanceof Resource) {
                                if (!object2.toString().equals("http://www.w3.org/2002/07/owl#NamedIndividual")) {
                                    hisFactor.add(object2.toString());
                                    hiscount++;
                                }
                            } else if (!object2.toString().equals("http://www.w3.org/2002/07/owl#NamedIndividual")) {
                                if (predicate2.getURI().equals(reStartPeriod.getURI())) {
                                    hisFactor.add(object2.toString());
                                    hiscount = hiscount + 1;
                                }
                                if (predicate2.getURI().equals(reActivityArea.getURI())) {
                                    hisFactor.add(object2.toString());
                                    hiscount = hiscount + 1;
                                }
                            }
                        }
                    }
                }
            } else if (!object.toString().equals("http://www.w3.org/2002/07/owl#NamedIndividual")) {
                if (predicate.getURI().equals(reLevelofEvent.getURI())) {
                    hisFactor.add(object.toString());
                    hiscount = hiscount + 1;
                }
                if (predicate.getURI().equals(reTypeofEvent.getURI())) {
                    hisFactor.add(object.toString());
                    hiscount = hiscount + 1;
                }
                if (predicate.getURI().equals(reStandardofEvent.getURI())) {
                    hisFactor.add(object.toString());
                    hiscount = hiscount + 1;
                }
            }
        }

//        System.out.println("hiscount = " + hiscount);

        for (int j = 0; j < REList.size(); j++) {
            Individual rel = m.getIndividual(REList.get(j));
            StmtIterator REiter = rel.listProperties();

            while (REiter.hasNext()) {
                Statement stmt = REiter.nextStatement();
                Property predicate = stmt.getPredicate();
                RDFNode object = stmt.getObject();
                if (object instanceof Resource) {
                    if (predicate.getURI().equals(rehasRacetype.getURI())) {
                        Individual rtName = m.getIndividual(object.toString());
                        if (rtName.getProperty(reRaceTypeName).getString().equals(latest)) {
                            StmtIterator iter2 = rtName.listProperties();
                            while (iter2.hasNext()) {
                                Statement stmt2 = iter2.nextStatement();
                                RDFNode object2 = stmt2.getObject();
                                if (object2 instanceof Resource) {
                                    REFactor.add(object2.toString());
                                } else {
                                    REFactor.add(object2.toString());
                                }
                            }
                        }
                    }
                    REFactor.add(object.toString());
                } else {
                    REFactor.add(object.toString());
                }
            }

            int count = 0;
            for (int k = 0; k < hisFactor.size(); k++) {
                for (int l = 0; l < REFactor.size(); l++) {
                    if (hisFactor.get(k).equals(REFactor.get(l))) {
                        count = count + 1;
//                        System.out.println("Match found: " + hisFactor.get(k));
//                        System.out.println("count = " + count);
                        if (count == hiscount) {
                            Individual re = m.getIndividual(REList.get(j));
                            matchRE.add(re.getProperty(RunningEventName).getString());
                        }

                    }
                }
            }
            REFactor.clear();
        }
        for (int j = 0; j < matchRE.size(); j++) {
            System.out.println("hisRecommend Running event: " + matchRE.get(j));
        }
        return matchRE;
    }


    public static synchronized GetUserProfileResponse run(GetUserProfileRequest request) {
//        public static GetUserProfileResponse run(GetUserProfileRequest request) {
//        synchronized (SharedConstants.lock) {

            int method = SharedConstants.methods;

            GetUserProfileResponse response = new GetUserProfileResponse();
            System.out.println("GetUserProfileResponse");

            ArrayList<String> formattedEventNames = new ArrayList<String>();
            ArrayList<String> hisRecEventNames = new ArrayList<String>();

            String NS = SharedConstants.NS;

//            String output_filename = SharedConstants.output_filename;
            String output_filename = "src/main/resources/WriteInstance3-2.rdf";

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

                    Model data = RDFDataMgr.loadModel("file:" + output_filename);

                Model dataOnto = RDFDataMgr.loadModel("file:" + ontologyPath);

                OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
                OntDocumentManager dm = m.getDocumentManager();
                dm.addAltEntry("http://www.semanticweb.org/guind/ontologies/runningeventontology",
                        "file:" + ontologyPath);
                m.read("http://www.semanticweb.org/guind/ontologies/runningeventontology", "RDF/XML");
                OntClass userClass = m.getOntClass(NS + "User");

                String userProfileName = "tempUserInf";
                String userURI = NS + userProfileName;
                Resource userInstance = m.createResource(userURI);
                userInstance.addProperty(RDF.type, userClass);
                Property usernameProperty = dataOnto.createProperty(NS + "Username");
                ResIterator users = dataOnto.listResourcesWithProperty(usernameProperty, username);

                OntClass RunningEventClass = (OntClass) m.getOntClass(NS + "RunningEvent");
                OntProperty RunningEventName = m.getDatatypeProperty(NS + "RunningEventName");
                boolean hasHisRe = false;

                if (users.hasNext()) {

                    Resource user = users.nextResource();
                    StmtIterator properties = user.listProperties();
                    StmtIterator properties2 = user.listProperties();
                    while (properties.hasNext()) {
                        Statement stmt = properties.nextStatement();
                        Property property = stmt.getPredicate();
                        RDFNode value = stmt.getObject();
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
                                    if (hasHisRe && !latestRT.isEmpty()) {
                                        System.out.println("hisRecommend "+ latestRT);
                                        hisRecEventNames = hisRecommend(thisInstance, latestRT);
                                    } else {
                                        System.out.println("hisRecommend null");
                                        hisRecEventNames = hisRecommend(thisInstance, "null");
                                    }
                                }
                            }
                        }
                    }

                    //
                    // old hisRec below
                    //


                } else {
                    System.out.println("User not found");
                }
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
                Resource a = dataInf.getResource(runURI + userProfileName);
                Property rn = dataInf.getProperty(runURI, "RunningEventName");

                StmtIterator i1 = inf.listStatements(a, p, (RDFNode) null);

                while (i1.hasNext()) {
                    Statement statement = i1.nextStatement();
                    String resultName = PrintUtil.print(statement.getProperty(rn).getString());

                    String statementString = statement.getObject().toString();
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
                        if (roundedConfValue > conf) {
                            conf = roundedConfValue;
                        }
                    }
                    formattedEventNames.add(resultName);

                }

                if(hasHisRe) {
                    hisRecEventNames.forEach(eventName -> {
                        if (!formattedEventNames.contains(eventName)) {
                            formattedEventNames.add(eventName);
                        }
                    });
                }

                //replace
                if(method ==1 ) {
                    Resource userResource = data.getResource(userURI);
                    data.removeAll(userResource, null, (RDFNode) null);
                    try {
                        data.write(new PrintWriter(new FileOutputStream(output_filename)), "RDF/XML");
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }

                String filterClause = formattedEventNames.stream()
                        .map(eventName -> "?eventName = \"" + eventName + "\"")
                        .collect(Collectors.joining(" || ", "FILTER (", ") ."));

                Model runOnto = RDFDataMgr.loadModel("file:" + SharedConstants.ontologyPath);

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
//                System.out.println(queryString);

                Query query = QueryFactory.create(queryString);

                QueryExecution qexec = QueryExecutionFactory.create(query, runOnto);

                ResultSet resultSet = qexec.execSelect();


                try {
                    Map<String, GetUserProfileResponse.RunningEvent> eventsMap = new HashMap<>();

                    while (resultSet.hasNext()) {
                        QuerySolution solution = resultSet.nextSolution();

                        String eventName = solution.getLiteral("eventName").getString().trim();
                        GetUserProfileResponse.RunningEvent event = eventsMap.computeIfAbsent(eventName, k -> {
                            GetUserProfileResponse.RunningEvent newEvent = new GetUserProfileResponse.RunningEvent();
                            newEvent.setRunningEventName(eventName);
                            newEvent.setDistrict(solution.getLiteral("district").getString().trim());
                            newEvent.setTypeofEvent(solution.getLiteral("typeOfEvent").getString().trim());
                            newEvent.setOrganization(solution.getLiteral("organizationName").getString().trim());
                            newEvent.setActivityArea(solution.getLiteral("activityArea").getString().trim());
                            newEvent.setStandard(solution.getLiteral("standardOfEvent").getString().trim());
                            newEvent.setLevel(solution.getLiteral("levelOfEvent").getString().trim());
                            newEvent.setStartPeriod(solution.getLiteral("startPeriod").getString().trim());
                            newEvent.setPrices(new GetUserProfileResponse.RunningEvent.Prices());
                            newEvent.getPrices().getPrice().clear();
                            newEvent.setRaceTypes(new GetUserProfileResponse.RunningEvent.RaceTypes());
                            newEvent.getRaceTypes().getRaceType().clear();
                            newEvent.setRewards(new GetUserProfileResponse.RunningEvent.Rewards());
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

                } finally {

                    qexec.close();
                }
            }

            return response;
//        }




    }

}

//                    while (properties.hasNext()) {
//
//                        Statement stmt = properties.nextStatement();
//                        Property property = stmt.getPredicate();
//                        RDFNode value = stmt.getObject();
////                        System.out.println(NS+"reHisone");
//
//                        System.out.println(value);
//                        System.out.println(property);
//                        if (value.isLiteral()) {
//                            userInstance.addLiteral(property, value.asLiteral());
//                        } else if (value.isResource()) {
//                            userInstance.addProperty(property, value.asResource());
//                        }
//                        else if (property.getURI().trim().equals("http://www.semanticweb.org/guind/ontologies/runningeventontology#reHisOne")) {
////                            System.out.println(value.asLiteral());
//                            System.out.println("ssfd");
//                        }
//                    }

//                    while (properties.hasNext()) {
//                        Statement stmt = properties.nextStatement();
//                        Property property = stmt.getPredicate();
//                        RDFNode value = stmt.getObject();
//
//                        if (value.isLiteral()) {
//                            userInstance.addLiteral(property, value.asLiteral());
//                        }
//                        if (value.isResource()) {
//                            userInstance.addProperty(property, value.asResource());
//                        }
//
//                        if (property.getURI().equals(NS + "reHisOne")) {
//                            String rehisValue = value.asLiteral().getString();
//                            ExtendedIterator rehisInstances = RunningEventClass.listInstances();
//                            while (rehisInstances.hasNext()) {
//                                Individual thisInstance = (Individual) rehisInstances.next();
//                                String instanceRunningEventName = thisInstance.getProperty(RunningEventName).getString();
//                                if (rehisValue.equals(instanceRunningEventName)) {
//                                    boolean hasLatestRaceType = false;
//                                    String latestRT = "";
//
//                                    while (properties.hasNext()) {
//                                        Statement stmt2 = properties.nextStatement();
//                                        Property property2 = stmt2.getPredicate();
//                                        RDFNode value2 = stmt.getObject();
//                                        if (property2.getURI().equals(NS + "hasLatestRaceType")) {
//                                            hasLatestRaceType = true;
//                                            latestRT = value2.asLiteral().getString();
//                                        }
//                                    }
//                                    if (hasLatestRaceType && !latestRT.isEmpty()) {
//                                        System.out.println("hisRecommend "+ latestRT);
//                                        hisRecommend(thisInstance, latestRT);
//                                    } else {
//                                        System.out.println("hisRecommend null");
//                                        hisRecommend(thisInstance, "null");
//                                    }
//                                }
//                            }
//                        }
//
//
//                    }
