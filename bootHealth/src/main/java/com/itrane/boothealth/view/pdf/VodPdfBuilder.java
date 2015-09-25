package com.itrane.boothealth.view.pdf;

import java.util.List;
import java.util.Map;

import com.itrane.boothealth.AppUtil;
import com.itrane.boothealth.model.Vital;
import com.itrane.boothealth.model.VitalMst;
import com.itrane.boothealth.model.Vod;
import com.itrane.boothealth.view.AbstractPdfBuilder;
import com.itrane.boothealth.view.HeaderDef;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPTable;

public class VodPdfBuilder extends AbstractPdfBuilder {

    @SuppressWarnings("unchecked")
    @Override
    public PdfPTable createPdfTable(Map<String, Object> model)
            throws DocumentException {
        float fontSize = 10f;
        List<Vod> vods = (List<Vod>) model.get(AppUtil.MKEY_VODS);
        List<VitalMst> vms = (List<VitalMst>) model.get(AppUtil.MKEY_LST);
        String headLabel = (String) model.get(AppUtil.MKEY_HEAD_LABEL);

        String[] headers1 = new String[1 + vms.size()];
        short[] aligns = new short[1 + vms.size()];
        int[] wds = new int[1 + vms.size()];
        int col = 0;
        headers1[col] = "バイタル";
        aligns[col] = Element.ALIGN_CENTER;
        wds[0] = 10;
        for (VitalMst vm : vms) {
            headers1[++col] = vm.getName();
            aligns[col] = Element.ALIGN_CENTER;
            wds[col] = 8;
        }
        HeaderDef headerDef1 = new HeaderDef(headers1, aligns, wds);
        PdfPTable table = createDefaultSettingTable(headerDef1, fontSize,
                bf.getFontDescriptor(BaseFont.ASCENT, fontSize) - fontSize + 2,
                5f, 5f, 3f);
        table.addCell(
                createPCell(headLabel, fontSize * 0.9f, Element.ALIGN_CENTER));
        for (VitalMst vm : vms) {
            table.addCell(createPCell(vm.getJikan(), fontSize * 0.9f,
                    Element.ALIGN_CENTER));
        }
        // データの行
        for (Vod vod : vods) {
            String sokuteiBi = vod.getSokuteiBi();
            table.addCell(createPCell(sokuteiBi, fontSize * 0.9f,
                    Element.ALIGN_CENTER));
            for (Vital vt : vod.getVitals()) {
                table.addCell(createPCell(vt.getSokuteiTi(), fontSize * 0.9f,
                        Element.ALIGN_RIGHT));
            }
        }
        return table;
    }

}
