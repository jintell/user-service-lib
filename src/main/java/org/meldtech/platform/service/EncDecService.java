package org.meldtech.platform.service;

import org.meldtech.platform.util.cipher.Crypto;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class EncDecService {
    Logger log = org.slf4j.LoggerFactory.getLogger(EncDecService.class);

    @Value("${meld.key}")
    private String aesKey;

    @Value("${meld.iv}")
    private String aesIv;

    public String encrypt(String data) throws InvalidAlgorithmParameterException,
            NoSuchPaddingException,
            IllegalBlockSizeException,
            NoSuchAlgorithmException,
            BadPaddingException,
            InvalidKeyException {
        log.debug("Encrypting data");
        Crypto.setKey(aesKey);
        Crypto.setIv(aesIv.replace("%3D", "="));
        log.debug("Encrypted data");
        return Crypto.encrypt(data);
    }

    public String decrypt(String data) throws InvalidAlgorithmParameterException,
            NoSuchPaddingException,
            IllegalBlockSizeException,
            NoSuchAlgorithmException,
            BadPaddingException,
            InvalidKeyException {
        log.debug("Decrypting data");
        Crypto.setKey(aesKey);
        Crypto.setIv(aesIv.replace("%3D", "="));
        log.debug("Decrypted data");
        return Crypto.decrypt(data);
    }
}
