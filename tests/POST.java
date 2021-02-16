import com.openwebserver.core.Objects.Request;
import com.openwebserver.core.Objects.Response;
import com.openwebserver.services.Annotations.Route;
import com.openwebserver.services.Objects.Service;

public class POST extends Service {

    public POST(String path) {
        super(path);
    }

    @Route(path = "/", method = Method.POST)
    public Response post(Request request) {
        throw new NullPointerException();
    }

}
