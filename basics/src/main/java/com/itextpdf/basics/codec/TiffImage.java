package com.itextpdf.basics.codec;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.color.IccProfile;
import com.itextpdf.basics.image.CcittImage;
import com.itextpdf.basics.image.Image;
import com.itextpdf.basics.image.JpegImage;
import com.itextpdf.basics.image.RawImage;
import com.itextpdf.basics.io.RandomAccessFileOrArray;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;

public class TiffImage {

    /**
     * Gets the number of pages the TIFF document has.
     *
     * @param s the file source
     * @return the number of pages
     */
    public static int getNumberOfPages(RandomAccessFileOrArray s) throws PdfException {
        try {
            return TIFFDirectory.getNumDirectories(s);
        } catch (Exception e) {
            throw new PdfException(PdfException.CannotGetTiffNumberOfPages, e);
        }
    }

    static int getDpi(TIFFField fd, int resolutionUnit) {
        if (fd == null)
            return 0;
        long res[] = fd.getAsRational(0);
        float frac = (float) res[0] / (float) res[1];
        int dpi = 0;
        switch (resolutionUnit) {
            case TIFFConstants.RESUNIT_INCH:
            case TIFFConstants.RESUNIT_NONE:
                dpi = (int) (frac + 0.5);
                break;
            case TIFFConstants.RESUNIT_CENTIMETER:
                dpi = (int) (frac * 2.54 + 0.5);
                break;
        }
        return dpi;
    }

    public static Image getTiffImage(RandomAccessFileOrArray s, boolean recoverFromImageError, int page, boolean direct) throws PdfException {
        if (page < 1)
            throw new PdfException(PdfException.PageNumberMustBeGtEq1);
        try {
            TIFFDirectory dir = new TIFFDirectory(s, page - 1);
            if (dir.isTagPresent(TIFFConstants.TIFFTAG_TILEWIDTH))
                throw new PdfException(PdfException.TilesAreNotSupported);
            int compression = (int) dir.getFieldAsLong(TIFFConstants.TIFFTAG_COMPRESSION);
            switch (compression) {
                case TIFFConstants.COMPRESSION_CCITTRLEW:
                case TIFFConstants.COMPRESSION_CCITTRLE:
                case TIFFConstants.COMPRESSION_CCITTFAX3:
                case TIFFConstants.COMPRESSION_CCITTFAX4:
                    break;
                default:
                    return getTiffImageColor(dir, s);
            }
            float rotation = 0;
            if (dir.isTagPresent(TIFFConstants.TIFFTAG_ORIENTATION)) {
                int rot = (int) dir.getFieldAsLong(TIFFConstants.TIFFTAG_ORIENTATION);
                if (rot == TIFFConstants.ORIENTATION_BOTRIGHT || rot == TIFFConstants.ORIENTATION_BOTLEFT)
                    rotation = (float) Math.PI;
                else if (rot == TIFFConstants.ORIENTATION_LEFTTOP || rot == TIFFConstants.ORIENTATION_LEFTBOT)
                    rotation = (float) (Math.PI / 2.0);
                else if (rot == TIFFConstants.ORIENTATION_RIGHTTOP || rot == TIFFConstants.ORIENTATION_RIGHTBOT)
                    rotation = -(float) (Math.PI / 2.0);
            }

            Image img = null;
            long tiffT4Options = 0;
            long tiffT6Options = 0;
            int fillOrder = 1;
            int h = (int) dir.getFieldAsLong(TIFFConstants.TIFFTAG_IMAGELENGTH);
            int w = (int) dir.getFieldAsLong(TIFFConstants.TIFFTAG_IMAGEWIDTH);
            float XYRatio = 0;
            int resolutionUnit = TIFFConstants.RESUNIT_INCH;
            if (dir.isTagPresent(TIFFConstants.TIFFTAG_RESOLUTIONUNIT))
                resolutionUnit = (int) dir.getFieldAsLong(TIFFConstants.TIFFTAG_RESOLUTIONUNIT);
            int dpiX = getDpi(dir.getField(TIFFConstants.TIFFTAG_XRESOLUTION), resolutionUnit);
            int dpiY = getDpi(dir.getField(TIFFConstants.TIFFTAG_YRESOLUTION), resolutionUnit);
            if (resolutionUnit == TIFFConstants.RESUNIT_NONE) {
                if (dpiY != 0)
                    XYRatio = (float) dpiX / (float) dpiY;
                dpiX = 0;
                dpiY = 0;
            }
            int rowsStrip = h;
            if (dir.isTagPresent(TIFFConstants.TIFFTAG_ROWSPERSTRIP))
                rowsStrip = (int) dir.getFieldAsLong(TIFFConstants.TIFFTAG_ROWSPERSTRIP);
            if (rowsStrip <= 0 || rowsStrip > h)
                rowsStrip = h;
            long offset[] = getArrayLongShort(dir, TIFFConstants.TIFFTAG_STRIPOFFSETS);
            long size[] = getArrayLongShort(dir, TIFFConstants.TIFFTAG_STRIPBYTECOUNTS);
            if ((size == null || (size.length == 1 && (size[0] == 0 || size[0] + offset[0] > s.length()))) && h == rowsStrip) { // some TIFF producers are really lousy, so...
                size = new long[]{s.length() - (int) offset[0]};
            }
            boolean reverse = false;
            TIFFField fillOrderField = dir.getField(TIFFConstants.TIFFTAG_FILLORDER);
            if (fillOrderField != null)
                fillOrder = fillOrderField.getAsInt(0);
            reverse = (fillOrder == TIFFConstants.FILLORDER_LSB2MSB);
            int params = 0;
            if (dir.isTagPresent(TIFFConstants.TIFFTAG_PHOTOMETRIC)) {
                long photo = dir.getFieldAsLong(TIFFConstants.TIFFTAG_PHOTOMETRIC);
                if (photo == TIFFConstants.PHOTOMETRIC_MINISBLACK)
                    params |= CcittImage.CCITT_BLACKIS1;
            }
            int imagecomp = 0;
            switch (compression) {
                case TIFFConstants.COMPRESSION_CCITTRLEW:
                case TIFFConstants.COMPRESSION_CCITTRLE:
                    imagecomp = CcittImage.CCITTG3_1D;
                    params |= CcittImage.CCITT_ENCODEDBYTEALIGN | CcittImage.CCITT_ENDOFBLOCK;
                    break;
                case TIFFConstants.COMPRESSION_CCITTFAX3:
                    imagecomp = CcittImage.CCITTG3_1D;
                    params |= CcittImage.CCITT_ENDOFLINE | CcittImage.CCITT_ENDOFBLOCK;
                    TIFFField t4OptionsField = dir.getField(TIFFConstants.TIFFTAG_GROUP3OPTIONS);
                    if (t4OptionsField != null) {
                        tiffT4Options = t4OptionsField.getAsLong(0);
                        if ((tiffT4Options & TIFFConstants.GROUP3OPT_2DENCODING) != 0)
                            imagecomp = CcittImage.CCITTG3_2D;
                        if ((tiffT4Options & TIFFConstants.GROUP3OPT_FILLBITS) != 0)
                            params |= CcittImage.CCITT_ENCODEDBYTEALIGN;
                    }
                    break;
                case TIFFConstants.COMPRESSION_CCITTFAX4:
                    imagecomp = CcittImage.CCITTG4;
                    TIFFField t6OptionsField = dir.getField(TIFFConstants.TIFFTAG_GROUP4OPTIONS);
                    if (t6OptionsField != null)
                        tiffT6Options = t6OptionsField.getAsLong(0);
                    break;
            }
            if (direct && rowsStrip == h) { //single strip, direct
                byte im[] = new byte[(int) size[0]];
                s.seek(offset[0]);
                s.readFully(im);
                img = Image.getInstance(w, h, false, imagecomp, params, im);
                img.setInverted(true);
            } else {
                int rowsLeft = h;
                CCITTG4Encoder g4 = new CCITTG4Encoder(w);
                for (int k = 0; k < offset.length; ++k) {
                    byte im[] = new byte[(int) size[k]];
                    s.seek(offset[k]);
                    s.readFully(im);
                    int height = Math.min(rowsStrip, rowsLeft);
                    TIFFFaxDecoder decoder = new TIFFFaxDecoder(fillOrder, w, height);
                    decoder.setRecoverFromImageError(recoverFromImageError);
                    byte outBuf[] = new byte[(w + 7) / 8 * height];
                    switch (compression) {
                        case TIFFConstants.COMPRESSION_CCITTRLEW:
                        case TIFFConstants.COMPRESSION_CCITTRLE:
                            decoder.decode1D(outBuf, im, 0, height);
                            g4.fax4Encode(outBuf, height);
                            break;
                        case TIFFConstants.COMPRESSION_CCITTFAX3:
                            try {
                                decoder.decode2D(outBuf, im, 0, height, tiffT4Options);
                            } catch (RuntimeException e) {
                                // let's flip the fill bits and try again...
                                tiffT4Options ^= TIFFConstants.GROUP3OPT_FILLBITS;
                                try {
                                    decoder.decode2D(outBuf, im, 0, height, tiffT4Options);
                                } catch (RuntimeException e2) {
                                    if (!recoverFromImageError)
                                        throw e;
                                    if (rowsStrip == 1)
                                        throw e;
                                    // repeat of reading the tiff directly (the if section of this if else structure)
                                    // copy pasted to avoid making a method with 10 parameters
                                    im = new byte[(int) size[0]];
                                    s.seek(offset[0]);
                                    s.readFully(im);
                                    img = Image.getInstance(w, h, false, imagecomp, params, im);
                                    img.setInverted(true);
                                    img.setDpi(dpiX, dpiY);
                                    img.setOriginalType(Image.TIFF);
                                    if (rotation != 0)
                                        img.setRotation(rotation);
                                    return img;
                                }
                            }
                            g4.fax4Encode(outBuf, height);
                            break;
                        case TIFFConstants.COMPRESSION_CCITTFAX4:
                            try {
                                decoder.decodeT6(outBuf, im, 0, height, tiffT6Options);
                            } catch (PdfException e) {
                                if (!recoverFromImageError) {
                                    throw e;
                                }
                            }

                            g4.fax4Encode(outBuf, height);
                            break;
                    }
                    rowsLeft -= rowsStrip;
                }
                byte g4pic[] = g4.close();
                img = Image.getInstance(w, h, false, CcittImage.CCITTG4, params & CcittImage.CCITT_BLACKIS1, g4pic);
            }
            img.setDpi(dpiX, dpiY);
            if (dir.isTagPresent(TIFFConstants.TIFFTAG_ICCPROFILE)) {
                try {
                    TIFFField fd = dir.getField(TIFFConstants.TIFFTAG_ICCPROFILE);
                    IccProfile icc_prof = IccProfile.getInstance(fd.getAsBytes());
                    if (icc_prof.getNumComponents() == 1)
                        img.setProfile(icc_prof);
                } catch (RuntimeException e) {
                    //empty
                }
            }
            img.setOriginalType(Image.TIFF);
            if (rotation != 0)
                img.setRotation(rotation);
            return img;
        } catch (Exception e) {
            throw new PdfException(PdfException.CannotReadTiffImage);
        }
    }

    public static Image getTiffImage(RandomAccessFileOrArray s, boolean recoverFromImageError, int page) throws PdfException {
        return getTiffImage(s, recoverFromImageError, page, false);
    }

    /**
     * Reads a page from a TIFF image. Direct mode is not used.
     *
     * @param s    the file source
     * @param page the page to get. The first page is 1
     * @return the <CODE>Image</CODE>
     */
    public static Image getTiffImage(RandomAccessFileOrArray s, int page) throws PdfException {
        return getTiffImage(s, page, false);
    }

    /**
     * Reads a page from a TIFF image.
     *
     * @param s      the file source
     * @param page   the page to get. The first page is 1
     * @param direct for single strip, CCITT images, generate the image
     *               by direct byte copying. It's faster but may not work
     *               every time
     * @return the <CODE>Image</CODE>
     */
    public static Image getTiffImage(RandomAccessFileOrArray s, int page, boolean direct) throws PdfException {
        return getTiffImage(s, false, page, direct);
    }

    protected static Image getTiffImageColor(TIFFDirectory dir, RandomAccessFileOrArray s) throws PdfException {
        try {
            int compression = (int) dir.getFieldAsLong(TIFFConstants.TIFFTAG_COMPRESSION);
            int predictor = 1;
            TIFFLZWDecoder lzwDecoder = null;
            switch (compression) {
                case TIFFConstants.COMPRESSION_NONE:
                case TIFFConstants.COMPRESSION_LZW:
                case TIFFConstants.COMPRESSION_PACKBITS:
                case TIFFConstants.COMPRESSION_DEFLATE:
                case TIFFConstants.COMPRESSION_ADOBE_DEFLATE:
                case TIFFConstants.COMPRESSION_OJPEG:
                case TIFFConstants.COMPRESSION_JPEG:
                    break;
                default:
                    throw new PdfException(PdfException.Compression1IsNotSupported).setMessageParams(compression);
            }
            int photometric = (int) dir.getFieldAsLong(TIFFConstants.TIFFTAG_PHOTOMETRIC);
            switch (photometric) {
                case TIFFConstants.PHOTOMETRIC_MINISWHITE:
                case TIFFConstants.PHOTOMETRIC_MINISBLACK:
                case TIFFConstants.PHOTOMETRIC_RGB:
                case TIFFConstants.PHOTOMETRIC_SEPARATED:
                case TIFFConstants.PHOTOMETRIC_PALETTE:
                    break;
                default:
                    if (compression != TIFFConstants.COMPRESSION_OJPEG && compression != TIFFConstants.COMPRESSION_JPEG)
                        throw new PdfException(PdfException.Photometric1IsNotSupported).setMessageParams(photometric);
            }
            float rotation = 0;
            if (dir.isTagPresent(TIFFConstants.TIFFTAG_ORIENTATION)) {
                int rot = (int) dir.getFieldAsLong(TIFFConstants.TIFFTAG_ORIENTATION);
                if (rot == TIFFConstants.ORIENTATION_BOTRIGHT || rot == TIFFConstants.ORIENTATION_BOTLEFT)
                    rotation = (float) Math.PI;
                else if (rot == TIFFConstants.ORIENTATION_LEFTTOP || rot == TIFFConstants.ORIENTATION_LEFTBOT)
                    rotation = (float) (Math.PI / 2.0);
                else if (rot == TIFFConstants.ORIENTATION_RIGHTTOP || rot == TIFFConstants.ORIENTATION_RIGHTBOT)
                    rotation = -(float) (Math.PI / 2.0);
            }
            if (dir.isTagPresent(TIFFConstants.TIFFTAG_PLANARCONFIG)
                    && dir.getFieldAsLong(TIFFConstants.TIFFTAG_PLANARCONFIG) == TIFFConstants.PLANARCONFIG_SEPARATE)
                throw new PdfException(PdfException.PlanarImagesAreNotSupported);
            int extraSamples = 0;
            if (dir.isTagPresent(TIFFConstants.TIFFTAG_EXTRASAMPLES))
                extraSamples = 1;
            int samplePerPixel = 1;
            if (dir.isTagPresent(TIFFConstants.TIFFTAG_SAMPLESPERPIXEL)) // 1,3,4
                samplePerPixel = (int) dir.getFieldAsLong(TIFFConstants.TIFFTAG_SAMPLESPERPIXEL);
            int bitsPerSample = 1;
            if (dir.isTagPresent(TIFFConstants.TIFFTAG_BITSPERSAMPLE))
                bitsPerSample = (int) dir.getFieldAsLong(TIFFConstants.TIFFTAG_BITSPERSAMPLE);
            switch (bitsPerSample) {
                case 1:
                case 2:
                case 4:
                case 8:
                    break;
                default:
                    throw new PdfException(PdfException.BitsPerSample1IsNotSupported).setMessageParams(bitsPerSample);
            }
            Image img;

            int h = (int) dir.getFieldAsLong(TIFFConstants.TIFFTAG_IMAGELENGTH);
            int w = (int) dir.getFieldAsLong(TIFFConstants.TIFFTAG_IMAGEWIDTH);
            int dpiX;
            int dpiY;
            int resolutionUnit = TIFFConstants.RESUNIT_INCH;
            if (dir.isTagPresent(TIFFConstants.TIFFTAG_RESOLUTIONUNIT))
                resolutionUnit = (int) dir.getFieldAsLong(TIFFConstants.TIFFTAG_RESOLUTIONUNIT);
            dpiX = getDpi(dir.getField(TIFFConstants.TIFFTAG_XRESOLUTION), resolutionUnit);
            dpiY = getDpi(dir.getField(TIFFConstants.TIFFTAG_YRESOLUTION), resolutionUnit);
            int fillOrder = 1;
            TIFFField fillOrderField = dir.getField(TIFFConstants.TIFFTAG_FILLORDER);
            if (fillOrderField != null)
                fillOrder = fillOrderField.getAsInt(0);
            boolean reverse = (fillOrder == TIFFConstants.FILLORDER_LSB2MSB);
            int rowsStrip = h;
            if (dir.isTagPresent(TIFFConstants.TIFFTAG_ROWSPERSTRIP)) //another hack for broken tiffs
                rowsStrip = (int) dir.getFieldAsLong(TIFFConstants.TIFFTAG_ROWSPERSTRIP);
            if (rowsStrip <= 0 || rowsStrip > h)
                rowsStrip = h;
            long offset[] = getArrayLongShort(dir, TIFFConstants.TIFFTAG_STRIPOFFSETS);
            long size[] = getArrayLongShort(dir, TIFFConstants.TIFFTAG_STRIPBYTECOUNTS);
            if ((size == null || (size.length == 1 && (size[0] == 0 || size[0] + offset[0] > s.length()))) && h == rowsStrip) { // some TIFF producers are really lousy, so...
                size = new long[]{s.length() - (int) offset[0]};
            }
            if (compression == TIFFConstants.COMPRESSION_LZW || compression == TIFFConstants.COMPRESSION_DEFLATE || compression == TIFFConstants.COMPRESSION_ADOBE_DEFLATE) {
                TIFFField predictorField = dir.getField(TIFFConstants.TIFFTAG_PREDICTOR);
                if (predictorField != null) {
                    predictor = predictorField.getAsInt(0);
                    if (predictor != 1 && predictor != 2) {
                        throw new PdfException(PdfException.IllegalValueForPredictorInTiffFile);
                    }
                    if (predictor == 2 && bitsPerSample != 8) {
                        throw new PdfException(PdfException._1BitSamplesAreNotSupportedForHorizontalDifferencingPredictor).setMessageParams(bitsPerSample);
                    }
                }
            }
            if (compression == TIFFConstants.COMPRESSION_LZW) {
                lzwDecoder = new TIFFLZWDecoder(w, predictor, samplePerPixel);
            }
            int rowsLeft = h;
            ByteArrayOutputStream stream = null;
            ByteArrayOutputStream mstream = null;
            DeflaterOutputStream zip = null;
            DeflaterOutputStream mzip = null;
            if (extraSamples > 0) {
                mstream = new ByteArrayOutputStream();
                mzip = new DeflaterOutputStream(mstream);
            }

            CCITTG4Encoder g4 = null;
            if (bitsPerSample == 1 && samplePerPixel == 1 && photometric != TIFFConstants.PHOTOMETRIC_PALETTE) {
                g4 = new CCITTG4Encoder(w);
            } else {
                stream = new ByteArrayOutputStream();
                if (compression != TIFFConstants.COMPRESSION_OJPEG && compression != TIFFConstants.COMPRESSION_JPEG)
                    zip = new DeflaterOutputStream(stream);
            }
            if (compression == TIFFConstants.COMPRESSION_OJPEG) {

                // Assume that the TIFFTAG_JPEGIFBYTECOUNT tag is optional, since it's obsolete and
                // is often missing

                if ((!dir.isTagPresent(TIFFConstants.TIFFTAG_JPEGIFOFFSET))) {
                    throw new PdfException(PdfException.MissingTagSForOjpegCompression);
                }
                int jpegOffset = (int) dir.getFieldAsLong(TIFFConstants.TIFFTAG_JPEGIFOFFSET);
                int jpegLength = (int) s.length() - jpegOffset;

                if (dir.isTagPresent(TIFFConstants.TIFFTAG_JPEGIFBYTECOUNT)) {
                    jpegLength = (int) dir.getFieldAsLong(TIFFConstants.TIFFTAG_JPEGIFBYTECOUNT) +
                            (int) size[0];
                }

                byte[] jpeg = new byte[Math.min(jpegLength, (int) s.length() - jpegOffset)];

                int posFilePointer = (int) s.getPosition();
                posFilePointer += jpegOffset;
                s.seek(posFilePointer);
                s.readFully(jpeg);
                img = new JpegImage(jpeg);
            } else if (compression == TIFFConstants.COMPRESSION_JPEG) {
                if (size.length > 1)
                    throw new PdfException(PdfException.CompressionJpegIsOnlySupportedWithASingleStripThisImageHas1Strips).setMessageParams(size.length);
                byte[] jpeg = new byte[(int) size[0]];
                s.seek(offset[0]);
                s.readFully(jpeg);
                // if quantization and/or Huffman tables are stored separately in the tiff,
                // we need to add them to the jpeg data
                TIFFField jpegtables = dir.getField(TIFFConstants.TIFFTAG_JPEGTABLES);
                if (jpegtables != null) {
                    byte[] temp = jpegtables.getAsBytes();
                    int tableoffset = 0;
                    int tablelength = temp.length;
                    // remove FFD8 from start
                    if (temp[0] == (byte) 0xFF && temp[1] == (byte) 0xD8) {
                        tableoffset = 2;
                        tablelength -= 2;
                    }
                    // remove FFD9 from end
                    if (temp[temp.length - 2] == (byte) 0xFF && temp[temp.length - 1] == (byte) 0xD9)
                        tablelength -= 2;
                    byte[] tables = new byte[tablelength];
                    System.arraycopy(temp, tableoffset, tables, 0, tablelength);
                    // TODO insert after JFIF header, instead of at the start
                    byte[] jpegwithtables = new byte[jpeg.length + tables.length];
                    System.arraycopy(jpeg, 0, jpegwithtables, 0, 2);
                    System.arraycopy(tables, 0, jpegwithtables, 2, tables.length);
                    System.arraycopy(jpeg, 2, jpegwithtables, tables.length + 2, jpeg.length - 2);
                    jpeg = jpegwithtables;
                }
                img = new JpegImage(jpeg);
                if (photometric == TIFFConstants.PHOTOMETRIC_RGB) {
                    img.setColorTransform(0);
                }
            } else {
                for (int k = 0; k < offset.length; ++k) {
                    byte im[] = new byte[(int) size[k]];
                    s.seek(offset[k]);
                    s.readFully(im);
                    int height = Math.min(rowsStrip, rowsLeft);
                    byte outBuf[] = null;
                    if (compression != TIFFConstants.COMPRESSION_NONE)
                        outBuf = new byte[(w * bitsPerSample * samplePerPixel + 7) / 8 * height];
                    if (reverse)
                        TIFFFaxDecoder.reverseBits(im);
                    switch (compression) {
                        case TIFFConstants.COMPRESSION_DEFLATE:
                        case TIFFConstants.COMPRESSION_ADOBE_DEFLATE:
                            inflate(im, outBuf);
                            applyPredictor(outBuf, predictor, w, height, samplePerPixel);
                            break;
                        case TIFFConstants.COMPRESSION_NONE:
                            outBuf = im;
                            break;
                        case TIFFConstants.COMPRESSION_PACKBITS:
                            decodePackbits(im, outBuf);
                            break;
                        case TIFFConstants.COMPRESSION_LZW:
                            lzwDecoder.decode(im, outBuf, height);
                            break;
                    }
                    if (bitsPerSample == 1 && samplePerPixel == 1 && photometric != TIFFConstants.PHOTOMETRIC_PALETTE) {
                        g4.fax4Encode(outBuf, height);
                    } else {
                        if (extraSamples > 0)
                            ProcessExtraSamples(zip, mzip, outBuf, samplePerPixel, bitsPerSample, w, height);
                        else
                            zip.write(outBuf);
                    }
                    rowsLeft -= rowsStrip;
                }
                if (bitsPerSample == 1 && samplePerPixel == 1 && photometric != TIFFConstants.PHOTOMETRIC_PALETTE) {
                    img = Image.getInstance(w, h, false, CcittImage.CCITTG4,
                            photometric == TIFFConstants.PHOTOMETRIC_MINISBLACK ? CcittImage.CCITT_BLACKIS1 : 0, g4.close());
                } else {
                    zip.close();
                    img = new RawImage(w, h, samplePerPixel - extraSamples, bitsPerSample, stream.toByteArray());
                    img.setDeflated(true);
                }
            }
            img.setDpi(dpiX, dpiY);
            if (compression != TIFFConstants.COMPRESSION_OJPEG && compression != TIFFConstants.COMPRESSION_JPEG) {
                if (dir.isTagPresent(TIFFConstants.TIFFTAG_ICCPROFILE)) {
                    try {
                        TIFFField fd = dir.getField(TIFFConstants.TIFFTAG_ICCPROFILE);
                        IccProfile icc_prof = IccProfile.getInstance(fd.getAsBytes());
                        if (samplePerPixel - extraSamples == icc_prof.getNumComponents())
                            img.setProfile(icc_prof);
                    } catch (RuntimeException e) {
                        //empty
                    }
                }
                if (dir.isTagPresent(TIFFConstants.TIFFTAG_COLORMAP)) {
                    TIFFField fd = dir.getField(TIFFConstants.TIFFTAG_COLORMAP);
                    char rgb[] = fd.getAsChars();
                    byte palette[] = new byte[rgb.length];
                    int gColor = rgb.length / 3;
                    int bColor = gColor * 2;
                    for (int k = 0; k < gColor; ++k) {
                        palette[k * 3] = (byte) (rgb[k] >>> 8);
                        palette[k * 3 + 1] = (byte) (rgb[k + gColor] >>> 8);
                        palette[k * 3 + 2] = (byte) (rgb[k + bColor] >>> 8);
                    }
                    // Colormap components are supposed to go from 0 to 655535 but,
                    // as usually, some tiff producers just put values from 0 to 255.
                    // Let's check for these broken tiffs.
                    boolean colormapBroken = true;
                    for (int k = 0; k < palette.length; ++k) {
                        if (palette[k] != 0) {
                            colormapBroken = false;
                            break;
                        }
                    }
                    if (colormapBroken) {
                        for (int k = 0; k < gColor; ++k) {
                            palette[k * 3] = (byte) rgb[k];
                            palette[k * 3 + 1] = (byte) rgb[k + gColor];
                            palette[k * 3 + 2] = (byte) rgb[k + bColor];
                        }
                    }
                    img.setAdditional(new Image.Indexed(gColor - 1, palette));
                }
                img.setOriginalType(Image.TIFF);
            }
            if (photometric == TIFFConstants.PHOTOMETRIC_MINISWHITE)
                img.setInverted(true);
            if (rotation != 0)
                img.setRotation(rotation);
            if (extraSamples > 0) {
                mzip.close();
                Image mimg = Image.getInstance(w, h, 1, bitsPerSample, mstream.toByteArray());
                mimg.makeMask();
                mimg.setDeflated(true);
                img.setImageMask(mimg);
            }
            return img;
        } catch (Exception e) {
            throw new PdfException(PdfException.CannotGetTiffImageColor);
        }
    }

    static Image ProcessExtraSamples(DeflaterOutputStream zip, DeflaterOutputStream mzip, byte[] outBuf, int samplePerPixel, int bitsPerSample, int width, int height) throws IOException, PdfException {
        if (bitsPerSample == 8) {
            byte[] mask = new byte[width * height];
            int mptr = 0;
            int optr = 0;
            int total = width * height * samplePerPixel;
            for (int k = 0; k < total; k += samplePerPixel) {
                for (int s = 0; s < samplePerPixel - 1; ++s) {
                    outBuf[optr++] = outBuf[k + s];
                }
                mask[mptr++] = outBuf[k + samplePerPixel - 1];
            }
            zip.write(outBuf, 0, optr);
            mzip.write(mask, 0, mptr);
        } else
            throw new PdfException(PdfException.ExtraSamplesAreNotSupported);
        return null;
    }

    static long[] getArrayLongShort(TIFFDirectory dir, int tag) {
        TIFFField field = dir.getField(tag);
        if (field == null)
            return null;
        long offset[];
        if (field.getType() == TIFFField.TIFF_LONG)
            offset = field.getAsLongs();
        else { // must be short
            char temp[] = field.getAsChars();
            offset = new long[temp.length];
            for (int k = 0; k < temp.length; ++k)
                offset[k] = temp[k];
        }
        return offset;
    }

    // Uncompress packbits compressed image data.
    public static void decodePackbits(byte data[], byte[] dst) {
        int srcCount = 0, dstCount = 0;
        byte repeat, b;

        try {
            while (dstCount < dst.length) {
                b = data[srcCount++];
                if (b >= 0 && b <= 127) {
                    // literal run packet
                    for (int i = 0; i < (b + 1); i++) {
                        dst[dstCount++] = data[srcCount++];
                    }

                } else if (b <= -1 && b >= -127) {
                    // 2 byte encoded run packet
                    repeat = data[srcCount++];
                    for (int i = 0; i < (-b + 1); i++) {
                        dst[dstCount++] = repeat;
                    }
                } else {
                    // no-op packet. Do nothing
                    srcCount++;
                }
            }
        } catch (Exception e) {
            // do nothing
        }
    }

    public static void inflate(byte[] deflated, byte[] inflated) throws PdfException {
        Inflater inflater = new Inflater();
        inflater.setInput(deflated);
        try {
            inflater.inflate(inflated);
        } catch (DataFormatException dfe) {
            throw new PdfException(PdfException.CannotInflateTiffImage);
        }
    }

    public static void applyPredictor(byte[] uncompData, int predictor, int w, int h, int samplesPerPixel) {
        if (predictor != 2)
            return;
        int count;
        for (int j = 0; j < h; j++) {
            count = samplesPerPixel * (j * w + 1);
            for (int i = samplesPerPixel; i < w * samplesPerPixel; i++) {
                uncompData[count] += uncompData[count - samplesPerPixel];
                count++;
            }
        }
    }

}