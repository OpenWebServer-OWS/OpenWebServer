import com.openwebserver.core.Domain;
import com.openwebserver.core.Routing.Router;
import com.openwebserver.core.Security.CORS.Policy;
import com.openwebserver.core.Security.CORS.PolicyManager;
import com.openwebserver.core.WebServer;

public class Main {

    public static void main(String[] args) {

        PolicyManager.Register(new Policy("all").AllowAnyHeader().AllowAnyOrgin().AllowAnyMethods());

        new WebServer().addDomain(
                new Domain()
                    .addHandler(new Service("/"))
        ).start();
        Router.print();
    }

}
