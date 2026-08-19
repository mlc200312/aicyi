package io.github.aicyi.commons.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.handler.WriteHandler;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import io.github.aicyi.commons.lang.Assert;
import io.github.aicyi.commons.lang.exception.SystemException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFSheet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Mr.Min
 * @description Excel工具类
 * @date 11:19
 **/
public final class ExcelUtils {
    /**
     * 默认读取的批处理大小
     */
    private static final int DEFAULT_BATCH_SIZE = 1000;

    private ExcelUtils() {
    }

    /**
     * 从字节数组读取Excel
     *
     * @param bytes 字节数组
     * @param clazz 数据模型类
     * @return 数据列表
     */
    public static <T> List<T> readFromBytes(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
            return EasyExcel.read(in).head(clazz).sheet().doReadSync();
        } catch (IOException e) {
            throw new SystemException("读取Excel失败", e);
        }
    }

    /**
     * 读取Excel文件
     *
     * @param filePath 文件路径
     * @param clazz    数据模型类
     * @return 数据列表
     */
    public static <T> List<T> readExcel(String filePath, Class<T> clazz) {
        return EasyExcel.read(filePath).head(clazz).sheet().doReadSync();
    }

    /**
     * 读取Excel文件（带监听器，适合大数据量）
     *
     * @param filePath 文件路径
     * @param clazz    数据模型类
     * @param listener 自定义监听器
     */
    public static <T> void readExcelWithListener(String filePath, Class<T> clazz, ExcelListener<T> listener) {
        EasyExcel.read(filePath, clazz, listener).sheet().doRead();
    }

    /**
     * 读取Excel文件（分批回调，内存受控，适合大数据量）
     * <p>
     * 每读满一批即回调 batchConsumer，全程不累积全量数据；
     * 参数非法在调用时立即抛出
     *
     * @param filePath      文件路径
     * @param clazz         数据模型类
     * @param batchSize     每批大小
     * @param batchConsumer 批次数据消费者
     */
    public static <T> void readExcelInBatches(String filePath, Class<T> clazz, int batchSize, Consumer<List<T>> batchConsumer) {
        Assert.notBlank(filePath, "filePath");
        Assert.notNull(clazz, "clazz");
        Assert.positive(batchSize, "batchSize");
        Assert.notNull(batchConsumer, "batchConsumer");
        readExcelWithListener(filePath, clazz, new ExcelListener<T>(batchSize) {
            @Override
            protected void processBatch(List<T> batchData) {
                batchConsumer.accept(batchData);
            }
        });
    }

    /**
     * 读取Excel文件（分批迭代）
     * <p>
     * 注意：返回的迭代器在创建时即一次性完成全文件解析并将全部批次载入内存，
     * 仅适合中小数据量；大文件请改用
     * {@link #readExcelInBatches(String, Class, int, Consumer)} 回调式 API
     *
     * @param filePath  文件路径
     * @param clazz     数据模型类
     * @param batchSize 每批大小
     * @return 分批数据迭代器
     */
    public static <T> Iterable<List<T>> readExcelInBatches(String filePath, Class<T> clazz, int batchSize) {
        Assert.notBlank(filePath, "filePath");
        Assert.notNull(clazz, "clazz");
        Assert.positive(batchSize, "batchSize");
        return () -> new ExcelBatchReader<>(filePath, clazz, batchSize);
    }

    /**
     * 读取Excel文件（默认分批大小）
     * <p>
     * 注意：同 {@link #readExcelInBatches(String, Class, int)}，迭代器会全量载入内存
     *
     * @param filePath 文件路径
     * @param clazz    数据模型类
     * @return 分批数据迭代器
     */
    public static <T> Iterable<List<T>> readExcelInBatches(String filePath, Class<T> clazz) {
        return readExcelInBatches(filePath, clazz, DEFAULT_BATCH_SIZE);
    }

    /**
     * 读取多个sheet的Excel文件
     *
     * @param filePath     文件路径
     * @param sheetClasses sheet配置（Map<sheetNo, Class>）
     * @return Map<sheetNo, 数据列表>
     */
    public static Map<Integer, List<?>> readMultiSheetExcel(String filePath, Map<Integer, Class<?>> sheetClasses) {
        Map<Integer, List<?>> result = new HashMap<>();
        try (ExcelReader excelReader = EasyExcel.read(filePath).build()) {
            for (Map.Entry<Integer, Class<?>> entry : sheetClasses.entrySet()) {
                result.put(entry.getKey(), readSheetData(excelReader, entry.getKey(), entry.getValue()));
            }
        }
        return result;
    }

    private static <T> List<T> readSheetData(ExcelReader excelReader, Integer sheetNo, Class<T> clazz) {
        List<T> data = new ArrayList<>();
        ReadSheet readSheet = EasyExcel
                .readSheet(sheetNo)
                .head(clazz)
                .registerReadListener(new ExcelListener<T>() {
                    @Override
                    protected void processBatch(List<T> batchData) {
                        data.addAll(batchData);
                    }
                }).build();
        excelReader.read(readSheet);
        return data;
    }

    /**
     * 导出Excel到字节数组
     *
     * @param sheetName sheet名称
     * @param data      数据列表
     * @param clazz     数据模型类
     * @return 字节数组
     */
    public static <T> byte[] exportToBytes(String sheetName, List<T> data, Class<T> clazz) {
        HorizontalCellStyleStrategy cellStyle = createCellStyle();
        return exportToBytes(sheetName, data, clazz, cellStyle);
    }

    /**
     * 导出Excel到字节数组
     *
     * @param sheetName    sheet名称
     * @param data         数据列表
     * @param clazz        数据模型类
     * @param writeHandler 样式策略
     * @return 字节数组
     */
    public static <T> byte[] exportToBytes(String sheetName, List<T> data, Class<T> clazz, WriteHandler writeHandler) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ExcelWriterSheetBuilder sheetBuilder = EasyExcel.write(out, clazz)
                    .sheet(1, sheetName);
            if (writeHandler != null) {
                sheetBuilder.registerWriteHandler(writeHandler);
            }
            sheetBuilder.doWrite(data);
            return out.toByteArray();
        } catch (IOException e) {
            throw new SystemException("导出Excel失败", e);
        }
    }

    /**
     * 导出Excel到文件
     *
     * @param filePath  文件路径
     * @param sheetName sheet名称
     * @param data      数据列表
     * @param clazz     数据模型类
     */
    public static <T> void exportToFile(String filePath, String sheetName, List<T> data, Class<T> clazz) {
        HorizontalCellStyleStrategy cellStyle = createCellStyle();
        exportToFile(filePath, sheetName, data, clazz, cellStyle);
    }

    /**
     * 导出Excel到文件
     *
     * @param filePath     文件路径
     * @param sheetName    sheet名称
     * @param data         数据列表
     * @param clazz        数据模型类
     * @param writeHandler 样式策略
     */
    public static <T> void exportToFile(String filePath, String sheetName, List<T> data, Class<T> clazz, WriteHandler writeHandler) {
        ExcelWriterSheetBuilder sheetBuilder = EasyExcel.write(filePath, clazz)
                .sheet(1, sheetName);
        if (writeHandler != null) {
            sheetBuilder.registerWriteHandler(writeHandler);
        }
        sheetBuilder.doWrite(data);
    }

    /**
     * 设置默认样式策略
     *
     * @return 默认样式策略
     */
    private static HorizontalCellStyleStrategy createCellStyle() {
        return new AutoColumnWidthAndWrapHandler();
    }

    /**
     * Excel监听器基类
     */
    public static abstract class ExcelListener<T> implements ReadListener<T> {
        private final List<T> dataList = new ArrayList<>();
        private final int batchSize;

        public ExcelListener() {
            this(DEFAULT_BATCH_SIZE);
        }

        public ExcelListener(int batchSize) {
            this.batchSize = batchSize;
        }

        @Override
        public void invoke(T data, AnalysisContext context) {
            dataList.add(data);
            if (dataList.size() >= batchSize) {
                processBatch(new ArrayList<>(dataList));
                dataList.clear();
            }
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            if (!dataList.isEmpty()) {
                processBatch(dataList);
            }
            onCompleted();
        }

        protected abstract void processBatch(List<T> batchData);

        protected void onCompleted() {
            // 可选的完成回调
        }
    }

    /**
     * @author Mr.Min
     * @description Excel分批读取器（构造时全量解析入内存，仅供中小数据量的迭代器式 API 使用）
     * @date 2026/4/21
     **/
    private static class ExcelBatchReader<T> implements Iterator<List<T>> {
        private final Iterator<List<T>> batchIterator;

        public ExcelBatchReader(String filePath, Class<T> clazz, int batchSize) {
            // 单次遍历文件，由监听器按 batchSize 累批，避免每批重复重读整个文件
            List<List<T>> batches = new ArrayList<>();
            ExcelListener<T> listener = new ExcelListener<T>(batchSize) {
                @Override
                protected void processBatch(List<T> batchData) {
                    batches.add(batchData);
                }
            };
            EasyExcel.read(filePath, clazz, listener).sheet().doRead();
            this.batchIterator = batches.iterator();
        }

        @Override
        public boolean hasNext() {
            return batchIterator.hasNext();
        }

        @Override
        public List<T> next() {
            return batchIterator.next();
        }
    }

    /**
     * @author Mr.Min
     * @description 自动列宽和换行处理程序
     * <p>
     * 换行通过内容样式 {@link WriteCellStyle#setWrapped(Boolean)} 声明，
     * 不在单元格级别修改共享 CellStyle（POI 样式为 workbook 级共享对象，
     * 运行时修改会污染所有复用该样式的单元格）；
     * 自动列宽默认开启，大数据量导出可通过构造器关闭以提升性能
     * @date 2026/4/21
     **/
    public static class AutoColumnWidthAndWrapHandler extends HorizontalCellStyleStrategy {

        private final boolean autoSizeEnabled;

        public AutoColumnWidthAndWrapHandler() {
            this(true);
        }

        public AutoColumnWidthAndWrapHandler(boolean autoSizeEnabled) {
            this.autoSizeEnabled = autoSizeEnabled;

            // 定义表头样式
            WriteCellStyle headWriteCellStyle = new WriteCellStyle();
            headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);

            // 定义内容样式（样式对象级别声明换行，避免逐单元格修改共享样式）
            WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
            contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.LEFT);
            contentWriteCellStyle.setWrapped(Boolean.TRUE);

            setHeadWriteCellStyle(headWriteCellStyle);

            setContentWriteCellStyleList(Collections.singletonList(contentWriteCellStyle));
        }

        public AutoColumnWidthAndWrapHandler(WriteCellStyle headWriteCellStyle, WriteCellStyle contentWriteCellStyle) {
            super(headWriteCellStyle, contentWriteCellStyle);
            this.autoSizeEnabled = true;
        }

        @Override
        public void afterCellDispose(CellWriteHandlerContext context) {
            super.afterCellDispose(context);

            if (!autoSizeEnabled) {
                return;
            }

            Sheet sheet = context.getWriteSheetHolder().getSheet();
            Cell cell = context.getCell();

            // 关键点：先确保列被跟踪（SXSSF 模式下必须）
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
        }
    }
}
