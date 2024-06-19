package com.example.filter;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class RotateFilter {
  public static byte[] applyFilter(final byte[] imageData, final String mediaType)
      throws IOException {
    ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
    BufferedImage originalImage = ImageIO.read(bais);
    int width = originalImage.getWidth();
    int height = originalImage.getHeight();
    BufferedImage rotatedImage = new BufferedImage(height, width, originalImage.getType());
    Graphics2D g2d = rotatedImage.createGraphics();
    g2d.translate((height - width) / 2, (height - width) / 2);
    g2d.rotate(Math.PI / 2, height / 2, width / 2);
    g2d.drawRenderedImage(originalImage, null);
    g2d.dispose();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageIO.write(rotatedImage, mediaType.substring(mediaType.indexOf('/') + 1), baos);
    return baos.toByteArray();
  }
}
