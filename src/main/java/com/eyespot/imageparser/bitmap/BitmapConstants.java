package com.eyespot.imageparser.bitmap;

final class BitmapConstants {

  // Private constructor to prevent instantiation
  private BitmapConstants() {}

  // --- DIB Header Sizes ---
  public static final int BITMAPCOREHEADER_SIZE = 12;
  public static final int BITMAPINFOHEADER_SIZE = 40;
  public static final int BITMAPV2INFOHEADER_SIZE = 52;
  public static final int BITMAPV3INFOHEADER_SIZE = 56;
  public static final int BITMAPV4HEADER_SIZE = 108;
  public static final int BITMAPV5HEADER_SIZE = 124;

  // BITMAPFILEHEADER
  public static final int FILE_HEADER_SIZE = 14;
  public static final int BF_TYPE_OFFSET = 0; // "BM" magic number
  public static final int BF_SIZE_OFFSET = 2; // Total file size
  public static final int BF_RESERVED1_OFFSET = 6;
  public static final int BF_RESERVED2_OFFSET = 8;
  public static final int BF_OFFBITS_OFFSET = 10; // Offset to pixel data

  // DIB Header (BITMAPCOREHEADER and variants)
  // These offsets are relative to the START of the DIB header (which is usually byte 14)
  public static final int BI_CORE_WIDTH_OFFSET = 4;
  public static final int BI_CORE_HEIGHT_OFFSET = 6;
  public static final int BI_CORE_PLANES_OFFSET = 8;
  public static final int BI_CORE_BITCOUNT_OFFSET = 10;

  // DIB Header (BITMAPINFOHEADER and variants)
  // These offsets are relative to the START of the DIB header (which is usually byte 14)
  public static final int BI_SIZE_OFFSET = 0; // Size of the DIB header itself
  public static final int BI_WIDTH_OFFSET = 4;
  public static final int BI_HEIGHT_OFFSET = 8;
  public static final int BI_PLANES_OFFSET = 12;
  public static final int BI_BITCOUNT_OFFSET = 14;
  public static final int BI_COMPRESSION_OFFSET = 16;
  public static final int BI_SIZEIMAGE_OFFSET = 20; // Size of the bitmap data
  public static final int BI_X_PELS_PER_METER_OFFSET = 24;
  public static final int BI_Y_PELS_PER_METER_OFFSET = 28;
  public static final int BI_CLR_USED_OFFSET = 32;
  public static final int BI_CLR_IMPORTANT_OFFSET = 36;

  // BITMAPV4HEADER specific offsets (relative to DIB header start)
  public static final int BV4_RED_MASK_OFFSET = 40;
  public static final int BV4_GREEN_MASK_OFFSET = 44;
  public static final int BV4_BLUE_MASK_OFFSET = 48;
  public static final int BV4_ALPHA_MASK_OFFSET = 52;
  public static final int BV4_CS_TYPE_OFFSET = 56;
  public static final int BV4_ENDPOINTS_OFFSET = 60;
  public static final int BV4_GAMMA_RED_OFFSET = 96;
  public static final int BV4_GAMMA_GREEN_OFFSET = 100;
  public static final int BV4_GAMMA_BLUE_OFFSET = 104;

  // BITMAPV5HEADER specific offsets (relative to DIB header start)
  public static final int BV5_INTENT_OFFSET = 108;
  public static final int BV5_PROFILE_DATA_OFFSET = 112;
  public static final int BV5_PROFILE_SIZE_OFFSET = 116;
  public static final int BV5_RESERVED_OFFSET = 120; // Often 0
}
