package io.github.aicyi.test.commons.util;

import io.github.aicyi.commons.lang.BaseBean;
import io.github.aicyi.commons.util.EasyExcelUtils;
import io.github.aicyi.commons.util.Maps;
import io.github.aicyi.commons.util.json.JsonUtils;
import io.github.aicyi.commons.util.mapper.MapperUtils;
import io.github.aicyi.example.domain.Student;
import io.github.aicyi.example.dto.StudentResp;
import io.github.aicyi.test.util.BaseLoggerTest;
import io.github.aicyi.test.util.DataSource;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
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
public class EasyExcelUtilsTest extends BaseLoggerTest {

    @Test
    @Override
    public void test() {
        List<BankExcel> bankExcelList = EasyExcelUtils.readExcel("src/test/resources/test/bank_insert.xlsx", BankExcel.class);

        assert bankExcelList != null && bankExcelList.size() == 15;

        log("test", bankExcelList);
    }

    @Test
    public void readExcelWithListenerTest() {
        int batchSize = 4;
        EasyExcelUtils.readExcelWithListener("src/test/resources/test/bank_insert.xlsx", BankExcel.class, new EasyExcelUtils.ExcelListener<BankExcel>(batchSize) {
            @Override
            protected void processBatch(List<BankExcel> batchData) {

                log("readExcelWithListenerTest", batchData);
            }
        });
    }

    @Test
    public void readExcelInBatchesTest() {
        Iterable<List<BankExcel>> iterable = EasyExcelUtils.readExcelInBatches("src/test/resources/test/bank_insert.xlsx", BankExcel.class);
        List<BankExcel> next = iterable.iterator().next();

        assert next != null && next.size() == 15;

        log("readExcelInBatchesTest", next);
    }

    @Test
    public void readMultiSheetExcelTest() {
        Map<Integer, Class<?>> classMap = Maps.of(0, BankExcel.class).and(1, BankExcel.class).and(2, BankExcel.class).build();
        Map<Integer, List<?>> map = EasyExcelUtils.readMultiSheetExcel("src/test/resources/test/bank_insert.xlsx", classMap);

        assert map != null && map.keySet().size() == 3;

        log("readMultiSheetExcelTest", JsonUtils.getInstance().toJson(map));
    }

    @SneakyThrows
    @Test
    public void exportToBytesTest() {
        List<Student> studentList = DataSource.getStudentList();
        List<StudentResp> respList = MapperUtils.INSTANCE.mapAsList(studentList, StudentResp.class);
        String absolutePath = new File("").getAbsoluteFile().getParentFile().getPath();
        Path path = Paths.get(absolutePath + "/aicyi-test/src/test/resources/test/excel_test_001.xlsx");
        byte[] bytes = EasyExcelUtils.exportToBytes("12123", respList, StudentResp.class);
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        }
        List<StudentResp> studentRespList = EasyExcelUtils.readFromBytes(bytes, StudentResp.class);

        assert studentRespList != null && studentRespList.size() == 3;

        log("exportToBytesTest", studentRespList);
    }

    @Test
    public void exportToFileTest() {
        List<Student> studentList = DataSource.getStudentList();
        List<StudentResp> respList = MapperUtils.INSTANCE.mapAsList(studentList, StudentResp.class);
        String absolutePath = new File("").getAbsoluteFile().getParentFile().getPath();
        String path = absolutePath + "/aicyi-test/src/test/resources/test/excel_test_002.xlsx";
        EasyExcelUtils.exportToFile(path, "123", respList, StudentResp.class);
    }

    @Getter
    @Setter
    public static class BankExcel extends BaseBean {
        //id	bankEnAbbr	bankEnName	countryCode	serviceCountryCode	bankType	bankCode
        private String id;
        private String bankEnAbbr;
        private String bankEnName;
        private String countryCode;
        private String serviceCountryCode;
        private String bankType;
        private String bankCode;
    }
}
