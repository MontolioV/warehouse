package com.myapp.storing.UT;

import com.myapp.storing.CreateItemController;
import com.myapp.storing.ItemStore;
import com.myapp.storing.TagStore;
import com.myapp.storing.TextItem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.security.Principal;

import static com.myapp.utils.TestSecurityConstants.LOGIN_VALID;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * <p>Created by MontolioV on 17.04.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class CreateItemControllerTest {
    @InjectMocks
    private CreateItemController controller;
    @Mock
    private ItemStore isMock;
    @Mock
    private TagStore tsMock;
    @Mock
    private FacesContext fcMock;
    @Mock
    private ExternalContext ecMock;
    @Mock
    private Principal principalMock;
    private String tagsString;
    private String tag1 = "tag1";
    private String tag2 = "tag2";
    private String tag3 = "tag3";

    @Before
    public void setUp() throws Exception {
        tagsString = tag1 + "\n" + tag2 + "\n" + tag3;
        when(fcMock.getExternalContext()).thenReturn(ecMock);
        when(ecMock.getUserPrincipal()).thenReturn(principalMock);
        when(principalMock.getName()).thenReturn(LOGIN_VALID);
    }

    @Test
    public void createTextItem() throws IOException {
        TextItem textItem = new TextItem();
        controller.setTextItem(textItem);
        controller.setTagsString(tagsString);

        controller.createTextItem();

        verify(isMock).saveItems(textItem);
        verify(tsMock).saveTag(tag1, textItem);
        verify(tsMock).saveTag(tag2, textItem);
        verify(tsMock).saveTag(tag3, textItem);
        assertNotNull(textItem.getCreationDate());
        assertThat(textItem.getOwner(), is(LOGIN_VALID));

        textItem = new TextItem();
        controller.setPrincipal(null);
        controller.createTextItem();
        assertNull(textItem.getOwner());
    }
}