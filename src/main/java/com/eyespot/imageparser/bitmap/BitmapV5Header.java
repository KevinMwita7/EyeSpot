package com.eyespot.imageparser.bitmap;

import com.eyespot.imageparser.util.ImageUtils;

/**
 * The bitmap info header type. See <a
 * href="https://web.archive.org/web/20141224112131/http://netghost.narod.ru/gff/graphics/summary/micbmp.htm">Bitmap
 * Information Header</a>">
 */
public class BitmapV5Header extends BitmapV4Header {
  private final int intent;
  private final int profileData;
  private final int profileSize;
  private final int reservedV5;

  protected BitmapV5Header(byte[] data, int headerOffset) {
    super(data, headerOffset); // Parse V4-specific fields

    // Parse V5-specific fields
    int currentOffset = headerOffset + BitmapConstants.BITMAPV4HEADER_SIZE;

    this.intent = ImageUtils.readInt(data, currentOffset);
    this.profileData = ImageUtils.readInt(data, currentOffset + 4);
    this.profileSize = ImageUtils.readInt(data, currentOffset + 8);
    this.reservedV5 = ImageUtils.readInt(data, currentOffset + 12);
  }

  @Override
  public InfoHeaderType getType() {
    return InfoHeaderType.BITMAPV5HEADER;
  }

  // Getters for V5-specific fields
  public int getIntent() {
    return intent;
  }

  public int getProfileData() {
    return profileData;
  }

  public int getProfileSize() {
    return profileSize;
  }

  public int getReservedV5() {
    return reservedV5;
  }
}
