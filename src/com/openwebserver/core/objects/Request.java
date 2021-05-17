package com.openwebserver.core.objects;

import FileManager.Local;
import com.bytereader.ByteReader;
import com.bytestream.ByteStream;
import com.bytestream.Bytes;
import com.openwebserver.core.connection.Connection;
import com.openwebserver.core.connection.ConnectionManager;
import com.openwebserver.core.content.Code;
import com.openwebserver.core.handlers.RequestHandler;
import com.openwebserver.core.objects.headers.Header;
import com.openwebserver.core.objects.headers.Headers;
import com.openwebserver.core.routing.Route;
import com.openwebserver.core.routing.Router;
import com.openwebserver.core.security.sessions.Session;
import com.openwebserver.core.WebException;
import com.openwebserver.core.WebServer;
import com.together.Pair;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Request{

    protected HashMap<String, Object> POST = new HashMap<>();
    protected final HashMap<String, String> GET;
    protected final HashMap<String, Pair<String, Local>> FILES = new HashMap<>();

    public HashMap<String, Object> SESSION;
    public Session session;

    public HashMap<String, Object> AUTH = new HashMap<>();

    public final Headers headers;
    public final String path;
    public final Route.Method method;
    private final Domain domain;
    private String connectionRef;
    private RequestHandler handler;

    private Request(Headers headers) throws Router.RoutingException {
        this.path = URLDecoder.decode(headers.getPath(),Charset.defaultCharset());
        this.method = Route.Method.valueOf(headers.getMethod());
        this.headers = headers;
        this.domain = Router.getDomain(getAlias());
        this.GET = URLEncoded(headers.getPath());
    }

    public String getAlias() {
        return headers.get("Host").getValue().split(":")[0];
    }

    public static Request deserialize(Connection connection) throws ByteReader.ByteReaderException.PrematureStreamException, RequestException, Bytes.BytesException, ByteStream.PrematureStreamException, Router.RoutingException {
        Request request = new Request(Headers.Incoming(connection));
        request.connectionRef = connection.getConnectionString();
        request.decode(connection);

        if (!request.headers.containsKey("Host")) {
            throw new RequestException("Can't map request to virtual host");
        }
        return request;
    }

    private int size() {
        if (headers.containsKey("Content-Length")) {
            return Integer.parseInt(headers.get("Content-Length").getValue());
        }
        return 0;
    }

    public HashMap<String, String> GET() {
        return GET;
    }

    public HashMap<String, Pair<String, Local>> FILES() {
        return FILES;
    }

    public Local FILES(String key) {
        return FILES.get(key).getValue();
    }

    public String GET(String key) {
        return GET.get(key);
    }

    public HashMap<String, Object> POST(){
        return POST;
    }

    @SuppressWarnings("unchecked")
    public <T> T POST(String key, Class<T> type){
        return (T) POST.get(key);
    }

    public Object POST(String key){
        return POST.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T SESSION(String key, Class<T> type){
        return (T) SESSION.get(key);
    }

    public Object SESSION(String key){
        return SESSION.get(key);
    }

    public void setHandler(RequestHandler requestHandler) {
        this.handler = requestHandler;
    }

    public RequestHandler getHandler() {
        return handler;
    }

    @SuppressWarnings("unchecked")
    public <T> T access(ConnectionManager.Access access) throws IOException, ConnectionManager.ConnectionManagerException {
        return (T) ConnectionManager.Access(connectionRef, access.getReturnType());
    }

    //region file handling
    public String getPath() {
        return path;
    }

    public String getPath(boolean clean) {
        if (clean && getPath().contains("?")) {
            return getPath().substring(0, getPath().indexOf("?"));
        }
        return getPath();
    }

    public boolean isFile(){
        return getPath(true).contains(".");
    }

    public String getFileName(){
        String path = getPath(true);
        return path.substring(path.lastIndexOf("/") +1);
    }
    //endregion

    //region request decoding
    private void decode(Connection connection) throws ByteStream.PrematureStreamException, Bytes.BytesException {
        if (headers.containsKey("Content-Length")) {
            Header contentType = headers.get("Content-Type");
            switch (contentType.getValue()) {
                case "multipart/form-data" -> MultipartDecoder(connection);
                case "application/x-www-form-urlencoded" -> POST.putAll(URLEncoded(connection.readAll().toString()));
                case "application/json" -> POST = (HashMap<String, Object>) new JSONObject(connection.readAll().toString()).toMap();
                default -> {
                    HashMap<String, Object> post = new HashMap<>();
                    post.put("$plain", connection.readAll().toString());
                    POST = post;
                }
            }
        }

    }

    private static HashMap<String, String> URLEncoded(String encoded) {
        HashMap<String, String> fields = new HashMap<>();
        if (encoded != null) {
            if (encoded.contains("?")) {
                encoded = encoded.substring(encoded.indexOf("?") + 1);
            }
            for (String param : encoded.split("&")) {
                String[] entry = param.split("=");
                if (entry.length > 1) {
                    fields.put(entry[0], URLDecoder.decode(entry[1], Charset.defaultCharset()));
                }
            }
        }
        return fields;
    }

    private void MultipartDecoder(ByteStream stream) throws ByteStream.PrematureStreamException {
        stream.readFor(size()).split("--" + headers.get("Content-Type").get("boundary").getValue() + Header.separator,StandardCharsets.UTF_8)
        .forEach(bytes -> FormData.decode(bytes, this));
    }

    public Route.Method getMethod() {
        return method;
    }

    private static class FormData{

        public static String getName(Headers headers) {
            return headers.get("Content-Disposition").get("name").getValue().replaceAll("\"", "").trim();
        }

        public static String getFilename(Headers headers) {
            return headers.get("Content-Disposition").get("filename").getValue().replaceAll("\"", "").trim();
        }

        public static boolean isFile(Headers headers) {
            return headers.get("Content-Disposition").contains("filename") && !headers.get("Content-Disposition").get("filename").getValue().equals("");
        }

        public static void decode(Bytes bytes, Request request){
            AtomicInteger headerLength = new AtomicInteger();
            Headers headers = Headers.Decode(bytes.until(Headers.end, Charset.defaultCharset()).inline(bytes1 -> headerLength.set(bytes1.size())));
            bytes = bytes.range(headerLength.get()).dropLast(2).dropFirst(Headers.end.length());
            if (isFile(headers)) {
                try {
                    request.FILES.put(getName(headers), new Pair<>(getFilename(headers), Local.fromBytes(WebServer.tempFolder, UUID.randomUUID() + "_" + getFilename(headers),  bytes.toPrimitive())));
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("Can't create file from data data to raw format");
                }
            } else {
                request.POST.put(getName(headers), bytes.toString());
            }
        }
    }

    //endregion

    //region exception
    public static class RequestException extends WebException {

        public RequestException(String message) {
            super(Code.Internal_Server_Error, message);
        }

        public static class DecodingException extends RequestException {
            public DecodingException(String message) {
                super(message);
            }
        }

    }
    //endregion
}
