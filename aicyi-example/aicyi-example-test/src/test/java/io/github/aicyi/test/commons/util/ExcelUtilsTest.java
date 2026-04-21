package io.github.aicyi.test.commons.util;

import io.github.aicyi.commons.util.ExcelUtils;
import io.github.aicyi.commons.util.Maps;
import io.github.aicyi.commons.util.json.JsonUtils;
import io.github.aicyi.commons.util.mapper.MapperUtils;
import io.github.aicyi.example.domain.StudentBean;
import io.github.aicyi.test.domin.BankExcel;
import io.github.aicyi.test.domin.StudentExcel;
import io.github.aicyi.test.util.BaseLoggerTest;
import io.github.aicyi.test.util.DataSource;
import lombok.SneakyThrows;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 18:32
 **/
public class ExcelUtilsTest extends BaseLoggerTest {

    @Override
    public void beforeTest() {
    }

    @Test
    @Override
    public void test() {
        List<BankExcel> bankExcelList = ExcelUtils.readExcel("src/test/resources/test/bank_insert.xlsx", BankExcel.class);
        assert bankExcelList != null && bankExcelList.size() == 15;

        log("test", bankExcelList);
    }

    @Test
    public void readExcelWithListenerTest() {
        int batchSize = 4;
        ExcelUtils.readExcelWithListener("src/test/resources/test/bank_insert.xlsx", BankExcel.class, new ExcelUtils.ExcelListener<BankExcel>(batchSize) {
            @Override
            protected void processBatch(List<BankExcel> batchData) {

                log("readExcelWithListenerTest", batchData);
            }
        });
    }

    @Test
    public void readExcelInBatchesTest() {
        Iterable<List<BankExcel>> iterable = ExcelUtils.readExcelInBatches("src/test/resources/test/bank_insert.xlsx", BankExcel.class);
        List<BankExcel> next = iterable.iterator().next();
        assert next != null && next.size() == 15;

        log("readExcelInBatchesTest", next);
    }

    @Test
    public void readMultiSheetExcelTest() {
        Map<Integer, Class<?>> classMap = Maps.of(0, BankExcel.class).and(1, BankExcel.class).and(2, BankExcel.class).build();
        Map<Integer, List<?>> map = ExcelUtils.readMultiSheetExcel("src/test/resources/test/bank_insert.xlsx", classMap);
        assert map != null && map.keySet().size() == 3;

        log("readMultiSheetExcelTest", JsonUtils.getInstance().toJson(map));
    }

    @SneakyThrows
    @Test
    public void exportToBytesTest() {
        List<StudentBean> studentList = DataSource.getStudentList();
        List<StudentExcel> studentExcelList = MapperUtils.INSTANCE.mapAsList(studentList, StudentExcel.class);
        Path path = Paths.get("excel_001.xlsx");
        byte[] bytes = ExcelUtils.exportToBytes("12123", studentExcelList, StudentExcel.class);
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        }
        List<StudentExcel> readStudentExcelList = ExcelUtils.readFromBytes(bytes, StudentExcel.class);

        assert readStudentExcelList != null && readStudentExcelList.size() == 3;

        log("exportToBytesTest", readStudentExcelList);
    }

    @Test
    public void exportToFileTest() {
        List<StudentBean> studentList = DataSource.getStudentList();
        List<StudentExcel> studentExcelList = MapperUtils.INSTANCE.mapAsList(studentList, StudentExcel.class);
        String path = "excel_002.xlsx";
        ExcelUtils.exportToFile(path, "123", studentExcelList, StudentExcel.class);
    }
}
