package com.openwebserver.core.Objects.Headers;

import Collective.Collective;
import Pair.Pair;
import Serialization.Deserializer;
import Serialization.Serializer;

import java.util.ArrayList;
import java.util.Arrays;

public class Header extends Pair<String, String> implements Serializer<String>, Deserializer<Header, String> {

    public static String separator = "\r\n";
    public static String KeyValueSeparator = ":";

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

    @Override
    public String serialize() {
        if (raw != null) {
            return raw;
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append(getKey()).append(KeyValueSeparator).append(getValue()!= null? getValue(): "");
            Collective.On(new Collective<Attribute<String>>() {
                @Override
                public void forEach(Attribute<String> attribute) {
                    builder.append(attribute.toString());
                }

                @Override
                public void between() {
                    builder.append(Attribute.KeyValuePairSeparator);
                }
            }, attributes);
            return builder.toString();
        }
    }

    @Override
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
