package com.eyespot.imageparser.bitmap;

import static com.eyespot.imageparser.util.ImageUtils.readInt;
import static com.eyespot.imageparser.util.ImageUtils.readShort;

/**
 * Abstract base class representing a DIB (Device Independent Bitmap) header.
 *
 * <p>This class provides common fields and parsing logic for all DIB header variants used in BMP
 * files. It handles both legacy and extended header types and is capable of interpreting key bitmap
 * metadata such as dimensions, color depth, compression method, and resolution.
 *
 * <p>Subclasses should provide the specific header format implementations.
 *
 * @author Kevin Babu
 */
public abstract class DIBHeader {
  protected final int headerSize;
  protected final int width;
  protected final int height;
  protected final short colourPlanes;
  protected final int bitsPerPixel;
  protected final int compression;
  protected final int imageSize; // This should be the *actual* image size
  protected final int xResolution;
  protected final int yResolution;
  protected final int nColours;
  protected final int importantColours;

  /**
   * Parses the common fields from a byte array representing a BMP file's DIB header.
   *
   * @param data the byte array containing the BMP file data
   * @param headerOffset the offset at which the DIB header begins
   * @throws IllegalArgumentException if the data is too short for the declared DIB header
   */
  protected DIBHeader(byte[] data, int headerOffset) {
    this.headerSize = readInt(data, headerOffset);
    if (data.length < BitmapConstants.FILE_HEADER_SIZE + headerSize) {
      throw new IllegalArgumentException(
          "Byte array too short for declared DIB header size: " + headerSize);
    }

    // Parse BITMAPCOREHEADER format
    if (this.headerSize == BitmapConstants.BITMAPCOREHEADER_SIZE) {
      this.width = readShort(data, headerOffset + BitmapConstants.BI_CORE_WIDTH_OFFSET);
      this.height = readShort(data, headerOffset + BitmapConstants.BI_CORE_HEIGHT_OFFSET);
      this.colourPlanes = readShort(data, headerOffset + BitmapConstants.BI_CORE_PLANES_OFFSET);
      this.bitsPerPixel = readShort(data, headerOffset + BitmapConstants.BI_CORE_BITCOUNT_OFFSET);
      this.compression = 0;
      this.imageSize = 0;
      this.xResolution = 0;
      this.yResolution = 0;
      this.nColours = 0;
      this.importantColours = 0;
      return;
    }

    // Parse BITMAPINFOHEADER and newer formats
    this.width = Math.abs(readInt(data, headerOffset + BitmapConstants.BI_WIDTH_OFFSET));
    this.height = readInt(data, headerOffset + BitmapConstants.BI_HEIGHT_OFFSET);
    this.colourPlanes = readShort(data, headerOffset + BitmapConstants.BI_PLANES_OFFSET);
    this.bitsPerPixel =
        Math.abs(readShort(data, headerOffset + BitmapConstants.BI_BITCOUNT_OFFSET));
    this.compression = readInt(data, headerOffset + BitmapConstants.BI_COMPRESSION_OFFSET);

    int rawImageSize = readInt(data, headerOffset + BitmapConstants.BI_SIZEIMAGE_OFFSET);
    if (rawImageSize == 0 && this.compression == 0) { // BI_RGB (uncompressed)
      this.imageSize = calculateBitmapDataSize(this.width, this.height, this.bitsPerPixel);
    } else {
      this.imageSize = rawImageSize;
    }

    this.xResolution = readInt(data, headerOffset + BitmapConstants.BI_X_PELS_PER_METER_OFFSET);
    this.yResolution = readInt(data, headerOffset + BitmapConstants.BI_Y_PELS_PER_METER_OFFSET);
    this.nColours = Math.abs(readInt(data, headerOffset + BitmapConstants.BI_CLR_USED_OFFSET));
    this.importantColours = readInt(data, headerOffset + BitmapConstants.BI_CLR_IMPORTANT_OFFSET);
  }

  /**
   * Factory method to create an appropriate {@code DIBHeader} subclass based on the header size
   * field.
   *
   * @param data the byte array containing the BMP file
   * @return the correct {@code DIBHeader} instance
   * @throws IllegalArgumentException if the header size is unknown or unsupported
   */
  public static DIBHeader createDIBHeader(byte[] data) {
    int dibHeaderFileOffset = BitmapConstants.FILE_HEADER_SIZE;
    int headerSize = readInt(data, dibHeaderFileOffset);

    switch (headerSize) {
      case BitmapConstants.BITMAPCOREHEADER_SIZE: // 12 bytes
        return new BitmapCoreHeader(data, dibHeaderFileOffset);
      case BitmapConstants.BITMAPINFOHEADER_SIZE: // 40 bytes
        return new BitmapInfoHeader(data, dibHeaderFileOffset);
      case BitmapConstants.BITMAPV2INFOHEADER_SIZE: // 52 bytes
        return new BitmapV2InfoHeader(data, dibHeaderFileOffset);
      case BitmapConstants.BITMAPV3INFOHEADER_SIZE: // 56 bytes
        return new BitmapV3InfoHeader(data, dibHeaderFileOffset);
      case BitmapConstants.BITMAPV4HEADER_SIZE: // 108 bytes
        return new BitmapV4Header(data, dibHeaderFileOffset);
      case BitmapConstants.BITMAPV5HEADER_SIZE: // 124 bytes
        return new BitmapV5Header(data, dibHeaderFileOffset);
      default:
        throw new IllegalArgumentException("Unknown or unsupported DIB header size: " + headerSize);
    }
  }

  /**
   * Returns the specific DIB header type represented by this instance.
   *
   * @return the header type enum value
   */
  public abstract InfoHeaderType getType();

  // Getters for common fields
  /** @return the size of the DIB header in bytes */
  public int getHeaderSize() {
    return headerSize;
  }

  /** @return the bitmap width in pixels */
  public int getWidth() {
    return width;
  }

  /** @return the bitmap height in pixels */
  public int getHeight() {
    return height;
  }

  /** @return number of color planes (always 1 in modern BMPs) */
  public short getColourPlanes() {
    return colourPlanes;
  }

  /** @return the number of bits per pixel */
  public int getBitsPerPixel() {
    return bitsPerPixel;
  }

  /** @return the compression method used */
  public int getCompression() {
    return compression;
  }

  /** @return the size of the actual bitmap data in bytes */
  public int getImageDataSize() {
    return imageSize;
  }

  /** @return the horizontal resolution in pixels per meter */
  public int getXResolution() {
    return xResolution;
  }

  /** @return the vertical resolution in pixels per meter */
  public int getYResolution() {
    return yResolution;
  }

  /** @return the number of colors used in the bitmap */
  public int getNColours() {
    return nColours;
  }

  /** @return the number of important colors (generally ignored) */
  public int getImportantColours() {
    return importantColours;
  }

  /**
   * Calculates the size of one scanline (row of pixels), aligned to a 4-byte boundary.
   *
   * @param width the width of the image in pixels
   * @param bitsPerPixel the number of bits per pixel
   * @return the scanline size in bytes
   */
  protected static int calculateScanlineSize(int width, int bitsPerPixel) {
    int bytesPerRow = (width * bitsPerPixel + 7) / 8;
    return (int) Math.ceil(bytesPerRow / 4.0) * 4;
  }

  /**
   * Calculates the total bitmap data size based on dimensions and color depth.
   *
   * @param width the image width
   * @param height the image height
   * @param bitsPerPixel the color depth
   * @return the size of the bitmap data in bytes
   */
  protected static int calculateBitmapDataSize(int width, int height, int bitsPerPixel) {
    return calculateScanlineSize(width, bitsPerPixel) * Math.abs(height);
  }
}
