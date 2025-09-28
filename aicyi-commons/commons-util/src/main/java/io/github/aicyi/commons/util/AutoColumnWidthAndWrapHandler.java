package io.github.aicyi.commons.util;

import com.alibaba.excel.util.ListUtils;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFSheet;


/**
 * @author Mr.Min
 * @description 业务描述
 * @date 15:03
 **/
public class AutoColumnWidthAndWrapHandler extends HorizontalCellStyleStrategy {

    public AutoColumnWidthAndWrapHandler() {
        // 定义表头样式
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);

        // 定义内容样式
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.LEFT);

        setHeadWriteCellStyle(headWriteCellStyle);

        setContentWriteCellStyleList(ListUtils.newArrayList(new WriteCellStyle[]{contentWriteCellStyle}));
    }

    public AutoColumnWidthAndWrapHandler(WriteCellStyle headWriteCellStyle, WriteCellStyle contentWriteCellStyle) {
        super(headWriteCellStyle, contentWriteCellStyle);
    }

    @Override
    public void afterCellDispose(CellWriteHandlerContext context) {
        Sheet sheet = context.getWriteSheetHolder().getSheet();

        Cell cell = context.getCell();

        // 关键点：先确保列被跟踪
        if (sheet instanceof SXSSFSheet) {
            ((SXSSFSheet) sheet).trackColumnForAutoSizing(cell.getColumnIndex());
        }

        // 自动列宽
        sheet.autoSizeColumn(cell.getColumnIndex());

        // 设置最小宽度
        int columnWidth = sheet.getColumnWidth(cell.getColumnIndex());
        if (columnWidth < 3000) {
            sheet.setColumnWidth(cell.getColumnIndex(), 3000);
        }

        // 自动换行
        CellStyle cellStyle = cell.getCellStyle();
        cellStyle.setWrapText(true);

        super.afterCellDispose(context);
    }
}
