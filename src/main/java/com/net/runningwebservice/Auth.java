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

        String username = request.getUsername();
        String plainPassword = request.getPassword();

        String queryString = String.format("""
        PREFIX : <http://www.semanticweb.org/guind/ontologies/runningeventontology#>
        SELECT ?password
        WHERE {
           ?user :Username "%s" .
           ?user :Password ?password .
        }
        """, username);

        String hashedPassword = null;
        Model dataOnto = RDFDataMgr.loadModel("file:" + SharedConstants.ontologyPath);
        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, dataOnto)) {
            ResultSet resultSet = qexec.execSelect();
            if (resultSet.hasNext()) {
                QuerySolution solution = resultSet.nextSolution();
                RDFNode passwordNode = solution.get("password");
                if (passwordNode != null && passwordNode.isLiteral()) {
                    String password = passwordNode.asLiteral().getString();
                    hashedPassword = password;
                    System.out.println("Username: " + username);
                    System.out.println("Password: " + password);
                } else {
                    System.out.println("Password not found or not a literal.");
                }
            }
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean result = encoder.matches(plainPassword, hashedPassword);
        if(result){
            long nowMillis = System.currentTimeMillis();
            Date now = new Date(nowMillis);
            String jwt = Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(now)
                    .setExpiration(new Date(nowMillis + 3600000)) // 1 hour expiration
                    .signWith(SharedConstants.key, SignatureAlgorithm.HS256)
                    .compact();
            response.setToken(jwt);
            response.setStatus("Success");
        } else {
            response.setStatus("Fail");
        }

        return response;
    }
}
