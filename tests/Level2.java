import com.openwebserver.core.Content.Code;
import com.openwebserver.core.Objects.Request;
import com.openwebserver.core.Objects.Response;
import com.openwebserver.core.Security.Authorization.Authorize;
import com.openwebserver.core.Security.Authorization.JWT.JsonWebToken;
import com.openwebserver.services.Annotations.Route;
import com.openwebserver.services.Objects.Service;

public class Level2 extends Service {



    public Level2(String path) {
        super(path);
        setAuthorizor(JsonWebToken.validate(((request, jsonWebToken) -> {
            request.AUTH.put("token", jsonWebToken);
            return true;
        })));
    }

    @Authorize
    @Route(path = "/hello", method = Method.GET)
    public Response root(Request request){
        return Response.simple(Code.Ok);
    }

}
