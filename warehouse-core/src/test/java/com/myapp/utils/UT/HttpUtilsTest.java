package com.myapp.utils.UT;

import com.myapp.utils.HttpUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * <p>Created by MontolioV on 04.04.18.
 */
@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor("javax.servlet.http.Cookie")
public class HttpUtilsTest {
    private HttpUtils httpUtils = new HttpUtils();
    @Mock
    private HttpServletRequest requestMock;
    @Mock
    private Cookie cookieMock;
    @Mock
    private Cookie cookieOtherMock;
    private String name = "name";

    @Test
    public void findCookie() {
        when(cookieMock.getName()).thenReturn(name);
        when(cookieOtherMock.getName()).thenReturn("other name");
        when(requestMock.getCookies()).thenReturn(new Cookie[]{cookieOtherMock, cookieMock});

        Cookie result = httpUtils.findCookie(requestMock, name);
        assertThat(result, is(cookieMock));
        result = httpUtils.findCookie(requestMock, "null");
        assertNull(result);

        when(requestMock.getCookies()).thenReturn(null);

        result = httpUtils.findCookie(requestMock, name);
        assertNull(result);
    }
}