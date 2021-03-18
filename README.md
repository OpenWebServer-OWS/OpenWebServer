# OpenWebServer
Java Multi domain HTTPS and HTTP WebServer

## Simple setup 


### Localhost setup http://localhost
```java

import com.openwebserver.core.Content.Code;
import com.openwebserver.core.Domain;
import com.openwebserver.core.Handlers.RequestHandler;
import com.openwebserver.core.Objects.Response;
import com.openwebserver.core.Routing.Route;
import com.openwebserver.core.WebServer;

public class Main {

    public static void main(String[] args) {
        new WebServer().addDomain(
                new Domain()
                        .addHandler(
                                new RequestHandler(
                                        new Route("/", Route.Method.GET),
                                        request -> Response.simple(Code.Ok))
                        )
        ).start();
    }

}
```

### External domain http://example.com

```java
import com.openwebserver.core.Content.Code;
import com.openwebserver.core.Domain;
import com.openwebserver.core.Handlers.RequestHandler;
import com.openwebserver.core.Objects.Response;
import com.openwebserver.core.Routing.Route;
import com.openwebserver.core.WebServer;

import java.net.MalformedURLException;

public class Main {

    public static void main(String[] args) throws MalformedURLException {
        new WebServer().addDomain(
                new Domain("http://example.com") \\<-- Domain name
                        .addHandler(
                                new RequestHandler(
                                        new Route("/", Route.Method.GET),
                                        request -> Response.simple(Code.Ok))
                        )
        ).start();
    }

}
```
