package com.myapp.storing.UT;

import com.myapp.storing.FetchItemsController;
import com.myapp.storing.Item;
import com.myapp.storing.ItemStore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.faces.context.ExternalContext;
import java.security.Principal;
import java.util.ArrayList;

import static com.myapp.utils.TestSecurityConstants.LOGIN_VALID;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

/**
 * <p>Created by MontolioV on 18.04.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class FetchItemsControllerTest {
    @InjectMocks
    private FetchItemsController controller;
    @Mock
    private ItemStore isMock;
    @Mock
    private ExternalContext ecMock;
    @Mock
    private Principal principalMock;

    @Test
    public void fetchRecentItems() {
        ArrayList<Item> items = new ArrayList<>();
        when(isMock.getTenLastSharedItems()).thenReturn(items);

        controller.fetchRecentItems();

        assertThat(controller.getItems(), sameInstance(items));
    }

    @Test
    public void fetchById() {
        Item item = new Item();
        controller.setId(0L);
        when(ecMock.getUserPrincipal()).thenReturn(principalMock);
        when(principalMock.getName()).thenReturn(LOGIN_VALID);
        when(isMock.getItemById(0L, LOGIN_VALID)).thenReturn(item);

        controller.fetchById();
        assertThat(controller.getItem(), sameInstance(item));
    }
}