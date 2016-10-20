package com.orctom.laputa.server.example.util;

import com.orctom.laputa.server.example.exception.CryptoException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public abstract class AES {

  public static final String ALGORITHM = "AES";

  private static Key toKey(byte[] key) {
    return new SecretKeySpec(key, ALGORITHM);
  }

  public static byte[] getKey(String key) {
    return Base64.getDecoder().decode(key);
  }

  public static byte[] decrypt(byte[] encrypted, byte[] key) {
    Key k = toKey(key);
    try {
      Cipher cipher = Cipher.getInstance(ALGORITHM);
      cipher.init(Cipher.DECRYPT_MODE, k);
      return cipher.doFinal(encrypted);
    } catch (Exception e) {
      throw new CryptoException(e.getMessage(), e);
    }
  }

  public static byte[] encrypt(byte[] data, byte[] key) {
    Key k = toKey(key);
    try {
      Cipher cipher = Cipher.getInstance(ALGORITHM);
      cipher.init(Cipher.ENCRYPT_MODE, k);
      return cipher.doFinal(data);
    } catch (Exception e) {
      throw new CryptoException(e.getMessage(), e);
    }
  }

  public static byte[] generateKey() {
    try {
      KeyGenerator generator = KeyGenerator.getInstance(ALGORITHM);
      generator.init(256);
      SecretKey secretKey = generator.generateKey();
      return secretKey.getEncoded();
    } catch (NoSuchAlgorithmException e) {
      throw new CryptoException(e.getMessage(), e);
    }
  }

  public static String generateKeyString() {
    return Base64.getEncoder().encodeToString(generateKey());
  }
}
