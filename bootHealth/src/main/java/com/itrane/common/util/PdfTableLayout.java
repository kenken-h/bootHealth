package com.itrane.common.util;

import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPTableEvent;

/**
 * <p>
 * PDFテーブルのレイアウト定義. 
 * </p>
 * <p>
 * テーブル枠 1.0f, 黒 ヘッダ枠 1.0f, 黒 セル枠 0.5f, (200,200,200)
 * </p>
 */
public class PdfTableLayout implements PdfPTableEvent {

    /**
     * <p>
     * PDFテーブルを設定する.
     * <p>
     * @param table
     * @param width
     * @param heights
     * @param headerRows
     * @param rowStart
     * @param canvases
     */
    @Override
    public void tableLayout(PdfPTable table, float[][] width, float[] heights,
            int headerRows, int rowStart, PdfContentByte[] canvases) {

        // １行の各列の幅
        float widths[] = width[0];

        PdfContentByte cb = canvases[PdfPTable.TEXTCANVAS];
        cb.saveState();

        // テーブル枠
        cb.setLineWidth(1);
        cb.setRGBColorStroke(0, 0, 0);
        cb.rectangle(widths[0], heights[heights.length - 1],
                widths[widths.length - 1] - widths[0],
                heights[0] - heights[heights.length - 1]);
        cb.stroke();

        // ヘッダ行の描画
        if (headerRows > 0) {
            float headerHeight = heights[0];
            for (int k = 0; k < headerRows; ++k)
                headerHeight += heights[k];
            cb.setRGBColorStroke(0, 0, 0);
            cb.rectangle(widths[0], heights[headerRows],
                    widths[widths.length - 1] - widths[0],
                    heights[0] - heights[headerRows]);
            cb.stroke();
        }
        cb.restoreState();

        cb = canvases[PdfPTable.BASECANVAS];
        cb.saveState();

        // 各セルの枠
        cb.setLineWidth(.5f);

        // 行処理の繰り返し
        for (int line = 0; line < heights.length - 1; ++line) {
            widths = width[line];

            // 行ごとの列処理の繰り返し
            for (int col = 0; col < widths.length - 1; ++col) {
                // 水平線
                cb.setRGBColorStrokeF(0.3f, 0.3f, 0.3f);
                cb.moveTo(widths[col], heights[line]);
                cb.lineTo(widths[col + 1], heights[line]);
                cb.stroke();
                // 垂直線
                cb.setRGBColorStrokeF(0.6f, 0.6f, 0.6f);
                cb.moveTo(widths[col], heights[line]);
                cb.lineTo(widths[col], heights[line + 1]);
                cb.stroke();
            }
        }
        cb.restoreState();
    }
}
