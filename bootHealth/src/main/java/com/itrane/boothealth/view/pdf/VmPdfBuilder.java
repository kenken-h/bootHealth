package com.itrane.boothealth.view.pdf;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import com.itrane.boothealth.AppUtil;
import com.itrane.boothealth.model.VitalMst;
import com.itrane.boothealth.view.AbstractPdfBuilder;
import com.itrane.boothealth.view.HeaderDef;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPTable;

public class VmPdfBuilder extends AbstractPdfBuilder {

    @Override
    public PdfPTable createPdfTable(Map<String, Object> model)
            throws DocumentException {
        float fontSize = 10f;
        @SuppressWarnings("unchecked")
        List<VitalMst> vms = (List<VitalMst>) model.get(AppUtil.MKEY_LST);
        String headLabel = (String) model.get(AppUtil.MKEY_HEAD_LABEL);

        HeaderDef headerDef = new HeaderDef(new String[] {
                "順序", "名称", "タイプ", headLabel, "基準値最小", "基準値最大", "種別"
        }, new short[] {
                Element.ALIGN_RIGHT, Element.ALIGN_LEFT, Element.ALIGN_LEFT,
                Element.ALIGN_LEFT, Element.ALIGN_RIGHT, Element.ALIGN_RIGHT,
                Element.ALIGN_CENTER
        }, new int[] {
                8, 20, 20, 30, 10, 10, 8
        });
        PdfPTable table = createDefaultSettingTable(headerDef, fontSize,
                bf.getFontDescriptor(BaseFont.ASCENT, fontSize) - fontSize + 2,
                5f, 5f, 3f);

        // データの行
        DecimalFormat df = new DecimalFormat("###0.00");
        for (VitalMst vm : vms) {
            table.addCell(createPCell(vm.getJunjo() + "", fontSize,
                    headerDef.aligns[0]));
            table.addCell(
                    createPCell(vm.getName(), fontSize, headerDef.aligns[1]));
            table.addCell(
                    createPCell(vm.getType(), fontSize, headerDef.aligns[2]));
            table.addCell(
                    createPCell(vm.getJikan(), fontSize, headerDef.aligns[3]));
            table.addCell(createPCell(df.format(vm.getKijunMin()), fontSize,
                    headerDef.aligns[4]));
            table.addCell(createPCell(df.format(vm.getKijunMax()), fontSize,
                    headerDef.aligns[5]));
            table.addCell(createPCell(vm.getSyubetu() + "", fontSize,
                    headerDef.aligns[6]));
        }
        return table;
    }

}
