package com.eyespot.imageparser.bitmap;

/**
 * Defines constants used for parsing and interpreting various types of BMP (Bitmap) file headers,
 * including the BITMAPFILEHEADER, BITMAPCOREHEADER, BITMAPINFOHEADER, and extended DIB header
 * formats.
 *
 * <p>These constants represent byte sizes and offsets within BMP file structures, and are intended
 * for use when reading raw BMP binary data.
 *
 * <p>This class cannot be instantiated.
 *
 * @author Kevin Babu
 */
public final class BitmapConstants {

  /** Private constructor to prevent instantiation. */
  private BitmapConstants() {}

  // --- DIB Header Sizes ---
  /** Size of the BITMAPCOREHEADER in bytes. */
  public static final int BITMAPCOREHEADER_SIZE = 12;

  /** Size of the BITMAPINFOHEADER in bytes. */
  public static final int BITMAPINFOHEADER_SIZE = 40;

  /** Size of the BITMAPV2INFOHEADER in bytes. */
  public static final int BITMAPV2INFOHEADER_SIZE = 52;

  /** Size of the BITMAPV3INFOHEADER in bytes. */
  public static final int BITMAPV3INFOHEADER_SIZE = 56;

  /** Size of the BITMAPV4HEADER in bytes. */
  public static final int BITMAPV4HEADER_SIZE = 108;

  /** Size of the BITMAPV5HEADER in bytes. */
  public static final int BITMAPV5HEADER_SIZE = 124;

  // --- BITMAPFILEHEADER ---
  /** Size of the BITMAPFILEHEADER in bytes. */
  public static final int FILE_HEADER_SIZE = 14;

  /** Offset to the "BM" file type signature. */
  public static final int BF_TYPE_OFFSET = 0;

  /** Offset to the file size field. */
  public static final int BF_SIZE_OFFSET = 2;

  /** Offset to the first reserved field. */
  public static final int BF_RESERVED1_OFFSET = 6;

  /** Offset to the second reserved field. */
  public static final int BF_RESERVED2_OFFSET = 8;

  /** Offset to the pixel data (bitmap bits). */
  public static final int BF_OFFBITS_OFFSET = 10;

  // These offsets are relative to the START of the DIB header (which is usually byte 14)
  // --- DIB Header (BITMAPCOREHEADER and variants) ---
  /** Offset to the image width (in pixels) in BITMAPCOREHEADER. */
  public static final int BI_CORE_WIDTH_OFFSET = 4;

  /** Offset to the image height (in pixels) in BITMAPCOREHEADER. */
  public static final int BI_CORE_HEIGHT_OFFSET = 6;

  /** Offset to the number of color planes in BITMAPCOREHEADER. */
  public static final int BI_CORE_PLANES_OFFSET = 8;

  /** Offset to the bits-per-pixel field in BITMAPCOREHEADER. */
  public static final int BI_CORE_BITCOUNT_OFFSET = 10;

  // --- DIB Header (BITMAPINFOHEADER and variants) ---
  /** Offset to the size of the DIB header. */
  public static final int BI_SIZE_OFFSET = 0;

  /** Offset to the image width (in pixels). */
  public static final int BI_WIDTH_OFFSET = 4;

  /** Offset to the image height (in pixels). */
  public static final int BI_HEIGHT_OFFSET = 8;

  /** Offset to the number of color planes. */
  public static final int BI_PLANES_OFFSET = 12;

  /** Offset to the bits-per-pixel field. */
  public static final int BI_BITCOUNT_OFFSET = 14;

  /** Offset to the compression method field. */
  public static final int BI_COMPRESSION_OFFSET = 16;

  /** Offset to the size of the bitmap data. */
  public static final int BI_SIZEIMAGE_OFFSET = 20;

  /** Offset to the horizontal resolution (pixels per meter). */
  public static final int BI_X_PELS_PER_METER_OFFSET = 24;

  /** Offset to the vertical resolution (pixels per meter). */
  public static final int BI_Y_PELS_PER_METER_OFFSET = 28;

  /** Offset to the number of colors used in the bitmap. */
  public static final int BI_CLR_USED_OFFSET = 32;

  /** Offset to the number of important colors. */
  public static final int BI_CLR_IMPORTANT_OFFSET = 36;

  // --- BITMAPV4HEADER specific offsets --- (relative to DIB header start)
  /** Offset to the red channel mask. */
  public static final int BV4_RED_MASK_OFFSET = 40;

  /** Offset to the green channel mask. */
  public static final int BV4_GREEN_MASK_OFFSET = 44;

  /** Offset to the blue channel mask. */
  public static final int BV4_BLUE_MASK_OFFSET = 48;

  /** Offset to the alpha channel mask. */
  public static final int BV4_ALPHA_MASK_OFFSET = 52;

  /** Offset to the color space type. */
  public static final int BV4_CS_TYPE_OFFSET = 56;

  /** Offset to the endpoints structure. */
  public static final int BV4_ENDPOINTS_OFFSET = 60;

  /** Offset to the red gamma correction value. */
  public static final int BV4_GAMMA_RED_OFFSET = 96;

  /** Offset to the green gamma correction value. */
  public static final int BV4_GAMMA_GREEN_OFFSET = 100;

  /** Offset to the blue gamma correction value. */
  public static final int BV4_GAMMA_BLUE_OFFSET = 104;

  // --- BITMAPV5HEADER specific offsets (relative to DIB header start)
  /** Offset to the rendering intent field. */
  public static final int BV5_INTENT_OFFSET = 108;

  /** Offset to the profile data location. */
  public static final int BV5_PROFILE_DATA_OFFSET = 112;

  /** Offset to the profile data size. */
  public static final int BV5_PROFILE_SIZE_OFFSET = 116;

  /** Offset to the reserved field (usually zero). */
  public static final int BV5_RESERVED_OFFSET = 120;
}
