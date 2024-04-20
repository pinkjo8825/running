package com.net.runningwebservice;

import com.net.running_web_service.GetRecommendEventResponse;
import com.net.running_web_service.GetUserProfileRequest;
import com.net.running_web_service.GetUserProfileResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.rulesys.GenericRuleReasonerFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.util.PrintUtil;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.ReasonerVocabulary;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class GetUserProfile {
    public static GetUserProfileResponse run(GetUserProfileRequest request) {
        GetUserProfileResponse response = new GetUserProfileResponse();

        ArrayList<String> recEvents = new ArrayList<String>();

        String NS = SharedConstants.NS;
        String output_filename = SharedConstants.output_filename;
        String rulesPath = SharedConstants.rulesPath;
        String runURI = SharedConstants.runURI;
        String ontologyPath = SharedConstants.ontologyPath;

        String username = request.getUsername();
        String token = request.getToken();
        System.out.println(username);
        System.out.println(token);

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

                Statement statement = i1.nextStatement();

                String statementString = statement.getObject().toString();
//                System.out.println(statementString);
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
//                    System.out.println("=> " + roundedConfValue);
                    if (roundedConfValue > conf) {
                        conf = roundedConfValue;
                    }
                }
                String[] parts = statementString.split("#");
                String extractedName = parts[1];
                recEvents.add(extractedName);


            }
        }
        ArrayList<String> formattedEventNames = SharedConstants.formatEventNames(recEvents);

        String filterClause = formattedEventNames.stream()
                .map(eventName -> "?eventName = \"" + eventName + "\"")
                .collect(Collectors.joining(" || ", "FILTER (", ") ."));

        Model dataOnto = RDFDataMgr.loadModel("file:" + SharedConstants.ontologyPath);

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
                     
                """ + filterClause  + "}";
//        System.out.println(queryString);

        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, dataOnto);
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

        return response;
    }
}
