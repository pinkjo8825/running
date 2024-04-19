package com.net.runningwebservice;

import com.net.running_web_service.SetUserProfileRequest;
import com.net.running_web_service.SetUserProfileResponse;
import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.FileOutputStream;
import java.io.IOException;

public class SetUserProfile {
    public static SetUserProfileResponse run(SetUserProfileRequest request) {
        SetUserProfileResponse response = new SetUserProfileResponse();

        String ontologyPath = SharedConstants.ontologyPath;
        String NS = SharedConstants.NS;

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
        OntProperty password = m.getDatatypeProperty(NS + "Password");



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
        String plainPassword = request.getPassword();

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

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(plainPassword);

        userInstance.addProperty(password, hashedPassword);




        try (FileOutputStream out = new FileOutputStream(ontologyPath)) {
            m.write(out, "RDF/XML");
            System.out.println(userInstance);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Number of statements in OntModel: " + m.size());
        response.setStatus("Success");

        return response;
    }

}
