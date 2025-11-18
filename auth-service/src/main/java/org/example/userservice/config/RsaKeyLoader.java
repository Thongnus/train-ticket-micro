package org.example.userservice.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Getter
@Component
public class RsaKeyLoader {

    private final PrivateKey privateKey;

    public RsaKeyLoader(@Value("${jwt.private-key}") Resource privateKeyResource) {
        try (InputStream is = privateKeyResource.getInputStream()) {
            String pem = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            this.privateKey = loadPrivateKeyFromPem(pem);
        } catch (Exception e) {
            throw new IllegalStateException("Không load được private key", e);
        }
    }

    private PrivateKey loadPrivateKeyFromPem(String pem) throws Exception {
        String cleanKey = pem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] keyBytes = Base64.getDecoder().decode(cleanKey);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

}
