package org.glassfish.soteria.test.client;

import static org.glassfish.soteria.test.server.OidcProvider.CLIENT_ID_VALUE;
import static org.glassfish.soteria.test.server.OidcProvider.CLIENT_SECRET_VALUE;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.AuthenticationException;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.authentication.mechanism.http.FormAuthenticationMechanismDefinition;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.security.enterprise.authentication.mechanism.http.LoginToContinue;
import jakarta.security.enterprise.authentication.mechanism.http.OpenIdAuthenticationMechanismDefinition;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.glassfish.soteria.test.BuiltInAuthMechanism;

@ApplicationScoped
public class DelegatingAuthMechanism implements HttpAuthenticationMechanism {

    @Inject
    @BuiltInAuthMechanism(form
            = @FormAuthenticationMechanismDefinition(loginToContinue = @LoginToContinue(loginPage = "/login.xhtml"))
    )
    HttpAuthenticationMechanism formAuthentication;

    @Inject
    @BuiltInAuthMechanism(
            openId = @OpenIdAuthenticationMechanismDefinition(
                    providerURI = "http://localhost:8080/openid-server/webresources/oidc-provider-demo",
                    clientId = CLIENT_ID_VALUE,
                    clientSecret = CLIENT_SECRET_VALUE,
                    redirectURI = "${baseURL}/Callback")
    )
    HttpAuthenticationMechanism openIdAuthentication;

    private HttpAuthenticationMechanism getMechanism(HttpServletRequest request) {
        if ("authType".equals(request.getParameter("form"))) {
            return formAuthentication;
        } else {
            return openIdAuthentication;
        }
    }

    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response, HttpMessageContext httpMessageContext) throws AuthenticationException {
        return getMechanism(request).validateRequest(request, response, httpMessageContext);
    }

    @Override
    public AuthenticationStatus secureResponse(HttpServletRequest request, HttpServletResponse response, HttpMessageContext httpMessageContext) throws AuthenticationException {
        return getMechanism(request).secureResponse(request, response, httpMessageContext);
    }

    @Override
    public void cleanSubject(HttpServletRequest request, HttpServletResponse response, HttpMessageContext httpMessageContext) {
        getMechanism(request).cleanSubject(request, response, httpMessageContext);
    }

}
