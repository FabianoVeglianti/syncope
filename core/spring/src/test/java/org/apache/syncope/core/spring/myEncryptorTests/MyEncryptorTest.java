package org.apache.syncope.core.spring.myEncryptorTests;


import org.apache.syncope.common.lib.types.CipherAlgorithm;
import org.apache.syncope.core.spring.security.Encryptor;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Stream;


public class MyEncryptorTest {

    private Encryptor encryptor;

    private void setup(){
        encryptor = Encryptor.getInstance();
    }

    private static Stream<Arguments>  getEncodeParameters(){
        return Stream.of(
                Arguments.of(null, null, CipherAlgorithm.SHA, null),
                Arguments.of("", "DA39A3EE5E6B4B0D3255BFEF95601890AFD80709", CipherAlgorithm.SHA1, null),
                Arguments.of("fabiano", "UhgavyjwSlGEJyyevj4whg==", null, null),
                Arguments.of("fabiano", "UhgavyjwSlGEJyyevj4whg==", CipherAlgorithm.AES, null),
                Arguments.of("fabiano", "67B8957DA97DC943F201DCB516DE1081AD24E97F92AB4527F68162ADF78EAE09", CipherAlgorithm.SHA256, null),
                Arguments.of("fabiano", "2E4655C36B3475D76B37A3409D52A6DE2EDBCB56F1E1F224C8EDD1A72F5AA4B43FB683A62EED500F26569DA08CE861D7BB2D87BD2B0C3D9A34C1849F00705A28", CipherAlgorithm.SHA512, null),
                Arguments.of("fabiano", null, CipherAlgorithm.BCRYPT, null)
        );
    }

    @ParameterizedTest(name = "encodeTest")
    @MethodSource("getEncodeParameters")
    public void encodeTest(String plaintext, String ciphertext, CipherAlgorithm cipherAlgorithm, Class<? extends Exception> expectedException) throws NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        setup();
        if(expectedException != null) {
            Assertions.assertThrows(expectedException, () -> {
                encryptor.encode(plaintext, cipherAlgorithm);
            });
        } else {
            if(cipherAlgorithm != CipherAlgorithm.BCRYPT) {
                String actualValue = encryptor.encode(plaintext, cipherAlgorithm);
                Assertions.assertEquals(ciphertext, actualValue);
            } else {
                String encoded = encryptor.encode(plaintext, cipherAlgorithm);
                boolean verified = encryptor.verify(plaintext, cipherAlgorithm, encoded);
                Assertions.assertEquals(true, verified);
            }
        }
    }

    private static Stream<Arguments>  getDecodeParameters(){
        return Stream.of(
                Arguments.of(null, null, CipherAlgorithm.SHA, null),
                Arguments.of("", "FN0NSfsM70DxXlF9hhLitQ==", CipherAlgorithm.AES, null),
                Arguments.of(null, "UhgavyjwSlGEJyyevj4whg==", null, null)
                );
    }

    @ParameterizedTest(name = "decodeTest")
    @MethodSource("getDecodeParameters")
    public void decodeTest(String plaintext, String ciphertext, CipherAlgorithm cipherAlgorithm, Class<? extends Exception> expectedException) throws NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        setup();
        if(expectedException != null) {
            Assertions.assertThrows(expectedException, () -> {
                encryptor.encode(ciphertext, cipherAlgorithm);
            });
        } else {
            String actualValue = encryptor.decode(ciphertext, cipherAlgorithm);
            Assertions.assertEquals(plaintext, actualValue);
        }


    }

    private static Stream<Arguments>  getVerifyParameters(){
        return Stream.of(
                Arguments.of(null, null, CipherAlgorithm.SHA, null, false),
                Arguments.of("", null, CipherAlgorithm.SHA1, null, false),
                Arguments.of("fabiano", "UhgavyjwSlGEJyyevj4whg==", null, null, true),
                Arguments.of("fabiano", "UhgavyjwSlGEJyyevj4whg==", CipherAlgorithm.AES, null, true),
                Arguments.of("fabiano", "64c06b88ddd8f8aefa9a31c8e3a3a7894d430641db20673321c675089f3bc4cc", CipherAlgorithm.SHA256, null, false),
                Arguments.of("fabiano", "2E4655C36B3475D76B37A3409D52A6DE2EDBCB56F1E1F224C8EDD1A72F5AA4B43FB683A62EED500F26569DA08CE861D7BB2D87BD2B0C3D9A34C1849F00705A28", CipherAlgorithm.SHA512, null, true),
                Arguments.of("fabiano", "$2y$12$cXHPBGT7CAXdwkAHop6pF.C/DwpBnsvnB6cPVuNv3QQPq8mTEIwUC ", CipherAlgorithm.BCRYPT, null, false),
                Arguments.of("fabiano", "a", CipherAlgorithm.SHA256, null, false)
        );
    }

    @ParameterizedTest(name = "verifyTest")
    @MethodSource("getVerifyParameters")
    public void verifyTest(String plaintext, String ciphertext, CipherAlgorithm cipherAlgorithm, Class<? extends Exception> expectedException, boolean expected) throws NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        setup();
        if(expectedException != null) {
            //in questo caso l'eccezione non viene lanciata dal metodo testato, ma viene gestita al suo
            //interno stampando un messaggio di errore sul log e proseguendo con l'esecuzione dell'algoritmo
            //se viene lanciata un'eccezione ci aspettiamo che la verifica dia esito negativo
                boolean actualValue = encryptor.verify(plaintext, cipherAlgorithm, ciphertext);
                Assertions.assertEquals(false, actualValue);
        } else {
            boolean actualValue = encryptor.verify(plaintext, cipherAlgorithm, ciphertext);
            Assertions.assertEquals(expected, actualValue);
        }
    }

}
