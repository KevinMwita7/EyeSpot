[![Build](https://github.com/KevinMwita7/EyeSpot/actions/workflows/build.yml/badge.svg?branch=main)](https://github.com/KevinMwita7/EyeSpot/actions/workflows/build.yml)
[![Coverage Status](https://coveralls.io/repos/github/KevinMwita7/EyeSpot/badge.svg?branch=main)](https://coveralls.io/github/KevinMwita7/EyeSpot?branch=main)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
![Java](https://img.shields.io/badge/java-11-blue)

# Eyespot

_Eyespot_ is a Java-based image parsing project, named after [eyespots](https://en.wikipedia.org/wiki/Eyespot_(mimicry))—the circular, eye-like markings found on butterflies and other animals.

## Overview

Eyespot is an educational and exploratory image parser written in Java. It currently supports **Bitmap (BMP)** image parsing. The aim is to build a low-level understanding of how image formats are structured and processed, without relying on Java's built-in abstractions like `BufferedImage`.

This project is intentionally low-level and experimental. We'll see how and where it goes.

---

## 🧩 Class Hierarchy & Methods

This section outlines the structure of BMP DIB headers implemented as an inheritance tree. 

**Note:** All header classes take a `byte[]`(image data) and an offset (where the header starts) and delegate to their superclass constructors.

### `abstract class DIBHeader`

- `static DibHeader createDibHeader(byte[] bytes)`
- `int getHeaderSize()`
- `int getHeight()`
- `int getWidth()`
- `int getColourPlanes()`
- `int getBitsPerPixel()`
- `int getCompression()`
- `int getImageDataSize()`
- `int getXResolution()`
- `int getYResolution()`
- `int getNColours()`
- `int getImportantColours()`

### `class BitmapCoreHeader extends DIBHeader`

- `InfoHeaderType getType()`

### `class BitmapInfoHeader extends DIBHeader`

- `InfoHeaderType getType()`

### `public class BitmapV2InfoHeader extends BitmapInfoHeader`

- `InfoHeaderType getType()`
- `int getRedMask()`
- `int getGreenMask()`
- `int getBlueMask()`

### `public class BitmapV3InfoHeader extends BitmapV2InfoHeader`

- `InfoHeaderType getType()`
- `long getAlphaMask()`

### `public class BitmapV4Header extends BitmapInfoHeader`

- `InfoHeaderType getType()`
- `int getRedMask()`
- `int getGreenMask()`
- `int getBlueMask()`
- `long getAlphaMask()`
- `int getCsType()`
- `CIEXYZTriple getEndpoints()`
- `int getGammaRed()`
- `int getGammaGreen()`
- `int getGammaBlue()`

#### `public static class CIEXYZTriple`

Defined within `BitmapV4Header`. Encapsulates the XYZ chromaticity endpoints of red, green, and blue channels.

- `int getRedX()`
- `int getRedY()`
- `int getRedZ()`
- `int getGreenX()`
- `int getGreenY()`
- `int getGreenZ()`
- `int getBlueX()`
- `int getBlueY()`
- `int getBlueZ()`

### `class BitmapV5Header extends BitmapV4Header`

- `InfoHeaderType getType()`
- `int getIntent()`
- `int getProfileData()`
- `int getProfileSize()`
- `int getReservedV5()`

---

## 🧵 BitmapParser

`BitmapParser` is the core class for decoding and interpreting a BMP image. It performs validation, reads header information, and optionally parses the color palette for images with a bit depth ≤ 8.

### Fields

- `byte[] data`
- `Header header`
- `DIBHeader dibHeader`
- `ColourPalette colourPalette`

### Constructors

- `BitmapParser(Path path)`
- `BitmapParser(byte[] bytes)`

### Methods

- `ImageType getType()`
- `int getSize()`
- `int getOffset()`
- `byte[] getRawData()`
- `InfoHeaderType getDibHeaderType()`
- `int getWidth()`
- `int getHeight()`
- `short getBitsPerPixel()`
- `int getCompression()`
- `int getImageDataSize()`
- `int getNColours()`
- `int getImportantColours()`
- `int getXResolution()`
- `int getYResolution()`
- `long getAlphaMask()`
- `int getProfileSize()`
- `int getHeaderSize()`
- `boolean hasColourPalette()`
- `boolean hasAlphaChannel()`

### `private static class Header`

Encapsulates the BITMAPFILEHEADER portion of the image.

- `ImageType getType()`
- `int getSize()`
- `int getOffset()`

### `private static class ColourPalette`

Handles palette extraction and alpha detection logic for 1/4/8-bit images.

- `int getNumberOfEntries()`
- `int getColour(int index)`
- `boolean hasAlphaChannel()`

---

## 🛠️ ImageUtils

This utility class provides helper methods for format detection and endian-specific parsing.

### Methods

- `static ImageType detectType(Path path)`
- `static ImageType detectType(byte[] bytes)`
- `static int readInt(byte[] data, int offset)`
- `static short readShort(byte[] data, int offset)`

---

## 📏 Constants & Enums

These classes support the parser through fixed values and descriptive types.

### `public final class BitmapConstants`

- `public static final int BITMAPCOREHEADER_SIZE = 12`
- `public static final int BITMAPINFOHEADER_SIZE = 40`
- `public static final int BITMAPV2INFOHEADER_SIZE = 52`
- `public static final int BITMAPV3INFOHEADER_SIZE = 56`
- `public static final int BITMAPV4HEADER_SIZE = 108`
- `public static final int BITMAPV5HEADER_SIZE = 124`
- `public static final int FILE_HEADER_SIZE = 14`
- `public static final int BF_TYPE_OFFSET = 0`
- `public static final int BF_SIZE_OFFSET = 2`
- `public static final int BF_RESERVED1_OFFSET = 6`
- `public static final int BF_RESERVED2_OFFSET = 8`
- `public static final int BF_OFFBITS_OFFSET = 10`
- `public static final int BI_CORE_WIDTH_OFFSET = 4`
- `public static final int BI_CORE_HEIGHT_OFFSET = 6`
- `public static final int BI_CORE_PLANES_OFFSET = 8`
- `public static final int BI_CORE_BITCOUNT_OFFSET = 10`
- `public static final int BI_SIZE_OFFSET = 0`
- `public static final int BI_WIDTH_OFFSET = 4`
- `public static final int BI_HEIGHT_OFFSET = 8`
- `public static final int BI_PLANES_OFFSET = 12`
- `public static final int BI_BITCOUNT_OFFSET = 14`
- `public static final int BI_COMPRESSION_OFFSET = 16`
- `public static final int BI_SIZEIMAGE_OFFSET = 20`
- `public static final int BI_X_PELS_PER_METER_OFFSET = 24`
- `public static final int BI_Y_PELS_PER_METER_OFFSET = 28`
- `public static final int BI_CLR_USED_OFFSET = 32`
- `public static final int BI_CLR_IMPORTANT_OFFSET = 36`
- `public static final int BV4_RED_MASK_OFFSET = 40`
- `public static final int BV4_GREEN_MASK_OFFSET = 44`
- `public static final int BV4_BLUE_MASK_OFFSET = 48`
- `public static final int BV4_ALPHA_MASK_OFFSET = 52`
- `public static final int BV4_CS_TYPE_OFFSET = 56`
- `public static final int BV4_ENDPOINTS_OFFSET = 60`
- `public static final int BV4_GAMMA_RED_OFFSET = 96`
- `public static final int BV4_GAMMA_GREEN_OFFSET = 100`
- `public static final int BV4_GAMMA_BLUE_OFFSET = 104`
- `public static final int BV5_INTENT_OFFSET = 108`
- `public static final int BV5_PROFILE_DATA_OFFSET = 112`
- `public static final int BV5_PROFILE_SIZE_OFFSET = 116`
- `public static final int BV5_RESERVED_OFFSET = 120`

### `public enum InfoHeaderType`

Used to categorize DIB header versions based on their byte size.

- `BITMAPCOREHEADER(BitmapConstants.BITMAPCOREHEADER_SIZE)`
- `BITMAPINFOHEADER(BitmapConstants.BITMAPINFOHEADER_SIZE)`
- `BITMAPV2INFOHEADER(BitmapConstants.BITMAPV2INFOHEADER_SIZE)`
- `BITMAPV3INFOHEADER(BitmapConstants.BITMAPV3INFOHEADER_SIZE)`
- `BITMAPV4HEADER(BitmapConstants.BITMAPV4HEADER_SIZE)`
- `BITMAPV5HEADER(BitmapConstants.BITMAPV5HEADER_SIZE)`
- `public int getSize()`
- `public static InfoHeaderType fromSize(int size)`

### `public enum ImageType`

Defines recognized image formats.

- `BITMAP`
- `UNDETERMINED`

---

## 🗺️ Roadmap

- [x] BMP file parsing
- [ ] JPEG support
- [ ] PNG support
- [ ] WEBP support
- [ ] Image rendering (simple GUI viewer)

---

## ❓ Why This Exists

This project is built for learning purposes. It's an exercise in understanding how image formats work under the hood, parsing file headers, pixel data, and eventually rendering them. While `BufferedImage` and related APIs exist, the goal here is to go beyond abstraction and explore the raw image data processing pipeline.

---

## 🔮 Future

This is a work in progress and an evolving codebase. The direction of development may shift depending on what is learned and discovered along the way. We'll see how and where the project goes.

---
## 📄 References
- [BMP file format](https://en.wikipedia.org/wiki/BMP_file_format)
- [Bitmap Header Types](https://learn.microsoft.com/en-us/windows/win32/gdi/bitmap-header-types)

---

## 📄 License

[MIT License](https://opensource.org/license/mit)
