import com.openwebserver.core.Objects.Request;
import com.openwebserver.core.Objects.Response;
import com.openwebserver.services.Annotations.Route;
import com.openwebserver.services.Objects.Service;

public class REST extends Service {

    public REST(String path) {
        super(path);
    }


    @Route(path = "/{id}")
    public Response handle(Request request) {
        return Response.simple(request.GET());
    }
}
