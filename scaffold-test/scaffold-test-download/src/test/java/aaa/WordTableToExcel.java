package aaa;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordTableToExcel {

    public static void main(String[] args) throws Exception {
        String inputDocx = "C:\\Users\\lqsgo\\Desktop\\trans\\111.docx";
        String outputXlsx = "C:\\Users\\lqsgo\\Desktop\\trans\\111.xlsx";

        try (
                FileInputStream fis = new FileInputStream(inputDocx);
                XWPFDocument document = new XWPFDocument(fis);
                Workbook workbook = new XSSFWorkbook();
                FileOutputStream fos = new FileOutputStream(outputXlsx)
        ) {
            List<XWPFTable> tables = document.getTables();
            int sheetIndex = 1;

            for (XWPFTable table : tables) {
                Sheet sheet = workbook.createSheet("Table" + (sheetIndex++));
                processTable(table, sheet);
            }

            workbook.write(fos);
        }

        System.out.println("✅ 导出完成！");
    }

    private static void processTable(XWPFTable table, Sheet sheet) {
        Map<String, CellRangeAddress> mergedCells = new HashMap<>();
        Map<Integer, Map<Integer, Boolean>> skip = new HashMap<>();

        List<XWPFTableRow> rows = table.getRows();

        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            XWPFTableRow row = rows.get(rowIndex);
            Row excelRow = sheet.createRow(rowIndex);

            int colIndex = 0;
            List<XWPFTableCell> cells = row.getTableCells();

            for (int i = 0; i < cells.size(); i++) {
                // 跳过已合并进来的单元格
                while (skip.getOrDefault(rowIndex, new HashMap<>()).getOrDefault(colIndex, false)) {
                    colIndex++;
                }

                XWPFTableCell cell = cells.get(i);
                Cell excelCell = excelRow.createCell(colIndex);
                excelCell.setCellValue(cell.getText().trim());

                int rowSpan = getVMergeSpan(table, rowIndex, i);
                int colSpan = getHMergeSpan(cell);

                // 合并记录
                if (rowSpan > 1 || colSpan > 1) {
                    int endRow = rowIndex + rowSpan - 1;
                    int endCol = colIndex + colSpan - 1;
                    sheet.addMergedRegion(new CellRangeAddress(rowIndex, endRow, colIndex, endCol));

                    // 标记跳过单元格
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
