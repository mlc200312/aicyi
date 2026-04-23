package io.github.aicyi.test.util;

import io.github.aicyi.commons.lang.type.BooleanType;
import io.github.aicyi.commons.util.mapper.FieldMapBuilder;
import io.github.aicyi.commons.util.mapper.MapperUtils;
import io.github.aicyi.test.domain.Example;
import io.github.aicyi.test.domain.ExampleBean;
import io.github.aicyi.test.domain.Message;
import io.github.aicyi.example.domain.StudentBean;
import io.github.aicyi.example.domain.UserBean;
import io.github.aicyi.example.domain.type.GradeType;
import io.github.aicyi.example.domain.type.Week;
import io.github.aicyi.example.domain.type.Season;

import java.util.ArrayList;
import java.util.Date;

import io.github.aicyi.commons.util.id.IdGenerator;
import io.github.aicyi.commons.util.json.JsonUtils;
import io.github.aicyi.example.domain.type.GenderType;
import io.github.aicyi.test.dto.ExampleResp;
import io.github.aicyi.example.web.dto.StudentResp;
import org.apache.commons.lang3.RandomUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author Mr.Min
 * @description 数据测试Mock
 * @date 2019-05-22
 **/
public class DataSource {
    public static final int MAX_NUM = 3;

    public static BigDecimal randomBigDecimal() {
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

    public static UserBean getUser() {
        UserBean user = new UserBean();
        user.setId(IdGenerator.generateId());

        user.setId(IdGenerator.generateId());
        user.setAge(RandomUtils.nextInt(0, 100));
        user.setIdCard(IdGenerator.generateV7Id());
        user.setMobile(RandomGenerator.generatePhoneNum());
        user.setGenderType(RandomGenerator.randomEnum(GenderType.class));
        user.setBirthday(LocalDate.now());
        return user;
    }

    public static String getUserJson() {
        return JsonUtils.getInstance().toJson(getUser());
    }

    public static StudentBean getStudent() {
        StudentBean student = MapperUtils.INSTANCE.map(getUser(), StudentBean.class);
        student.setGradeType(RandomGenerator.randomEnum(GradeType.class));
        student.setScore(randomBigDecimal().doubleValue());
        student.setRegisterTime(LocalDateTime.now());
        student.setCreateTime(LocalDateTime.now());
        student.setUpdateTime(LocalDateTime.now());
        return student;
    }

    public static StudentResp getStudentResp() {
        return MapperUtils.INSTANCE.map(getStudent(), StudentResp.class, FieldMapBuilder.create()

                .add("score", "score0")
                .build());
    }

    public static List<StudentBean> getStudentList() {
        List<StudentBean> list = new ArrayList<>();
        for (int i = 0; i < MAX_NUM; i++) {
            list.add(getStudent());
        }
        return list;
    }

    public static Example getEmptyData() {
        return new Example();
    }

    public static ExampleBean getExample() {
        ExampleBean example = new ExampleBean();
        example.setId(IdGenerator.generateId());
        example.setIdx(RandomUtils.nextInt(1, 99));
        example.setStatus(RandomGenerator.randomEnum(BooleanType.class));
        example.setAmount(randomBigDecimal());
        example.setScore(randomBigDecimal().doubleValue());
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

    public static ExampleResp getExampleResp() {
        ExampleResp resp = MapperUtils.INSTANCE.map(getExample(), ExampleResp.class, FieldMapBuilder.create()
                .add("id", "uuid")
                .ignore("user")
                .ignore("student")
                .build());
        resp.setUser(getUserJson());
        resp.setStudent(getStudentResp());
        return resp;
    }

    public static List<ExampleBean> getExampleList() {
        List<ExampleBean> list = new ArrayList<>();
        for (int i = 0; i < MAX_NUM; i++) {
            list.add(getExample());
        }
        return list;
    }

    public static Map<Long, ExampleBean> getExampleMap() {
        Map<Long, ExampleBean> map = new HashMap<>(MAX_NUM);
        for (int i = 0; i < MAX_NUM; i++) {
            ExampleBean data = getExample();
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

    public static AicyiFactory getFactory(AicyiFactory.Robot robot) {
        return new AicyiFactory(1000, robot);
    }
}
