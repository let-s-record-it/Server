package com.sillim.recordit.config.security.encrypt;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

public interface RsaKeyUtils {

    static Key getKeyByRsa(String modulus, String exponent)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] decodedModulus = Base64.getUrlDecoder().decode(modulus);
        byte[] decodeExponent = Base64.getUrlDecoder().decode(exponent);

        return KeyFactory.getInstance("RSA")
                .generatePublic(
                        new RSAPublicKeySpec(
                                new BigInteger(1, decodedModulus),
                                new BigInteger(1, decodeExponent)));
    }
}
