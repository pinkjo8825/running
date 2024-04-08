package com.net.runningwebservice;

import com.net.running_web_service.GetRecommendEventRequest;
import com.net.running_web_service.GetRecommendEventResponse;
import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.rulesys.GenericRuleReasonerFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.util.PrintUtil;
import org.apache.jena.vocabulary.ReasonerVocabulary;

import java.io.FileOutputStream;
import java.io.IOException;

public class GetRecommend {
    public static GetRecommendEventResponse run(GetRecommendEventRequest request) {
        GetRecommendEventResponse response = new GetRecommendEventResponse();

        String NS = SharedConstants.NS;
        String output_filename = SharedConstants.output_filename;
        String rulesPath = SharedConstants.rulesPath;
        String runURI = SharedConstants.runURI;
        String ontologyPath = SharedConstants.ontologyPath;

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
}
