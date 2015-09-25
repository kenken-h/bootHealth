package com.itrane.boothealth.view;

import java.text.SimpleDateFormat;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import com.itrane.boothealth.AppUtil;

public abstract class AbstractExcelBuilder extends AbstractExcelView {

    protected static final SimpleDateFormat SDF =
            new SimpleDateFormat("yyyy/MM/dd");

    @Override
    protected void buildExcelDocument(Map<String, Object> model,
            HSSFWorkbook workbook, HttpServletRequest req,
            HttpServletResponse res) throws Exception {
        String fileName = (String) model.get(AppUtil.MKEY_FILE_NAME);
        res.setHeader("Content-Disposition",
                "attachment; filename=" + fileName);
        createSheet(model, workbook);

    }

    /**
     * シートを作成する抽象メソッド.
     */
    protected abstract void createSheet(Map<String, Object> model,
            HSSFWorkbook workbook);

    // ヘッダ行を作成する.
    protected void setHeader(HSSFSheet sheet, HeaderDef hd, int row,
            HSSFWorkbook wb) {
        HSSFRow header = sheet.createRow(row);
        for (int col = 0; col < hd.headers.length; col++) {
            sheet.setColumnWidth(col, hd.widths[col] * 256);
            HSSFCell cell =
                    createCellWithStyle(header, col, hd.aligns[col], wb);
            cell.setCellValue(hd.headers[col]);
        }
    }

    // スタイル付きのセルを作成
    protected HSSFCell createCellWithStyle(HSSFRow row, int c, short align,
            HSSFWorkbook wb) {
        HSSFCell cell = row.createCell(c);
        cell.setCellStyle(createCellStyle(wb, align));
        return cell;
    }

    // セルスタイルを作成する（文字列の折り曲げと、垂直位置のトップは共通）
    protected HSSFCellStyle createCellStyle(HSSFWorkbook workbook,
            short align) {
        HSSFCellStyle style = workbook.createCellStyle();
        // style.setWrapText(true);
        style.setWrapText(false);
        style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        style.setAlignment(align);
        return style;
    }
}
