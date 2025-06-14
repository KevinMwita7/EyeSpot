package com.eyespot.imageparser.bitmap;

import com.eyespot.imageparser.util.ImageUtils;

abstract class DIBHeader {
  protected final int headerSize;
  protected final int width;
  protected final int height;
  protected final short colourPlanes;
  protected final short bitsPerPixel;
  protected final int compression;
  protected final int imageSize; // This should be the *actual* image size
  protected final int xResolution, yResolution;
  protected final int nColours;
  protected final int importantColours;

  // Common constructor to parse fields common to many DIB headers
  protected DIBHeader(byte[] data, int headerOffset) {
    // Should be 14 for first DIB field
    this.headerSize = ImageUtils.readInt(data, headerOffset);

    // Is BITMAPCOREHEADER
    if (this.headerSize == BitmapConstants.BITMAPCOREHEADER_SIZE) {
      this.width = ImageUtils.readShort(data, headerOffset + BitmapConstants.BI_CORE_WIDTH_OFFSET);
      this.height =
          ImageUtils.readShort(data, headerOffset + BitmapConstants.BI_CORE_HEIGHT_OFFSET);
      this.colourPlanes =
          ImageUtils.readShort(data, headerOffset + BitmapConstants.BI_CORE_PLANES_OFFSET);
      this.bitsPerPixel =
          ImageUtils.readShort(data, headerOffset + BitmapConstants.BI_CORE_BITCOUNT_OFFSET);
      compression = 0;
      imageSize = 0;
      xResolution = 0;
      yResolution = 0;
      nColours = 0;
      importantColours = 0;
      return;
    }

    // Is BITMAPINFOHEADER and later
    this.width = ImageUtils.readInt(data, headerOffset + BitmapConstants.BI_WIDTH_OFFSET);
    this.height = ImageUtils.readInt(data, headerOffset + BitmapConstants.BI_HEIGHT_OFFSET);
    this.colourPlanes = ImageUtils.readShort(data, headerOffset + BitmapConstants.BI_PLANES_OFFSET);
    this.bitsPerPixel =
        ImageUtils.readShort(data, headerOffset + BitmapConstants.BI_BITCOUNT_OFFSET);
    this.compression =
        ImageUtils.readInt(data, headerOffset + BitmapConstants.BI_COMPRESSION_OFFSET);

    int rawImageSize = ImageUtils.readInt(data, headerOffset + BitmapConstants.BI_SIZEIMAGE_OFFSET);
    if (rawImageSize == 0 && this.compression == 0) { // BI_RGB (uncompressed)
      this.imageSize = calculateBitmapDataSize(this.width, this.height, this.bitsPerPixel);
    } else {
      this.imageSize = rawImageSize;
    }

    this.xResolution =
        ImageUtils.readInt(data, headerOffset + BitmapConstants.BI_X_PELS_PER_METER_OFFSET);
    this.yResolution =
        ImageUtils.readInt(data, headerOffset + BitmapConstants.BI_Y_PELS_PER_METER_OFFSET);
    this.nColours = ImageUtils.readInt(data, headerOffset + BitmapConstants.BI_CLR_USED_OFFSET);
    this.importantColours =
        ImageUtils.readInt(data, headerOffset + BitmapConstants.BI_CLR_IMPORTANT_OFFSET);
  }

  // Abstract method to get the specific InfoHeaderType
  public abstract InfoHeaderType getType();

  // Getters for common fields
  public int getHeaderSize() {
    return headerSize;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public short getColourPlanes() {
    return colourPlanes;
  }

  public short getBitsPerPixel() {
    return bitsPerPixel;
  }

  public int getCompression() {
    return compression;
  }

  public int getImageDataSize() {
    return imageSize;
  }

  public int getXResolution() {
    return xResolution;
  }

  public int getYResolution() {
    return yResolution;
  }

  public int getNColours() {
    return nColours;
  }

  public int getImportantColours() {
    return importantColours;
  }

  protected static int calculateScanlineSize(int width, int bitsPerPixel) {
    int bytesPerRow = (width * bitsPerPixel + 7) / 8;
    return (int) Math.ceil(bytesPerRow / 4.0) * 4;
  }

  protected static int calculateBitmapDataSize(int width, int height, int bitsPerPixel) {
    return calculateScanlineSize(width, bitsPerPixel) * Math.abs(height);
  }
}
