package com.myapp.utils.UT;

import com.myapp.utils.HttpUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * <p>Created by MontolioV on 04.04.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class HttpUtilsTest {
    @Mock
    private HttpServletRequest requestMock;

    @Test
    public void findCookie() {
        Cookie cookie = new Cookie("exists", "1");
        when(requestMock.getCookies()).thenReturn(new Cookie[]{cookie});

        Cookie result = HttpUtils.findCookie(requestMock, "exists");
        assertThat(result, is(cookie));
        result = HttpUtils.findCookie(requestMock, "null");
        assertNull(result);
    }

    @Test
    public void findCookieNPE() {
        when(requestMock.getCookies()).thenReturn(null);

        Cookie result = HttpUtils.findCookie(requestMock, "");
        assertNull(result);
    }
}