package com.net.runningwebservice;

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
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.rulesys.GenericRuleReasonerFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.util.PrintUtil;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.ReasonerVocabulary;

import java.io.FileOutputStream;
import java.io.IOException;

public class GetUserProfile {
    public static GetUserProfileResponse run(GetUserProfileRequest request) {
        GetUserProfileResponse response = new GetUserProfileResponse();

        String NS = SharedConstants.NS;
        String output_filename = SharedConstants.output_filename;
        String rulesPath = SharedConstants.rulesPath;
        String runURI = SharedConstants.runURI;
        String ontologyPath = SharedConstants.ontologyPath;

        String username = request.getUsername();
        String token = request.getToken();

        boolean result = false;
        try {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(SharedConstants.SECRET_KEY)
                    .parseClaimsJws(token);

            String subject = claims.getBody().getSubject();
            if (subject.equals(username)) {
                result = true;
            }
        } catch (JwtException e) {
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
                String[] parts = statementString.split("#");
                String extractedName = parts[1];

                event.setRunningEventName(extractedName);
                event.setConfidence(String.valueOf(conf));


                response.getRunningEvent().add(event);
                System.out.println(conf);
            }
        }
        return response;
    }
}
