package com.openwebserver.core.Objects.Headers;

import com.together.Pair;

import java.util.function.Consumer;

public class Attribute<T> extends Pair<T, T> {

    public final static String KeyValueSeparator = "=";
    public final static String KeyValuePairSeparator = ";";

    public Attribute(T key, T value) {
        super(key, value);
    }

    public static Attribute<String> decode(String encodedAttribute) {
        Pair<String, String> keyValueAttribute = Pair.split("=", encodedAttribute);
        return new Attribute<>(keyValueAttribute.getKey().trim(), keyValueAttribute.getValue().trim());
    }

    public static String Decode(String encodedAttributes, Consumer<Attribute<String>> attributeBiConsumer) {
        if (!encodedAttributes.contains(KeyValuePairSeparator) && !encodedAttributes.contains(KeyValueSeparator)) {
            return encodedAttributes;
        } else if (encodedAttributes.contains(KeyValuePairSeparator) && !encodedAttributes.contains(KeyValueSeparator)) {
            return encodedAttributes;
        }
        StringBuilder value = new StringBuilder();
        for (String attribute : encodedAttributes.split(KeyValuePairSeparator)) {
            if (attribute.contains(KeyValueSeparator)) {
                try {
                    attributeBiConsumer.accept(Attribute.decode(attribute));
                } catch (NullPointerException e) {
                    value.append(attribute);
                }
            } else {
                value.append(attribute);
            }
        }
        return value.toString();
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
