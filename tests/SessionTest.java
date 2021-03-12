import com.openwebserver.core.Annotations.Session;
import com.openwebserver.core.Content.Code;
import com.openwebserver.core.Objects.Request;
import com.openwebserver.core.Objects.Response;
import com.openwebserver.services.Annotations.Route;
import com.openwebserver.services.Objects.Service;

public class SessionTest extends Service {

    public SessionTest(String path) {
        super(path);
    }

    @Session(redirect = "https://google.com")
    @Route(path = "/session", method = Method.GET)
    public Response info(Request request){
        return Response.simple(request.session.store);
    }

    @Route(path = "/bind", method = Method.GET)
    public Response bind(Request request){
        return Response.simple(Code.Ok).addHeader(new com.openwebserver.core.Sessions.Session().store("hello", 123));
    }

    @Session
    @Route(path = "/unbind", method = Method.GET)
    public Response unbind(Request request){
        if(request.session != null) {
            return Response.simple(Code.Ok).addHeader(request.session.revoke());
        }else{
            return Response.simple(Code.Not_Found);
        }
    }
}
