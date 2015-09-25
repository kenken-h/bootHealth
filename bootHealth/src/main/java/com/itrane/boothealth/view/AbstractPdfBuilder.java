package com.itrane.boothealth.view;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import com.itrane.boothealth.AppUtil;
import com.itrane.boothealth.view.pdf.HeaderFooterEvent;
import com.itrane.common.util.PdfTableLayout;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public abstract class AbstractPdfBuilder extends AbstractPdfView {

    protected static final SimpleDateFormat SDF =
            new SimpleDateFormat("yyyy/MM/dd");
    protected String headerTitle;
    protected BaseFont bf;

    @Override
    protected void buildPdfDocument(Map<String, Object> model,
            Document document, PdfWriter writer, HttpServletRequest req,
            HttpServletResponse resp) throws Exception {
        this.headerTitle = (String) model.get(AppUtil.MKEY_TITLE);
        createPdf(model, document, writer);

    }

    /**
     * PDFドキュメントを作成する.
     * 
     * @param document
     * @param writer
     * @throws DocumentException
     * @throws IOException
     */
    protected void createPdf(Map<String, Object> model, Document document,
            PdfWriter writer) throws DocumentException, IOException {
        // 初期処理
        document.setPageSize(PageSize.A4.rotate());
        PdfTableLayout event = new PdfTableLayout();

        /*
         * //平成角ゴ、横書き
         * bf = bf = BaseFont.createFont("HeiseiKakuGo-W5",
         * "UniJIS-UCS2-H", BaseFont.EMBEDDED);
         */
        bf = BaseFont.createFont("/Library/Fonts/Hiragino Sans GB W3.otf",
                BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        PdfContentByte cb = writer.getDirectContent();
        float fontSize = 7;
        cb.setFontAndSize(bf, fontSize);
        Rectangle size = document.getPageSize();

        PdfPTable table = createPdfTable(model);

        String date = DateTime.now().toString("yyyy/MM/dd");
        HeaderFooterEvent hfevent = new HeaderFooterEvent(headerTitle, date);
        writer.setPageEvent(hfevent);
        document.open();

        table.setTableEvent(event);
        table.setHeaderRows(1);
        document.add(table);
    }

    /**
     * Pdf ドキュメントを作成する抽象メソッド.
     */
    public abstract PdfPTable createPdfTable(Map<String, Object> model)
            throws DocumentException;

    // ヘッダ行を作成する.
    // デフォルト設定のテーブルを作成
    protected PdfPTable createDefaultSettingTable(HeaderDef hd, float fontSize,
            float top, float left, float right, float bottom)
                    throws DocumentException {
        PdfPTable table = new PdfPTable(hd.headers.length);
        float[] widths = new float[hd.headers.length];
        for (int col = 0; col < widths.length; col++) {
            widths[col] = 1.0f * hd.widths[col] / hd.widths[0];
        }
        table.setWidths(widths);
        // テーブル全体の幅：
        table.setWidthPercentage(100);
        // パディング設定
        table.getDefaultCell().setPaddingTop(top);
        table.getDefaultCell().setPaddingLeft(left);
        table.getDefaultCell().setPaddingRight(right);
        table.getDefaultCell().setPaddingBottom(bottom);
        // ボーダー設定
        table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        // ---- テーブルヘッダ ----
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        for (int col = 0; col < hd.headers.length; col++) {
            table.addCell(
                    createPCell(hd.headers[col], fontSize, hd.aligns[col]));
        }
        return table;
    }

    protected PdfPCell createPCell(String val, float fSize, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(val, new Font(bf, fSize)));
        cell.setHorizontalAlignment(align);
        cell.setVerticalAlignment(Element.ALIGN_TOP);
        return cell;
    }
}
