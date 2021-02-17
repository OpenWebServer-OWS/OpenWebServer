import com.openwebserver.core.Objects.Request;
import com.openwebserver.core.Objects.Response;
import com.openwebserver.services.Annotations.Route;


public class Service extends com.openwebserver.services.Objects.Service {

    public Service(String path) {
        super(path);
    }

    @Route(path = "/", method = Method.POST, require = {""})
    public Response post(Request request) {
        return Response.simple(request.POST());
    }

    @Route(path = "/", method = Method.GET)
    public Response get(Request request) {
        return Response.simple(request.POST());
    }

    @Route(path = "/{id}", method = Method.GET)
    public Response id(Request request) {
        return Response.simple(request.GET());
    }

}
