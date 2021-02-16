import com.openwebserver.core.Objects.Request;
import com.openwebserver.core.Objects.Response;
import com.openwebserver.services.Annotations.Route;
import com.openwebserver.services.Objects.Service;

public class Level2 extends Service {

    public Level2(String path) {
        super(path);
    }

    @Route(path = "level2")
    public Response level2(Request request){
        return Response.simple("level2");
    }
}
