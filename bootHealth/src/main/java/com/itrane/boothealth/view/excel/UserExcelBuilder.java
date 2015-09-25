package com.itrane.boothealth.view.excel;

import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;

import com.itrane.boothealth.AppUtil;
import com.itrane.boothealth.model.UserInfo;
import com.itrane.boothealth.view.AbstractExcelBuilder;
import com.itrane.boothealth.view.HeaderDef;

public class UserExcelBuilder extends AbstractExcelBuilder {

    @Override
    protected void createSheet(Map<String, Object> model,
            HSSFWorkbook workbook) {
        @SuppressWarnings("unchecked")
        List<UserInfo> users = (List<UserInfo>) model.get(AppUtil.MKEY_LST);
        String sheetName = (String) model.get(AppUtil.MKEY_TITLE);

        HSSFSheet sheet = workbook.createSheet(sheetName);
        HeaderDef headerDef = new HeaderDef(new String[] {
                "ユーザー名", "パスワード", "氏名", "よみ", "ロール"
        }, new short[] {
                CellStyle.ALIGN_LEFT, CellStyle.ALIGN_LEFT,
                CellStyle.ALIGN_LEFT, CellStyle.ALIGN_LEFT, CellStyle.ALIGN_LEFT
        }, new int[] {
                14, 24, 24, 24, 24
        });
        int row = 0;
        setHeader(sheet, headerDef, row++, workbook);
        for (UserInfo user : users) {
            int c = 0;
            HSSFRow arow = sheet.createRow(row++);
            createCellWithStyle(arow, c, headerDef.aligns[c++], workbook)
                    .setCellValue(user.getName());
            createCellWithStyle(arow, c, headerDef.aligns[c++], workbook)
                    .setCellValue("");
            createCellWithStyle(arow, c, headerDef.aligns[c++], workbook)
                    .setCellValue(user.getSimei());
            createCellWithStyle(arow, c, headerDef.aligns[c++], workbook)
                    .setCellValue(user.getYomi());
            createCellWithStyle(arow, c, headerDef.aligns[c++], workbook)
                    .setCellValue(user.getRoles());
        }
    }

}
