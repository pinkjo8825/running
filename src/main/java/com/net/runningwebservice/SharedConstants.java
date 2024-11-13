package com.net.runningwebservice;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


public class SharedConstants {

        public static void debug() {
            int lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();
            System.out.println("Line " + lineNumber);
        }

    public static InputStream ontologyPathStream() {
        return SharedConstants.class.getClassLoader().getResourceAsStream("RunningEventOntologyFinal2.rdf");
    }

    static final String output_filename = "src/main/resources/WriteInstance3.rdf";
    static final String output_filename_2 = "src/main/resources/WriteInstance3-2.rdf";
    static final String rulesPath = "src/main/resources/testrules1.rules";
    static final String ontologyPath = "src/main/resources/RunningEventOntologyFinal2.rdf";
    static final String backup_filename =  "src/main/resources/WriteInstance3-backup.rdf";

    static final String SOURCE = "http://www.semanticweb.org/guind/ontologies/runningeventontology";
    static final String NS = SOURCE + "#";
    static final String runURI = "http://www.semanticweb.org/guind/ontologies/runningeventontology#";
    static final String SECRET_KEY = "OIgDYngM08yDE6z7NQi8RqwzIOqErSaZM1crYnAK0tI";
    static final SecretKey key = Keys.hmacShaKeyFor(SharedConstants.SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    static final Object lock = new Object();

    //1 remove 2//replace //3reload
    static final int methods = 3;

}


//package com.net.runningwebservice;
//
//import io.jsonwebtoken.security.Keys;
//import javax.crypto.SecretKey;
//import java.io.InputStream;
//import java.nio.charset.StandardCharsets;
//
//public class SharedConstants {
//    // Use InputStream to load files from the classpath
//    public static InputStream outputFileStream() {
//        return SharedConstants.class.getClassLoader().getResourceAsStream("WriteInstance3.rdf");
//    }
//
//    public static InputStream outputFileStream2() {
//        return SharedConstants.class.getClassLoader().getResourceAsStream("WriteInstance3-2.rdf");
//    }
//
//    public static InputStream rulesPathStream() {
//        return SharedConstants.class.getClassLoader().getResourceAsStream("testrules1.rules");
//    }
//
//    public static InputStream ontologyPathStream() {
//        return SharedConstants.class.getClassLoader().getResourceAsStream("RunningEventOntologyFinal2.rdf");
//    }
//
//    public static InputStream backupFileStream() {
//        return SharedConstants.class.getClassLoader().getResourceAsStream("WriteInstance3-backup.rdf");
//    }
//
//    static final String SOURCE = "http://www.semanticweb.org/guind/ontologies/runningeventontology";
//    static final String NS = SOURCE + "#";
//    static final String runURI = "http://www.semanticweb.org/guind/ontologies/runningeventontology#";
//    static final String SECRET_KEY = "OIgDYngM08yDE6z7NQi8RqwzIOqErSaZM1crYnAK0tI";
//    static final SecretKey key = Keys.hmacShaKeyFor(SharedConstants.SECRET_KEY.getBytes(StandardCharsets.UTF_8));
//    static final Object lock = new Object();
//
//    //1 remove 2//replace //3reload
//    static final int methods = 3;
//}