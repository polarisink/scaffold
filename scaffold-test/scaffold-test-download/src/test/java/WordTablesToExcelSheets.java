import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordTablesToExcelSheets {

    public static void main(String[] args) throws Exception {
        String inputDocx = "input.docx";
        String outputXlsx = "output.xlsx";

        try (
                FileInputStream fis = new FileInputStream(inputDocx);
                XWPFDocument document = new XWPFDocument(fis);
                Workbook workbook = new XSSFWorkbook();
                FileOutputStream fos = new FileOutputStream(outputXlsx)
        ) {
            List<IBodyElement> bodyElements = document.getBodyElements();
            int tableCount = 0;

            for (int i = 0; i < bodyElements.size(); i++) {
                IBodyElement element = bodyElements.get(i);

                if (element.getElementType() == BodyElementType.TABLE) {
                    tableCount++;
                    XWPFTable table = (XWPFTable) element;
                    String title = findTitleForTable(bodyElements, i);
                    if (title == null || title.isEmpty()) {
                        title = "Table" + tableCount;
                    }

                    String sheetName = sanitizeSheetName(title);
                    if (sheetName.length() > 31) {
                        sheetName = sheetName.substring(0, 31);
                    }

                    // 保证 Sheet 唯一
                    String baseName = sheetName;
                    int suffix = 1;
                    while (workbook.getSheet(sheetName) != null) {
                        sheetName = baseName + "_" + suffix++;
                    }

                    Sheet sheet = workbook.createSheet(sheetName);
                    processTable(table, sheet);
                }
            }

            workbook.write(fos);
        }

        System.out.println("✅ 所有表格已提取并命名 Sheet！");
    }

    // 找出表格前面的标题段落
    private static String findTitleForTable(List<IBodyElement> elements, int tableIndex) {
        for (int i = tableIndex - 1; i >= 0; i--) {
            IBodyElement prev = elements.get(i);
            if (prev.getElementType() == BodyElementType.PARAGRAPH) {
                String text = ((XWPFParagraph) prev).getText();
                if (text != null && !text.trim().isEmpty()) {
                    return text.trim();
                }
            }
        }
        return null;
    }

    // 清洗为合法 sheet 名称
    private static String sanitizeSheetName(String name) {
        return name.replaceAll("[\\\\/?*\\[\\]:]", "_").trim();
    }

    // 处理表格逻辑（如前）
    private static void processTable(XWPFTable table, Sheet sheet) {
        Map<Integer, Map<Integer, Boolean>> skip = new HashMap<>();
        List<XWPFTableRow> rows = table.getRows();

        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            XWPFTableRow row = rows.get(rowIndex);
            Row excelRow = sheet.createRow(rowIndex);
            int colIndex = 0;
            List<XWPFTableCell> cells = row.getTableCells();

            for (int i = 0; i < cells.size(); i++) {
                while (skip.getOrDefault(rowIndex, new HashMap<>()).getOrDefault(colIndex, false)) {
                    colIndex++;
                }

                XWPFTableCell cell = cells.get(i);
                Cell excelCell = excelRow.createCell(colIndex);
                excelCell.setCellValue(cell.getText().trim());

                int rowSpan = getVMergeSpan(table, rowIndex, i);
                int colSpan = getHMergeSpan(cell);

                if (rowSpan > 1 || colSpan > 1) {
                    int endRow = rowIndex + rowSpan - 1;
                    int endCol = colIndex + colSpan - 1;
                    sheet.addMergedRegion(new CellRangeAddress(rowIndex, endRow, colIndex, endCol));
                    for (int r = rowIndex; r <= endRow; r++) {
                        for (int c = colIndex; c <= endCol; c++) {
                            if (r == rowIndex && c == colIndex) continue;
                            skip.computeIfAbsent(r, x -> new HashMap<>()).put(c, true);
                        }
                    }
                }

                colIndex++;
            }
        }
    }

    private static int getHMergeSpan(XWPFTableCell cell) {
        CTTc ctTc = cell.getCTTc();
        CTTcPr tcPr = ctTc.getTcPr();
        if (tcPr != null && tcPr.isSetGridSpan()) {
            return tcPr.getGridSpan().getVal().intValue();
        }
        return 1;
    }

    private static int getVMergeSpan(XWPFTable table, int rowIndex, int cellIndex) {
        int span = 1;
        List<XWPFTableRow> rows = table.getRows();
        XWPFTableCell firstCell = rows.get(rowIndex).getTableCells().get(cellIndex);
        if (!"restart".equals(getVMergeVal(firstCell))) return 1;

        for (int i = rowIndex + 1; i < rows.size(); i++) {
            List<XWPFTableCell> cells = rows.get(i).getTableCells();
            if (cellIndex >= cells.size()) break;
            String v = getVMergeVal(cells.get(cellIndex));
            if (!"continue".equals(v)) break;
            span++;
        }
        return span;
    }

    private static String getVMergeVal(XWPFTableCell cell) {
        if (cell.getCTTc() != null
            && cell.getCTTc().getTcPr() != null
            && cell.getCTTc().getTcPr().isSetVMerge()) {
            STMerge.Enum val = cell.getCTTc().getTcPr().getVMerge().getVal();
            return val != null ? val.toString() : "continue";
        }
        return null;
    }
}
