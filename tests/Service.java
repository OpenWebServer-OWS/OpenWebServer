import com.openwebserver.core.Objects.Request;
import com.openwebserver.core.Objects.Response;


import com.openwebserver.core.Security.Authorization.Authorize;
import com.openwebserver.core.Security.Authorization.JWT.JsonWebToken;
import com.openwebserver.core.Security.CORS.CORS;

import com.openwebserver.services.Annotations.Route;


public class Service extends com.openwebserver.services.Objects.Service {

    public Service(String path) {
        super(path);
        setAuthorizor(JsonWebToken.validate(((request, jsonWebToken) -> {
            return true;
        })));
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
