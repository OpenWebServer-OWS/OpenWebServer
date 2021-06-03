package com.openwebserver.core.http;

import com.openwebserver.core.objects.Request;
import com.together.Pair;


public class Attribute<T> extends Pair<T, T> {

    public final static String KeyValueSeparator = "=";
    public final static String KeyValuePairSeparator = ";";

    public Attribute(T key, T value) {
        super(key, value);
    }

    public static Attribute<String> decode(String encodedAttribute) throws Request.RequestException.DecodingException {
        Pair<String, String> keyValueAttribute = Pair.split("=", encodedAttribute);
        if (keyValueAttribute == null) {
            throw new Request.RequestException.DecodingException("Unknown header attribute");
        }
        if (keyValueAttribute.getValue().startsWith("\"") && keyValueAttribute.getValue().endsWith("\"")) {
            keyValueAttribute.setValue(keyValueAttribute.getValue().replaceAll("\"", ""));
        }
        return new Attribute<>(keyValueAttribute.getKey().trim(), keyValueAttribute.getValue().trim());
    }

    public static void Decode(String encoded, Header h) {
        if (!encoded.contains(Attribute.KeyValuePairSeparator) && !encoded.contains(KeyValueSeparator)) {
            h.raw = null;
            return;
        }
        for (String keyValuePairs : h.getValue().split(KeyValuePairSeparator)) {
            try {
                if(keyValuePairs.contains(KeyValueSeparator)) {
                    h.add(Attribute.decode(keyValuePairs));
                    h.raw = null;
                    h.setValue(h.getValue().replace(keyValuePairs,"").trim().replace(KeyValuePairSeparator, ""));
                }
            } catch (Request.RequestException.DecodingException e) {
                break;
            }
        }
    }

    @Override
    public String toString() {
        if (getValue() != null) {
            return getKey() + KeyValueSeparator + getValue();
        } else {
            return (String) getKey();
        }
    }
}
