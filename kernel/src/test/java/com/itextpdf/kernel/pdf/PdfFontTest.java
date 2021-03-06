package com.itextpdf.kernel.pdf;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.font.CidFont;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.font.FontEncoding;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.TrueTypeCollection;
import com.itextpdf.io.font.TrueTypeFont;
import com.itextpdf.io.font.Type1Font;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfTrueTypeFont;
import com.itextpdf.kernel.font.PdfType0Font;
import com.itextpdf.kernel.font.PdfType1Font;
import com.itextpdf.kernel.font.PdfType3Font;
import com.itextpdf.kernel.font.Type3Glyph;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfFontTest extends ExtendedITextTest {
    public static final int PageCount = 1;
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfFontTest/";
    public static final String fontsFolder = "./src/test/resources/com/itextpdf/kernel/pdf/fonts/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfFontTest/";

    static final String author = "Alexander Chingarev";
    static final String creator = "iText 6";
    @SuppressWarnings("unused")
    static final String pangramme = "Amazingly few discothegues provide jukeboxes" +
            "but it now while sayingly ABEFGHJKNOPQRSTUWYZ?";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void createDocumentWithKozmin() throws IOException, InterruptedException {
        String filename = destinationFolder + "DocumentWithKozmin.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithKozmin.pdf";
        String title = "Type 0 test";

        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);

        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        PdfFont type0Font = PdfFontFactory.createFont("KozMinPro-Regular", "UniJIS-UCS2-H");
        Assert.assertTrue("Type0Font expected", type0Font instanceof PdfType0Font);
        Assert.assertTrue("CidFont expected", type0Font.getFontProgram() instanceof CidFont);
        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(type0Font, 72)
                .showText("Hello World")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();
        page.flush();
        pdfDoc.close();
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithStSongUni() throws IOException, InterruptedException {
        String filename = destinationFolder + "DocumentWithStSongUni.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithStSongUni.pdf";
        String title = "Type0 test";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename).setCompressionLevel(CompressionConstants.NO_COMPRESSION));

        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        PdfFont type0Font = PdfFontFactory.createFont("STSong-Light", "UniGB-UTF16-H");
        Assert.assertTrue("Type0Font expected", type0Font instanceof PdfType0Font);
        Assert.assertTrue("CidFont expected", type0Font.getFontProgram() instanceof CidFont);
        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(type0Font, 72)
                .showText("Hello World")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();
        page.flush();
        pdfDoc.close();
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }


    @Test
    public void createDocumentWithStSong() throws IOException, InterruptedException {
        String filename = destinationFolder + "DocumentWithStSong.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithStSong.pdf";
        String title = "Type0 test";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename).setCompressionLevel(CompressionConstants.NO_COMPRESSION));

        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        PdfFont type0Font = PdfFontFactory.createFont("STSong-Light", "Adobe-GB1-4");
        Assert.assertTrue("Type0Font expected", type0Font instanceof PdfType0Font);
        Assert.assertTrue("CidFont expected", type0Font.getFontProgram() instanceof CidFont);
        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(type0Font, 72)
                .showText("Hello World")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();
        page.flush();
        pdfDoc.close();
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithTrueTypeAsType0() throws IOException, InterruptedException {
        String filename = destinationFolder + "DocumentWithTrueTypeAsType0.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithTrueTypeAsType0.pdf";
        String title = "Type0 test";

        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);

        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        String font = fontsFolder + "abserif4_5.ttf";
        PdfFont type0Font = PdfFontFactory.createFont(font, "Identity-H");
//        type0Font.setSubset(false);
        Assert.assertTrue("PdfType0Font expected", type0Font instanceof PdfType0Font);
        Assert.assertTrue("TrueType expected", type0Font.getFontProgram() instanceof TrueTypeFont);
        PdfPage page = pdfDoc.addNewPage();
        new PdfCanvas(page)
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(type0Font, 72)
                .showText("Hello World")
                .endText()
                .restoreState()
                .rectangle(100, 500, 100, 100).fill()
                .release();

//        new PdfCanvas(page)
//                .saveState()
//                .beginText()
//                .moveText(36, 650)
//                .setFontAndSize(type0Font, 12)
//                .showText(pangramme)
//                .endText()
//                .restoreState()
//                .release();
        page.flush();

        byte[] ttf = StreamUtil.inputStreamToArray(new FileInputStream(font));
        type0Font = PdfFontFactory.createFont(ttf, "Identity-H");
        Assert.assertTrue("PdfType0Font expected", type0Font instanceof PdfType0Font);
        Assert.assertTrue("TrueType expected", type0Font.getFontProgram() instanceof TrueTypeFont);
        page = pdfDoc.addNewPage();
        new PdfCanvas(page)
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(type0Font, 72)
                .showText("Hello World")
                .endText()
                .restoreState()
                .rectangle(100, 500, 100, 100).fill()
                .release();
        page.flush();
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithType3Font() throws IOException, InterruptedException {
        String filename = destinationFolder + "DocumentWithType3Font.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithType3Font.pdf";
        String testString = "A A A A E E E ~ \u00E9"; // A A A A E E E ~ é

        //writing type3 font characters
        String title = "Type3 font iText 6 Document";

        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);

        PdfType3Font type3 = PdfFontFactory.createType3Font(pdfDoc, false);
        Type3Glyph a = type3.addGlyph('A', 600, 0, 0, 600, 700);
        a.setLineWidth(100);
        a.moveTo(5, 5);
        a.lineTo(300, 695);
        a.lineTo(595, 5);
        a.closePathFillStroke();

        Type3Glyph space = type3.addGlyph(' ', 600, 0, 0, 600, 700);
        space.setLineWidth(10);
        space.closePathFillStroke();

        Type3Glyph e = type3.addGlyph('E', 600, 0, 0, 600, 700);
        e.setLineWidth(100);
        e.moveTo(595, 5);
        e.lineTo(5, 5);
        e.lineTo(300, 350);
        e.lineTo(5, 695);
        e.lineTo(595, 695);
        e.stroke();

        Type3Glyph tilde = type3.addGlyph('~', 600, 0, 0, 600, 700);
        tilde.setLineWidth(100);
        tilde.moveTo(595, 5);
        tilde.lineTo(5, 5);
        tilde.stroke();

        Type3Glyph symbol233 = type3.addGlyph('\u00E9', 600, 0, 0, 600, 700);
        symbol233.setLineWidth(100);
        symbol233.moveTo(540, 5);
        symbol233.lineTo(5, 340);
        symbol233.stroke();

        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);

        for (int i = 0; i < PageCount; i++) {
            PdfPage page = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            canvas.saveState()
                    .beginText()
                    .setFontAndSize(type3, 12)
                    .moveText(50, 800)
                    .showText(testString)
                    .endText();
            page.flush();
        }
        pdfDoc.close();

        // reading and comparing text
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithHelvetica() throws IOException, InterruptedException {
        String filename = destinationFolder + "DocumentWithHelvetica.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithHelvetica.pdf";
        String title = "Type3 test";

        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);

        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);

        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        PdfFont pdfFont = PdfFontFactory.createFont(FontConstants.HELVETICA);
        Assert.assertTrue("PdfType1Font expected", pdfFont instanceof PdfType1Font);
        canvas.saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfFont, 72)
                .showText("Hello World")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();
        page.flush();
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithHelveticaOblique() throws IOException, InterruptedException {
        String filename = destinationFolder + "DocumentWithHelveticaOblique.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithHelveticaOblique.pdf";
        String title = "Empty iText 6 Document";

        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);

        PdfFont pdfFont = PdfFontFactory.createFont(FontConstants.HELVETICA_OBLIQUE);
        Assert.assertTrue("PdfType1Font expected", pdfFont instanceof PdfType1Font);

        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfFont, 72)
                .showText("Hello World")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();
        page.flush();
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithHelveticaBoldOblique() throws IOException, InterruptedException {
        String filename = destinationFolder + "DocumentWithHelveticaBoldOblique.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithHelveticaBoldOblique.pdf";

        String title = "Empty iText 6 Document";

        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);

        PdfFont pdfFont = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLDOBLIQUE);
        Assert.assertTrue("PdfType1Font expected", pdfFont instanceof PdfType1Font);

        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfFont, 72)
                .showText("Hello World")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();
        page.flush();
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithCourierBold() throws IOException, InterruptedException {
        String filename = destinationFolder + "DocumentWithCourierBold.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithCourierBold.pdf";
        String title = "Empty iText 6 Document";

        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);

        PdfFont pdfFont = PdfFontFactory.createFont(FontConstants.COURIER_BOLD);
        Assert.assertTrue("PdfType1Font expected", pdfFont instanceof PdfType1Font);

        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfFont, 72)
                .showText("Hello World")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();
        page.flush();
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithType1FontAfm() throws IOException, InterruptedException {
        String filename = destinationFolder + "DocumentWithCMR10Afm.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithCMR10Afm.pdf";
        String title = "Empty iText 6 Document";

        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        PdfFont pdfType1Font = PdfFontFactory.createFont(FontProgramFactory.createType1Font(fontsFolder + "cmr10.afm", fontsFolder + "cmr10.pfb"),
                FontEncoding.FONT_SPECIFIC, true);
        Assert.assertTrue("PdfType1Font expected", pdfType1Font instanceof PdfType1Font);

        new PdfCanvas(pdfDoc.addNewPage())
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfType1Font, 72)
                .showText("\u0000\u0001\u007cHello world")
                .endText()
                .restoreState()
                .rectangle(100, 500, 100, 100).fill();

        byte[] afm = StreamUtil.inputStreamToArray(new FileInputStream(fontsFolder + "cmr10.afm"));
        byte[] pfb = StreamUtil.inputStreamToArray(new FileInputStream(fontsFolder + "cmr10.pfb"));
        pdfType1Font = PdfFontFactory.createFont(FontProgramFactory.createType1Font(afm, pfb), FontEncoding.FONT_SPECIFIC, true);
        Assert.assertTrue("PdfType1Font expected", pdfType1Font instanceof PdfType1Font);

        new PdfCanvas(pdfDoc.addNewPage())
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfType1Font, 72)
                .showText("\u0000\u0001\u007cHello world")
                .endText()
                .restoreState()
                .rectangle(100, 500, 100, 100).fill();

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithType1FontPfm() throws IOException, InterruptedException {
        String filename = destinationFolder + "DocumentWithCMR10Pfm.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithCMR10Pfm.pdf";
        String title = "Empty iText 6 Document";

        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        PdfFont pdfType1Font = PdfFontFactory.createFont(FontProgramFactory.createType1Font(fontsFolder + "cmr10.pfm", fontsFolder + "cmr10.pfb"), FontEncoding.FONT_SPECIFIC, true);
        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfType1Font, 72)
                .showText("Hello world")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();
        page.flush();
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }


    @Test
    public void createDocumentWithTrueTypeFont1() throws IOException, InterruptedException {
        String filename = destinationFolder + "DocumentWithTrueTypeFont1.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithTrueTypeFont1.pdf";
        String title = "Empty iText 6 Document";
        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        String font = fontsFolder + "abserif4_5.ttf";
        PdfFont pdfTrueTypeFont = PdfFontFactory.createFont(font, true);
        Assert.assertTrue("PdfTrueTypeFont expected", pdfTrueTypeFont instanceof PdfTrueTypeFont);
        pdfTrueTypeFont.setSubset(true);
        PdfPage page = pdfDoc.addNewPage();
        new PdfCanvas(page)
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfTrueTypeFont, 72)
                .showText("Hello world")
                .endText()
                .restoreState()
                .rectangle(100, 500, 100, 100).fill()
                .release();
        page.flush();

        byte[] ttf = StreamUtil.inputStreamToArray(new FileInputStream(font));
        pdfTrueTypeFont = PdfFontFactory.createFont(ttf, true);
        Assert.assertTrue("PdfTrueTypeFont expected", pdfTrueTypeFont instanceof PdfTrueTypeFont);
        pdfTrueTypeFont.setSubset(true);
        page = pdfDoc.addNewPage();
        new PdfCanvas(page)
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfTrueTypeFont, 72)
                .showText("Hello world")
                .endText()
                .restoreState()
                .rectangle(100, 500, 100, 100).fill()
                .release();

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithTrueTypeOtfFont() throws IOException, InterruptedException {
        String filename = destinationFolder + "DocumentWithTrueTypeOtfFont.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithTrueTypeOtfFont.pdf";
        String title = "Empty iText 6 Document";

        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);

        String font = fontsFolder + "Puritan2.otf";

        PdfFont pdfTrueTypeFont = PdfFontFactory.createFont(font, true);
        Assert.assertTrue("PdfTrueTypeFont expected", pdfTrueTypeFont instanceof PdfTrueTypeFont);
        pdfTrueTypeFont.setSubset(true);
        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfTrueTypeFont, 72)
                .showText("Hello world")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();
        page.flush();

        byte[] ttf = StreamUtil.inputStreamToArray(new FileInputStream(font));
        pdfTrueTypeFont = PdfFontFactory.createFont(ttf, true);
        Assert.assertTrue("PdfTrueTypeFont expected", pdfTrueTypeFont instanceof PdfTrueTypeFont);
        pdfTrueTypeFont.setSubset(true);
        page = pdfDoc.addNewPage();
        canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfTrueTypeFont, 72)
                .showText("Hello world")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithType0OtfFont() throws IOException, InterruptedException {
        String filename = destinationFolder + "DocumentWithType0OtfFont.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithType0OtfFont.pdf";
        String title = "Empty iText 6 Document";

        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);

        String font = fontsFolder + "Puritan2.otf";

        PdfFont pdfFont = PdfFontFactory.createFont(font, "Identity-H");
        Assert.assertTrue("PdfType0Font expected", pdfFont instanceof PdfType0Font);
        pdfFont.setSubset(true);
        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfFont, 72)
                .showText("Hello world")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();
        page.flush();

        byte[] ttf = StreamUtil.inputStreamToArray(new FileInputStream(font));
        pdfFont = PdfFontFactory.createFont(ttf, "Identity-H");
        Assert.assertTrue("PdfTrueTypeFont expected", pdfFont instanceof PdfType0Font);
        pdfFont.setSubset(true);
        page = pdfDoc.addNewPage();
        canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfFont, 72)
                .showText("Hello world")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void testUpdateType3FontBasedExistingFont() throws IOException, InterruptedException {
        String inputFileName = sourceFolder + "type3Font.pdf";
        String outputFileName = destinationFolder + "type3Font_update.pdf";
        String cmpOutputFileName = sourceFolder + "cmp_type3Font_update.pdf";
        String title = "Type3 font iText 6 Document";

        PdfReader reader = new PdfReader(inputFileName);
        PdfWriter writer = new PdfWriter(outputFileName);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);


        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);

        PdfType3Font pdfType3Font = (PdfType3Font) PdfFontFactory.createFont((PdfDictionary) pdfDoc.getPdfObject(4));

        Type3Glyph newGlyph = pdfType3Font.addGlyph('\u00F6', 600, 0, 0, 600, 700);
        newGlyph.setLineWidth(100);
        newGlyph.moveTo(540, 5);
        newGlyph.lineTo(5, 840);
        newGlyph.stroke();

        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .beginText()
                .setFontAndSize(pdfType3Font, 12)
                .moveText(50, 800)
                .showText("AAAAAA EEEE ~ \u00E9 \u00F6") // é ö
                .endText();
        page.flush();
        pdfDoc.close();

        Assert.assertEquals(6, pdfType3Font.getFontProgram().getGlyphsCount());

        Assert.assertNull(new CompareTool().compareByContent(outputFileName, cmpOutputFileName, destinationFolder, "diff_"));
    }

    @Test
    public void testNewType3FontBasedExistingFont() throws IOException, InterruptedException {
        String inputFileName = sourceFolder + "type3Font.pdf";
        String outputFileName = destinationFolder + "type3Font_new.pdf";
        String cmpOutputFileName = sourceFolder + "cmp_type3Font_new.pdf";
        String title = "Type3 font iText 6 Document";

        PdfReader reader = new PdfReader(inputFileName);
        PdfWriter pdfWriter = new PdfWriter(outputFileName);
        pdfWriter.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument inputPdfDoc = new PdfDocument(reader);
        PdfDocument outputPdfDoc = new PdfDocument(pdfWriter);


        outputPdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);

        PdfDictionary pdfType3FontDict = (PdfDictionary) inputPdfDoc.getPdfObject(4);
        PdfType3Font pdfType3Font = (PdfType3Font) PdfFontFactory.createFont(pdfType3FontDict.copyTo(outputPdfDoc));

        Type3Glyph newGlyph = pdfType3Font.addGlyph('\u00F6', 600, 0, 0, 600, 700);
        newGlyph.setLineWidth(100);
        newGlyph.moveTo(540, 5);
        newGlyph.lineTo(5, 840);
        newGlyph.stroke();

        PdfPage page = outputPdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .beginText()
                .setFontAndSize(pdfType3Font, 12)
                .moveText(50, 800)
                .showText("AAAAAA EEEE ~ \u00E9 \u00F6") // é ö
                .endText();
        page.flush();
        outputPdfDoc.close();

        Assert.assertEquals(6, pdfType3Font.getFontProgram().getGlyphsCount());

        Assert.assertNull(new CompareTool().compareByContent(outputFileName, cmpOutputFileName, destinationFolder, "diff_"));
    }

    @Test
    public void testNewType1FontBasedExistingFont() throws IOException, InterruptedException {
        String inputFileName1 = sourceFolder + "DocumentWithCMR10Afm.pdf";
        String filename = destinationFolder + "DocumentWithCMR10Afm_new.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithCMR10Afm_new.pdf";
        String title = "Type 1 font iText 6 Document";

        PdfReader reader1 = new PdfReader(inputFileName1);
        PdfDocument inputPdfDoc1 = new PdfDocument(reader1);
        PdfDictionary pdfDictionary = (PdfDictionary) inputPdfDoc1.getPdfObject(4);

        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);

        PdfFont pdfType1Font = PdfFontFactory.createFont(pdfDictionary.copyTo(pdfDoc));
        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfType1Font, 72)
                .showText("New Hello world")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();
        page.flush();
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void testNewTrueTypeFont1BasedExistingFont() throws IOException, InterruptedException {
        String inputFileName1 = sourceFolder + "DocumentWithTrueTypeFont1.pdf";
        String filename = destinationFolder + "DocumentWithTrueTypeFont1_new.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithTrueTypeFont1_new.pdf";
        String title = "testNewTrueTypeFont1BasedExistingFont";

        PdfReader reader1 = new PdfReader(inputFileName1);
        PdfDocument inputPdfDoc1 = new PdfDocument(reader1);

        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        PdfDictionary pdfDictionary = (PdfDictionary) inputPdfDoc1.getPdfObject(4);
        PdfFont pdfTrueTypeFont = PdfFontFactory.createFont(pdfDictionary.copyTo(pdfDoc));
        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfTrueTypeFont, 72)
                .showText("New Hello world")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();
        page.flush();
        pdfDoc.close();
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void testNewTrueTypeFont2BasedExistingFont() throws IOException, InterruptedException {
        String inputFileName1 = sourceFolder + "DocumentWithTrueTypeFont2.pdf";
        String filename = destinationFolder + "DocumentWithTrueTypeFont2_new.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithTrueTypeFont2_new.pdf";
        String title = "True Type font iText 6 Document";

        PdfReader reader1 = new PdfReader(inputFileName1);
        PdfDocument inputPdfDoc1 = new PdfDocument(reader1);
        PdfDictionary pdfDictionary = (PdfDictionary) inputPdfDoc1.getPdfObject(4);

        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);

        PdfFont pdfFont = PdfFontFactory.createFont(pdfDictionary.copyTo(pdfDoc));
        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfFont, 72)
                .showText("New Hello world")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();
        page.flush();
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void testTrueTypeFont1BasedExistingFont() throws IOException, InterruptedException {
        String inputFileName1 = sourceFolder + "DocumentWithTrueTypeFont1.pdf";
        String filename = destinationFolder + "DocumentWithTrueTypeFont1_updated.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithTrueTypeFont1_updated.pdf";

        PdfReader reader1 = new PdfReader(inputFileName1);
        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(reader1, writer);

        PdfDictionary pdfDictionary = (PdfDictionary) pdfDoc.getPdfObject(4);
        PdfFont pdfFont = PdfFontFactory.createFont(pdfDictionary);
        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfFont, 72)
                .showText("New Hello world")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();
        page.flush();
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void testUpdateCjkFontBasedExistingFont() throws IOException, InterruptedException {
        String inputFileName1 = sourceFolder + "DocumentWithKozmin.pdf";
        String filename = destinationFolder + "DocumentWithKozmin_update.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithKozmin_update.pdf";
        String title = "Type0 font iText 6 Document";

        PdfReader reader = new PdfReader(inputFileName1);
        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);

        PdfDictionary pdfDictionary = (PdfDictionary) pdfDoc.getPdfObject(6);
        PdfFont pdfTrueTypeFont = PdfFontFactory.createFont(pdfDictionary);
        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfTrueTypeFont, 72)
                .showText("New Hello world")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();
        page.flush();
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void testNewCjkFontBasedExistingFont() throws IOException, InterruptedException {
        String inputFileName1 = sourceFolder + "DocumentWithKozmin.pdf";
        String filename = destinationFolder + "DocumentWithKozmin_new.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithKozmin_new.pdf";
        String title = "Type0 font iText 6 Document";

        PdfReader reader1 = new PdfReader(inputFileName1);
        PdfDocument inputPdfDoc1 = new PdfDocument(reader1);
        PdfDictionary pdfDictionary = (PdfDictionary) inputPdfDoc1.getPdfObject(6);

        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);

        PdfFont pdfTrueTypeFont = PdfFontFactory.createFont(pdfDictionary.copyTo(pdfDoc));
        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfTrueTypeFont, 72)
                .showText("New Hello world")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();
        page.flush();
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithTrueTypeAsType0BasedExistingFont() throws IOException, InterruptedException {
        String inputFileName1 = sourceFolder + "DocumentWithTrueTypeAsType0.pdf";
        String filename = destinationFolder + "DocumentWithTrueTypeAsType0_new.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithTrueTypeAsType0_new.pdf";
        String title = "Type0 font iText 6 Document";

        PdfReader reader1 = new PdfReader(inputFileName1);
        PdfDocument inputPdfDoc1 = new PdfDocument(reader1);
        PdfDictionary pdfDictionary = (PdfDictionary) inputPdfDoc1.getPdfObject(6);

        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);

        PdfFont pdfTrueTypeFont = PdfFontFactory.createFont(pdfDictionary.copyTo(pdfDoc));
        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfTrueTypeFont, 72)
                .showText("New Hello World")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();
        page.flush();
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createUpdatedDocumentWithTrueTypeAsType0BasedExistingFont() throws IOException, InterruptedException {
        String inputFileName1 = sourceFolder + "DocumentWithTrueTypeAsType0.pdf";
        String filename = destinationFolder + "DocumentWithTrueTypeAsType0_update.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithTrueTypeAsType0_update.pdf";
        String title = "Type0 font iText 6 Document";

        PdfReader reader = new PdfReader(inputFileName1);
        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);

        PdfFont pdfTrueTypeFont = PdfFontFactory.createFont((PdfDictionary) pdfDoc.getPdfObject(6));
        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfTrueTypeFont, 72)
                .showText("New Hello World")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();
        page.flush();
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithType1WithToUnicodeBasedExistingFont() throws IOException, InterruptedException {
        String inputFileName1 = sourceFolder + "fontWithToUnicode.pdf";
        String filename = destinationFolder + "fontWithToUnicode_new.pdf";
        String cmpFilename = sourceFolder + "cmp_fontWithToUnicode_new.pdf";
        String title = "Type1 font iText 6 Document";

        PdfReader reader1 = new PdfReader(inputFileName1);
        PdfDocument inputPdfDoc1 = new PdfDocument(reader1);
        PdfDictionary pdfDictionary = (PdfDictionary) inputPdfDoc1.getPdfObject(4);

        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        PdfFont pdfType1Font = PdfFontFactory.createFont(pdfDictionary.copyTo(pdfDoc));
        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 756)
                .setFontAndSize(pdfType1Font, 10)
                .showText("New MyriadPro-Bold font.")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();
        page.flush();
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void testType1FontUpdateContent() throws IOException, InterruptedException {
        String inputFileName1 = sourceFolder + "DocumentWithCMR10Afm.pdf";
        String filename = destinationFolder + "DocumentWithCMR10Afm_updated.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithCMR10Afm_updated.pdf";

        PdfReader reader = new PdfReader(inputFileName1);
        PdfWriter writer = new PdfWriter(filename).setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);

        PdfDictionary pdfDictionary = (PdfDictionary) pdfDoc.getPdfObject(4);
        PdfFont pdfType1Font = PdfFontFactory.createFont(pdfDictionary);
        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfType1Font, 72)
                .showText("New Hello world")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();
        page.flush();
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createWrongAfm1() throws IOException, InterruptedException {
        String message = "";
        try {
            byte[] pfb = StreamUtil.inputStreamToArray(new FileInputStream(fontsFolder + "cmr10.pfb"));
            FontProgramFactory.createType1Font(null, pfb);
        } catch (com.itextpdf.io.IOException e) {
            message = e.getMessage();
        }
        Assert.assertEquals("invalid.afm.or.pfm.font.file", message);
    }

    @Test
    public void createWrongAfm2() throws IOException, InterruptedException {
        String message = "";
        String font = fontsFolder + "cmr10.pfb";
        try {
            FontProgramFactory.createType1Font(font, null);
        } catch (com.itextpdf.io.IOException e) {
            message = e.getMessage();
        }
        Assert.assertEquals(MessageFormat.format(com.itextpdf.io.IOException._1IsNotAnAfmOrPfmFontFile, font), message);

    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.START_MARKER_MISSING_IN_PFB_FILE)
    })
    public void createWrongPfb() throws IOException, InterruptedException {
        byte[] afm = StreamUtil.inputStreamToArray(new FileInputStream(fontsFolder + "cmr10.afm"));
        PdfFont font = PdfFontFactory.createFont(FontProgramFactory.createType1Font(afm, afm), null);
        byte[] streamContent = ((PdfType1Font) font).getFontProgram().getFontStreamBytes();
        Assert.assertTrue("Empty stream content expected", streamContent == null);
    }

    @Test
    public void autoDetect1() throws IOException, InterruptedException {
        byte[] afm = StreamUtil.inputStreamToArray(new FileInputStream(fontsFolder + "cmr10.afm"));

        Assert.assertTrue("Type1 font expected", FontProgramFactory.createFont(afm) instanceof Type1Font);
    }

    @Test
    public void autoDetect2() throws IOException, InterruptedException {
        byte[] afm = StreamUtil.inputStreamToArray(new FileInputStream(fontsFolder + "cmr10.afm"));
        byte[] pfb = StreamUtil.inputStreamToArray(new FileInputStream(fontsFolder + "cmr10.pfb"));

        Assert.assertTrue("Type1 font expected", FontProgramFactory.createType1Font(afm, pfb) instanceof Type1Font);
    }

    @Test
    public void autoDetect3() throws IOException, InterruptedException {
        byte[] otf = StreamUtil.inputStreamToArray(new FileInputStream(fontsFolder + "Puritan2.otf"));
        Assert.assertTrue("TrueType (OTF) font expected", FontProgramFactory.createFont(otf) instanceof TrueTypeFont);
    }

    @Test
    public void autoDetect4() throws IOException, InterruptedException {
        byte[] ttf = StreamUtil.inputStreamToArray(new FileInputStream(fontsFolder + "abserif4_5.ttf"));
        Assert.assertTrue("TrueType (TTF) expected", FontProgramFactory.createFont(ttf) instanceof TrueTypeFont);
    }

    @Test
    public void autoDetect5() throws IOException, InterruptedException {
        byte[] ttf = StreamUtil.inputStreamToArray(new FileInputStream(fontsFolder + "abserif4_5.ttf"));
        Assert.assertTrue("TrueType (TTF) expected", FontProgramFactory.createFont(ttf) instanceof TrueTypeFont);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.FONT_HAS_INVALID_GLYPH, count = 131)
    })
    public void testWriteTTC() throws IOException, InterruptedException {
        String filename = destinationFolder + "DocumentWithTTC.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithTTC.pdf";
        String title = "Empty iText 6 Document";

        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);

        String font = fontsFolder + "uming.ttc";

        PdfFont pdfTrueTypeFont = PdfFontFactory.createTtcFont(font, 0, PdfEncodings.WINANSI, true, false);

        pdfTrueTypeFont.setSubset(true);
        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfTrueTypeFont, 72)
                .showText("Hello world")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();
        page.flush();

        byte[] ttc = StreamUtil.inputStreamToArray(new FileInputStream(font));
        pdfTrueTypeFont = PdfFontFactory.createTtcFont(ttc, 1, PdfEncodings.WINANSI, true, false);
        pdfTrueTypeFont.setSubset(true);
        page = pdfDoc.addNewPage();
        canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfTrueTypeFont, 72)
                .showText("Hello world")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));

    }

    @Test
    public void testNotoFont() throws IOException, InterruptedException {
        String filename = destinationFolder + "testNotoFont.pdf";
        String cmpFilename = sourceFolder + "cmp_testNotoFont.pdf";

        String japanese = "\u713C";

        PdfDocument doc = new PdfDocument(new PdfWriter(filename));
        PdfPage page = doc.addNewPage();

        PdfFont font = PdfFontFactory.createFont(fontsFolder + "NotoSansCJKjp-Bold.otf", "Identity-H", true);

        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .beginText()
                .moveText(36, 680)
                .setFontAndSize(font, 12)
                .showText(japanese)
                .endText()
                .restoreState();

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }


    @Test
    @Ignore("Invalid subset")
    public void NotoSansCJKjpTest() throws IOException, InterruptedException {
        String filename = destinationFolder + "NotoSansCJKjpTest.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithTTC.pdf";

        PdfDocument doc = new PdfDocument(new PdfWriter(filename));
        PdfPage page = doc.addNewPage();
        // Identity-H must be embedded
        PdfFont font = PdfFontFactory.createFont(fontsFolder + "NotoSansCJKjp-Bold.otf", "Identity-H");
        // font.setSubset(false);
        PdfCanvas canvas = new PdfCanvas(page);
//        canvas.saveState()
//                .setFillColor(DeviceRgb.GREEN)
//                .beginText()
//                .moveText(36, 700)
//                .setFontAndSize(font, 12)
//                .showText(pangramme)
//                .endText()
//                .restoreState();
        canvas.saveState()
                .setFillColor(DeviceRgb.RED)
                .beginText()
                .moveText(36, 680)
                .setFontAndSize(font, 12)
                .showText("1")
                .endText()
                .restoreState();

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void testCheckTTCSize() throws IOException {
        TrueTypeCollection collection = new TrueTypeCollection(fontsFolder + "uming.ttc", "WinAnsi");
        Assert.assertTrue(collection.getTTCSize() == 4);
    }

    @Test
    public void testFontDirectoryRegister() throws IOException {
        PdfFontFactory.registerDirectory(sourceFolder);
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        for (String name : PdfFontFactory.getRegisteredFonts()) {
            PdfFont pdfFont = PdfFontFactory.createRegisteredFont(name);
            if (pdfFont == null)
                Assert.assertTrue("Font {" + name + "} can't be empty", false);
        }

        pdfDoc.addNewPage();

        pdfDoc.close();
    }

    @Test
    public void testFontRegister() throws IOException {
        FontProgramFactory.registerFont(fontsFolder + "Aller_Rg.ttf", "aller");
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        PdfFont pdfFont = PdfFontFactory.createRegisteredFont("aller");
        Assert.assertTrue(pdfFont instanceof PdfTrueTypeFont);
        pdfDoc.addNewPage();
        pdfDoc.close();
    }

    @Test
    public void testSplitString() throws IOException {
        PdfFont font = PdfFontFactory.createFont();
        List<String> list1 = font.splitString("Hello", 12f, 10);
        Assert.assertTrue(list1.size() == 2);

        List<String> list2 = font.splitString("Digitally signed by Dmitry Trusevich\nDate: 2015.10.25 14:43:56 MSK\nReason: Test 1\nLocation: Ghent", 12f, 176);
        Assert.assertTrue(list2.size() == 5);
    }


}
