package com.openwebserver.core.Objects.Headers;


import com.together.Pair;

import java.util.ArrayList;
import java.util.Arrays;

public class Header extends Pair<String, String> {

    public static String separator = "\r\n";
    public final static String KeyValueSeparator = ":";

    private final ArrayList<Attribute<String>> attributes;

    private String raw;

    @SafeVarargs
    public Header(String key, String value, Attribute<String>... attributes) {
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

    public static Header Decode(String encoded) {
        return new Header().deserialize(encoded);
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

    public String serialize() {
        if (raw != null) {
            return raw;
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append(getKey()).append(KeyValueSeparator).append(getValue()!= null? getValue(): "");
            for (int i = 0; i < attributes.size(); i++) {
                builder.append(attributes.get(i).toString());
                if (i != attributes.size() - 1) {
                    builder.append(Attribute.KeyValuePairSeparator);
                }
            }
            return builder.toString();
        }
    }

    public Header deserialize(String encoded) {
        if (!encoded.contains(KeyValueSeparator)) {
            return Header.raw(encoded);
        }
        Header h = new Header();
        Pair.split(KeyValueSeparator, encoded).relocate((String k, String v) -> {
            h.setKey(k.trim());
            h.setValue(v.trim());
        });
        h.setValue(Attribute.Decode(h.getValue(), h.attributes::add));
        return h;
    }

    public String raw() {
        return raw;
    }
}
