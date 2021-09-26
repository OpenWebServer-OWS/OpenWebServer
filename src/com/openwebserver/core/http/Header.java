package com.openwebserver.core.http;


import com.together.Pair;

import java.util.ArrayList;
import java.util.Arrays;

public class Header extends Pair<String, String> {

    public static String separator = "\r\n";
    public final static String KeyValueSeparator = ":";

    private final ArrayList<Attribute<String>> attributes;

    String raw;

    @SafeVarargs
    public Header(String key, String value, Attribute<String> ... attributes) {
        super(key, value);
        this.attributes = new ArrayList<>(Arrays.asList(attributes));
    }

    public Header(String key, String value) {
        super(key, value);
        this.attributes = new ArrayList<>();
    }

    public Header() {
        super(null, null);
        attributes = new ArrayList<>();
    }

    public static Header raw(String headerString) {
        Header h = new Header();
        h.raw = headerString;
        return h;
    }
    public static Header decode(String encoded) {
        if (!encoded.contains(KeyValueSeparator) || encoded.contains("://")) {
            return Header.raw(encoded);
        }
        String[] kvp = encoded.split(":");
        Header h = new Header(kvp[0].trim(), kvp[1].trim());
        Attribute.Decode(encoded,h);
        return h;
    }

    public boolean contains(String attribute) {
        for (Attribute<String> stringAttribute : attributes) {
            if (stringAttribute.getKey().equals(attribute)) {
                return true;
            }
        }
        return false;
    }

    public Attribute<String> get(String attribute) {
        for (Attribute<String> stringAttribute : attributes) {
            if (stringAttribute.getKey().equals(attribute)) {
                return stringAttribute;
            }
        }
        return null;
    }

    public ArrayList<Attribute<String>> getAttributes() {
        return attributes;
    }

    public Header add(Attribute<String> attribute) {
        this.attributes.add(attribute);
        return this;
    }

    public String raw() {
        return raw;
    }

    @Override
    public String toString() {
        if (raw != null) {
            return raw;
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append(getKey()).append(KeyValueSeparator).append(getValue()!= null? " " + getValue(): "");
            if(!attributes.isEmpty()){
                builder.append(Attribute.KeyValuePairSeparator).append(" ");
            }
            for (int i = 0; i < attributes.size(); i++) {
                if(i != 0){
                    builder.append(Attribute.KeyValuePairSeparator).append(" ");
                }
                builder.append(attributes.get(i).toString());
            }
            return builder.toString();
        }
    }
}
