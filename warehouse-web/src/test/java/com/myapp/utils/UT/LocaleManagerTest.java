package com.myapp.utils.UT;

import com.myapp.utils.LocaleManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.faces.context.ExternalContext;
import java.util.Locale;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * <p>Created by MontolioV on 25.09.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class LocaleManagerTest {
    @InjectMocks
    private LocaleManager localeManager;
    @Mock
    private ExternalContext ecMock;
    private Locale en = new Locale("en");
    private Locale ru = new Locale("ru");
    private Locale uk = new Locale("uk");

    @Before
    public void setUp() throws Exception {
        when(ecMock.getRequestLocale()).thenReturn(ru);
    }

    @Test
    public void init() {
        localeManager.init();
        Map<String, Locale> availableLocales = localeManager.getAvailableLocales();
        assertThat(availableLocales.size(), is(3));
        assertThat(availableLocales.get("en"), is(en));
        assertThat(availableLocales.get("ru"), is(ru));
        assertThat(availableLocales.get("uk"), is(uk));        
        assertThat(localeManager.getCurrentLocale(), is(ru));        
    }

    @Test
    public void initNoPreferredLanguage() {
        when(ecMock.getRequestLocale()).thenReturn(null);
        localeManager.init();
        assertThat(localeManager.getCurrentLocale(), is(en));        
    }

    @Test
    public void changeLocale() {
        localeManager.init();
        assertThat(localeManager.getCurrentLocale(), is(ru));

        localeManager.changeLocale("uk");
        assertThat(localeManager.getCurrentLocale(), is(uk));

        localeManager.changeLocale("ru");
        assertThat(localeManager.getCurrentLocale(), is(ru));

        localeManager.changeLocale("en");
        assertThat(localeManager.getCurrentLocale(), is(en));

        localeManager.changeLocale("bg");
        assertThat(localeManager.getCurrentLocale(), is(en));

        localeManager.changeLocale("");
        assertThat(localeManager.getCurrentLocale(), is(en));

        localeManager.changeLocale(null);
        assertThat(localeManager.getCurrentLocale(), is(en));
    }
}