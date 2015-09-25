package com.itrane.boothealth.view.pdf;

import java.util.List;
import java.util.Map;

import com.itrane.boothealth.AppUtil;
import com.itrane.boothealth.model.UserInfo;
import com.itrane.boothealth.view.AbstractPdfBuilder;
import com.itrane.boothealth.view.HeaderDef;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPTable;

public class UserPdfBuilder extends AbstractPdfBuilder {

    @Override
    public PdfPTable createPdfTable(Map<String, Object> model)
            throws DocumentException {
        float fontSize = 10f;
        @SuppressWarnings("unchecked")
        List<UserInfo> users = (List<UserInfo>) model.get(AppUtil.MKEY_LST);

        HeaderDef headerDef = new HeaderDef(new String[] {
                "ユーザー名", "パスワード", "氏名", "よみ", "ロール"
        }, new short[] {
                Element.ALIGN_LEFT, Element.ALIGN_LEFT, Element.ALIGN_LEFT,
                Element.ALIGN_LEFT, Element.ALIGN_LEFT
        }, new int[] {
                14, 20, 24, 24, 30
        });
        PdfPTable table = createDefaultSettingTable(headerDef, fontSize,
                bf.getFontDescriptor(BaseFont.ASCENT, fontSize) - fontSize + 2,
                5f, 5f, 3f);
        // データの行
        for (UserInfo user : users) {
            table.addCell(
                    createPCell(user.getName(), fontSize, headerDef.aligns[0]));
            table.addCell(createPCell("", fontSize, headerDef.aligns[1]));
            table.addCell(createPCell(user.getSimei(), fontSize,
                    headerDef.aligns[2]));
            table.addCell(
                    createPCell(user.getYomi(), fontSize, headerDef.aligns[3]));
            table.addCell(createPCell(user.getRoles(), fontSize,
                    headerDef.aligns[4]));
        }
        return table;
    }

}
