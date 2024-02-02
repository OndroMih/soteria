/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/CDI/Qualifier.java to edit this template
 */
package org.glassfish.soteria.test;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.enterprise.util.Nonbinding;
import jakarta.inject.Qualifier;
import jakarta.security.enterprise.authentication.mechanism.http.BasicAuthenticationMechanismDefinition;
import jakarta.security.enterprise.authentication.mechanism.http.CustomFormAuthenticationMechanismDefinition;
import jakarta.security.enterprise.authentication.mechanism.http.FormAuthenticationMechanismDefinition;
import jakarta.security.enterprise.authentication.mechanism.http.OpenIdAuthenticationMechanismDefinition;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 *
 * @author Ondro Mihalyi
 */
@Qualifier
@Retention(RUNTIME)
@Target({METHOD, FIELD, PARAMETER, TYPE})
public @interface BuiltInAuthMechanism {

    @Nonbinding
    BasicAuthenticationMechanismDefinition[] basic() default {};

    @Nonbinding
    FormAuthenticationMechanismDefinition[] form() default {};

    @Nonbinding
    CustomFormAuthenticationMechanismDefinition[] customForm() default {};

    @Nonbinding
    OpenIdAuthenticationMechanismDefinition[] openId() default {};
}
