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

import java.time.LocalDateTime;
import java.util.List;

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
    private User user;
    private Student student;

    @Before
    public void testBefore() {
        testMobile = "15902571792";
        UserBean userBean = DataSource.getUser();
        user = MapperUtils.INSTANCE.map(userBean, User.class);
        user.setMobile(testMobile);
        user.setIdCard("1f0a9a831f9e6ec3817d77e5fd2ca3bb");
        user.setBirthday(DateTimeUtils.toLDateTime("2025-10-11 00:00:00.000").toLocalDate());

        student = new Student();
        student.setUserId(user.getId());
        student.setScore(DataSource.randomBigDecimal());
        student.setGradeType(RandomGenerator.randomEnum(GradeType.class));
        student.setRegisterTime(LocalDateTime.now());
    }


    @Test
    @Override
    public void test() {
        userService.save(user);
        student.setUserId(user.getId());
        studentService.register(student);

        user = userService.getById(user.getId());
        student = studentService.getById(student.getId());

        log("test", user, student);
    }

    @Test
    public void test0() {
        List<StudentBean> studentList = DataSource.getStudentList();
        studentList.forEach(item -> {
            User newUser = MapperUtils.INSTANCE.map(item, User.class);
            userService.save(newUser);
            Student newStudent = MapperUtils.INSTANCE.map(item, Student.class, FieldMapBuilder.create().ignore("userId").build());
            newStudent.setUserId(item.getId());
            studentService.register(newStudent);

            log("test0", item, newStudent);
        });
    }

    @Test
    public void test1() {
        StudentQuery query = new StudentQuery();
        query.setRegisterTimeStart(DateTimeUtils.toLDateTime("2020-10-01 00:00:00.000"));
        query.setRegisterTimeEnd(DateTimeUtils.toLDateTime("2026-11-01 23:59:59.999"));

        Pageable pageable = PageUtils.createPageable(1, 10, Sort.by(Sort.Order.desc("update_time"), Sort.Order.asc("id")));
        Page<Student> studentPage = studentService.pagedList(pageable, query);
        List<Student> content = studentPage.getContent();

        Student first = null;
        if (CollectionUtils.isNotEmpty(content)) {
            first = content.get(0);

            studentService.delete(first.getId());
        }

        log("test1", first);
    }

    @Test
    public void test2() {
        UserQuery query = new UserQuery();
        query.setMobileEq(testMobile);
        query.setIdCardEq("1f0a9a831f9e6ec3817d77e5fd2ca3bb");
        query.setBirthdayStart(DateTimeUtils.toLDateTime("2025-10-01 00:00:00.000").toLocalDate());
        query.setBirthdayEnd(DateTimeUtils.toLDateTime("2025-11-01 00:00:00.000").toLocalDate());

        Pageable pageable = PageUtils.createPageable(1, 10, Sort.by(Sort.Order.desc("update_time"), Sort.Order.asc("id")));
        List<User> list = userService.list(pageable, query);

        log("test2", list);
    }

    @Test
    public void test3() {
        UserQuery query = new UserQuery();
        query.setBirthdayStart(DateTimeUtils.toLDateTime("2024-10-01 00:00:00.000").toLocalDate());
        query.setBirthdayEnd(DateTimeUtils.toLDateTime("2026-11-01 00:00:00.000").toLocalDate());

        Pageable pageable = PageUtils.createPageable(1, 10, Sort.by(Sort.Order.desc("update_time"), Sort.Order.asc("id")));
        Page<User> pageResult = userService.pagedList(pageable, query);

        log("test3", pageResult.getContent(), pageResult.getTotalPages());
    }
}
