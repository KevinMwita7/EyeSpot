package com.eyespot.imageparser.bitmap;

/**
 * The bitmap info header type. See <a
 * href="https://web.archive.org/web/20141224112131/http://netghost.narod.ru/gff/graphics/summary/micbmp.htm">Bitmap
 * Information Header</a>">
 */
class BitmapInfoHeader extends DIBHeader {
  public BitmapInfoHeader(byte[] data, int headerOffset) {
    super(data, headerOffset);
  }

  @Override
  public InfoHeaderType getType() {
    return InfoHeaderType.BITMAPINFOHEADER;
  }
}
