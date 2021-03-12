package com.openwebserver.core.Content;


import ByteReader.ByteReader;
import com.openwebserver.core.Connection.ConnectionContent;
import com.openwebserver.core.Objects.Headers.Header;
import com.openwebserver.core.Objects.Headers.Headers;

import com.openwebserver.core.Objects.Response;
import com.openwebserver.core.WebServer;

import java.util.Arrays;


public interface Content extends ConnectionContent {

    long length();

    Type getType();

    default Code getCode() {
        return Code.Ok;
    }

    byte[] raw();


    default byte[] get() {
        return ByteReader.combine(getHeaders().get(), raw());
    }

    default Content addHeader(Header... headers){
        getHeaders().addAll(Arrays.asList(headers));
        return this;
    }

    Headers getHeaders();

    default void HEAD() {
        getHeaders().add(0, Header.raw("HTTP/" + Response.version + " " + getCode().toString()));
        getHeaders().add(WebServer.serverHeader);
        getHeaders().add(new Header("Content-Type", getType().toString()));
        getHeaders().add(new Header("Content-Length", String.valueOf(length())));
    }

    enum Type {
        Audio("audio", ""),
        Video("video", ""),
        Image("image", ""),
        Text("text", "plain"),
        Font("font", ""),
        Application("application", ""),
        Custom("", "");

        public String category;
        public String type;

        Type(String category, String type) {
            this.category = category;
            this.type = type;
        }

        public static Type custom(String category, String type) {
            return Type.Custom.edit(category, type);
        }

        public static Type wrap(String mime) {
            String[] MIME = mime.split("/");
            return custom(MIME[0], MIME[1]);
        }

        public Type edit(String category, String type) {
            this.category = category;
            this.type = type;
            return this;
        }

        public Type edit(String type) {
            this.type = type;
            return this;
        }

        @Override
        public String toString() {
            return category + "/" + type;
        }

        public String toString(String type) {
            this.type = type;
            return category + "/" + type;
        }

    }

}
