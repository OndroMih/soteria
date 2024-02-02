/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.glassfish.soteria.test;

import static org.glassfish.soteria.cdi.CdiUtils.getAnnotation;
import static org.glassfish.soteria.cdi.CdiUtils.getBeanReference;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.UnsatisfiedResolutionException;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.interceptor.Interceptor;
import jakarta.security.enterprise.authentication.mechanism.http.BasicAuthenticationMechanismDefinition;
import jakarta.security.enterprise.authentication.mechanism.http.CustomFormAuthenticationMechanismDefinition;
import jakarta.security.enterprise.authentication.mechanism.http.FormAuthenticationMechanismDefinition;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import jakarta.security.enterprise.authentication.mechanism.http.OpenIdAuthenticationMechanismDefinition;
import org.glassfish.soteria.cdi.BasicAuthenticationMechanismDefinitionAnnotationLiteral;
import org.glassfish.soteria.cdi.LoginToContinueAnnotationLiteral;
import org.glassfish.soteria.mechanisms.BasicAuthenticationMechanism;
import org.glassfish.soteria.mechanisms.CustomFormAuthenticationMechanism;
import org.glassfish.soteria.mechanisms.FormAuthenticationMechanism;
import org.glassfish.soteria.mechanisms.OpenIdAuthenticationMechanism;

/**
 *
 * @author Ondro Mihalyi
 */
@ApplicationScoped
@Priority(Interceptor.Priority.PLATFORM_BEFORE)
@Alternative
public class BuiltInAuthMechanismProducer {

    private OpenIdAuthenticationMechanismDefinition openIdAuthMechanismDefinition = null;

    @Produces
    @Dependent
    @BuiltInAuthMechanism
    public HttpAuthenticationMechanism createBuiltInAuthMechanism(InjectionPoint ip, BeanManager beanManager) {
        BuiltInAuthMechanism builtInAuthMechanism = getAnnotation(beanManager, ip.getAnnotated(), BuiltInAuthMechanism.class)
                .orElseThrow(() -> new UnsatisfiedResolutionException("No definition of an auth mechanism found on the injection point"));
        if (builtInAuthMechanism.basic().length > 0) {
            BasicAuthenticationMechanismDefinition definition = builtInAuthMechanism.basic()[0];
            return new BasicAuthenticationMechanism(
                    BasicAuthenticationMechanismDefinitionAnnotationLiteral.eval(
                            definition));
        }
        if (builtInAuthMechanism.form().length > 0) {
            FormAuthenticationMechanismDefinition definition = builtInAuthMechanism.form()[0];
            FormAuthenticationMechanism authMethod = getBeanReference(FormAuthenticationMechanism.class);
            authMethod.setLoginToContinue(
                    LoginToContinueAnnotationLiteral.eval(definition.loginToContinue()));

            return authMethod;
        }
        if (builtInAuthMechanism.customForm().length > 0) {
            CustomFormAuthenticationMechanismDefinition definition = builtInAuthMechanism.customForm()[0];
            CustomFormAuthenticationMechanism authMethod = getBeanReference(CustomFormAuthenticationMechanism.class);
            authMethod.setLoginToContinue(
                    LoginToContinueAnnotationLiteral.eval(definition.loginToContinue()));

            return authMethod;
        }
        if (builtInAuthMechanism.openId().length > 0) {
            openIdAuthMechanismDefinition = builtInAuthMechanism.openId()[0];
            return getBeanReference(OpenIdAuthenticationMechanism.class);
        }
        throw new UnsatisfiedResolutionException("No definition of an auth mechanism found on the injection point");
    }

    @Produces
    @RequestScoped
    @Priority(Interceptor.Priority.PLATFORM_BEFORE)
    public OpenIdAuthenticationMechanismDefinition createOpenIdAuthMechDefinition() {
        if (openIdAuthMechanismDefinition != null) {
            return openIdAuthMechanismDefinition;
        } else {
            throw new UnsatisfiedResolutionException("No OpenId definition provided in the @BuiltInAuthMechanism annotation on the injection point");
        }
    }

}
