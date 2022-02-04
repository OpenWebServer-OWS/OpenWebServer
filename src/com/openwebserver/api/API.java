package com.openwebserver.api;

import com.openwebserver.core.objects.Request;
import com.openwebserver.core.objects.Response;
import com.openwebserver.core.routing.Router;
import com.openwebserver.services.annotations.Route;
import com.openwebserver.services.objects.Service;
import org.json.JSONArray;
import org.json.JSONObject;

import static com.openwebserver.core.routing.Route.Method.*;

public class API extends Service {
    public API() {
        super("ows/api/");
        allowPrivateAccess(() -> API.class);
    }

    @Hidden
    @Route(path = "/routes", method = GET)
    private Response getRoutes(Request request){
        JSONArray routeArray = new JSONArray();
        Router.getRoutes(request.getDomain()).forEach(route -> routeArray.put(JSONObject.wrap(route)));
        return Response.simple(routeArray);
    }

    @Hidden
    @Route(path = "/domains", method = POST)
    private Response getDomains(Request request){
        return Response.simple();
    }


}
