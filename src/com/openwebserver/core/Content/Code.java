package com.openwebserver.core.Content;

public enum Code {
    Accepted(202, "Accepted"),
    Im_a_Teapot(418, "I'm a teapot"),
    Bad_Gateway(502, "Bad Gateway"),
    Bad_Request(400, "Bad Request"),
    Conflict(409, "Conflict"),
    Created(201, "Created"),
    Expectation_Failed(417, "Expectation Failed"),
    Forbidden(403, "Forbidden"),
    Found(302, "Found"),
    Gateway_TimeOut(504, "Gateway Timeout"),
    Gone(410, "Gone"),
    Http_Version_Not_Supported(505, "HTTP Version Not Supported"),
    Internal_Server_Error(500, "Internal Server Error"),
    Length_Required(411, "Length Required"),
    Method_Not_Allowed(405, "Method Not Allowed"),
    Moved_Permanently(301, "Moved Permanently"),
    No_Content(204, "No Content"),
    Not_Acceptable(406, "Not Acceptable"),
    Not_Found(404, "Not Found"),
    Not_Implemented(501, "Not Implemented"),
    Not_Modified(304, "Not Modified"),
    Ok(200, "OK"),
    Partial_Content(206, "Partial Content"),
    Payment_Required(402, "Payment Required"),
    Precondition_Failed(412, "Precondition Failed"),
    Proxy_Authentication_Required(407, "Proxy Authentication Required"),
    Request_Entity_Too_Large(413, "Request Entity Too Large"),
    Request_TimeOut(408, "Request Timeout"),
    Request_Uri_Too_Large(414, "Request-URI Too Long"),
    Request_Range_Not_Satisfiable(416, "Requested Range Not Satisfiable"),
    Reset_Content(202, "Reset Content"),
    See_Other(303, "See Other"),
    Service_Unavailable(503, "Service Unavailable"),
    Temporary_Redirect(307, "Temporary Redirect"),
    Unauthorized(401, "Unauthorized"),
    Unsupported_Media_Type(415, "Unsupported Media Type"),
    Use_Proxy(305, "Use Proxy");

    private final int code;
    private final String description;

    Code(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static Code match(int code) {
        for (Code c : Code.class.getEnumConstants()) {
            if (c.code == code) {
                return c;
            }
        }
        return Internal_Server_Error;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return code + " " + description;
    }
}
