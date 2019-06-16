package com.project.juan_.jpc_locator;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Algoritmo_AES {

    public Algoritmo_AES() {
    }

    public static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    public static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

//    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//    bm.compress(Bitmap.CompressFormat.PNG, 100, baos); // bm is the bitmap object
//        byte[] b = baos.toByteArray();
//
//        byte[] keyStart = "this is a key".getBytes();
//        KeyGenerator kgen = KeyGenerator.getInstance("AES");
//        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
//    sr.setSeed(keyStart);
//    kgen.init(128, sr); // 192 and 256 bits may not be available
//        SecretKey skey = kgen.generateKey();
//        byte[] key = skey.getEncoded();
//
//        // encrypt
//        byte[] encryptedData = encrypt(key,b);
//        // decrypt
//        byte[] decryptedData = decrypt(key,encryptedData);
}
