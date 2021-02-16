package com.openwebserver.core.Handlers;


import com.openwebserver.core.Objects.Request;
import com.openwebserver.core.Objects.Response;

public interface ContentHandler {

    Response respond(Request request) throws Throwable;
}
