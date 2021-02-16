import com.openwebserver.core.Domain;
import com.openwebserver.core.Routing.Router;
import com.openwebserver.core.WebServer;

public class Main {

    public static void main(String[] args) {
        WebServer webServer = new WebServer();
        webServer.addDomain(new Domain().addHandler(new POST("/")));
        webServer.start();
        Router.print();
    }

}
