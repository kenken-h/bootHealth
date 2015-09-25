package com.itrane.boothealth.view.excel;

import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;

import com.itrane.boothealth.AppUtil;
import com.itrane.boothealth.model.VitalMst;
import com.itrane.boothealth.view.AbstractExcelBuilder;
import com.itrane.boothealth.view.HeaderDef;

public class VmExcelBuilder extends AbstractExcelBuilder {

    @Override
    protected void createSheet(Map<String, Object> model,
            HSSFWorkbook workbook) {
        @SuppressWarnings("unchecked")
        List<VitalMst> vms = (List<VitalMst>) model.get(AppUtil.MKEY_LST);
        String sheetName = (String) model.get(AppUtil.MKEY_TITLE);
        String headLabel = (String) model.get(AppUtil.MKEY_HEAD_LABEL);

        HeaderDef headerDef = new HeaderDef(new String[] {
                "順序", "名称", "タイプ", headLabel, "基準値最小", "基準値最大", "種別"
        }, new short[] {
                CellStyle.ALIGN_RIGHT, CellStyle.ALIGN_LEFT,
                CellStyle.ALIGN_LEFT, CellStyle.ALIGN_LEFT,
                CellStyle.ALIGN_RIGHT, CellStyle.ALIGN_RIGHT,
                CellStyle.ALIGN_CENTER
        }, new int[] {
                8, 20, 20, 30, 10, 10, 8
        });

        HSSFSheet sheet = workbook.createSheet(sheetName);
        int row = 0;
        setHeader(sheet, headerDef, row++, workbook);
        for (VitalMst vm : vms) {
            int c = 0;
            HSSFRow arow = sheet.createRow(row++);
            createCellWithStyle(arow, c, headerDef.aligns[c++], workbook)
                    .setCellValue(vm.getJunjo());
            createCellWithStyle(arow, c, headerDef.aligns[c++], workbook)
                    .setCellValue(vm.getName());
            createCellWithStyle(arow, c, headerDef.aligns[c++], workbook)
                    .setCellValue(vm.getType());
            createCellWithStyle(arow, c, headerDef.aligns[c++], workbook)
                    .setCellValue(vm.getJikan());
            createCellWithStyle(arow, c, headerDef.aligns[c++], workbook)
                    .setCellValue(vm.getKijunMin().doubleValue());
            createCellWithStyle(arow, c, headerDef.aligns[c++], workbook)
                    .setCellValue(vm.getKijunMax().doubleValue());
            createCellWithStyle(arow, c, headerDef.aligns[c++], workbook)
                    .setCellValue(vm.getSyubetu());
        }

    }

}
