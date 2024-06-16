package com.example.filter;

import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;

public class SharpFilter {

  public static byte[] applyFilter(final byte[] imageData, final String mediaType)
      throws IOException {
    ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);
    BufferedImage originalImage = ImageIO.read(inputStream);
    if (originalImage == null) {
      throw new IOException("Failed to read image from byte array");
    }
    int width = originalImage.getWidth();
    int height = originalImage.getHeight();
        int numThreads =
        Runtime.getRuntime().availableProcessors();
    ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

    try {
      int stripHeight = height / numThreads;
      BufferedImage[] processedImages = new BufferedImage[numThreads];
      for (int i = 0; i < numThreads; i++) {
        final int startRow = i * stripHeight;
        final int endRow = (i == numThreads - 1) ? height : startRow + stripHeight;

        final int finalI = i;
        executorService.submit(() -> {
          processedImages[finalI] = applySharpeningPart(originalImage, startRow, endRow);
        });
      }
      executorService.shutdown();
      executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
      BufferedImage sharpenedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
      Graphics2D g = sharpenedImage.createGraphics();
      for (int i = 0; i < numThreads; i++) {
        if (processedImages[i] != null) {
          g.drawImage(processedImages[i], 0, i * stripHeight, null);
        }
      }
      g.dispose();
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      String formatName = mediaType.substring(mediaType.indexOf('/') + 1);
      ImageIO.write(sharpenedImage, formatName, outputStream);

      return outputStream.toByteArray();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IOException("Interrupted while processing image", e);
    }
  }

  private static BufferedImage applySharpeningPart(BufferedImage originalImage, int startRow,
                                                   int endRow) {
    BufferedImage subImage =
        originalImage.getSubimage(0, startRow, originalImage.getWidth(), endRow - startRow);

    BufferedImage grayImagePart =
        new BufferedImage(subImage.getWidth(), subImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
    ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
    op.filter(subImage, grayImagePart);
    float[] laplacianKernelData = {
        0.0f, -1.0f, 0.0f,
        -1.0f, 4.0f, -1.0f,
        0.0f, -1.0f, 0.0f
    };
    Kernel laplacianKernel = new Kernel(3, 3, laplacianKernelData);
    ConvolveOp laplacianOp = new ConvolveOp(laplacianKernel, ConvolveOp.EDGE_NO_OP, null);
    return laplacianOp.filter(grayImagePart, null);
  }
}