import com.openwebserver.core.Content.Code;
import com.openwebserver.core.Objects.Request;
import com.openwebserver.core.Objects.Response;
import com.openwebserver.services.Annotations.Route;
import com.openwebserver.services.Objects.Service;

public class Level2 extends Service {
    public Level2(String path) {
        super(path);

    }


    @Route(path = "/hello", method = com.openwebserver.core.Routing.Route.Method.GET)
    public Response root(Request request){
        return Response.simple(Code.Ok);
    }

}
