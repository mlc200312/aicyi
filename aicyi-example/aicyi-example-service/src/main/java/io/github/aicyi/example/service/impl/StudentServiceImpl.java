package io.github.aicyi.example.service.impl;

import io.github.aicyi.commons.lang.SmartMapper;
import io.github.aicyi.commons.lang.exception.BusinessException;
import io.github.aicyi.commons.util.NumberUtils;
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

import java.time.LocalDateTime;
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
    private SmartMapper smartMapper;
    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private StudentCustomMapper studentCustomMapper;
    @Autowired
    private UserService userService;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void add(StudentBean bean) {
        UserQuery userQuery = new UserQuery();
        userQuery.setMobileEq(bean.getMobile());
        userQuery.setIdCardEq(bean.getIdCard());
        List<User> list = userService.list(userQuery);

        User newUser;
        if (CollectionUtils.isEmpty(list)) {
            newUser = smartMapper.map(bean, User.class);
            userService.save(newUser);
        } else {
            newUser = list.get(0);
        }

        Student newStudent = smartMapper.map(bean, Student.class);
        newStudent.setUserId(newUser.getId());
        newStudent.setRegisterTime(LocalDateTime.now());
        BaseEntityUtils.setDefaultValue(newStudent);
        studentMapper.insertSelective(newStudent);
    }

    @Override
    public void delete(Long id) {
        studentMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void update(StudentBean bean) {
        Student student = smartMapper.map(bean, Student.class);
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
    public StudentBean getByMobile(String mobile) {
        Student student = studentCustomMapper.selectByMobile(mobile);
        if (Objects.isNull(student)) {
            throw new BusinessException(ExampleResultCode.OBJECT_NOT_FOUND);
        }
        User user = userService.getById(student.getUserId());
        return createStudentBean(student, user);
    }

    @Override
    public List<Student> list(StudentQuery query) {
        StudentExample studentExample = new StudentExample();
        StudentExample.Criteria criteria = studentExample.createCriteria();
        if (Objects.isNull(query)) {
            return Collections.emptyList();
        }
        if (NumberUtils.isPositive(query.getUserIdEq())) {
            criteria.andUserIdEqualTo(query.getUserIdEq());
        }
        if (Objects.nonNull(query.getGradeTypeEq())) {
            criteria.andGradeTypeEqualTo(query.getGradeTypeEq());
        }
        if (Objects.nonNull(query.getRegisterTimeStart())) {
            criteria.andRegisterTimeGreaterThan(query.getRegisterTimeStart());
        }
        if (Objects.nonNull(query.getRegisterTimeEnd())) {
            criteria.andRegisterTimeLessThanOrEqualTo(query.getRegisterTimeEnd());
        }
        return studentMapper.selectByExample(studentExample);
    }

    @Override
    public Page<StudentBean> pagedList(StudentQuery query) {
        Page<Student> page = PageUtils.getPage(query, () -> list(query));
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
        StudentBean studentBean = smartMapper.map(student, StudentBean.class);
        smartMapper.map(user, studentBean);
        return studentBean;
    }
}
