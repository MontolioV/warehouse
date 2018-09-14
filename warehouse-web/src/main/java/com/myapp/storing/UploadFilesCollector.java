package com.myapp.storing;

import com.myapp.utils.PrimeFacesBean;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

/**
 * <p>Created by MontolioV on 29.08.18.
 */
@Named
@ViewScoped
public class UploadFilesCollector implements Serializable {
    private static final long serialVersionUID = 2816000489613843682L;
    @Inject
    private FacesContext facesContext;
    @Inject
    private PrimeFacesBean primeFacesBean;
    @EJB
    private FileStore fileStore;
    private Queue<FileItem> temporalFileItems = new LinkedList<>();

    public void fileUpload(FileUploadEvent event) {
        UploadedFile uploadedFile = event.getFile();
        if (uploadedFile == null) {
            return;
        }
        if (uploadedFile.getSize() > FileItem.MAX_SIZE_BYTE) {
            facesContext.addMessage("fileInput", new FacesMessage("File is too large!"));
            return;
        }

        FileItem fileItem = new FileItem();
        try {
            String hash = fileStore.persistFile(uploadedFile::getInputstream, uploadedFile.getContentType());
            fileItem.setHash(hash);
            fileItem.setContentType(uploadedFile.getContentType());
            fileItem.setNativeName(uploadedFile.getFileName());
            fileItem.setSize(uploadedFile.getSize());

            temporalFileItems.add(fileItem);
            primeFacesBean.getInstance().executeScript("createFileItems();");
        } catch (IOException e) {
            e.printStackTrace();
            facesContext.addMessage("fileInput", new FacesMessage("File download fail. Try again."));
        }
    }

    public void reset() {
        temporalFileItems = new LinkedList<>();
    }

    public Queue<FileItem> getTemporalFileItems() {
        return temporalFileItems;
    }
}
