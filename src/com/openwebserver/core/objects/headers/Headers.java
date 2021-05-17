package com.openwebserver.core.objects.headers;

import com.bytestream.ByteStream;
import com.bytestream.Bytes;
import com.openwebserver.core.connection.Connection;
import com.openwebserver.core.connection.ConnectionContent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.function.Consumer;

public class Headers extends ArrayList<Header> implements ConnectionContent {

    public final static String end = Header.separator + Header.separator;

    public Headers(Header... headers) {
        addAll(Arrays.asList(headers));
    }

    public static Headers Incoming(Connection connection) throws Bytes.BytesException, ByteStream.PrematureStreamException {
        Headers headers = Headers.Decode(connection.readUntil(end).toString());
        if(headers.isEmpty()){
            throw new ByteStream.PrematureStreamException("No headers in request");
        }
        return headers;
    }

    public static Headers Decode(String encoded) {
        return new Headers().deserialize(encoded);
    }

    public static Headers Decode(Bytes bytes) {
        return new Headers().deserialize(bytes.toString());
    }

    public Header get(String key, boolean ignoreUppercase) {
        for (Header header : this) {
            if (header.getKey() == null) continue;
            if (header.getKey().equals(key) || (header.getKey().equals(key.toLowerCase(Locale.ROOT)) && ignoreUppercase)) {
                return header;
            }
        }
        return null;
    }

    public int IndexOf(Header header){
        return indexOf(header);
    }

    public void replace(String key, String value){
        get(key).setValue(value);
    }

    public Header get(String key) {
        return get(key, false);
    }

    public boolean tryGet(String key, Consumer<Header> consumer){
        if(containsKey(key)){
            consumer.accept(get(key));
            return true;
        }
        return false;
    }

    public boolean containsKey(String key, boolean ignoreUppercase) {
        return get(key, ignoreUppercase) != null;

    }

    public boolean containsKey(String key) {
        return containsKey(key, false);
    }

    public HashMap<String, Header> toMap() {
        HashMap<String, Header> map = new HashMap<>();
        this.forEach(header -> map.put((header.getKey() != null) ? header.getKey() : "", header));
        return map;
    }

    @Override
    public byte[] get() {
        StringBuilder contentBuilder = new StringBuilder();
        for (int i = 0; i < this.size(); i++) {
            contentBuilder.append(this.get(i));
            if (i != this.size() - 1) {
                contentBuilder.append(Header.separator);
            }
        }
        contentBuilder.append(Headers.end);
        return contentBuilder.toString().getBytes();
    }

    public Headers  deserialize(String encoded) {
        for (String encodedHeader : encoded.split(Header.separator)) {
            this.add(Header.decode(encodedHeader));
        }
        return this;
    }

    public String getMethod() {
        return get(0).raw().split(" ")[0].trim();
    }

    public String getPath(boolean clean) {
        String pathString = get(0).raw().split(" ")[1].trim();
        if (clean && pathString.contains("?")) {
            return pathString.substring(0, pathString.indexOf("?"));
        }
        return pathString;
    }

    public String getPath() {
        return getPath(false);
    }

    public String getProtocol() {
        return get(0).raw().split(" ")[2].trim();
    }


}
