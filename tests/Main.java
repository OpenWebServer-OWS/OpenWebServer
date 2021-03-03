import com.openwebserver.core.Content.Code;
import com.openwebserver.core.Domain;
import com.openwebserver.core.Handlers.ContentHandler;
import com.openwebserver.core.Handlers.RequestHandler;
import com.openwebserver.core.Objects.Request;
import com.openwebserver.core.Objects.Response;
import com.openwebserver.core.Routing.Route;
import com.openwebserver.core.Routing.Router;
import com.openwebserver.core.Security.CORS.Policy;
import com.openwebserver.core.Security.CORS.PolicyManager;
import com.openwebserver.core.WebServer;

public class Main {

    public static void main(String[] args) {

        PolicyManager.Register(new Policy("all").AllowAnyHeader().AllowAnyOrgin().AllowAnyMethods());

        new WebServer().addDomain(
                new Domain()
                    .addHandler(new RequestHandler(new Route("#", Route.Method.GET), request -> Response.simple(Code.Ok)))
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
