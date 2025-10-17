package io.github.aicyi.example.boot.service;

import io.github.aicyi.commons.util.DateTimeUtils;
import io.github.aicyi.commons.util.mapper.MapperUtils;
import io.github.aicyi.example.boot.AicyiExampleApplication;
import io.github.aicyi.example.domain.UserBean;
import io.github.aicyi.example.domain.UserQuery;
import io.github.aicyi.example.domain.entity.base.Student;
import io.github.aicyi.example.domain.entity.base.User;
import io.github.aicyi.example.domain.type.GradeType;
import io.github.aicyi.example.service.StudentService;
import io.github.aicyi.example.service.UserService;
import io.github.aicyi.test.util.BaseLoggerTest;
import io.github.aicyi.test.util.DataSource;
import io.github.aicyi.test.util.RandomGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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


    @Test
    @Override
    public void test() {
        UserBean userBean = DataSource.getUser();
        User user = MapperUtils.INSTANCE.map(userBean, User.class);
        userService.save(user);

        user = userService.getById(user.getId());

        Student student = new Student();
        student.setUserId(user.getId());
        student.setScore(DataSource.randomBigDecimal());
        student.setGradeType(RandomGenerator.randomEnum(GradeType.class));
        student.setRegisterTime(LocalDateTime.now());
        studentService.save(student);

        student = studentService.getById(student.getId());

        log("test", user, student);
    }

    @Test
    public void test1() {
        UserQuery query = new UserQuery();
        query.setMobileEq("15902571792");
        query.setIdCardEq("1f0a9a831f9e6ec3817d77e5fd2ca3bb");
        query.setBirthdayStart(DateTimeUtils.toLDateTime("2025-10-01 00:00:00.000").toLocalDate());
        query.setBirthdayEnd(DateTimeUtils.toLDateTime("2025-11-01 00:00:00.000").toLocalDate());

        List<User> userList = userService.list(query);

        log("test1", userList);
    }
}
