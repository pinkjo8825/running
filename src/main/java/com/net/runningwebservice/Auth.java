package com.net.runningwebservice;

import com.net.running_web_service.AuthRequest;
import com.net.running_web_service.AuthResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.RDFDataMgr;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Date;


public class Auth {

    public static AuthResponse run(AuthRequest request) {
        AuthResponse response = new AuthResponse();
        System.out.println("AuthResponse");
        String username = request.getUsername();
        String plainPassword = request.getPassword();
        SharedConstants.debug();

        String queryString = String.format("""
        PREFIX : <http://www.semanticweb.org/guind/ontologies/runningeventontology#>
        SELECT ?password
        WHERE {
           ?user :Username "%s" .
           ?user :Password ?password .
        }
        """, username);
        SharedConstants.debug();
        String hashedPassword = null;
        String ontologyPath = "file:RunningEventOntologyFinal2.rdf";
        Model dataOnto = RDFDataMgr.loadModel(ontologyPath);
        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, dataOnto)) {
            ResultSet resultSet = qexec.execSelect();
            if (resultSet.hasNext()) {
                QuerySolution solution = resultSet.nextSolution();
                RDFNode passwordNode = solution.get("password");
                if (passwordNode != null && passwordNode.isLiteral()) {
                    String password = passwordNode.asLiteral().getString();
                    hashedPassword = password;
                    SharedConstants.debug();
//                    System.out.println("Username: " + username);
//                    System.out.println("Password: " + password);
                } else {
//                    System.out.println("Password not found or not a literal.");
                }
            }
        }
        SharedConstants.debug();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean result = encoder.matches(plainPassword, hashedPassword);
        SharedConstants.debug();
        if(result){
            SharedConstants.debug();
            long nowMillis = System.currentTimeMillis();
            Date now = new Date(nowMillis);
            SharedConstants.debug();
            String jwt = Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(now)
                    .setExpiration(new Date(nowMillis + 7200000)) // 2 hour expiration
                    .signWith(SharedConstants.key, SignatureAlgorithm.HS256)
                    .compact();
            SharedConstants.debug();
            response.setToken(jwt);
            response.setStatus("Success");
            SharedConstants.debug();
        } else {
            response.setStatus("Fail");
        }

        return response;
    }
}
