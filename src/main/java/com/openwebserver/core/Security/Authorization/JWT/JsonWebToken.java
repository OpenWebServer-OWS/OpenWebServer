package com.openwebserver.core.Security.Authorization.JWT;

import com.openwebserver.core.Objects.Request;
import com.openwebserver.core.Security.Authorization.Authorizor;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.function.BiFunction;

import static java.nio.charset.StandardCharsets.*;

public class JsonWebToken implements Authorizor<JsonWebToken> {

    public final JSONObject HEADER = new JSONObject();
    public final JSONObject PAYLOAD = new JSONObject();
    private String SIGNATURE;
    private BiFunction<Request, JsonWebToken, Boolean> validator;

    public JsonWebToken() {
        this.HEADER.put("alg", "HS256").put("typ", "JWT");
    }

    public JsonWebToken(String encoded) throws JsonWebTokenException {
        String[] split_string = encoded.split("\\.");
        if(split_string.length == 3) {
            this.SIGNATURE = split_string[2];
            new JSONObject(decode(split_string[0])).toMap().forEach((key, value) -> this.HEADER.put(key, value));
            new JSONObject(decode(split_string[1])).toMap().forEach((key, value) -> this.PAYLOAD.put(key, value));
        }else{
            throw new JsonWebTokenException("Invalid encoded JWT format");
        }
    }

    public String sign(String secret) throws Exception {
        String header = encode(HEADER.toString().getBytes(UTF_8));
        String payload = encode(PAYLOAD.toString().getBytes(UTF_8));
        this.SIGNATURE = JsonWebToken.hash(secret, header + "." + payload);
        return header + "." + payload + "." + SIGNATURE;
    }

    public static String hash(String secret,String data) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256Hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256Hmac.init(secretKey);
        return encode(sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }

    public boolean verify(String secret){
        try {
            return JsonWebToken.hash(secret, encode(HEADER.toString().getBytes(UTF_8)) + "." + encode(PAYLOAD.toString().getBytes(UTF_8))).equals(this.SIGNATURE);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static String encode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String decode(String encodedString) {
        return new String(Base64.getUrlDecoder().decode(encodedString));
    }

    @Override
    public JsonWebToken decode(Request request) throws AuthorizorException {
        final JsonWebToken[] token = {null};
        final AuthorizorException[] exceptions = {null};
        if(request.headers.tryGet("Authorization", header -> {
            try {
                token[0] = new JsonWebToken(header.getValue().split(" ")[1]);
            } catch (JsonWebTokenException e) {
                exceptions[0] = e;
            }
        })){
            if(exceptions[0] != null){
                throw exceptions[0];
            }
            return token[0];
        }

        throw new AuthorizorException("Authorization header not found");
    }

    @Override
    public void setValidator(BiFunction<Request, JsonWebToken, Boolean> validator) {
        this.validator = validator;
    }

    @Override
    public BiFunction<Request, JsonWebToken, Boolean> getValidator() {
        return validator;
    }

    public static Authorizor<JsonWebToken> validate(BiFunction<Request, JsonWebToken, Boolean> validator){
        JsonWebToken authorizor = new JsonWebToken();
        authorizor.setValidator(validator);
        return authorizor;
    }

    public static class JsonWebTokenException extends AuthorizorException {
        public JsonWebTokenException(String message) {
            super(message);
        }
    }
}
