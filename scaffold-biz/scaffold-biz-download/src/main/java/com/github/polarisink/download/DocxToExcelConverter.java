package com.github.polarisink.download;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class DocxToExcelConverter {
    public static void convertDocxToExcel(Path docxPath, Path xlsxPath) throws Exception {
        try (XWPFDocument doc = new XWPFDocument(Files.newInputStream(docxPath));
             XSSFWorkbook workbook = new XSSFWorkbook()) {

            List<IBodyElement> elements = doc.getBodyElements();
            int sheetIndex = 1;
            for (int i = 0; i < elements.size(); i++) {
                if (elements.get(i).getElementType() == BodyElementType.TABLE) {
                    XWPFTable table = (XWPFTable) elements.get(i);
                    String sheetName = "Sheet" + sheetIndex;

                    // 查找前一段落作为表格标题
                    for (int j = i - 1; j >= 0; j--) {
                        if (elements.get(j).getElementType() == BodyElementType.PARAGRAPH) {
                            String title = ((XWPFParagraph) elements.get(j)).getText();
                            if (!title.isBlank()) {
                                sheetName = title.length() > 31 ? title.substring(0, 31) : title;
                                break;
                            }
                        }
                    }

                    Sheet sheet = workbook.createSheet(sheetName);
                    int rowIndex = 0;
                    for (XWPFTableRow row : table.getRows()) {
                        Row excelRow = sheet.createRow(rowIndex++);
                        List<XWPFTableCell> cells = row.getTableCells();
                        for (int k = 0; k < cells.size(); k++) {
                            excelRow.createCell(k).setCellValue(cells.get(k).getText().trim());
                        }
                    }
                    sheetIndex++;
                }
            }

            try (FileOutputStream fos = new FileOutputStream(xlsxPath.toFile())) {
                workbook.write(fos);
            }
        }
    }
}
