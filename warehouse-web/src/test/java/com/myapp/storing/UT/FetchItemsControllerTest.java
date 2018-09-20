package com.myapp.storing.UT;

import com.myapp.storing.*;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.faces.context.ExternalContext;
import java.security.Principal;
import java.util.ArrayList;

import static com.myapp.utils.TestSecurityConstants.LOGIN_INVALID;
import static com.myapp.utils.TestSecurityConstants.LOGIN_VALID;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 * <p>Created by MontolioV on 18.04.18.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Jsoup.class, Whitelist.class})
@PowerMockIgnore("javax.security.auth.Subject")
public class FetchItemsControllerTest {
    @InjectMocks
    private FetchItemsController controller;
    @Mock
    private ItemStore isMock;
    @Mock
    private ExternalContext ecMock;
    @Mock
    private Principal principalMock;

    @Before
    public void setUp() throws Exception {
        when(ecMock.getUserPrincipal()).thenReturn(principalMock);
        when(principalMock.getName()).thenReturn(LOGIN_VALID);
    }

    @Test
    public void fetchRecentItems() {
        ArrayList<Item> items = new ArrayList<>();
        when(isMock.getTenLastSharedItems()).thenReturn(items);

        controller.fetchRecentItems();

        assertThat(controller.getRecentItems(), sameInstance(items));
    }

    @Test
    public void fetchById() {
        Item item = new Item();
        controller.setId(0L);
        when(isMock.getItemById(0L)).thenReturn(item);

        controller.fetchById();
        assertThat(controller.getItem(), sameInstance(item));
    }

    @Test
    public void castItem() {
        controller.setTextItem(null);
        controller.setFileItem(null);
        Item item1 = new TextItem();
        item1.setdType(TextItem.class.getSimpleName());
        controller.setItem(item1);
        controller.castItem();
        assertThat(controller.getTextItem(), sameInstance(item1));
        assertThat(controller.getFileItem(), nullValue());

        controller.setTextItem(null);
        controller.setFileItem(null);
        Item item2 = new FileItem();
        item2.setdType(FileItem.class.getSimpleName());
        controller.setItem(item2);
        controller.castItem();
        assertThat(controller.getTextItem(), nullValue());
        assertThat(controller.getFileItem(), sameInstance(item2));

        controller.setTextItem(null);
        controller.setFileItem(null);
        controller.setItem(null);
        controller.castItem();
        assertThat(controller.getTextItem(), nullValue());
        assertThat(controller.getFileItem(), nullValue());
    }

    @Test
    public void itemIsUsersOwn() {
        boolean isUsersOwn = controller.itemIsUsersOwn();
        assertFalse(isUsersOwn);

        Item itemMock = mock(Item.class);
        controller.setItem(itemMock);

        when(itemMock.getOwner()).thenReturn(LOGIN_VALID);
        isUsersOwn = controller.itemIsUsersOwn();
        assertTrue(isUsersOwn);

        when(itemMock.getOwner()).thenReturn(null);
        isUsersOwn = controller.itemIsUsersOwn();
        assertFalse(isUsersOwn);

        when(itemMock.getOwner()).thenReturn(LOGIN_INVALID);
        isUsersOwn = controller.itemIsUsersOwn();
        assertFalse(isUsersOwn);

        when(ecMock.getUserPrincipal()).thenReturn(null);
        isUsersOwn = controller.itemIsUsersOwn();
        assertFalse(isUsersOwn);
    }

    @Test
    public void sanitisedText() {
        mockStatic(Jsoup.class);
        mockStatic(Whitelist.class);
        TextItem tiMock = mock(TextItem.class);
        Whitelist whitelist = mock(Whitelist.class);

        when(tiMock.getText()).thenReturn("rawText");
        PowerMockito.when(Whitelist.relaxed()).thenReturn(whitelist);
        PowerMockito.when(Jsoup.clean("rawText", whitelist)).thenReturn("sanitisedText");

        controller.setTextItem(tiMock);
        String s = controller.sanitisedText();
        assertThat(s, is("sanitisedText"));

        controller.setTextItem(null);
        s = controller.sanitisedText();
        assertThat(s, nullValue());
    }

    @Test
    public void getLinkToItem() {
        controller.setWebAppAddress("waa/");
        controller.setId(10L);
        String linkToItem = controller.getLinkToItem();
        assertThat(linkToItem,is("waa/public/show-item.jsf?id=10"));
    }
}