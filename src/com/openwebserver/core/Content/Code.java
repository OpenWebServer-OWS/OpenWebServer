package com.openwebserver.core.Content;

public enum Code {

    Continue(100, "Continue"),
    Switching_Protocol(101, "Switching Protocol"),
    Processing(102, "Processing"),

    Ok(200, "OK"),
    Created(201, "Created"),
    Accepted(202, "Accepted"),
    No_Content(204, "No Content"),
    Reset_Content(205, "Reset Content"),
    Partial_Content(206, "Partial Content"),
    Multi_Status(207, "Multi-Status"),
    IM_Used(226, "IM  Used"),

    Multiple_Choice(300, "Multiple Choice"),
    Moved_Permanently(301, "Moved Permanently"),
    Found(302, "Found"),
    See_Other(303, "See Other"),
    Not_Modified(304, "Not Modified"),
    Use_Proxy(305, "Use Proxy"),
    Temporary_Redirect(307, "Temporary Redirect"),
    Permanent_Redirect(308, "Permanent Redirect"),

    Bad_Request(400, "Bad Request"),
    Unauthorized(401, "Unauthorized"),
    Payment_Required(402, "Payment Required"),
    Forbidden(403, "Forbidden"),
    Not_Found(404, "Not Found"),
    Method_Not_Allowed(405, "Method Not Allowed"),
    Not_Acceptable(406, "Not Acceptable"),
    Proxy_Authentication_Required(407, "Proxy Authentication Required"),
    Request_TimeOut(408, "Request Timeout"),
    Conflict(409, "Conflict"),
    Gone(410, "Gone"),
    Length_Required(411, "Length Required"),
    Precondition_Failed(412, "Precondition Failed"),
    Request_Entity_Too_Large(413, "Request Entity Too Large"),
    Request_Uri_Too_Large(414, "Request-URI Too Long"),
    Unsupported_Media_Type(415, "Unsupported Media Type"),
    Request_Range_Not_Satisfiable(416, "Requested Range Not Satisfiable"),
    Expectation_Failed(417, "Expectation Failed"),
    Im_a_Teapot(418, "I'm a teapot"),
    Misdirected_Request(421, "Misdirected Request"),
    Unprocessable_Entity(422, "Unprocessable Entity"),
    Locked(423, "Locked"),
    Failed_Dependency(424, "Failed Dependency"),
    Upgrade_Required(426, "Upgrade Required"),
    Precondition_Required(428, "Precondition Required"),
    Too_Many_Requests(429, "Too Many Requests"),
    Request_Header_Fields_Too_Large(431, "Request Header Fields Too Large"),
    Unavailable_For_Legal_Reasons(451, "Unavailable For Legal Reasons"),

    Internal_Server_Error(500, "Internal Server Error"),
    Not_Implemented(501, "Not Implemented"),
    Bad_Gateway(502, "Bad Gateway"),
    Service_Unavailable(503, "Service Unavailable"),
    Gateway_TimeOut(504, "Gateway Timeout"),
    Http_Version_Not_Supported(505, "HTTP Version Not Supported"),
    Variant_Also_Negotiates(506, "Variant Also Negotiates"),
    Insufficient_Storage(507, "Insufficient Storage"),
    Loop_Detected(508, "Insufficient Storage"),
    Not_Extended(510, "Not Extended"),
    Network_Authentication_Required(511, "Network Authentication Required");

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
