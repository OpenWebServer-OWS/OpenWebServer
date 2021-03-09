package com.openwebserver.core.Content;

public enum Code {

    /**
     * This interim response indicates that everything so far is OK and that the client should continue with the request or ignore it if it is already finished.
     * @see <a href="https://developer.mozilla.org/nl/docs/Web/HTTP/Status/100">https://developer.mozilla.org</a>
     */
    Continue(100, "Continue"),
    /**
     * This code is sent in response to an Upgrade request header by the client, and indicates the protocol the server is switching too.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/101">https://developer.mozilla.org</a>
     */
    Switching_Protocol(101, "Switching Protocol"),
    /**
     * This code indicates that the server has received and is processing the request, but no response is available yet.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/102">https://developer.mozilla.org</a>
     */
    Processing(102, "Processing"),
    /**
     * The request has succeeded. The meaning of a success varies depending on the HTTP method:
     * GET: The resource has been fetched and is transmitted in the message body.
     * HEAD: The entity headers are in the message body.
     * POST: The resource describing the result of the action is transmitted in the message body.
     * TRACE: The message body contains the request message as received by the server
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/200">https://developer.mozilla.org</a>
     */
    Ok(200, "OK"),
    /**
     * The request has succeeded and a new resource has been created as a result of it. This is typically the response sent after a PUT request.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/201">https://developer.mozilla.org</a>
     */
    Created(201, "Created"),
    /**
     * The request has been received but not yet acted upon.
     * It is non-committal, meaning that there is no way in HTTP to later send an asynchronous response indicating the outcome of processing the request.
     * It is intended for cases where another process or server handles the request, or for batch processing.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/202">https://developer.mozilla.org</a>
     */
    Accepted(202, "Accepted"),
    /**
     * There is no content to send for this request, but the headers may be useful.
     * The user-agent may update its cached headers for this resource with the new ones.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/204">https://developer.mozilla.org</a>
     */
    No_Content(204, "No Content"),
    /**
     * This response code is sent after accomplishing request to tell user agent reset document view which sent this request.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/205">https://developer.mozilla.org</a>
     */
    Reset_Content(205, "Reset Content"),
    /**
     * This response code is used because of range header sent by the client to separate download into multiple streams.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/206">https://developer.mozilla.org</a>
     */
    Partial_Content(206, "Partial Content"),
    /**
     * A Multi-Status response conveys information about multiple resources in situations where multiple status codes might be appropriate
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/207">https://developer.mozilla.org</a>
     */
    Multi_Status(207, "Multi-Status"),
    /**
     * Used inside a DAV: propstat response element to avoid enumerating the internal members of multiple bindings to the same collection repeatedly.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/226">https://developer.mozilla.org</a>
     */
    IM_Used(226, "IM  Used"),

    /**
     * The request has more than one possible responses. User-agent or user should choose one of them. There is no standardized way to choose one of the responses.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/300">https://developer.mozilla.org</a>
     */
    Multiple_Choice(300, "Multiple Choice"),
    /**
     * This response code means that URI of requested resource has been changed. Probably, new URI would be given in the response.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/301">https://developer.mozilla.org</a>
     */
    Moved_Permanently(301, "Moved Permanently"),
    /**
     * This response code means that URI of requested resource has been changed temporarily. New changes in the URI might be made in the future.
     * Therefore, this same URI should be used by the client in future requests.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/302">https://developer.mozilla.org</a>
     */
    Found(302, "Found"),
    /**
     * Server sent this response to directing client to get requested resource to another URI with an GET request.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/303">https://developer.mozilla.org</a>
     */
    See_Other(303, "See Other"),
    /**
     * This is used for caching purposes. It is telling to client that response has not been modified.
     * So, client can continue to use same cached version of response.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/304">https://developer.mozilla.org</a>
     */
    Not_Modified(304, "Not Modified"),
    /**
     * Was defined in a previous version of the HTTP specification to indicate that a requested response must be accessed by a proxy.
     * It has been deprecated due to security concerns regarding in-band configuration of a proxy.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/305">https://developer.mozilla.org</a>
     */
    @Deprecated
    Use_Proxy(305, "Use Proxy"),
    /**
     * Server sent this response to directing client to get requested resource to another URI with same method that used prior request.
     * This has the same semantic than the 302 Found HTTP response code, with the exception that the user agent must not change the HTTP method used: if a POST was used in the first request, a POST must be used in the second request.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/307">https://developer.mozilla.org</a>
     */
    Temporary_Redirect(307, "Temporary Redirect"),
    /**
     * This means that the resource is now permanently located at another URI, specified by the Location: HTTP Response header.
     * This has the same semantics as the 301 Moved Permanently HTTP response code, with the exception that the user agent must not change the HTTP method used: if a POST was used in the first request, a POST must be used in the second request.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/308">https://developer.mozilla.org</a>
     */
    Permanent_Redirect(308, "Permanent Redirect"),

    /**
     * This response means that server could not understand the request due to invalid syntax.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/400">https://developer.mozilla.org</a>
     */
    Bad_Request(400, "Bad Request"),

    /**
     * Although the HTTP standard specifies "unauthorized", semantically this response means "unauthenticated".
     * That is, the client must authenticate itself to get the requested response.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/401">https://developer.mozilla.org</a>
     */
    Unauthorized(401, "Unauthorized"),

    /**
     * This response code is reserved for future use.
     * Initial aim for creating this code was using it for digital payment systems however this is not used currently.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/402">https://developer.mozilla.org</a>
     */
    Payment_Required(402, "Payment Required"),
    /**
     * The client does not have access rights to the content, i.e. they are unauthorized, so server is rejecting to give proper response.
     * Unlike 401, the client's identity is known to the server.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/403">https://developer.mozilla.org</a>
     */
    Forbidden(403, "Forbidden"),

    /**
     * The server can not find requested resource. In the browser, this means the URL is not recognized.
     * In an API, this can also mean that the endpoint is valid but the resource itself does not exist.
     * Servers may also send this response instead of 403 to hide the existence of a resource from an unauthorized client.
     * This response code is probably the most famous one due to its frequent occurence on the web.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/404">https://developer.mozilla.org</a>
     */
    Not_Found(404, "Not Found"),

    /**
     * The request method is known by the server but has been disabled and cannot be used. For example, an API may forbid DELETE-ing a resource.
     * The two mandatory methods, GET and HEAD, must never be disabled and should not return this error code.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/405">https://developer.mozilla.org</a>
     */
    Method_Not_Allowed(405, "Method Not Allowed"),
    /**
     * This response is sent when the web server, after performing server-driven content negotiation, doesn't find any content following the criteria given by the user agent.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/406">https://developer.mozilla.org</a>
     */
    Not_Acceptable(406, "Not Acceptable"),
    /**
     * This is similar to 401 but authentication is needed to be done by a proxy.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/407">https://developer.mozilla.org</a>
     */
    Proxy_Authentication_Required(407, "Proxy Authentication Required"),
    /**
     * This response is sent on an idle connection by some servers, even without any previous request by the client.
     * It means that the server would like to shut down this unused connection.
     * This response is used much more since some browsers, like Chrome, Firefox 27+, or IE9, use HTTP pre-connection mechanisms to speed up surfing.
     * Also note that some servers merely shut down the connection without sending this message.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/408">https://developer.mozilla.org</a>
     */
    Request_TimeOut(408, "Request Timeout"),
    /**
     * This response is sent when a request conflicts with the current state of the server.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/409">https://developer.mozilla.org</a>
     */
    Conflict(409, "Conflict"),
    /**
     * This response would be sent when the requested content has been permenantly deleted from server, with no forwarding address.
     * Clients are expected to remove their caches and links to the resource.
     * The HTTP specification intends this status code to be used for "limited-time, promotional services".
     * APIs should not feel compelled to indicate resources that have been deleted with this status code.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/410>https://developer.mozilla.org</a>
     */
    Gone(410, "Gone"),
    /**
     * Server rejected the request because the Content-Length header field is not defined and the server requires it
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/411>https://developer.mozilla.org</a>
     */
    Length_Required(411, "Length Required"),
    /**
     * The client has indicated preconditions in its headers which the server does not meet.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/412>https://developer.mozilla.org</a>
     */
    Precondition_Failed(412, "Precondition Failed"),
    /**
     * Request entity is larger than limits defined by server;
     * the server might close the connection or return an Retry-After header field.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/413>https://developer.mozilla.org</a>
     */
    Payload_Too_Large(413, "Payload Too Large"),
    /**
     * The URI requested by the client is longer than the server is willing to interpret.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/414>https://developer.mozilla.org</a>
     */
    Request_Uri_Too_Large(414, "Request-URI Too Long"),
    /**
     * The media format of the requested data is not supported by the server, so the server is rejecting the request.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/415>https://developer.mozilla.org</a>
     */
    Unsupported_Media_Type(415, "Unsupported Media Type"),
    /**
     * The range specified by the Range header field in the request can't be fulfilled;
     * it's possible that the range is outside the size of the target URI's data.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/416>https://developer.mozilla.org</a>
     */
    Request_Range_Not_Satisfiable(416, "Requested Range Not Satisfiable"),
    /**
     * This response code means the expectation indicated by the Expect request header field can't be met by the server.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/417>https://developer.mozilla.org</a>
     */
    Expectation_Failed(417, "Expectation Failed"),
    /**
     * The server refuses the attempt to brew coffee with a teapot.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/418>https://developer.mozilla.org</a>
     */
    Im_a_Teapot(418, "I'm a teapot"),
    /**
     * The request was directed at a server that is not able to produce a response.
     * This can be sent by a server that is not configured to produce responses for the combination of scheme and authority that are included in the request URI.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/421>https://developer.mozilla.org</a>
     */
    Misdirected_Request(421, "Misdirected Request"),
    /**
     * The request was well-formed but was unable to be followed due to semantic errors.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/422>https://developer.mozilla.org</a>
     */
    Unprocessable_Entity(422, "Unprocessable Entity"),
    /**
     * The resource that is being accessed is locked.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/423>https://developer.mozilla.org</a>
     */
    Locked(423, "Locked"),
    /**
     * The request failed due to failure of a previous request.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/424>https://developer.mozilla.org</a>
     */
    Failed_Dependency(424, "Failed Dependency"),
    /**
     * The server refuses to perform the request using the current protocol but might be willing to do so after the client upgrades to a different protocol.
     * The server sends an Upgrade header in a 426 response to indicate the required protocol(s).
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/426>https://developer.mozilla.org</a>
     */
    Upgrade_Required(426, "Upgrade Required"),
    /**
     * The origin server requires the request to be conditional.
     * Intended to prevent the 'lost update' problem, where a client GETs a resource's state, modifies it, and PUTs it back to the server, when meanwhile a third party has modified the state on the server, leading to a conflict.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/428>https://developer.mozilla.org</a>
     */
    Precondition_Required(428, "Precondition Required"),
    /**
     * The user has sent too many requests in a given amount of time ("rate limiting").
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/429>https://developer.mozilla.org</a>
     */
    Too_Many_Requests(429, "Too Many Requests"),
    /**
     * The server is unwilling to process the request because its header fields are too large.
     * The request MAY be resubmitted after reducing the size of the request header fields.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/431>https://developer.mozilla.org</a>
     */
    Request_Header_Fields_Too_Large(431, "Request Header Fields Too Large"),
    /**
     * The user requests an illegal resource, such as a web page censored by a government.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/451>https://developer.mozilla.org</a>
     */
    Unavailable_For_Legal_Reasons(451, "Unavailable For Legal Reasons"),
    /**
     * The server has encountered a situation it doesn't know how to handle.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/500>https://developer.mozilla.org</a>
     */
    Internal_Server_Error(500, "Internal Server Error"),
    /**
     * The request method is not supported by the server and cannot be handled.
     * The only methods that servers are required to support (and therefore that must not return this code) are GET and HEAD.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/501>https://developer.mozilla.org</a>
     */
    Not_Implemented(501, "Not Implemented"),
    /**
     * This error response means that the server, while working as a gateway to get a response needed to handle the request, got an invalid response.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/502>https://developer.mozilla.org</a>
     */
    Bad_Gateway(502, "Bad Gateway"),

    /**
     * The server is not ready to handle the request.
     * Common causes are a server that is down for maintenance or that is overloaded.
     * @Note that together with this response, a user-friendly page explaining the problem should be sent.
     * This responses should be used for temporary conditions and the Retry-After: HTTP header should, if possible, contain the estimated time before the recovery of the service.
     * The webmaster must also take care about the caching-related headers that are sent along with this response, as these temporary condition responses should usually not be cached.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/503>https://developer.mozilla.org</a>
     */
    Service_Unavailable(503, "Service Unavailable"),
    /**
     * This error response is given when the server is acting as a gateway and cannot get a response in time.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/504>https://developer.mozilla.org</a>
     */
    Gateway_TimeOut(504, "Gateway Timeout"),
    /**
     * The HTTP version used in the request is not supported by the server.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/505>https://developer.mozilla.org</a>
     */
    Http_Version_Not_Supported(505, "HTTP Version Not Supported"),
    /**
     * The server has an internal configuration error: transparent content negotiation for the request results in a circular reference.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/506>https://developer.mozilla.org</a>
     */
    Variant_Also_Negotiates(506, "Variant Also Negotiates"),
    /**
     * The server has an internal configuration error: the chosen variant resource is configured to engage in transparent content negotiation itself, and is therefore not a proper end point in the negotiation process.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/507>https://developer.mozilla.org</a>
     */
    Insufficient_Storage(507, "Insufficient Storage"),
    /**
     * The server detected an infinite loop while processing the request.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/508>https://developer.mozilla.org</a>
     */
    Loop_Detected(508, "Insufficient Storage"),
    /**
     * Further extensions to the request are required for the server to fulfill it.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/510>https://developer.mozilla.org</a>
     */
    Not_Extended(510, "Not Extended"),
    /**
     * The 511 status code indicates that the client needs to authenticate to gain network access.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/511>https://developer.mozilla.org</a>
     */
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
