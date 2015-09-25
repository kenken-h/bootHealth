package com.itrane.boothealth.view.pdf;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itrane.boothealth.AppUtil;
import com.itrane.boothealth.model.KusuriMst;
import com.itrane.boothealth.view.AbstractPdfBuilder;
import com.itrane.boothealth.view.HeaderDef;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPTable;

public class KmPdfBuilder extends AbstractPdfBuilder {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @SuppressWarnings("unchecked")
    @Override
    public PdfPTable createPdfTable(Map<String, Object> model)
            throws DocumentException {

        float fontSize = 10f;
        List<KusuriMst> kms = (List<KusuriMst>) model.get(AppUtil.MKEY_LST);

        HeaderDef headerDef = new HeaderDef(new String[] {
                "薬品名", "説明", "ジェネリック変更前", "容量"
        }, new short[] {
                Element.ALIGN_LEFT, Element.ALIGN_LEFT, Element.ALIGN_LEFT,
                Element.ALIGN_LEFT
        }, new int[] {
                24, 100, 24, 24
        });
        PdfPTable table = createDefaultSettingTable(headerDef, fontSize,
                bf.getFontDescriptor(BaseFont.ASCENT, fontSize) - fontSize + 2,
                5f, 5f, 3f);

        // データの行
        for (KusuriMst km : kms) {
            table.addCell(
                    createPCell(km.getName(), fontSize, headerDef.aligns[0]));
            table.addCell(createPCell(km.getSetumei(), fontSize,
                    headerDef.aligns[1]));
            table.addCell(createPCell(km.getGeneric(), fontSize,
                    headerDef.aligns[2]));
            table.addCell(createPCell(km.getYouryou(), fontSize,
                    headerDef.aligns[3]));
        }
        return table;
    }

}
