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
                Arguments.of("fabiano", null, CipherAlgorithm.BCRYPT, null),
                Arguments.of("fabiano", null, CipherAlgorithm.SMD5, null),
                Arguments.of("fabiano", null, CipherAlgorithm.SSHA, null),
                Arguments.of("fabiano", null, CipherAlgorithm.SSHA1, null),
                Arguments.of("fabiano", null, CipherAlgorithm.SSHA256, null),
                Arguments.of("fabiano", null, CipherAlgorithm.SSHA512, null)
        );
    }

    @ParameterizedTest(name = "encodeTest")
    @MethodSource("getEncodeParameters")
    public void encodeTest(String plaintext, String ciphertext, CipherAlgorithm cipherAlgorithm, Class<? extends Exception> expectedException) throws NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        CipherAlgorithm[] random = {CipherAlgorithm.BCRYPT, CipherAlgorithm.SMD5, CipherAlgorithm.SSHA,
                CipherAlgorithm.SSHA1, CipherAlgorithm.SSHA256, CipherAlgorithm.SSHA512};
        setup();
        if(expectedException != null) {
            Assertions.assertThrows(expectedException, () -> {
                encryptor.encode(plaintext, cipherAlgorithm);
            });
        } else {
            boolean thereIsRandomness = false;
            for(int i = 0; i < random.length; i++){
                if(cipherAlgorithm == random[i]) {
                    thereIsRandomness = true;
                    break;
                }
            }
            if(!thereIsRandomness) {
                String actualValue = encryptor.encode(plaintext, cipherAlgorithm);
                Assertions.assertEquals(ciphertext, actualValue);
            } else {
                String encoded = encryptor.encode(plaintext, cipherAlgorithm);
                System.out.println(cipherAlgorithm.name() + " - " +encoded);
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
                Arguments.of("fabiano", "7819330D65ECD73C6E800AE995163CF3ED23F7B0ACE90CDE", CipherAlgorithm.SMD5, null, false),
                Arguments.of("fabiano", "9586AE9293F3CE9229D040206894BC76BA86E566184F7F50B6FBCA99", CipherAlgorithm.SSHA, null, true),
                Arguments.of("fabiano", "$E018BE09CCA3141D862AB87A4F4B9679FC2DEA0DB997EA2D4473A278", CipherAlgorithm.SSHA1, null, false),
                Arguments.of("fabiano", "40A9B8D6AC3F824DCEEFEDE0DEE8DAECD6B6392F62104F81DC28139D1065F3DE01261FD9AFE3B2A7", CipherAlgorithm.SSHA256, null, true),
                Arguments.of("fabiano", "0669ED522A1EB43261C31CA64C2654436FB79C1E66CA24D8C54AF5A52BB65B8838D73721E0D5FA37C505E98DC305E2D8D9D0C36578CE5CB98D63BDB55F049E029E21DD17737B64BD", CipherAlgorithm.SSHA512, null, false)
             //   Arguments.of("fabiano", "a", CipherAlgorithm.SHA256, null, false)
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
