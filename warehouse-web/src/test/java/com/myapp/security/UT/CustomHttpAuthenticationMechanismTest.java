package com.myapp.security.UT;

import com.myapp.security.CustomHttpAuthenticationMechanism;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.security.enterprise.AuthenticationException;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import javax.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * <p>Created by MontolioV on 13.03.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class CustomHttpAuthenticationMechanismTest {
    @InjectMocks
    private CustomHttpAuthenticationMechanism authenticationMechanism;
    @Mock
    private IdentityStore isMock;
    @Mock
    private HttpMessageContext contextMock;
    @Mock
    private AuthenticationParameters authParametersMock;
    @Mock
    private Credential credentialMock;

    @Before
    public void setUp() throws Exception {
        when(contextMock.getAuthParameters()).thenReturn(authParametersMock);
        when(contextMock.notifyContainerAboutLogin(any(CredentialValidationResult.class))).thenReturn(AuthenticationStatus.SUCCESS);
        when(contextMock.doNothing()).thenReturn(AuthenticationStatus.NOT_DONE);
    }

    @Test
    public void validateRequestWithCredentials() throws AuthenticationException {
        when(authParametersMock.getCredential()).thenReturn(credentialMock);
        AuthenticationStatus status = authenticationMechanism.validateRequest(mock(HttpServletRequest.class), mock(HttpServletResponse.class), contextMock);

        assertThat(status, is(AuthenticationStatus.SUCCESS));
    }

    @Test
    public void validateRequestWithoutCredentials() throws AuthenticationException {
        when(authParametersMock.getCredential()).thenReturn(null);
        AuthenticationStatus status = authenticationMechanism.validateRequest(mock(HttpServletRequest.class), mock(HttpServletResponse.class), contextMock);

        assertThat(status, is(AuthenticationStatus.NOT_DONE));
    }
}