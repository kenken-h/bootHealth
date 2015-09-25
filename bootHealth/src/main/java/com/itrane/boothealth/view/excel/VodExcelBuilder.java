package com.itrane.boothealth.view.excel;

import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;

import com.itrane.boothealth.AppUtil;
import com.itrane.boothealth.model.Vital;
import com.itrane.boothealth.model.VitalMst;
import com.itrane.boothealth.model.Vod;
import com.itrane.boothealth.view.AbstractExcelBuilder;
import com.itrane.boothealth.view.HeaderDef;

public class VodExcelBuilder extends AbstractExcelBuilder {

    @SuppressWarnings("unchecked")
    @Override
    protected void createSheet(Map<String, Object> model,
            HSSFWorkbook workbook) {
        List<VitalMst> vms = (List<VitalMst>) model.get(AppUtil.MKEY_LST);
        List<Vod> vods = (List<Vod>) model.get(AppUtil.MKEY_VODS);
        String sheetName = (String) model.get(AppUtil.MKEY_TITLE);

        HSSFSheet sheet = workbook.createSheet(sheetName);
        sheet.setDefaultColumnWidth(12);

        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);

        HSSFRow header = sheet.createRow(0);
        HSSFCell cell = header.createCell(0);

        // ヘッダ
        cell.setCellValue("日付");
        cell.setCellStyle(cellStyle);
        int col = 1;
        for (VitalMst vm : vms) {
            cell = header.createCell(col++);
            cell.setCellValue(vm.getName());
        }

        int row = 1;
        for (Vod vod : vods) {
            HSSFRow arow = sheet.createRow(row++);
            cell = createCellWithStyle(arow, 0, CellStyle.ALIGN_CENTER,
                    workbook);
            cell.setCellValue(vod.getSokuteiBi());
            cell.setCellStyle(cellStyle);
            int c = 1;
            for (Vital vt : vod.getVitals()) {
                arow.createCell(c++).setCellValue(vt.getSokuteiTi());
            }
        }

        createVmsSheet(model, workbook);
    }

    private void createVmsSheet(Map<String, Object> model,
            HSSFWorkbook workbook) {
        @SuppressWarnings("unchecked")
        List<VitalMst> vms = (List<VitalMst>) model.get(AppUtil.MKEY_LST);
        String headLabel = (String) model.get(AppUtil.MKEY_HEAD_LABEL);
        String sheetName = (String) model.get(AppUtil.MKEY_SHEET_NAME);

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
