package com.itrane.boothealth.view.pdf;

import java.io.IOException;

import com.itrane.boothealth.AppUtil;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

public class HeaderFooterEvent extends PdfPageEventHelper {

    private String title;
    private String date;
    private BaseFont bf;

    public HeaderFooterEvent(String title, String date) {
        this.title = title;
        this.date = date;
        // とりあえず、ベースフォントは固定
        try {
            this.bf = BaseFont.createFont(AppUtil.PDF_FONT_NAME,
                    BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }

    public HeaderFooterEvent() {
        this("Top Right", "Top Left");
    }

    public void onStartPage(PdfWriter writer, Document document) {
        // ヘッダーのフォントサイズ, 出力フォーマットは固定
        float xt = document.left();
        float xd = document.right();
        float y = document.top() + 5;

        ColumnText.showTextAligned(writer.getDirectContent(),
                Element.ALIGN_LEFT, new Phrase(title, new Font(bf, 10)), xt, y,
                0);
        ColumnText.showTextAligned(writer.getDirectContent(),
                Element.ALIGN_RIGHT, new Phrase(date, new Font(bf, 8)), xd, y,
                0);
    }

    public void onEndPage(PdfWriter writer, Document document) {
        // フッターのフォントサイズ, 出力フォーマットは固定
        float xt = document.left();
        float xd = document.right();
        float y = document.bottom() - 5;
        Font f = new Font(bf, 8);
        ColumnText.showTextAligned(writer.getDirectContent(),
                Element.ALIGN_LEFT, new Phrase(AppUtil.msg.COMPANY_URL, f), xt,
                y, 0);
        ColumnText.showTextAligned(writer.getDirectContent(),
                Element.ALIGN_RIGHT,
                new Phrase(
                        AppUtil.PDF_PAGE_LABEL + (document.getPageNumber() - 1),
                        f),
                xd, y, 0);
    }

}