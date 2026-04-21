package io.github.aicyi.example.boot.service;

import io.github.aicyi.commons.util.DateTimeUtils;
import io.github.aicyi.commons.util.mapper.FieldMapBuilder;
import io.github.aicyi.commons.util.mapper.MapperUtils;
import io.github.aicyi.example.boot.AicyiExampleApplication;
import io.github.aicyi.example.domain.StudentBean;
import io.github.aicyi.example.domain.StudentQuery;
import io.github.aicyi.example.domain.UserBean;
import io.github.aicyi.example.domain.UserQuery;
import io.github.aicyi.example.domain.entity.base.Student;
import io.github.aicyi.example.domain.entity.base.User;
import io.github.aicyi.example.domain.type.GradeType;
import io.github.aicyi.example.service.StudentService;
import io.github.aicyi.example.service.UserService;
import io.github.aicyi.midware.db.commons.PageUtils;
import io.github.aicyi.test.util.BaseLoggerTest;
import io.github.aicyi.test.util.DataSource;
import io.github.aicyi.test.util.RandomGenerator;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 15:32
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AicyiExampleApplication.class)
public class ServiceTest extends BaseLoggerTest {

    @Autowired
    private UserService userService;
    @Autowired
    private StudentService studentService;

    private String testMobile;
    private StudentBean studentBean;


    @Before
    @Override
    public void beforeTest() {
        testMobile = "15902571792";
        studentBean = DataSource.getStudent();
        studentBean.setMobile(testMobile);
        studentBean.setIdCard("1f0a9a831f9e6ec3817d77e5fd2ca3bb");
        studentBean.setBirthday(DateTimeUtils.toLDateTime("2025-10-11 00:00:00.000").toLocalDate());
    }

    @Test
    @Override
    public void test() {
        studentService.register(studentBean);
        Student student = studentService.getByMobile(studentBean.getMobile());
        User user = userService.getById(student.getUserId());

        log(user, student);
    }

    @Test
    public void test0() {
        List<StudentBean> studentBeanList = DataSource.getStudentList();
        studentBeanList.forEach(item -> studentService.register(item));
    }

    @Test
    public void test1() {
        Student student = studentService.getByMobile(testMobile);
        studentService.delete(student.getId());

        log(student);
    }

    @Test
    public void test2() {
        Student student = studentService.getByMobile(testMobile);
        if (Objects.nonNull(student)) {
            student.setScore(new BigDecimal(100));
            studentService.update(student);
        }
    }

    @Test
    public void test3() {
        UserQuery query = new UserQuery();
        query.setMobileEq(testMobile);
        query.setIdCardEq("1f0a9a831f9e6ec3817d77e5fd2ca3bb");
        query.setBirthdayStart(DateTimeUtils.toLDateTime("2025-10-01 00:00:00.000").toLocalDate());
        query.setBirthdayEnd(DateTimeUtils.toLDateTime("2025-11-01 00:00:00.000").toLocalDate());

        Pageable pageable = PageUtils.createPageable(1, 10, Sort.by(Sort.Order.desc("update_time"), Sort.Order.asc("id")));
        List<User> list = userService.list(pageable, query);

        log(list);
    }

    @Test
    public void test30() {
        UserQuery query = new UserQuery();
        query.setBirthdayStart(DateTimeUtils.toLDateTime("2024-10-01 00:00:00.000").toLocalDate());
        query.setBirthdayEnd(DateTimeUtils.toLDateTime("2026-11-01 00:00:00.000").toLocalDate());

        Pageable pageable = PageUtils.createPageable(1, 10, Sort.by(Sort.Order.desc("update_time"), Sort.Order.asc("id")));
        Page<User> pageResult = userService.pagedList(pageable, query);

        log(pageResult.getContent(), pageResult.getTotalPages());
    }
}
