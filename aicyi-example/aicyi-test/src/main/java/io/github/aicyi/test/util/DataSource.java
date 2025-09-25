package io.github.aicyi.test.util;

import io.github.aicyi.commons.lang.type.BooleanType;
import io.github.aicyi.commons.util.DateTimeUtils;
import io.github.aicyi.commons.util.DateUtils;
import io.github.aicyi.commons.util.mapper.MapperUtils;
import io.github.aicyi.example.domain.Example;
import io.github.aicyi.example.domain.Message;
import io.github.aicyi.example.domain.Student;
import io.github.aicyi.example.domain.User;
import io.github.aicyi.example.domain.type.GradeType;
import io.github.aicyi.example.domain.type.Week;
import io.github.aicyi.example.domain.type.Season;

import java.util.ArrayList;
import java.util.Date;

import io.github.aicyi.commons.util.id.IdGenerator;
import io.github.aicyi.commons.util.json.JsonUtils;
import io.github.aicyi.example.domain.type.GenderType;
import io.github.aicyi.example.dto.ExampleResp;
import io.github.aicyi.example.dto.StudentResp;
import org.apache.commons.lang3.RandomUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author Mr.Min
 * @description 功能描述
 * @date 2019-05-22
 **/
public class DataSource {
    public static final int MAX_NUM = 3;

    public static BigDecimal getBigDecimal() {
        return BigDecimal.valueOf(RandomUtils.nextDouble(0, 100)).setScale(2, RoundingMode.HALF_UP);
    }

    public static Message getMessage() {
        Message message = new Message();
        message.setToUserName(RandomGenerator.generateFullName());
        message.setFromUserName(RandomGenerator.generateFullName());
        message.setCreateTime(System.currentTimeMillis());
        message.setMsgType("1");
        message.setContent(RandomGenerator.generatePhoneNum());
        message.setMsgId(IdGenerator.generateId());
        return message;
    }

    public static String getMessageXml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<xml Content=\"" + RandomGenerator.generatePhoneNum() + "\" CreateTime=\"" + System.currentTimeMillis() + "\" FromUserName=\"" + RandomGenerator.generateFullName() + "\" MsgId=\"" + IdGenerator.generateId() + "\" MsgType=\"1\" ToUserName=\"" + RandomGenerator.generateFullName() + "\"/>";
    }

    public static User getUser() {
        User user = new User();
        user.setUserId(IdGenerator.generateV7Id());
        user.setDeviceId(IdGenerator.generateV7Id());
        user.setUsername(RandomGenerator.generateFullName());
        user.setMasterDevice(false);

        user.setId(IdGenerator.generateId());
        user.setAge(RandomUtils.nextInt(0, 100));
        user.setIdCard(IdGenerator.generateV7Id());
        user.setUsername(RandomGenerator.generateFullName());
        user.setMobile(RandomGenerator.generatePhoneNum());
        user.setGenderType(RandomGenerator.randomEnum(GenderType.class));
        user.setBirthday(LocalDate.now());
        return user;
    }

    public static String getUserJson() {
        return JsonUtils.getInstance().toJson(getUser());
    }

    public static Student getStudent() {
        Student student = MapperUtils.INSTANCE.map(getUser(), Student.class);
        student.setGradeType(RandomGenerator.randomEnum(GradeType.class));
        student.setScore(getBigDecimal().doubleValue());
        student.setRegisterTime(LocalDateTime.now());
        student.setCreateTime(new Date());
        student.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        return student;
    }

    public static List<Student> getStudentList() {
        List<Student> list = new ArrayList<>();
        for (int i = 0; i < MAX_NUM; i++) {
            list.add(getStudent());
        }
        return list;
    }

    public static Map<Long, Student> getStudentMap() {
        Map<Long, Student> map = new HashMap<>(MAX_NUM);
        for (int i = 0; i < MAX_NUM; i++) {
            Student data = getStudent();
            map.put(data.getId(), data);
        }
        return map;
    }

    public static StudentResp getStudentResp() {
        StudentResp resp = new StudentResp();
        resp.setGradeType(RandomGenerator.randomEnum(GradeType.class).getCode());
        resp.setScore0(getBigDecimal().doubleValue() + "");
        resp.setRegisterTime(DateTimeUtils.formatLDateTime(LocalDateTime.now()));
        resp.setCreateTime(DateUtils.formatDate(new Date()));
        resp.setUpdateTime(System.currentTimeMillis() + "");

        resp.setId(IdGenerator.generateId() + "");
        resp.setAge(RandomUtils.nextInt(0, 100));
        resp.setIdCard(IdGenerator.generateV7Id());
        resp.setUserName(RandomGenerator.generateFullName());
        resp.setMobile(RandomGenerator.generatePhoneNum());
        resp.setGenderType(RandomUtils.nextInt(1, 2));
        resp.setBirthday(LocalDate.now().format(DateTimeFormatter.ofPattern(DateTimeUtils.DATE_PATTERN)));
        return resp;
    }

    public static Example getEmptyData() {
        return new Example();
    }

    public static Example getExample() {
        Example example = new Example();
        example.setId(IdGenerator.generateId());
        example.setIdx(RandomUtils.nextInt(1, 99));
        example.setStatus(RandomGenerator.randomEnum(BooleanType.class));
        example.setAmount(getBigDecimal());
        example.setScore(getBigDecimal().doubleValue());
        example.setDate(new Date());
        example.setLocalDate(LocalDate.now());
        example.setDateTime(LocalDateTime.now());
        example.setTimestamp(new Timestamp(System.currentTimeMillis()));
        example.setSeason(RandomGenerator.randomEnum(Season.class));
        example.setWeek(RandomGenerator.randomEnum(Week.class));
        example.setIdList(Arrays.asList(1L, 2L, 3L));
        example.setUser(getUser());
        example.setStudent(getStudent());
        example.setNothing("nothing");
        return example;
    }

    public static List<Example> getExampleList() {
        List<Example> list = new ArrayList<>();
        for (int i = 0; i < MAX_NUM; i++) {
            list.add(getExample());
        }
        return list;
    }

    public static Map<Long, Example> getExampleMap() {
        Map<Long, Example> map = new HashMap<>(MAX_NUM);
        for (int i = 0; i < MAX_NUM; i++) {
            Example data = getExample();
            map.put(data.getId(), data);
        }
        return map;
    }

    public static String getExampleJson() {
        return JsonUtils.getInstance().toJson(getExample());
    }

    public static String getExampleListJson() {
        return JsonUtils.getInstance().toJson(getExampleList());
    }

    public static String getExampleMapJson() {
        return JsonUtils.getInstance().toJson(getExampleMap());
    }

    public static ExampleResp getExampleResp() {
        ExampleResp resp = new ExampleResp();
        resp.setUuid(IdGenerator.generateId() + "");
        resp.setIdx(RandomUtils.nextInt(1, 99));
        resp.setStatus(RandomUtils.nextInt(0, 1));
        resp.setAmount(getBigDecimal().toString());
        resp.setScore(getBigDecimal().toString());
        resp.setDate(DateUtils.formatDate(new Date()));
        resp.setLocalDate(LocalDate.now().format(DateTimeFormatter.ofPattern(DateTimeUtils.DATE_PATTERN)));
        resp.setDateTime(DateTimeUtils.formatLDateTime(LocalDateTime.now()));
        resp.setTimestamp(System.currentTimeMillis() + "");
        resp.setSeason(RandomGenerator.randomEnum(Season.class).getCode());
        resp.setWeek(RandomGenerator.randomEnum(Week.class).getCode());
        resp.setIdList(Arrays.asList("1", "2", "3"));
        resp.setUser(getUserJson());
        resp.setStudent(getStudentResp());
        resp.setNothing("nothing");
        return resp;
    }

    public static AicyiFactory getFactory(AicyiFactory.Robot robot) {
        return new AicyiFactory(1000, robot);
    }
}
