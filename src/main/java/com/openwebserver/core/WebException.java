package com.openwebserver.core;


import com.openwebserver.core.Content.Code;
import com.openwebserver.core.Content.Content;
import com.openwebserver.core.Handlers.RequestHandler;
import com.openwebserver.core.Objects.Headers.Header;
import com.openwebserver.core.Objects.Headers.Headers;
import com.openwebserver.core.Objects.Request;
import com.openwebserver.core.Objects.Response;
import com.openwebserver.services.Objects.Service;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;

public class WebException extends Throwable {

    public Code code = Code.Internal_Server_Error;
    private Request request;
    private final JSONObject exception = new JSONObject();
    private Service service;
    private Headers headers = new Headers();

    public static WebException Wrap(Throwable e){
        WebException exception;
        if(e instanceof WebException){
            exception = new WebException( ((WebException) e).getCode(), e.getMessage());
            if(((WebException) e).request != null) exception.request = ((WebException) e).request;

        }else{
            exception = new WebException(e.getMessage());
        }
        exception.addSuppressed(e);
        return exception;
    }

    private WebException(){}

    public WebException(Throwable t){
        this(Code.Internal_Server_Error, t);
    }

    public WebException(InvocationTargetException t){
        this(Code.Internal_Server_Error, t);
    }

    public WebException addHeader(Header ... headers){
        for (Header header : headers) {
            this.headers.add(header);
        }
        return this;
    }

    public WebException addHeaders(Headers headers){
        this.headers.addAll(headers);
        return this;
    }

    public WebException(Code code, String message){
        super(message);
        this.code = code;
    }

    public WebException(Code code, Throwable t){
        super(t);
        this.code = code;
    }

    @Deprecated
    public WebException setService(Service service){
        this.service = service;
        return this;
    }

    public WebException setHandler(RequestHandler handler){
        if(handler instanceof Service){
            this.service = service;
        }
        return this;
    }

    public WebException(Code code, InvocationTargetException t){
        super(t.getTargetException());
        if(t.getTargetException() instanceof WebException){
            this.code = ((WebException) t.getTargetException()).getCode();
            this.headers = ((WebException) t.getTargetException()).headers;
            for (Throwable throwable : t.getTargetException().getSuppressed()) {
                this.addSuppressed(t);
            }
        }

    }

    public WebException(String message){
        this(Code.Internal_Server_Error, message);
    }

    public WebException(WebException e){
        this(e.getCode(), e.getMessage());
    }

    public Code getCode() {
        return code;
    }

    public WebException addRequest(Request request){
        this.request = request;
        return this;
    }

    public WebException extra(String key, Object value){
        if(!exception.has("extra")){
            exception.put("extra",new JSONObject());
        }
        exception.getJSONObject("extra").put(key,value);
        return this;
    }

    public Response respond(boolean detailed){
        exception.put("message", this.getMessage());
        exception.put("code", getCode().getCode());
        exception.put("short", getCode().getDescription());
        if(service != null){
            exception.put("service", getService().getName());
        }
        if(detailed) {
            exception.put("class", this.getClass().getSimpleName());
            exception.put("localized-message", this.getLocalizedMessage());
            JSONArray stackTrace = new JSONArray();
            for (StackTraceElement stackTraceElement : this.getStackTrace()) {
                JSONObject trace = new JSONObject();
                trace.put("file", stackTraceElement.getFileName());
                trace.put("module", stackTraceElement.getModuleName());
                trace.put("method", stackTraceElement.getMethodName());
                trace.put("line", stackTraceElement.getLineNumber());
                trace.put("class", stackTraceElement.getClassName());
                trace.put("native", stackTraceElement.isNativeMethod());
                stackTrace.put(trace);
            }
            exception.put("stacktrace", stackTrace);
        }
        if(request != null){
            exception.put("GET", request.GET());
            exception.put("POST", request.POST());
            exception.put("method", request.getMethod());
            exception.put("path", request.getPath());
        }
        return new Response(code,String.valueOf(exception), Content.Type.Application.edit("json")).addHeader(headers.toArray(new Header[0]));
    }

    private Service getService() {
        return service;
    }

    public Response respond(){
        return respond(false);
    }

    public static WebException NotImplemented(Request request){
        return new WebException(Code.Not_Implemented, "Request not yet implemented").addRequest(request);
    }

}
