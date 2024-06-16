package com.example.filter;

import java.io.IOException;

@FunctionalInterface
public interface MyImageFilter {
  byte[] applyFilter(final byte[] imageData, final String mediaType) throws IOException;
}
