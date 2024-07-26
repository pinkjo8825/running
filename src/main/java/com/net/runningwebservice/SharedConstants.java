package com.net.runningwebservice;

import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;


public class SharedConstants {
//    static final String output_filename = "/Users/net/Downloads/running-web-service/src/main/resources/WriteInstance3.rdf";
//    static final String rulesPath = "/Users/net/Downloads/running-web-service/src/main/resources/testrules1.rules";
//    static final String ontologyPath = "/Users/net/Downloads/running-web-service/src/main/resources/RunningEventOntologyFinal2.rdf";
//    static final String backup_filename =  "/Users/net/Downloads/running-web-service/src/main/resources/WriteInstance3-backup.rdf";
static final String output_filename = "src/main/resources/WriteInstance3.rdf";
    static final String rulesPath = "src/main/resources/testrules1.rules";
    static final String ontologyPath = "src/main/resources/RunningEventOntologyFinal2.rdf";
    static final String backup_filename =  "src/main/resources/WriteInstance3-backup.rdf";
    static final String SOURCE = "http://www.semanticweb.org/guind/ontologies/runningeventontology";
    static final String NS = SOURCE + "#";
    static final String runURI = "http://www.semanticweb.org/guind/ontologies/runningeventontology#";
    static final String SECRET_KEY = "OIgDYngM08yDE6z7NQi8RqwzIOqErSaZM1crYnAK0tI";
    static final SecretKey key = Keys.hmacShaKeyFor(SharedConstants.SECRET_KEY.getBytes(StandardCharsets.UTF_8));
}
