import com.openwebserver.core.Content.Code;
import com.openwebserver.core.Objects.Request;
import com.openwebserver.core.Objects.Response;
import com.openwebserver.services.Annotations.Route;
import com.openwebserver.services.Objects.Service;

public class Nested extends Service {

    public Nested(String path) {
        super(path);
        add(new Level2("/2"));
    }

    @Route(path = "/hello", method = com.openwebserver.core.Routing.Route.Method.GET)
    public Response root(Request request){
        return Response.simple(Code.Ok);
    }

}
