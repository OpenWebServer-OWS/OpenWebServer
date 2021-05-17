package com.openwebserver.core.handlers;


import com.openwebserver.core.objects.Request;
import com.openwebserver.core.objects.Response;

public interface ContentHandler {

    Response respond(Request request) throws Throwable;
}
