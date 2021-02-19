package com.openwebserver.core.Objects;

import FileManager.Local;
import com.openwebserver.core.Content.Code;
import com.openwebserver.core.Content.Content;
import com.openwebserver.core.Objects.Headers.Header;
import com.openwebserver.core.Objects.Headers.Headers;
import com.openwebserver.core.WebException;
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
        } else {
            this.type = type;
            this.raw = content.toString().getBytes(StandardCharsets.UTF_8);
        }
        this.HEAD();
    }

    protected Response(){
        this.code = null;
        this.raw = null;
        this.type = null;
    }

//    public static Response file(Local file) {
//        return new Response(Code.Ok, Content.file(file), null);
//    }

    public static Response simple(Code code, Object o, Content.Type type) {
        if (o != null) {
            if (o instanceof JSONObject || o instanceof JSONArray) {
                return new Response(code, String.valueOf(o), Content.Type.Application.edit("json"));
            } else if (o instanceof Content) {
                return new Response(code, o, null);
            } else if (o instanceof Local) {
                try {
                    return new Response(code, ((Local)o).read(), Content.Type.wrap(((Local) o).getFilter().getMIME()));
                } catch (IOException e) {
                    return new WebException(e).respond();
                }
            } else if (o instanceof WebException) {
                return ((WebException) o).respond();
            } else if (o instanceof Map) {
                return simple(code, JSONObject.wrap(o), type);
            } else if (o instanceof Collection) {
                return simple(code, new JSONArray().put(o), type);
            } else if (o instanceof Throwable) {
                return new WebException((Throwable) o).respond();
            } else if (o instanceof String || o instanceof Float || o instanceof Double || o instanceof Integer || o instanceof Character || o instanceof Long) {
                return new Response(code, String.valueOf(o), (type != null) ? type : Content.Type.Text.edit("plain"));
            } else {
                return simple(code, JSONObject.wrap(o), type);
            }
        }
        return simple(code, code.getDescription());
    }

    public static Response simple(Code code, Object o) {
        return simple(code, o, null);
    }

    public static Response simple(Object o) {
        if (o instanceof Throwable) {
            return simple(Code.Internal_Server_Error, o, null);
        } else if (o instanceof Code) {
            return simple((Code) o, null, null);
        } else {
            return simple(Code.Ok, o);
        }
    }

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

    //region response methods

    @Override
    public byte[] raw() {
        return raw;
    }

    @Override
    public Response addHeader(Header... headers) {
        Collections.addAll(getHeaders(), headers);
        return this;
    }

    @Override
    public Headers getHeaders() {
        return headers;
    }

    //endregion
}