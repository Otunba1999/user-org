package com.otunba.utils;

import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import static com.otunba.utils.KeyGeneratorUtility.generateRsaKey;

@Component
public class RSAKeyProperties {
    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;

    public RSAKeyProperties() {
        KeyPair keyPair = generateRsaKey();
        this.privateKey = (RSAPrivateKey) keyPair.getPrivate();
        this.publicKey = (RSAPublicKey) keyPair.getPublic();
    }

    public RSAPrivateKey getPrivateKey() {
        return privateKey;
    }

    public RSAPublicKey getPublicKey() {
        return publicKey;
    }
}
