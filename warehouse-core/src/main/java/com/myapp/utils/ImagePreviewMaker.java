package com.myapp.utils;

import javax.enterprise.context.ApplicationScoped;
import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static java.awt.image.AffineTransformOp.TYPE_BILINEAR;

/**
 * <p>Created by MontolioV on 15.05.18.
 */
@ApplicationScoped
public class ImagePreviewMaker {
    public static final String PREVIEW_IMAGE_FORMAT = "png";

    public void makePreview(InputStream inputStream, File outputFile) throws IOException {
        BufferedImage srcImage = ImageIO.read(inputStream);
        double scaleFactor = calculateScaleFactor(srcImage.getWidth(), srcImage.getHeight());
        int newWidth = (int) (srcImage.getWidth() * scaleFactor);
        int newHeight = (int) (srcImage.getHeight() * scaleFactor);
        BufferedImage newImage = new BufferedImage(newWidth, newHeight, srcImage.getType());
        AffineTransform affineTransform = AffineTransform.getScaleInstance(scaleFactor, scaleFactor);
        AffineTransformOp scaler = new AffineTransformOp(affineTransform, TYPE_BILINEAR);
        scaler.filter(srcImage, newImage);
        ImageIO.write(newImage, PREVIEW_IMAGE_FORMAT, outputFile);
    }

    private double calculateScaleFactor(double width, double heigth) {
        if (width <= 200 && heigth <= 200) {
            return 1;
        }
        return 200 / (width > heigth ? width : heigth);
    }
}