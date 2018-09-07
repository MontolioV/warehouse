package com.myapp.storing.UT;

import com.myapp.storing.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.primefaces.component.organigram.OrganigramHelper;
import javax.faces.context.ExternalContext;

import static com.myapp.utils.TestSecurityConstants.LOGIN_VALID;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.*;

/**
 * <p>Created by MontolioV on 04.09.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class ItemSearchTest {
    @InjectMocks
    private ItemSearch itemSearch;
    @Mock
    private ExternalContext externalContext;
    @Mock
    private QueryPredicateFactory predicateFactory;
    @Mock
    private ItemStore itemStore;
    @Mock
    private TagStore tagStore;

//    @Test
//    public void initFilterParamsNoParams() {
//        itemSearch.init();
//        assertThat(itemSearch.getItemTypes(), containsInAnyOrder(TextItem.class.getSimpleName(), FileItem.class.getSimpleName()));
//        OrganigramHelper.findTreeNode();
//
//        assertTrue(itemSearch.getItemOwners().contains("'" + LOGIN_VALID + "'"));
//    }
//
//    @Test
//    public void initFilterParamsWithParams() {
//        itemSearch.setItemNameParam("name");
//        itemSearch.init();
//        assertThat(itemSearch.getItemNames().size(), is(1));
//        assertThat(itemSearch.getItemOwners(), nullValue());
//        assertThat(itemSearch.getTags(), nullValue());
//
//        itemSearch.setItemOwnerParam("owner");
//        itemSearch.setTagParam("tag");
//        itemSearch.initFilterParams();
//        assertThat(itemSearch.getItemNames().size(), is(1));
//        assertThat(itemSearch.getItemOwners().size(), is(1));
//        assertThat(itemSearch.getTags().size(), is(1));
//        assertThat(itemSearch.getItemNames().get(0), is("'name'"));
//        assertThat(itemSearch.getItemOwners().get(0), is("'owner'"));
//        assertThat(itemSearch.getTags().get(0), is("'tag'"));
//    }
}