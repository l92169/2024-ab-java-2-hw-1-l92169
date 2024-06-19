package com.example.filter;

import com.example.dto.TagsDto;
import com.example.dto.TagsResponseDto;
import com.example.dto.UploadResponseDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Objects;
import javax.imageio.ImageIO;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

public class IntegrationFilter {
  private static final String UPLOAD_URL = "https://api.imagga.com/v2/uploads";
  private static final String TAGS_URL = "https://api.imagga.com/v2/tags";
  private static final String API_KEY =
      "YWNjX2QxNmFlOTFiNzhjNGRlNDo4OTAxMjFjZTA2NjQ5Y2FjMmVkYjNjZGEwNjUwZGVmNg==";

  @CircuitBreaker(name = "MyCircuitBreaker")
  @Retry(name = "MyRetry")
  @RateLimiter(name = "MyRateLimiter")
  public static byte[] applyFilter(final byte[] imageData, final String mediaType)
      throws Exception {
    Resource fileResource = createImageResource(imageData);
    WebClient client = WebClient.create();
    String uploadId = uploadImage(client, fileResource);
    List<TagsDto> tags = fetchTags(client, uploadId);
    BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
    annotateImageWithTags(tags, image);
    return convertImageToByteArray(image, mediaType);
  }

  private static Resource createImageResource(byte[] imageData) {
    return new ByteArrayResource(imageData) {
      @Override
      public String getFilename() {
        return "image.jpg";
      }
    };
  }

  private static String uploadImage(WebClient client, Resource fileResource) {
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("image", fileResource);
    UploadResponseDto response = client.post()
        .uri(UPLOAD_URL)
        .header("Authorization", "Basic " + API_KEY)
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .bodyValue(body)
        .retrieve()
        .bodyToMono(UploadResponseDto.class)
        .block();
    return Objects.requireNonNull(response).getResult().getUploadId();
  }

  private static List<TagsDto> fetchTags(WebClient client, String uploadId) {
    TagsResponseDto response = client.get()
        .uri(TAGS_URL + "?limit=3&image_upload_id=" + uploadId)
        .header("Authorization", "Basic " + API_KEY)
        .retrieve()
        .bodyToMono(TagsResponseDto.class)
        .block();
    return Objects.requireNonNull(response).getResult().getTags();
  }

  private static byte[] convertImageToByteArray(BufferedImage image, String mediaType)
      throws Exception {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    String imageFormat = mediaType.contains("png") ? "png" : "jpeg";
    ImageIO.write(image, imageFormat, output);
    return output.toByteArray();
  }

  private static void annotateImageWithTags(List<TagsDto> tags, BufferedImage image) {
    int initialFontSize = 35;
    Font baseFont = new Font("Arial", Font.BOLD, initialFontSize);
    Graphics2D g2d = image.createGraphics();
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    int xPosition = 20;
    int yPosition = initialFontSize + 20;
    for (TagsDto tagDto : tags) {
      String tagText = tagDto.getTag().getEn();
      Font adjustedFont = adjustFontToFit(image, g2d, baseFont, tagText);
      g2d.setFont(adjustedFont);
      g2d.setColor(Color.GRAY);
      g2d.drawString(tagText, xPosition + 2, yPosition + 2);
      g2d.setColor(Color.BLACK);
      g2d.drawString(tagText, xPosition, yPosition);
      yPosition += adjustedFont.getSize() + 15;
    }
    g2d.dispose();
  }

  private static Font adjustFontToFit(BufferedImage image, Graphics2D g2d, Font baseFont,
                                      String text) {
    FontMetrics metrics = g2d.getFontMetrics(baseFont);
    int textWidth = metrics.stringWidth(text);
    if (textWidth <= image.getWidth() - 35) {
      return baseFont;
    }
    float newFontSize = baseFont.getSize2D() * (image.getWidth() - 35) / textWidth;
    return baseFont.deriveFont(newFontSize);
  }
}
