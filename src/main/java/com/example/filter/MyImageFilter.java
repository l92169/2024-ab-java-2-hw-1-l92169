package com.example.filter;

@FunctionalInterface
public interface MyImageFilter {
  byte[] applyFilter(final byte[] imageData, final String mediaType) throws Exception;
}
