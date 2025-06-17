package com.eyespot.imageparser.bitmap;

import com.eyespot.imageparser.util.ImageUtils;

/**
 * Represents the {@code BITMAPV5HEADER} structure, the most advanced DIB header format in BMP
 * files.
 *
 * <p>It extends {@link BitmapV4Header} and adds support for ICC color profiles and rendering
 * intent. This header is 124 bytes in size and is used in high-fidelity imaging scenarios,
 * including color-managed workflows.
 *
 * <p>This class parses additional fields related to ICC profile embedding: rendering intent,
 * profile data offset and size, and a reserved field.
 *
 * @author Kevin Babu
 * @see BitmapConstants#BITMAPV5HEADER_SIZE
 * @see InfoHeaderType#BITMAPV5HEADER
 */
public class BitmapV5Header extends BitmapV4Header {
  private final int intent;
  private final int profileData;
  private final int profileSize;
  private final int reservedV5;

  /**
   * Constructs a {@code BitmapV5Header} by parsing the extended header fields from the given byte
   * array.
   *
   * @param data the byte array containing BMP image data
   * @param headerOffset the offset in the file where the DIB header begins
   */
  protected BitmapV5Header(byte[] data, int headerOffset) {
    super(data, headerOffset);
    int currentOffset = headerOffset + BitmapConstants.BITMAPV4HEADER_SIZE;

    this.intent = ImageUtils.readInt(data, currentOffset);
    this.profileData = ImageUtils.readInt(data, currentOffset + 4);
    this.profileSize = ImageUtils.readInt(data, currentOffset + 8);
    this.reservedV5 = ImageUtils.readInt(data, currentOffset + 12);
  }

  /** @return the type of this header: {@link InfoHeaderType#BITMAPV5HEADER} */
  @Override
  public InfoHeaderType getType() {
    return InfoHeaderType.BITMAPV5HEADER;
  }

  // Getters for V5-specific fields
  /**
   * Returns the rendering intent used for color management.
   *
   * <p>Common values include:
   *
   * <ul>
   *   <li>0 = LCS_GM_ABS_COLORIMETRIC
   *   <li>1 = LCS_GM_BUSINESS
   *   <li>2 = LCS_GM_GRAPHICS
   *   <li>3 = LCS_GM_IMAGES
   * </ul>
   *
   * @return the rendering intent
   */
  public int getIntent() {
    return intent;
  }

  /** @return the offset in bytes from the beginning of the file to the ICC profile data */
  public int getProfileData() {
    return profileData;
  }

  /** @return the size of the ICC profile data in bytes */
  public int getProfileSize() {
    return profileSize;
  }

  /** @return the reserved field (typically zero) */
  public int getReservedV5() {
    return reservedV5;
  }
}
