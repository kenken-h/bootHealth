package com.itrane.boothealth.view.excel;

import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;

import com.itrane.boothealth.AppUtil;
import com.itrane.boothealth.model.KusuriMst;
import com.itrane.boothealth.view.AbstractExcelBuilder;
import com.itrane.boothealth.view.HeaderDef;

public class KmExcelBuilder extends AbstractExcelBuilder {

    @Override
    public void createSheet(Map<String, Object> model, HSSFWorkbook workbook) {
        @SuppressWarnings("unchecked")
        List<KusuriMst> kms = (List<KusuriMst>) model.get(AppUtil.MKEY_LST);
        String sheetName = (String) model.get(AppUtil.MKEY_TITLE);

        HSSFSheet sheet = workbook.createSheet(sheetName);
        HeaderDef headerDef = new HeaderDef(new String[] {
                "薬品名", "説明", "非ジェネリック", "服用量"
        }, new short[] {
                CellStyle.ALIGN_LEFT, CellStyle.ALIGN_LEFT,
                CellStyle.ALIGN_LEFT, CellStyle.ALIGN_LEFT
        }, new int[] {
                24, 100, 24, 24
        });
        int row = 0;
        setHeader(sheet, headerDef, row++, workbook);
        for (KusuriMst km : kms) {
            int c = 0;
            HSSFRow arow = sheet.createRow(row++);
            HSSFCell cell = createCellWithStyle(arow, c, headerDef.aligns[c++],
                    workbook);
            cell.setCellValue(km.getName());
            cell = createCellWithStyle(arow, c, headerDef.aligns[c++],
                    workbook);
            cell.setCellValue(km.getSetumei());
            cell = createCellWithStyle(arow, c, headerDef.aligns[c++],
                    workbook);
            cell.setCellValue(km.getGeneric());
            cell = createCellWithStyle(arow, c, headerDef.aligns[c++],
                    workbook);
            cell.setCellValue(km.getYouryou());
        }
    }

}
