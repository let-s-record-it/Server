package com.sillim.recordit.config.security.encrypt;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AESEncryptor {

	private static final String AES = "AES";
	private static final String SHA_256 = "SHA-256";
	private static final String AES_ECB_PKCS5PADDING = "AES/ECB/PKCS5Padding";

	public String encrypt(String strToEncrypt, String secret)
			throws NoSuchAlgorithmException,
					NoSuchPaddingException,
					InvalidKeyException,
					IllegalBlockSizeException,
					BadPaddingException {
		SecretKeySpec secretKey = createKey(secret);
		Cipher cipher = Cipher.getInstance(AES_ECB_PKCS5PADDING);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		return Base64.getEncoder()
				.encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
	}

	public String decrypt(String strToDecrypt, String secret)
			throws NoSuchAlgorithmException,
					NoSuchPaddingException,
					IllegalBlockSizeException,
					BadPaddingException,
					InvalidKeyException {
		SecretKeySpec secretKey = createKey(secret);
		Cipher cipher = Cipher.getInstance(AES_ECB_PKCS5PADDING);
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
	}

	public SecretKeySpec createKey(String secret) throws NoSuchAlgorithmException {
		MessageDigest sha = MessageDigest.getInstance(SHA_256);
		byte[] key =
				sha.digest(Base64.getDecoder().decode(secret.getBytes(StandardCharsets.UTF_8)));
		key = Arrays.copyOf(key, 16);
		return new SecretKeySpec(key, AES);
	}
}
