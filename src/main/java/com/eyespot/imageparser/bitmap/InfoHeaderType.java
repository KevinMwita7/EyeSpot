package com.eyespot.imageparser.bitmap;

/**
 * The bitmap info header type. See <a
 * href="https://web.archive.org/web/20141224112131/http://netghost.narod.ru/gff/graphics/summary/micbmp.htm">Bitmap
 * Information Header</a>">
 */
public enum InfoHeaderType {
  BITMAPCOREHEADER(BitmapConstants.BITMAPCOREHEADER_SIZE),
  BITMAPINFOHEADER(BitmapConstants.BITMAPINFOHEADER_SIZE),
  BITMAPV2INFOHEADER(BitmapConstants.BITMAPV2INFOHEADER_SIZE),
  BITMAPV3INFOHEADER(BitmapConstants.BITMAPV3INFOHEADER_SIZE),
  BITMAPV4HEADER(BitmapConstants.BITMAPV4HEADER_SIZE),
  BITMAPV5HEADER(BitmapConstants.BITMAPV5HEADER_SIZE);

  private final int size;

  InfoHeaderType(int size) {
    this.size = size;
  }

  public int getSize() {
    return size;
  }

  public static InfoHeaderType fromSize(int size) {
    for (InfoHeaderType type : values()) {
      if (type.getSize() == size) {
        return type;
      }
    }
    return null;
  }
}
