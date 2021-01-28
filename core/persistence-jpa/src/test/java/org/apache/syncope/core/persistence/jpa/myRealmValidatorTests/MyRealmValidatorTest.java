package org.apache.syncope.core.persistence.jpa.myRealmValidatorTests;

import org.apache.syncope.common.lib.SyncopeConstants;

import org.apache.syncope.core.persistence.api.entity.Realm;
import org.apache.syncope.core.persistence.jpa.validation.entity.RealmValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;


import javax.validation.ConstraintValidatorContext;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class MyRealmValidatorTest {

    private RealmValidator realmValidator;

    @Mock
    private Realm realm = mock(Realm.class);

    @Mock
    private ConstraintValidatorContext context;

    private static Stream<Arguments> getParameters(){

        return Stream.of(
                Arguments.of(null, false, NullPointerException.class, false),
                Arguments.of("valid_root", true, null, true),
                Arguments.of("invalid_root_with_parent", true, null, false),
                Arguments.of("invalid_not_root_with_no_parent", true, null, false),
                Arguments.of("invalid_out_pattern", true, null, false)
        );
    }

    private void setup(){
        realmValidator = new RealmValidator();
    }

    private void setupMockRealm(String realmInstanceType){
        if(realmInstanceType == null){
            realm = null;
        } else {
            realm = mock(Realm.class);
            switch (realmInstanceType){
                case "valid_root":
                    when(realm.getName()).thenReturn(SyncopeConstants.ROOT_REALM);
                    when(realm.getParent()).thenReturn(null);
                    break;
                case "invalid_root_with_parent":
                    when(realm.getName()).thenReturn(SyncopeConstants.ROOT_REALM);
                    when(realm.getParent()).thenReturn(mock(Realm.class));
                    break;
                case "invalid_not_root_with_no_parent":
                    when(realm.getName()).thenReturn("even");
                    when(realm.getParent()).thenReturn(null);
                    break;
                case "invalid_out_pattern":
                    when(realm.getName()).thenReturn(SyncopeConstants.ROOT_REALM.concat(".//'$"));
                    when(realm.getParent()).thenReturn(mock(Realm.class));
                    break;
                default:
                    break;
            }
        }
    }

    private void setupMockConstraintValidatorContext(boolean constraintNotNull){
        if(constraintNotNull){
            //context --> builder --> node
            context =mock(ConstraintValidatorContext.class);
            ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
            ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext node = mock(ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class);
            when(node.addConstraintViolation()).thenReturn(context);
            when(builder.addPropertyNode("parent")).thenReturn(node);
            when(builder.addPropertyNode("name")).thenReturn(node);
            when(context.buildConstraintViolationWithTemplate(any())).thenReturn(builder);
        } else {
            context = null;
        }
    }

    private void setupMock(String realmInstanceType, boolean constraintNotNull){
        setupMockRealm(realmInstanceType);
        setupMockConstraintValidatorContext(constraintNotNull);
    }



    @ParameterizedTest(name = "isValidTest")
    @MethodSource("getParameters")
    public void isValidTest(String realmInstanceType, boolean constraintNotNull, Class<? extends Exception> expectedException, boolean expected)  {
        setup();
        setupMock(realmInstanceType, constraintNotNull);

        if(expectedException != null) {
            Assertions.assertThrows(expectedException, () -> { realmValidator.isValid(realm, context); });
        }else {
            boolean actualValue = realmValidator.isValid(realm, context);
            Assertions.assertEquals(expected, actualValue);
        }
    }

}
