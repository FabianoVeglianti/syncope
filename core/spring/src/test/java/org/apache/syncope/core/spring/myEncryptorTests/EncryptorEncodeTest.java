package org.apache.syncope.core.spring.myEncryptorTests;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


@RunWith(value= Parameterized.class)
public class EncryptorEncodeTest {

    private long i;

    @Parameterized.Parameters
    public static Collection<?> getTestParameters(){

        return Arrays.asList(new Object[][]{
                {0}
        });
    }

    public EncryptorEncodeTest(long i){
        this.i = i;
    }

    @Test
    public void dummyTest(){
        assertEquals(0,i);
    }

}
