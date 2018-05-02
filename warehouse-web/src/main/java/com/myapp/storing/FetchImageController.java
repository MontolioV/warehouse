package com.myapp.storing;

import org.omnifaces.cdi.GraphicImageBean;

import javax.ejb.EJB;

@GraphicImageBean
public class FetchImageController {
    @EJB
    private ItemStore itemStore;

    public byte[] getImageFromFileItem(long id, String userName) {
        return ((FileItem) itemStore.getItemById(id, userName)).getBinaryData();
    }
}
