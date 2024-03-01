package com.example.exceptions;

public class ImageNotFoundException extends RuntimeException {
    public ImageNotFoundException(String link) {
        super(link);
    }
}
