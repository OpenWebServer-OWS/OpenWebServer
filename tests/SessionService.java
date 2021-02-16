import com.openwebserver.core.Content.Code;
import com.openwebserver.core.Objects.Request;
import com.openwebserver.core.Objects.Response;
import com.openwebserver.core.Sessions.Session;
import com.openwebserver.services.Annotations.Route;
import com.openwebserver.services.Objects.Service;

public class SessionService extends Service {

    public SessionService(String path) {
        super(path);
    }

    @Route(path = "create" , method = Method.GET)
    public Response create(Request request){
        return Response.simple(Code.Ok, "Session Bound").addHeader(new Session().store("test", true));
    }

    @com.openwebserver.core.Annotations.Session(require = {"test"})
    @Route(path = "revoke" , method = Method.GET)
    public Response revoke(Request request){
        return Response.simple(Code.Ok, "Session Unbound").addHeader(request.session.revoke());
    }

    @com.openwebserver.core.Annotations.Session(require = {"test"})
    @Route(path = "info", method = Method.GET)
    public Response info(Request request){
        return Response.simple(request.session);
    }


}
