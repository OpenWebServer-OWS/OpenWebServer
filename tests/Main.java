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
        new WebServer().addDomain(
                new Domain()
                    .addHandler(new Service("/"))
        ).start();
        Router.print();
    }

}
