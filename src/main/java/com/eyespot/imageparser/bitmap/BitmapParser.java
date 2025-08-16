package com.eyespot.imageparser.bitmap;

import static com.eyespot.imageparser.util.ImageUtils.readInt;
import static com.eyespot.imageparser.util.ImageUtils.readShort;

import com.eyespot.imageparser.IParser;
import com.eyespot.imageparser.ImageType;
import com.eyespot.imageparser.exception.CorruptedImageException;
import com.eyespot.imageparser.util.ImageUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@code Parser} class implements the {@link IParser} interface and provides parsing
 * functionality for image files. It supports:
 *
 * <ul>
 *   <li>Detection of images based on the file's magic number
 * </ul>
 *
 * <p>The parser reads either a {@link Path} to a file or a byte array directly and attempts to
 * determine the image type. It also exposes methods to retrieve image metadata such as type, size,
 * and offset
 *
 * <p>This implementation is read-only and does not modify the image data.
 *
 * @author Kevin Babu
 * @see DIBHeader
 * @see BitmapConstants
 * @see InfoHeaderType
 * @see ImageType
 */
public class BitmapParser implements IParser {

  /** The image data in bytes. */
  private final byte[] data;

  /** The bitmap file header. */
  private final Header header;

  /** The bitmap DIB (Device Independent Bitmap) header. */
  private final DIBHeader dibHeader;

  /** The bitmap colour palette, if present. */
  private final ColourPalette colourPalette;

  private static final Logger LOGGER = Logger.getLogger(BitmapParser.class.getName());

  /**
   * Represents the {@code BITMAPFILEHEADER} structure (14 bytes). Contains basic metadata such as
   * file type, size, and offset to pixel data.
   */
  private static final class Header {
    final ImageType type;
    final int size;
    final int offset; // Offset to actual image data
    final short reserved1;
    final short reserved2;

    /**
     * Constructs a Header from the byte array.
     *
     * @param data the byte array containing the file header
     * @throws IllegalArgumentException if data is too short to contain a valid header
     */
    private Header(byte[] data) {
      this.type = ImageType.BITMAP;
      this.size = readInt(data, BitmapConstants.BF_SIZE_OFFSET);
      this.reserved1 = readShort(data, BitmapConstants.BF_RESERVED1_OFFSET);
      this.reserved2 = readShort(data, BitmapConstants.BF_RESERVED2_OFFSET);
      this.offset = readInt(data, BitmapConstants.BF_OFFBITS_OFFSET);
    }

    /** @return The image type, typically {@code BITMAP} */
    public ImageType getType() {
      return type;
    }

    /** @return The total size of the BMP file in bytes */
    public int getSize() {
      return size;
    }

    /** @return The byte offset from start of file to pixel data */
    public int getOffset() {
      return offset;
    }
  }

  /**
   * Represents the colour palette (if present) in the BMP file.
   *
   * <p>Only present when bit depth is ≤ 8. Supports RGBTRIPLE (3-byte) and RGBQUAD (4-byte)
   * entries.
   *
   * @see <a href="https://en.wikipedia.org/wiki/BMP_file_format#Colour_table">BMP Colour Table</a>
   */
  private static final class ColourPalette {
    private final int[] colours;
    private final boolean hasAlphaChannel;

    /**
     * Parses the colour palette from the BMP byte array.
     *
     * @param data the image byte array
     * @param dibHeader the parsed DIB header
     * @param paletteStartFileOffset file offset where the palette begins
     */
    private ColourPalette(byte[] data, DIBHeader dibHeader, int paletteStartFileOffset) {
      int numEntries;
      // If the number of colours in the colour palette is 0 or colours used > important colours,
      // default to 2^n where n == bits per pixel
      if (dibHeader.getNColours() == 0
          || dibHeader.getNColours() > dibHeader.getImportantColours()) {
        numEntries = 1 << dibHeader.getBitsPerPixel(); // Max colours for bit-depth
      } else {
        numEntries = dibHeader.getNColours();
      }
      this.colours = new int[numEntries];
      int bytesPerPaletteEntry;

      // Determine bytes per palette entry based on DIB header type
      if (InfoHeaderType.BITMAPCOREHEADER.equals(dibHeader.getType())) {
        // RGBTRIPLE (no 4th byte for alpha)
        bytesPerPaletteEntry = 3;
      } else {
        // RGBQUAD (has the 4th byte)
        bytesPerPaletteEntry = 4;
      }

      boolean foundNonZeroReservedByte = false;
      boolean explicitAlphaFlag = false;

      // Check for explicit alpha mask in BITMAPV4/V5 headers
      if (dibHeader instanceof BitmapV4Header) {
        explicitAlphaFlag = ((BitmapV4Header) dibHeader).getAlphaMask() != 0;
      }

      for (int i = 0; i < numEntries; i++) {
        int entryOffset = paletteStartFileOffset + (i * bytesPerPaletteEntry);

        // Basic bounds check for reading from data array
        if (entryOffset + bytesPerPaletteEntry > data.length) {
          throw new IllegalArgumentException(
              String.format(
                  "Palette data truncated or out of bounds at entry %d (offset: %d)",
                  i, entryOffset));
        }

        // BMP stores BGR order. Alpha is often 0xFF (opaque) or ignored. But will try and handle it
        // Java lacks unsigned byte hence the need for bitwise operations
        int blue = data[entryOffset] & 0xFF;
        int green = data[entryOffset + 1] & 0xFF;
        int red = data[entryOffset + 2] & 0xFF;

        // Default to opaque (255)
        int alpha = 0xFF;

        // Check alpha in bytes, if not explicitly stated in header
        if (bytesPerPaletteEntry == 4) {
          int parsedAlpha = data[entryOffset + 3] & 0xFF;
          alpha = (parsedAlpha == 0) ? 0xFF : parsedAlpha;
        }

        foundNonZeroReservedByte = alpha != 0xFF;

        // Store as ARGB (AARRGGBB)
        this.colours[i] = (alpha << 24) | (red << 16) | (green << 8) | blue;
      }

      this.hasAlphaChannel = explicitAlphaFlag || foundNonZeroReservedByte;
    }

    /** @return the number of colour entries in the palette */
    public int getNumberOfEntries() {
      return colours.length;
    }

    /**
     * Retrieves a colour entry from the palette.
     *
     * @param index the palette index
     * @return the ARGB colour as {@code 0xAARRGGBB}
     * @throws IndexOutOfBoundsException if index is out of bounds
     */
    public int getColour(int index) {
      if (index < 0 || index >= colours.length) {
        throw new IndexOutOfBoundsException(
            String.format("Palette index %d out of bounds [0, %d]", index, colours.length - 1));
      }
      return colours[index];
    }

    /**
     * Indicates if any alpha channel data is present in the palette.
     *
     * @return true if alpha channel is likely present
     */
    public boolean hasAlphaChannel() {
      return hasAlphaChannel;
    }
  }

  /**
   * Constructs a new {@code BitmapParser} by reading all bytes from the given file path.
   *
   * @param path the path to a BMP image file
   * @throws IOException if an I/O error occurs reading from the file
   * @throws IllegalArgumentException if the file does not appear to be a valid BMP
   */
  public BitmapParser(Path path) throws IOException {
    this(Files.readAllBytes(path));
  }

  /**
   * Constructs a new {@code BitmapParser} using the provided byte array.
   *
   * <p>This constructor checks the first few bytes, commonly referred to as Magic Number, to
   * identify the image type. It also checks the image header to derive other image metadata such as
   * size and offset. f
   *
   * @param bytes the byte array representing image data
   * @throws IllegalArgumentException if the byte array does not represent a valid BMP image
   */
  public BitmapParser(byte[] bytes) {
    Objects.requireNonNull(bytes, "Input byte array cannot be null.");
    if (bytes.length < BitmapConstants.FILE_HEADER_SIZE + BitmapConstants.BITMAPCOREHEADER_SIZE) {
      throw new IllegalArgumentException("Byte array too short to be a minimal BMP image.");
    }

    this.data = Arrays.copyOf(bytes, bytes.length);

    // Validate the image type before attempting to parse headers.
    if (!ImageType.BITMAP.equals(ImageUtils.detectType(data))) {
      throw new IllegalArgumentException(
          "Provided data is not a valid BMP image (magic number mismatch).");
    }

    this.header = new Header(this.data);
    this.dibHeader = DIBHeader.createDIBHeader(this.data);

    if (this.hasColourPalette()) {
      int bitmask = 0;
      if (dibHeader.getCompression() == 3) {
        bitmask = 12;
      } else if (dibHeader.getCompression() == 6) {
        bitmask = 16;
      }
      int paletteStartOffset =
          BitmapConstants.FILE_HEADER_SIZE + this.dibHeader.getHeaderSize() + bitmask;
      this.colourPalette = new ColourPalette(this.data, this.dibHeader, paletteStartOffset);
    } else {
      this.colourPalette = null;
    }
  }

  /** Helper method to read uncompressed (BI_RGB) pixel data into the pixels array. */
  private void readUncompressedPixels(
      int[][] pixels, int displayRowMapMultiplier, int displayRowMapOffset)
      throws CorruptedImageException {

    int width = dibHeader.getWidth();
    int height = Math.abs(dibHeader.getHeight());
    int bitsPerPixel = dibHeader.getBitsPerPixel();
    int scanlineByteSize = DIBHeader.calculateScanlineSize(width, bitsPerPixel);
    int fileRowIndexStart = 0;
    int fileRowIncrement = 1;

    for (int i = fileRowIndexStart; i != height; i += fileRowIncrement) {
      // Calculate the corresponding row in the output array
      int displayRowIndex = displayRowMapOffset + (i * displayRowMapMultiplier);

      for (int x = 0; x < width; x++) {
        int pixelValueARGB;

        switch (bitsPerPixel) {
          case 1:
          case 4:
          case 8:
            pixelValueARGB =
                colourPalette.getColour(getIndexedColourModePixelValue(i, x, scanlineByteSize));
            break;
          case 16:
          case 24:
          case 32:
            pixelValueARGB = getNonIndexedColourModePixelValue(i, x, scanlineByteSize);
            break;
          default:
            throw new UnsupportedOperationException(
                "Unsupported bits per pixel for BI_RGB: " + bitsPerPixel);
        }
        pixels[displayRowIndex][x] = pixelValueARGB;
      }
    }
  }

  /**
   * Extracts the pixel index from a byte array representing image data, based on the specified bit
   * depth and pixel position.
   *
   * <p>This method handles three common bits-per-pixel configurations:
   *
   * <ul>
   *   <li><b>8 bits per pixel</b>: each byte directly represents one pixel index.
   *   <li><b>4 bits per pixel</b>: each byte contains two pixel indices (nibbles), with even
   *       positions using the high nibble and odd positions using the low nibble.
   *   <li><b>1 bit per pixel</b>: each byte contains 8 pixels, represented from the most
   *       significant bit (leftmost pixel) to the least significant bit (rightmost pixel).
   * </ul>
   *
   * @param currentPixelFileOffset the offset into the data array where the relevant byte is located
   * @param bitsPerPixel the number of bits used to represent each pixel (typically 1, 4, or 8)
   * @param pos the position of the pixel relative to the byte, used for determining which bits to
   *     extract
   * @return the extracted pixel index
   */
  private int getPixelIndex(int currentPixelFileOffset, int bitsPerPixel, int pos) {
    int byteContainingPixels = data[currentPixelFileOffset] & 0xFF;

    if (bitsPerPixel == 8) {
      return byteContainingPixels;
    }
    if (bitsPerPixel == 4) {
      if (pos % 2 == 0) {
        return (byteContainingPixels >> 4) & 0x0F;
      } else {
        return byteContainingPixels & 0x0F;
      }
    }

    int bitPosition = 7 - (pos % 8);
    return (byteContainingPixels >> bitPosition) & 0x01;
  }

  /**
   * Retrieves the pixel value (palette index) for a specific pixel in an image using indexed colour
   * mode.
   *
   * <p><b>Indexed Colour Mode:</b> In this mode, pixel values do not store actual colours but
   * instead store indices into a colour palette (also known as a colour table). The palette
   * contains the actual RGB colour definitions. Depending on the bit depth (e.g., 1, 4, or 8 bits
   * per pixel), multiple pixels may be packed into a single byte.
   *
   * <p>This method calculates the file offset of the specified pixel within the image data,
   * extracts the pixel index from the byte using {@code getPixelIndex()}, and then verifies that
   * the index falls within the bounds of the defined palette.
   *
   * @param row the row index (scanline number) of the pixel (0-based from top or bottom depending
   *     on image orientation)
   * @param cell the column index (horizontal position) of the pixel (0-based)
   * @param scanlineByteSize the total number of bytes in a single scanline (row) of the image
   * @return the palette index corresponding to the specified pixel
   * @throws IllegalArgumentException if the computed file offset is out of bounds or if the
   *     extracted palette index is not valid
   */
  private int getIndexedColourModePixelValue(int row, int cell, int scanlineByteSize)
      throws CorruptedImageException {
    // Calculate the current row's offset in the file
    int currentScanlineFileOffset = header.getOffset() + (row * scanlineByteSize);
    int bitsPerPixel = dibHeader.getBitsPerPixel();

    int currentPixelFileOffset = currentScanlineFileOffset + (cell * bitsPerPixel / 8);

    if (currentPixelFileOffset < 0 || currentPixelFileOffset >= data.length) {
      throw new CorruptedImageException(
          "Pixel data offset out of bounds for indexed pixel at (" + cell + "," + row + ")");
    }

    int pixelIndex = getPixelIndex(currentPixelFileOffset, bitsPerPixel, cell);
    if (pixelIndex >= colourPalette.getNumberOfEntries()) {
      throw new IllegalArgumentException(
          "Palette index " + pixelIndex + " out of bounds at pixel (" + cell + "," + row + ")");
    }
    return pixelIndex;
  }

  /**
   * Retrieves the ARGB colour value of a specific pixel in a non-indexed colour image (BI_RGB
   * format).
   *
   * <p><b>Non-Indexed Colour Mode (BI_RGB):</b> Unlike indexed colour mode, non-indexed images
   * store actual colour values for each pixel directly in the image data. This method supports 16,
   * 24, and 32 bits per pixel (bpp) formats:
   *
   * <ul>
   *   <li><b>16bpp (RGB555):</b> Each pixel is stored as a 2-byte (16-bit) value. Bits are
   *       allocated as:
   *       <pre>
   *       Bit layout: RRRRRGGGGGBBBBB
   *       - Bits 15–11: Red (5 bits)
   *       - Bits 10–5 : Green (5 bits)
   *       - Bits 4–0  : Blue (5 bits)
   *       </pre>
   *       These are expanded to 8-bit components for ARGB output.
   *   <li><b>24bpp (RGB888):</b> Each pixel uses 3 bytes (24 bits) in the order Blue, Green, Red
   *       (BGR). Alpha is assumed to be 255 (fully opaque).
   *   <li><b>32bpp (ARGB8888 or BGRA):</b> Each pixel uses 4 bytes. The order is Blue, Green, Red,
   *       Alpha. The fourth byte is interpreted as the alpha (transparency) channel.
   * </ul>
   *
   * @param i the row index (scanline number) of the pixel (0-based)
   * @param x the column index (horizontal pixel position) within the scanline (0-based)
   * @param scanlineByteSize the number of bytes that make up one scanline
   * @return the ARGB colour value of the specified pixel as a 32-bit integer
   * @throws IllegalArgumentException if the computed file offset is outside the bounds of the image
   *     data
   * @throws UnsupportedOperationException if the bits-per-pixel value is unsupported
   */
  private int getNonIndexedColourModePixelValue(int i, int x, int scanlineByteSize)
      throws CorruptedImageException {
    // Calculate the current row's offset in the file
    int currentScanlineFileOffset = header.getOffset() + (i * scanlineByteSize);

    switch (dibHeader.getBitsPerPixel()) {
      case 16:
        {
          int currentPixelFileOffset = currentScanlineFileOffset + (x * 2);
          ImageUtils.ensureBytesAvailable(
              currentPixelFileOffset,
              2,
              "Pixel data offset out of bounds for 16bpp pixel at (" + x + "," + i + ")",
              data.length,
              true);
          short pixelData = readShort(data, currentPixelFileOffset);

          // Convert 5-bit components to 8-bit.
          int b = (pixelData & 0x001F) * 255 / 31;
          int g = ((pixelData & 0x03E0) >> 5) * 255 / 31;
          int r = ((pixelData & 0x7C00) >> 10) * 255 / 31;

          return (0xFF << 24) | (r << 16) | (g << 8) | b;
        }
      case 24:
        {
          int currentPixelFileOffset = currentScanlineFileOffset + (x * 3);
          ImageUtils.ensureBytesAvailable(
              currentPixelFileOffset,
              3,
              "Pixel data offset out of bounds for 24bpp pixel at (" + x + "," + i + ")",
              data.length,
              true);
          int b = data[currentPixelFileOffset] & 0xFF;
          int g = data[currentPixelFileOffset + 1] & 0xFF;
          int r = data[currentPixelFileOffset + 2] & 0xFF;
          return (0xFF << 24) | (r << 16) | (g << 8) | b;
        }
      case 32:
        int currentPixelFileOffset = currentScanlineFileOffset + (x * 4);
        ImageUtils.ensureBytesAvailable(
            currentPixelFileOffset,
            4,
            "Pixel data offset out of bounds for 32bpp pixel at (" + x + "," + i + ")",
            data.length,
            true);
        int b = data[currentPixelFileOffset] & 0xFF;
        int g = data[currentPixelFileOffset + 1] & 0xFF;
        int r = data[currentPixelFileOffset + 2] & 0xFF;
        // For BI_RGB 32bpp, the 4th byte is usually alpha (A) or unused (X).
        // Currently, interpret it as alpha, as is common in modern usage.
        int a = data[currentPixelFileOffset + 3] & 0xFF;
        return (a << 24) | (r << 16) | (g << 8) | b;
      default:
        throw new UnsupportedOperationException(
            "Unsupported bits per pixel for BI_RGB: " + dibHeader.getBitsPerPixel());
    }
  }

  /**
   * Reads pixel data from a BMP image using bitfield masks (BI_BITFIELDS or BI_ALPHABITFIELDS
   * compression) and returns it as a 2D array of ARGB pixel values.
   *
   * <p><b>Bitfield Compression Mode:</b> In this mode, each pixel's colour components are not
   * assumed to follow a fixed layout like RGB888. Instead, individual bit masks are used to define
   * the positions of red, green, blue, and optionally alpha channels. These masks are typically
   * defined in the {@code BitmapV2InfoHeader}, {@code BitmapV3InfoHeader}, or {@code
   * BitmapV4Header}, and are extracted using {@link #extractMasks()}.
   *
   * <p>This method only supports 16bpp and 32bpp images. It dynamically chooses the appropriate
   * pixel-reading method for each scanline depending on the bit depth.
   *
   * <p>The scanline reading direction is flexible, allowing for bottom-up or top-down row ordering
   * via the {@code fileRowIndexStart}, {@code fileRowIndexEnd}, and {@code fileRowIncrement}
   * parameters. The mapping to display coordinates is controlled by {@code displayRowMapMultiplier}
   * and {@code displayRowMapOffset}, which determine how file rows map to the output image's row
   * indices.
   *
   * @param displayRowMapMultiplier multiplier for converting file row index to display row index
   * @param displayRowMapOffset offset to apply after scaling the file row index for display mapping
   * @throws IllegalArgumentException if the image's bits per pixel is not 16 or 32
   */
  private void readBitfieldPixels(
      int[][] pixels, int displayRowMapMultiplier, int displayRowMapOffset) {

    int fileRowIndexStart = 0;
    int fileRowIncrement = 1;
    int fileRowIndexEnd = Math.abs(dibHeader.getHeight());
    long[] masks = extractMasks();

    int bitsPerPixel = dibHeader.getBitsPerPixel();
    int width = dibHeader.getWidth();

    if (bitsPerPixel != 16 && bitsPerPixel != 32) {
      throw new IllegalArgumentException(
          "BI_BITFIELDS compression is only valid for 16 or 32 bits per pixel.");
    }

    int scanlineByteSize = DIBHeader.calculateScanlineSize(width, bitsPerPixel);

    long redMask = masks[0];
    long greenMask = masks[1];
    long blueMask = masks[2];
    long alphaMask = masks[3];

    int leap = bitsPerPixel / 8;
    long unsignedShortMask = (1L << bitsPerPixel) - 1;

    for (int i = fileRowIndexStart; i != fileRowIndexEnd; i += fileRowIncrement) {
      int scanlineOffset = header.getOffset() + (i * scanlineByteSize);
      int displayRowIndex = displayRowMapOffset + (i * displayRowMapMultiplier);

      for (int x = 0; x < width; x++) {
        int pixelOffset = scanlineOffset + (x * leap);

        if (pixelOffset + leap > data.length) {
          throw new IllegalArgumentException(
              String.format(
                  "Pixel data offset out of bounds for %d bitfield pixel at (%d, %d)",
                  bitsPerPixel, x, i));
        }

        long pixelData = readBitfieldPixelData(pixelOffset, leap, x, i) & unsignedShortMask;

        int r = extractComponent(pixelData, redMask);
        int g = extractComponent(pixelData, greenMask);
        int b = extractComponent(pixelData, blueMask);
        int a = (alphaMask != 0) ? extractComponent(pixelData, alphaMask) : 0xFF;

        pixels[displayRowIndex][x] = (a << 24) | (r << 16) | (g << 8) | b;
      }
    }
  }

  /**
   * Reads a pixel's raw bitfield data from the bitmap byte array at the given offset.
   *
   * <p>This method supports both 16-bit and 32-bit pixels. The number of bytes to read (2 or 4) is
   * determined by the {@code leap} parameter.
   *
   * @param offset the byte offset in the bitmap data where the pixel starts
   * @param leap the number of bytes per pixel (typically 2 for 16bpp, 4 for 32bpp)
   * @return the raw pixel data as an {@code int}; for 16bpp this will be a sign-extended short, for
   *     32bpp it will be the full 4-byte integer
   * @throws IllegalArgumentException if the bpp is not 16 or 32 or if offset is invalid
   */
  private int readBitfieldPixelData(int offset, int leap, int row, int col) {
    int bitsPerPixel = dibHeader.getBitsPerPixel();

    if (offset + leap > data.length) {
      throw new IllegalArgumentException(
          String.format(
              "Pixel data offset out of bounds for %d bitfield pixel at (%d, %d)",
              bitsPerPixel, row, col));
    }

    if (bitsPerPixel != 16 && bitsPerPixel != 32) {
      throw new IllegalArgumentException(
          "BI_BITFIELDS compression is only valid for 16 or 32 bits per pixel.");
    }
    if (leap == 2) {
      return readShort(data, offset);
    }
    return readInt(data, offset);
  }

  /**
   * Decodes pixel data compressed using BI_RLE8 (Run-Length Encoding for 8-bit BMP images). This
   * method reads the compressed byte stream and fills the provided pixels array. It handles both
   * encoded runs and absolute runs, mapping logical coordinates to display coordinates.
   *
   * @param pixels the 2D output pixel array to write decoded colors into
   * @param displayRowMapMultiplier determines if rows are bottom-up (-1) or top-down (1)
   * @param displayRowMapOffset offset to apply to the row index for display ordering
   * @throws IllegalArgumentException if the DIB header does not specify 8 bits per pixel, or if the
   *     data stream is malformed
   */
  private void readRLE8Pixels(int[][] pixels, int displayRowMapMultiplier, int displayRowMapOffset)
      throws CorruptedImageException {
    if (dibHeader.getBitsPerPixel() != 8) {
      throw new IllegalArgumentException("BI_RLE8 compression is only valid for 8 bits per pixel.");
    }

    int currentFileOffset = header.getOffset();
    int currentX = 0;
    int currentY = 0;
    final int endOfLine = 0x00;
    final int endOfBitmap = 0x01;
    final int delta = 0x02;

    // Loop through the compressed data until EOB
    while (currentFileOffset < data.length) {
      // Need at least 2 bytes for any command or encoded run
      if (currentFileOffset + 1 >= data.length) {
        break;
      }
      int runLength = data[currentFileOffset++] & 0xFF;

      if (runLength != 0) {
        ImageUtils.ensureBytesAvailable(
            currentFileOffset,
            1,
            "RLE8 decoding error: Missing color index for encoded run.",
            data.length,
            false);
        int colourIndex = data[currentFileOffset++] & 0xFF;
        currentX =
            writeBIRLE8EncodedRun(
                pixels,
                runLength,
                displayRowMapOffset,
                displayRowMapMultiplier,
                currentX,
                currentY,
                colourIndex);
      } else {
        ImageUtils.ensureBytesAvailable(
            currentFileOffset,
            1,
            "RLE8 decoding error: Missing escape code parameter.",
            data.length,
            false);
        int code = data[currentFileOffset++] & 0xFF;

        switch (code) {
          case endOfLine:
            // CRLF
            currentX = 0;
            currentY++;
            break;
          case endOfBitmap:
            return;
          case delta:
            ImageUtils.ensureBytesAvailable(
                currentFileOffset,
                2,
                "RLE8 decoding error: Missing delta offsets (x, y).",
                data.length,
                true);
            int dx = data[currentFileOffset++] & 0xFF;
            int dy = data[currentFileOffset++] & 0xFF;

            currentX += dx;
            currentY += dy;
            break;
            // Absolute Mode (byte2 is count > 2)
          default:
            ImageUtils.ensureBytesAvailable(
                currentFileOffset,
                code,
                "RLE8 decoding error: Not enough data for absolute run of " + code + " pixels.",
                data.length,
                false);
            currentX =
                writeBIRLE8AbsoluteRun(
                    pixels,
                    code,
                    displayRowMapOffset,
                    displayRowMapMultiplier,
                    currentX,
                    currentY,
                    currentFileOffset);
            // Advance past pixel data
            currentFileOffset += code;

            // Absolute Mode Padding: data must be aligned to a WORD (16-bit) boundary.
            // If count is odd, there's an extra padding byte. Skip padding byte
            if (code % 2 != 0) {
              currentFileOffset++;
            }
            break;
        }
      }
    }
  }

  /**
   * Writes an encoded run to the output pixel array. In encoded mode, a single color index is
   * repeated for the specified run length.
   *
   * @param pixels the 2D output pixel array
   * @param runLength the number of pixels to write with the same color
   * @param colourIndex the palette index for the color to use
   * @param displayRowMapOffset row offset for display mapping
   * @param displayRowMapMultiplier direction for row mapping (1 for top-down, -1 for bottom-up)
   * @param currentX starting X coordinate
   * @param currentY current Y coordinate
   * @return the updated X coordinate after writing the run
   */
  private int writeBIRLE8EncodedRun(
      int[][] pixels,
      int runLength,
      int displayRowMapOffset,
      int displayRowMapMultiplier,
      int currentX,
      int currentY,
      int colourIndex) {
    int width = dibHeader.getWidth();
    int displayHeight = Math.abs(dibHeader.getHeight());
    int colour = colourPalette.getColour(colourIndex);
    int row = displayRowMapOffset + currentY * displayRowMapMultiplier;

    for (int i = 0; i < runLength; i++, currentX++) {
      if (currentX >= 0
          && currentX < width
          && currentY >= 0
          && currentY < displayHeight
          && row >= 0
          && row < displayHeight) {
        pixels[row][currentX] = colour;
      }
    }
    return currentX;
  }

  /**
   * Writes an absolute run to the output pixel array. In absolute mode, each pixel index is
   * explicitly specified in the data.
   *
   * @param pixels the 2D output pixel array
   * @param count the number of explicit pixel indices to read and write
   * @param displayRowMapOffset row offset for display mapping
   * @param displayRowMapMultiplier direction for row mapping (1 for top-down, -1 for bottom-up)
   * @param currentX starting X coordinate
   * @param currentY current Y coordinate
   * @param currentFileOffset offset into the data array where the pixel indices begin
   * @return the updated X coordinate after writing the run
   */
  private int writeBIRLE8AbsoluteRun(
      int[][] pixels,
      int count,
      int displayRowMapOffset,
      int displayRowMapMultiplier,
      int currentX,
      int currentY,
      int currentFileOffset) {
    int width = dibHeader.getWidth();
    int displayHeight = Math.abs(dibHeader.getHeight());
    int row = displayRowMapOffset + (currentY * displayRowMapMultiplier);

    for (int i = 0; i < count; i++, currentX++) {
      if (currentX >= 0
          && currentX < width
          && currentY >= 0
          && currentY < displayHeight
          && row >= 0
          && row < displayHeight) {
        int pixelIndex = data[currentFileOffset + i] & 0xFF;
        pixels[row][currentX] = colourPalette.getColour(pixelIndex);
      }
    }

    return currentX;
  }

  /**
   * Extracts the colour channel bit masks (red, green, blue, alpha) from the DIB header.
   *
   * <p><b>Bitfields Compression (BI_BITFIELDS / BI_ALPHABITFIELDS):</b> In some bitmap formats,
   * especially when using 16bpp or 32bpp with BI_BITFIELDS compression, colour values are not
   * packed in a fixed layout like RGB888. Instead, each channel is defined by a mask indicating
   * which bits in the pixel value represent that colour component.
   *
   * <p>This method checks for specific extended DIB header types that define these masks:
   *
   * <ul>
   *   <li>{@code BitmapV4Header} – provides red, green, blue, and alpha masks
   *   <li>{@code BitmapV2InfoHeader} – provides red, green, and blue masks
   *   <li>{@code BitmapV3InfoHeader} – provides red, green, blue, and alpha mask
   * </ul>
   *
   * <p>If none of the headers provide masks, default masks are applied based on the bit depth:
   *
   * <ul>
   *   <li>For 16bpp: RGB565 (5 red, 6 green, 5 blue)
   *   <li>For 24bpp/32bpp: ARGB8888 format
   * </ul>
   *
   * @return an array of 4 {@code long} values representing the red, green, blue, and alpha masks
   *     (in that order)
   */
  private long[] extractMasks() {
    long redMask = 0;
    long greenMask = 0;
    long blueMask = 0;
    long alphaMask = 0;

    // Handle both BitmapV4Header and BitmapV5Header
    if (dibHeader instanceof BitmapV4Header) {
      BitmapV4Header v4Header = (BitmapV4Header) dibHeader;
      redMask = v4Header.getRedMask() & 0xFFFFFFFFL;
      greenMask = v4Header.getGreenMask() & 0xFFFFFFFFL;
      blueMask = v4Header.getBlueMask() & 0xFFFFFFFFL;
      alphaMask = v4Header.getAlphaMask() & 0xFFFFFFFFL;
    }

    // Handle both BitmapV2InfoHeader and BitmapV3InfoHeader. No need
    // to handle alpha masks since documentation does not indicate so
    if (dibHeader instanceof BitmapV2InfoHeader) {
      BitmapV2InfoHeader v2Header = (BitmapV2InfoHeader) dibHeader;
      redMask = v2Header.getRedMask();
      greenMask = v2Header.getGreenMask();
      blueMask = v2Header.getBlueMask();
    }

    // Fallback to common default masks if they were not explicitly found in headers
    if (redMask == 0 && greenMask == 0 && blueMask == 0) {
      int bpp = dibHeader.getBitsPerPixel();
      if (bpp == 16) {
        if (getCompression() == 3
            && data.length
                >= BitmapConstants.FILE_HEADER_SIZE
                    + BitmapConstants.BITMAPINFOHEADER_SIZE
                    + BitmapConstants.BI_PLANES_OFFSET) {
          redMask = readInt(data, 54);
          greenMask = readInt(data, 58);
          blueMask = readInt(data, 62);
        } else {
          redMask = 0xF800;
          greenMask = 0x07E0;
          blueMask = 0x001F;
        }
        alphaMask = 0x0000;
      } else if (bpp == 32) {
        redMask = 0x00FF0000;
        greenMask = 0x0000FF00;
        blueMask = 0x000000FF;
        alphaMask = InfoHeaderType.BITMAPINFOHEADER.equals(getDibHeaderType()) ? 0 : 0xFF000000L;
      }
    }

    return new long[] {redMask, greenMask, blueMask, alphaMask};
  }

  /**
   * Helper to extract an 8-bit colour component from raw pixel data using a mask. This takes a
   * masked component (e.g., 0x00FF0000) and shifts it to be an 8-bit value (0-255). It also scales
   * components from fewer than 8 bits (e.g., 5-bit to 8-bit).
   */
  private int extractComponent(long pixelData, long mask) {
    if (mask == 0) {
      return 0;
    }

    long maskedValue = pixelData & mask;
    int shift = Long.numberOfTrailingZeros(mask);

    int component = (int) (maskedValue >> shift);

    // Scale component to 0-255 range if its bit depth is less than 8.
    // For example, a 5-bit component (0-31) needs to be scaled to 0-255.
    int bitsInComponent = Long.bitCount(mask);
    if (bitsInComponent < 8) {
      return (int) (component * 255.0f / ((1 << bitsInComponent) - 1) + 0.5f);
    } else if (bitsInComponent > 8) {
      return component >> (bitsInComponent - 8);
    }
    return component;
  }

  /** @return the {@link ImageType} for this parser (always {@code BITMAP}) */
  @Override
  public ImageType getType() {
    return header.getType();
  }

  /** @return total file size in bytes */
  @Override
  public int getSize() {
    return header.getSize();
  }

  /** @return the byte offset to the pixel data */
  @Override
  public int getOffset() {
    return header.getOffset();
  }

  /** @return a defensive copy of the raw image data */
  @Override
  public byte[] getRawData() {
    return Arrays.copyOf(data, data.length);
  }

  // Getters
  /** @return the parsed DIB header type */
  public InfoHeaderType getDibHeaderType() {
    return dibHeader.getType();
  }

  /** @return image width in pixels */
  @Override
  public int getWidth() {
    return dibHeader.getWidth();
  }

  /** @return image height in pixels */
  @Override
  public int getHeight() {
    return Math.abs(dibHeader.getHeight());
  }

  /** @return number of bits per pixel */
  @Override
  public int getBitsPerPixel() {
    return dibHeader.getBitsPerPixel();
  }

  /** @return compression method (e.g., BI_RGB, BI_RLE8) */
  @Override
  public int getCompression() {
    return dibHeader.getCompression();
  }

  /** @return size of bitmap pixel data in bytes */
  @Override
  public int getImageDataSize() {
    return dibHeader.getImageDataSize();
  }

  /** @return number of colours used in the palette */
  @Override
  public int getNColours() {
    return dibHeader.getNColours();
  }

  /** @return number of important colours specified in the header */
  @Override
  public int getImportantColours() {
    return dibHeader.getImportantColours();
  }

  /** @return horizontal resolution in pixels per meter */
  @Override
  public int getXResolution() {
    return dibHeader.getXResolution();
  }

  /** @return vertical resolution in pixels per meter */
  @Override
  public int getYResolution() {
    return dibHeader.getYResolution();
  }

  /**
   * Returns the alpha mask for V3/V4/V5 headers.
   *
   * @return the alpha bitmask
   * @throws UnsupportedOperationException if alpha mask is not defined for this header type
   */
  @Override
  public long getAlphaMask() {
    if (InfoHeaderType.BITMAPV3INFOHEADER.equals(dibHeader.getType())) {
      return ((BitmapV3InfoHeader) dibHeader).getAlphaMask();
    }
    if (InfoHeaderType.BITMAPV4HEADER.equals(dibHeader.getType())) {
      return ((BitmapV4Header) dibHeader).getAlphaMask();
    }
    if (InfoHeaderType.BITMAPV5HEADER.equals(dibHeader.getType())) {
      return ((BitmapV5Header) dibHeader).getAlphaMask();
    }
    // For earlier headers, the concept of a dedicated alpha mask doesn't exist
    throw new UnsupportedOperationException(
        String.format("No alpha mask for image with %s header", dibHeader.getType()));
  }

  /**
   * @return ICC profile size for BITMAPV5HEADER
   * @throws UnsupportedOperationException if not a V5 header
   */
  @Override
  public int getProfileSize() {
    if (dibHeader instanceof BitmapV5Header) {
      return ((BitmapV5Header) dibHeader).getProfileSize();
    }
    throw new UnsupportedOperationException(
        String.format("No profile size for image with %s header", dibHeader.getType()));
  }

  /** @return size of the DIB header in bytes */
  @Override
  public int getHeaderSize() {
    return dibHeader.getHeaderSize();
  }

  /** @return true if a colour palette is present (typically for ≤ 8 bpp images) */
  @Override
  public boolean hasColourPalette() {
    return dibHeader.getBitsPerPixel() <= 8; // Typically for 1, 4, or 8 bpp
  }

  /** @return true if alpha channel is present in the colour palette and false otherwise */
  @Override
  public boolean hasAlphaChannel() {
    if (colourPalette != null) {
      return colourPalette.hasAlphaChannel();
    }

    int width = dibHeader.getWidth();
    int height = Math.abs(dibHeader.getHeight());
    int offset = header.getOffset();

    for (int col = 0; col < width; col++) {
      for (int row = 0; row < height; row++) {
        int argb = data[col * row + offset];
        int alpha = (argb >> 24) & 0xFF;
        if (alpha != 255) {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * Reads and returns the pixel data as a 2D array of ARGB integers. Each integer represents a
   * pixel in AARRGGBB format.
   *
   * @return A 2D array (height x width) of pixel data.
   * @throws IllegalStateException If the image data cannot be read (e.g., parser not initialized,
   *     colour palette missing, or unsupported compression).
   * @throws IllegalArgumentException If header values lead to invalid data access.
   * @throws UnsupportedOperationException If the specific bit depth or compression is not
   *     implemented.
   */
  @Override
  public int[][] getPixels() {
    int displayHeight = Math.abs(dibHeader.getHeight());
    int width = dibHeader.getWidth();
    int compression = dibHeader.getCompression();

    int[][] pixels = new int[displayHeight][width];

    // Logical image row to which the current file row maps
    // For bottom-up, fileRow 0 maps to displayHeight-1. For top-down, fileRow 0 maps to 0.
    int displayRowMapMultiplier = dibHeader.getHeight() > 0 ? -1 : 1;
    int displayRowMapOffset = dibHeader.getHeight() > 0 ? displayHeight - 1 : 0;

    // Handle Compression
    try {
      if (compression == 0) {
        readUncompressedPixels(pixels, displayRowMapMultiplier, displayRowMapOffset);
      } else if (compression == 1) {
        readRLE8Pixels(pixels, displayRowMapMultiplier, displayRowMapOffset);
      } else if (compression == 3) {
        readBitfieldPixels(pixels, displayRowMapMultiplier, displayRowMapOffset);
      } else if (compression == 2) {
        throw new UnsupportedOperationException(
            "Run-Length Encoded for 4bpp (BI_RLE4) compression is not implemented yet.");
      } else if (compression == 4 || compression == 5) {
        throw new UnsupportedOperationException(
            "JPEG or PNG embedded compression is not supported for direct pixel reading.");
      } else {
        throw new UnsupportedOperationException("Unsupported BMP compression type: " + compression);
      }
    } catch (CorruptedImageException e) {
      LOGGER.log(Level.SEVERE, e::getMessage);
    }

    return pixels;
  }
}
