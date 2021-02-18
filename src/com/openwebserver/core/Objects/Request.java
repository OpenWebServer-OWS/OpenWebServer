package com.openwebserver.core.Objects;

import ByteReader.ByteReader;
import FileManager.Local;
import Pair.Pair;
import Reflection.ObjectEditor.EditWatchdog;
import com.openwebserver.core.Connection.Connection;
import com.openwebserver.core.Connection.ConnectionManager;
import com.openwebserver.core.Content.Code;
import com.openwebserver.core.Domain;
import com.openwebserver.core.Handlers.RequestHandler;
import com.openwebserver.core.Objects.Headers.Header;
import com.openwebserver.core.Objects.Headers.Headers;
import com.openwebserver.core.Routing.Route;
import com.openwebserver.core.Routing.Router;
import com.openwebserver.core.Sessions.Session;
import com.openwebserver.core.WebException;
import com.openwebserver.core.WebServer;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

public class Request extends Route{



    protected HashMap<String, Object> POST = new HashMap<>();
    protected HashMap<String, String> GET;
    protected HashMap<String, Pair<String, Local>> FILES = new HashMap<>();
    public HashMap<String, Object> SESSION;
    public Session session;

    public final Headers headers;
    private Domain domain;
    private String connectionRef;
    private RequestHandler handler;

    private Request(Headers headers){
        super(headers.getPath(),Method.valueOf(headers.getMethod()));
        this.headers = headers;
        this.domain = Router.getDomain(getAlias());
        this.GET = URLEncoded(headers.getPath());

    }


    public String getAlias() {
        return headers.get("Host").getValue();
    }


    public static Request deserialize(Connection connection) throws ByteReader.ByteReaderException.PrematureStreamException, RequestException {
        Request request = new Request(Headers.Incoming(connection));
        request.connectionRef = connection.getConnectionString();
        if (request.headers.containsKey("Content-Length")) {
            request.decode(connection.readFor(request.size()).result());
        }
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

    public String GET(String key) {
        return GET.get(key);
    }

    public HashMap<String, Object> POST(){
        return POST;
    }

    public <T> T POST(String key, Class<T> type){
        return (T) POST.get(key);
    }

    public Object POST(String key){
        return POST.get(key);
    }

    public <T> T SESSION(String key, Class<T> type){
        return (T) POST.get(key);
    }

    public Object SESSION(String key){
        return POST.get(key);
    }

    public void setHandler(RequestHandler requestHandler) {
        this.handler = requestHandler;
    }

    public RequestHandler getHandler() {
        return handler;
    }

    //region request access

    public <T> T access(ConnectionManager.Access access) throws IOException, ConnectionManager.ConnectionManagerException {
        return (T) ConnectionManager.Access(connectionRef, access.getReturnType());
    }


    //endregion

    //region request decoding

    private void decode(byte[] data) throws RequestException.DecodingException {
        if (data.length <= 0) {
            return;
        }
        Header contentType = headers.get("Content-Type");
        switch (contentType.getValue()) {
            case "multipart/form-data": {
                MultipartDecoder(data);
                break;
            }
            case "application/x-www-form-urlencoded": {
                POST.putAll(URLEncoded(new String(data, Charset.defaultCharset())));
                break;
            }
            case "application/json": {
                POST = (HashMap<String, Object>) new JSONObject(new String(data, Charset.defaultCharset())).toMap();
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


    private void MultipartDecoder(byte[] encoded) throws RequestException.DecodingException {
        try {
            boolean done = false;
            String boundary = "--" + headers.get("Content-Type").get("boundary").getValue();
            Thread t = new ByteReader(encoded).foreachFind(boundary.getBytes(Charset.defaultCharset()), new ByteReader.PatternFunction() {
                @Override
                public void OnFinding(byte[] bytes) {
                    try {
                        FormData data = FormData.decode(bytes);
                        if (data.isFile()) {
                            FILES.put(data.getName(), new Pair<>(data.getFilename(), data.file));
                        } else {
                            POST.put(data.getName().replaceAll("\"", "").trim(), data.toString().trim());
                        }
                    } catch (ByteReader.ByteReaderException.PrematureStreamException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void OnException(Throwable t) {
                    t.printStackTrace();
                }
            });
            t.start();
            while (t.isAlive()) Thread.onSpinWait();
        } catch (Throwable t) {
            throw new RequestException.DecodingException("Can't decode multipart form data");
        }

    }



    private static class FormData {


        private final Headers headers;
        private final int headerLength;

        private Local file;
        private byte[] data;

        public FormData(String head) {
            this.headers = Headers.Decode(head);
            this.headerLength = head.length();
        }

        public static FormData decode(byte[] data) throws ByteReader.ByteReaderException.PrematureStreamException {
            return new FormData(new ByteReader(data).readUntil(Headers.end).toString()).setData(data);
        }

        public FormData setData(int beginIndex, byte... bytes) {
            if (isFile()) {
                try {
                    this.file = Local.fromBytes(WebServer.tempFolder, UUID.randomUUID().toString() + "_" + getFilename(), Arrays.copyOfRange(bytes, beginIndex, bytes.length - 2));
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("Can't create file from data data to raw format");
                    this.data = bytes;
                }
            } else {
                this.data = Arrays.copyOfRange(bytes, beginIndex, bytes.length - 2);
            }
            return this;
        }

        public FormData setData(byte... bytes) {
            return setData(headerLength, bytes);
        }

        public String getName() {
            return headers.get("Content-Disposition").get("name").getValue().replaceAll("\"", "").trim();
        }

        public String getFilename() {
            return headers.get("Content-Disposition").get("filename").getValue().replaceAll("\"", "").trim();
        }

        public boolean isFile() {
            return headers.get("Content-Disposition").contains("filename") && !headers.get("Content-Disposition").get("filename").getValue().equals("");
        }

        @Override
        public String toString() {
            return new String(data, Charset.defaultCharset());
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
