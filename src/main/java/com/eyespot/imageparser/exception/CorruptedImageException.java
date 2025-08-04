package com.eyespot.imageparser.exception;

/** Exception thrown to indicate that an image file is corrupted or unreadable during parsing. */
public class CorruptedImageException extends Exception {
  /**
   * Constructs a new {@code CorruptedImageException} with the specified detail message.
   *
   * @param message the detail message
   */
  public CorruptedImageException(String message) {
    super(message);
  }
}
