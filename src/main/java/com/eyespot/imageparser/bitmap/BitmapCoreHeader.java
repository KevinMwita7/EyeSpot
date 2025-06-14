package com.eyespot.imageparser.bitmap;

/**
 * Represents the BITMAPCOREHEADER (12 bytes) DIB header. Note: This header uses SHORTs for
 * width/height and has fewer fields. The base DIBHeader constructor needs adjustment or a separate
 * constructor to correctly parse CoreHeader specific field sizes/offsets. This implementation is
 * simplified.
 */
class BitmapCoreHeader extends DIBHeader {
  protected BitmapCoreHeader(byte[] data, int dibHeaderFileOffset) {
    super(data, dibHeaderFileOffset);
  }

  @Override
  public InfoHeaderType getType() {
    return InfoHeaderType.BITMAPCOREHEADER;
  }
}
