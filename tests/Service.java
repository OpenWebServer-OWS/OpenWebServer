import com.openwebserver.core.Objects.Request;
import com.openwebserver.core.Objects.Response;
import com.openwebserver.core.Security.Authorization.Authentication;
import com.openwebserver.core.Security.Authorization.Authorize;
import com.openwebserver.core.Security.CORS.CORS;
import com.openwebserver.services.Annotations.Route;

import static com.openwebserver.core.Security.Authorization.Authentication.Type.*;


public class Service extends com.openwebserver.services.Objects.Service {

    public Service(String path) {
        super(path);
        setAuthenticationHandler(new Authentication(BEARER).setHandler((token,request) -> token.equals("R0WJ56nqi2sbW73bS4WxDRyrwWsDSg")));
        add(new Nested("/hello"));
    }

    @Authorize
    @CORS("all")
    @Route(path = "/", method = Method.POST)
    public Response post(Request request) {
        return Response.simple(request.POST());
    }

    @Route(path = "/", method = Method.GET)
    public Response get(Request request) {
        return Response.simple(request.GET());
    }

    @Route(path = "/{id}", method = Method.GET)
    public Response id(Request request) {
        return Response.simple(request.GET());
    }

}
