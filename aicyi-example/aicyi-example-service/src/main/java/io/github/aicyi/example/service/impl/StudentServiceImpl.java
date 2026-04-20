package io.github.aicyi.example.service.impl;

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
import io.github.aicyi.example.service.StudentService;
import io.github.aicyi.example.service.UserService;
import io.github.aicyi.midware.db.commons.BaseEntityUtils;
import io.github.aicyi.midware.db.commons.PageUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
    public Student getById(Long id) {
        return studentMapper.selectByPrimaryKey(id);
    }

    @Override
    public Student getByMobile(String mobile) {
        return studentCustomMapper.selectByMobile(mobile);
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
    public Page<Student> pagedList(Pageable pageable, StudentQuery query) {
        return PageUtils.createPage(pageable, () -> list(query));
    }
}
