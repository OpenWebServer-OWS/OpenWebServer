package com.openwebserver.core.http.content;




import com.lownative.utils.CopyUtils;
import com.openwebserver.core.connection.client.utils.SocketContent;
import com.openwebserver.core.http.Header;
import com.openwebserver.core.http.Headers;

import com.openwebserver.core.objects.Response;
import com.openwebserver.core.WebServer;



import java.util.Arrays;


public interface Content extends SocketContent {

    default long length(){
        return raw().length;
    }

    Type getType();

    default Code getCode() {
        return Code.Ok;
    }

    byte[] raw();


    default byte[] get() {
        return CopyUtils.concat(getHeaders().get(), raw());
    }

    default Content addHeader(Header... headers){
        getHeaders().addAll(Arrays.asList(headers));
        return this;
    }

    Headers getHeaders();

    default void HEAD() {
        if(!getHeaders().containsKey("Content-Type")){
            getHeaders().add(new Header("Content-Type", getType().toString()));
        }
        if(!getHeaders().containsKey("Server")){
            getHeaders().add(WebServer.serverHeader);
        }
        if(!getHeaders().containsKey("Content-Length")){
            getHeaders().add(new Header("Content-Length", String.valueOf(length())));
        }
        if(getHeaders().get(0).getValue() != null){
            getHeaders().add(0, Header.raw("HTTP/" + Response.version + " " + getCode().toString()));
        }
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
            if(mime != null) {
                String[] MIME = mime.split("/");
                return custom(MIME[0], MIME[1]);
            }else{
                return Text;
            }
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
