package org.meldtech.platform.service.crypto;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for HMAC-based cryptographic operations including signing and verification.
 * Uses constant-time comparison to prevent timing attacks.
 */
public final class HmacUtil {
    private static final String HMAC_ALGO = "HmacSHA256"; // HmacSHA512 is also fine
    private static final long TIME_WINDOW_MILLIS = TimeUnit.MINUTES.toMillis(5); // ±5 minutes

    private HmacUtil() {}

    /**
     * Sign data with HMAC and return a Base64 encoded signature
     */
    public static String signToBase64(String data, byte[] secretKey) {
        byte[] sig = signToBytes(data, secretKey);
        return Base64.getEncoder().encodeToString(sig);
    }

    /**
     * Sign data with HMAC and return a raw byte array signature
     */
    public static byte[] signToBytes(String data, byte[] secretKey) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGO);
            mac.init(new SecretKeySpec(secretKey, HMAC_ALGO));
            return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("HMAC signing failure", e);
        }
    }

    /**
     * Compare two byte arrays in constant time to prevent timing attacks
     */
    public static boolean constantTimeEquals(byte[] a, byte[] b) {
        // Constant-time compare to avoid timing attacks
        return MessageDigest.isEqual(a, b);
    }

    /**
     * Verify a Base64 encoded signature against the expected data and secret key
     */
    public static boolean verifyBase64(String data, String providedBase64Sig, byte[] secretKey) {
        byte[] expected = signToBytes(data, secretKey);
        byte[] provided;
        try {
            provided = Base64.getDecoder().decode(providedBase64Sig);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return constantTimeEquals(expected, provided);
    }

    /**
     * Validate that the timestamp is within the allowed time window
     * to prevent replay attacks
     */
    public static boolean validateTimestamp(String timestampHeader) {
        try {
            long clientTimestamp = Long.parseLong(timestampHeader);
            long serverTimestamp = System.currentTimeMillis();
            return clientTimestamp <= serverTimestamp && (clientTimestamp + TIME_WINDOW_MILLIS) > serverTimestamp;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}