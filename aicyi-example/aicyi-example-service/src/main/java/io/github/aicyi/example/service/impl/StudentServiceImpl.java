package io.github.aicyi.example.service.impl;

import io.github.aicyi.example.dao.mapper.base.StudentMapper;
import io.github.aicyi.example.domain.StudentQuery;
import io.github.aicyi.example.domain.entity.base.Student;
import io.github.aicyi.example.domain.entity.base.StudentExample;
import io.github.aicyi.example.service.StudentService;
import io.github.aicyi.midware.db.commons.BaseEntityUtils;
import io.github.aicyi.midware.db.commons.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    @Override
    public void register(Student student) {
        BaseEntityUtils.setDefaultValue(student);
        studentMapper.insertSelective(student);
    }

    @Override
    public Student getById(Long id) {
        return studentMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Student> list(StudentQuery query) {
        StudentExample studentExample = new StudentExample();
        StudentExample.Criteria criteria = studentExample.createCriteria();
        if (Objects.isNull(query)) {
            return Collections.emptyList();
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

    @Override
    public void delete(Long id) {
        studentMapper.deleteByPrimaryKey(id);
    }
}
