package com.openwebserver.core.objects;

import FileManager.Local;
import com.openwebserver.core.content.Code;
import com.openwebserver.core.content.Content;
import com.openwebserver.core.objects.headers.Header;
import com.openwebserver.core.objects.headers.Headers;
import com.openwebserver.core.WebException;
import com.together.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class Response implements Content {

    public static String version = "1.1";

    private final Headers headers = new Headers();
    private final Type type;
    private final byte[] raw;
    private final Code code;

    public Response(Code code, Object content, Content.Type type) {
        this.code = code;
        if (content instanceof Content) {
            this.type = ((Content) content).getType();
            this.raw = ((Content) content).raw();
            if(((Content) content).getHeaders()!= null) {
                this.headers.addAll(((Content) content).getHeaders());
            }
        } else {
            this.type = type;
            this.raw = content.toString().getBytes(StandardCharsets.UTF_8);
        }
        this.HEAD();
    }

    public Response(Code code, Content content) {
        this(code,content, content.getType());
    }

    protected Response(){
        this.code = null;
        this.raw = null;
        this.type = null;
    }

    public static Response simple(Code code, Object o, Content.Type type) {
        if (o != null) {
            if (o instanceof JSONObject || o instanceof JSONArray) {
                return new Response(code, String.valueOf(o), Content.Type.Application.edit("json"));
            }
            else if (o instanceof Content) {
                return new Response(code, o, null);
            }
            else if (o instanceof Local l) {
                if(l.exists()) {
                        return new Response(code, new Content() {
                            @Override
                            public long length() {
                                return l.getSize();
                            }

                            @Override
                            public Type getType() {
                                if(l.getExtension().equals(".js")){
                                    return Type.Application.edit("javascript");
                                }
                                return Type.wrap(l.getMIME());
                            }

                            @Override
                            public byte[] raw() {
                                try {
                                    return l.read();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    return null;
                                }
                            }

                            @Override
                            public Headers getHeaders() {
                                return null;
                            }
                        }, null);
                    }
                    else{
                        return Response.simple(Code.Not_Found);
                    }
            }
            else if (o instanceof WebException) {
                return ((WebException) o).respond();
            }
            else if (o instanceof Map) {
                return simple(code, JSONObject.wrap(o), type);
            }
            else if (o instanceof Collection || o.getClass().isArray()) {
                return simple(code, new JSONArray(o), type);
            }
            else if (o instanceof Throwable) {
                return new WebException((Throwable) o).respond();
            }
            else if (o instanceof String || o instanceof Float || o instanceof Double || o instanceof Integer || o instanceof Character || o instanceof Long || o instanceof Boolean) {
                return new Response(code, String.valueOf(o), (type != null) ? type : Type.Text.edit("plain"));
            }
            else {
                return simple(code, JSONObject.wrap(o), type);
            }
        }
        return simple(code, code.getDescription());
    }

    public static Response simple(Code code, Object o) {
        return simple(code, o, null);
    }

    @SuppressWarnings("unchecked")
    public static Response simple(Object o) {
        if (o instanceof Throwable) {
            return simple(Code.Internal_Server_Error, o, null);
        } else if (o instanceof Code) {
            return simple((Code) o, null, null);
        } else if(o instanceof Pair){
            return simple(((Pair<Code, ?>) o).getKey(), ((Pair<Code, ?>) o).getValue(), null);
        } else if(o instanceof Header[]){
            return simple(Code.Ok, o).addHeader((Header[]) o);
        } else if(o instanceof Headers){
            return simple(Code.Ok, o).addHeaders((Headers) o);
        }else{
            return simple(Code.Ok, o);
        }
    }

    public static Response simple(){
        return Response.simple(Code.Ok);
    }

    //region response methods

    @Override
    public long length() {
        return raw.length;
    }

    @Override
    public Type getType() {
        return type;
    }

    public Code getCode() {
        return code;
    }

    @Override
    public byte[] raw() {
        return raw;
    }

    @Override
    public Response addHeader(Header... headers) {
        Collections.addAll(getHeaders(), headers);
        return this;
    }

    public Response addHeaders(Headers headers){
        getHeaders().addAll(headers);
        return this;
    }

    @Override
    public Headers getHeaders() {
        return headers;
    }

    //endregion
}