package io.github.aicyi.example.service.impl;

import io.github.aicyi.commons.lang.BusinessException;
import io.github.aicyi.commons.util.NumberUtils;
import io.github.aicyi.commons.util.mapper.FieldMapBuilder;
import io.github.aicyi.commons.util.mapper.MapperUtils;
import io.github.aicyi.example.dao.mapper.StudentCustomMapper;
import io.github.aicyi.example.dao.mapper.base.StudentMapper;
import io.github.aicyi.example.domain.StudentBean;
import io.github.aicyi.example.domain.StudentQuery;
import io.github.aicyi.example.domain.UserQuery;
import io.github.aicyi.example.domain.entity.base.Student;
import io.github.aicyi.example.domain.entity.base.StudentExample;
import io.github.aicyi.example.domain.entity.base.User;
import io.github.aicyi.example.domain.type.ExampleResultCode;
import io.github.aicyi.example.service.StudentService;
import io.github.aicyi.example.service.UserService;
import io.github.aicyi.midware.db.commons.BaseEntityUtils;
import io.github.aicyi.midware.db.commons.PageUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 17:52
 **/
@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private StudentCustomMapper studentCustomMapper;
    @Autowired
    private UserService userService;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void register(StudentBean studentBean) {
        FieldMapBuilder.FieldMapConfig config = FieldMapBuilder.create().ignore("userId").build();

        UserQuery userQuery = new UserQuery();
        userQuery.setMobileEq(studentBean.getMobile());
        List<User> list = userService.list(userQuery);

        User newUser;
        if (CollectionUtils.isEmpty(list)) {
            newUser = MapperUtils.INSTANCE.map(studentBean, User.class, config);
            userService.save(newUser);
        } else {
            newUser = list.get(0);
        }

        Student newStudent = MapperUtils.INSTANCE.map(studentBean, Student.class, config);
        newStudent.setUserId(newUser.getId());
        BaseEntityUtils.setDefaultValue(newStudent);
        studentMapper.insertSelective(newStudent);
    }

    @Override
    public void delete(Long id) {
        studentMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void update(Student student) {
        studentMapper.updateByPrimaryKeySelective(student);
    }

    @Override
    public StudentBean getById(Long id) {
        Student student = studentMapper.selectByPrimaryKey(id);
        if (Objects.isNull(student)) {
            throw new BusinessException(ExampleResultCode.OBJECT_NOT_FOUND);
        }
        User user = userService.getById(student.getUserId());
        return createStudentBean(student, user);
    }

    @Override
    public Student getByMobile(String mobile) {
        return studentCustomMapper.selectByMobile(mobile);
    }

    @Override
    public List<Student> list(StudentQuery studentQuery) {
        StudentExample studentExample = new StudentExample();
        StudentExample.Criteria criteria = studentExample.createCriteria();
        if (Objects.isNull(studentQuery)) {
            return Collections.emptyList();
        }
        if (NumberUtils.isPositive(studentQuery.getUserIdEq())) {
            criteria.andUserIdEqualTo(studentQuery.getUserIdEq());
        }
        if (Objects.nonNull(studentQuery.getGradeTypeEq())) {
            criteria.andGradeTypeEqualTo(studentQuery.getGradeTypeEq());
        }
        if (Objects.nonNull(studentQuery.getRegisterTimeStart())) {
            criteria.andRegisterTimeGreaterThan(studentQuery.getRegisterTimeStart());
        }
        if (Objects.nonNull(studentQuery.getRegisterTimeEnd())) {
            criteria.andRegisterTimeLessThanOrEqualTo(studentQuery.getRegisterTimeEnd());
        }
        return studentMapper.selectByExample(studentExample);
    }

    @Override
    public Page<StudentBean> pagedList(StudentQuery studentQuery) {
        Page<Student> page = PageUtils.getPage(studentQuery, () -> list(studentQuery));
        List<Student> studentList = page.getContent();
        if (CollectionUtils.isNotEmpty(studentList)) {
            UserQuery userQuery = new UserQuery();
            List<Long> userIdList = studentList.stream().map(Student::getUserId).collect(Collectors.toList());
            userQuery.setIdListIn(userIdList);
            List<User> userList = userService.list(userQuery);
            Map<Long, User> userMap = userList.stream().collect(Collectors.toMap(User::getId, o -> o));
            return page.map(item -> createStudentBean(item, userMap.get(item.getUserId())));
        }
        return Page.empty(page.getPageable());
    }

    private StudentBean createStudentBean(Student student, User user) {
        MapperUtils instance = MapperUtils.INSTANCE;
        StudentBean studentBean = instance.map(student, StudentBean.class, FieldMapBuilder.create()
                .ignore("userId")
                .build());
        instance.map(user, studentBean);
        return studentBean;
    }
}
