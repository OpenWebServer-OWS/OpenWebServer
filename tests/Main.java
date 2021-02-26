import com.openwebserver.core.Domain;
import com.openwebserver.core.Routing.Router;
import com.openwebserver.core.Security.Authorization.JWT.JsonWebToken;
import com.openwebserver.core.Security.CORS.Policy;
import com.openwebserver.core.Security.CORS.PolicyManager;
import com.openwebserver.core.WebServer;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import static java.nio.charset.StandardCharsets.US_ASCII;

public class Main {

    public static void main(String[] args) {

        PolicyManager.Register(new Policy("all").AllowAnyHeader().AllowAnyOrgin().AllowAnyMethods());

        new WebServer().addDomain(
                new Domain()
                    .addHandler(new Level2("/"))
        ).start();
        Router.print();

//        JsonWebToken token = new JsonWebToken();
//        token.PAYLOAD.put("sub", "1234567890");
//        token.PAYLOAD.put("name", "John Doe");
//        token.PAYLOAD.put("iat", 1516239022);
//
//        String jwt = token.sign("your-256-bit-secret");
//
//        System.out.println(new JsonWebToken(jwt).verify("you-256-bit-secret"));


    }

}
