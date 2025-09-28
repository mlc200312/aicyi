package io.github.aicyi.commons.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.handler.WriteHandler;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author Mr.Min
 * @description Excel工具类
 * @date 11:19
 **/
public class EasyExcelUtils {
    /**
     * 默认读取的批处理大小
     */
    private static final int DEFAULT_BATCH_SIZE = 1000;

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
            throw new RuntimeException("读取Excel失败", e);
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
     * 读取Excel文件（分批读取）
     *
     * @param filePath  文件路径
     * @param clazz     数据模型类
     * @param batchSize 每批大小
     * @return 分批数据迭代器
     */
    public static <T> Iterable<List<T>> readExcelInBatches(String filePath, Class<T> clazz, int batchSize) {
        return () -> new ExcelBatchReader<>(filePath, clazz, batchSize);
    }

    /**
     * 读取Excel文件（默认分批大小）
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
                Integer sheetNo = entry.getKey();
                Class<?> clazz = entry.getValue();
                List<?> batch = new ArrayList<>();
                ExcelListener listener = new ExcelListener() {
                    @Override
                    protected void processBatch(List batchData) {
                        batch.addAll(batchData);
                    }
                };
                ReadSheet readSheet = EasyExcel.readSheet(sheetNo).head(clazz).registerReadListener(listener).build();
                excelReader.read(readSheet);
                result.put(sheetNo, batch);
            }
        }
        return result;
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
            throw new RuntimeException("导出Excel失败", e);
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
     * 带样式的导出
     *
     * @param response  HttpServletResponse
     * @param fileName  文件名
     * @param sheetName sheet名称
     * @param data      数据列表
     * @param clazz     数据模型类
     */
    public static <T> void exportToResponse(HttpServletResponse response, String fileName, String sheetName, List<T> data, Class<T> clazz) throws IOException {
        HorizontalCellStyleStrategy cellStyle = createCellStyle();

        exportToResponse(response, fileName, sheetName, data, clazz, cellStyle);
    }

    /**
     * 带样式的导出
     *
     * @param response  HttpServletResponse
     * @param fileName  文件名
     * @param sheetName sheet名称
     * @param data      数据列表
     * @param clazz     数据模型类
     */
    public static <T> void exportToResponse(HttpServletResponse response, String fileName, String sheetName, List<T> data, Class<T> clazz, WriteHandler writeHandler) throws IOException {
        setResponse(response, fileName);

        ExcelWriterSheetBuilder sheetBuilder = EasyExcel.write(response.getOutputStream(), clazz)
                .sheet(1, sheetName);
        if (writeHandler != null) {
            sheetBuilder.registerWriteHandler(writeHandler);
        }
        sheetBuilder.doWrite(data);
    }

    /**
     * 设置默认样式策略
     *
     * @return
     */
    private static HorizontalCellStyleStrategy createCellStyle() {
        return new AutoColumnWidthAndWrapHandler();
    }


    /**
     * HttpServletResponse 设置
     *
     * @param response
     * @param fileName
     * @throws IOException
     */
    private static void setResponse(HttpServletResponse response, String fileName) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encodedFileName + ".xlsx");
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
     * Excel分批读取器
     */
    private static class ExcelBatchReader<T> implements Iterator<List<T>> {
        private final String filePath;
        private final Class<T> clazz;
        private final int batchSize;
        private ExcelReader excelReader;
        private List<T> nextBatch;

        public ExcelBatchReader(String filePath, Class<T> clazz, int batchSize) {
            this.filePath = filePath;
            this.clazz = clazz;
            this.batchSize = batchSize;
            this.excelReader = EasyExcel.read(filePath).build();
            this.nextBatch = readNextBatch();
        }

        public String getFilePath() {
            return filePath;
        }

        @Override
        public boolean hasNext() {
            return nextBatch != null && !nextBatch.isEmpty();
        }

        @Override
        public List<T> next() {
            List<T> currentBatch = nextBatch;
            nextBatch = readNextBatch();
            if (currentBatch == null) {
                throw new NoSuchElementException();
            }
            return currentBatch;
        }

        private List<T> readNextBatch() {
            if (excelReader == null) {
                return Collections.emptyList();
            }

            List<T> batch = new ArrayList<>();
            ExcelListener<T> listener = new ExcelListener<T>(batchSize) {
                @Override
                protected void processBatch(List<T> batchData) {
                    batch.addAll(batchData);
                }

                @Override
                protected void onCompleted() {
                    excelReader.finish();
                    excelReader = null;
                }
            };

            ReadSheet readSheet = EasyExcel.readSheet(0).head(clazz).registerReadListener(listener).build();
            excelReader.read(readSheet);

            return batch;
        }
    }
}
