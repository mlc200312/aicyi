package io.github.aicyi.example.service.impl;

import io.github.aicyi.example.dao.mapper.base.StudentMapper;
import io.github.aicyi.example.domain.StudentQuery;
import io.github.aicyi.example.domain.entity.base.Student;
import io.github.aicyi.example.domain.util.EntityUtils;
import io.github.aicyi.example.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

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
    public void save(Student student) {
        EntityUtils.setDefaultValue(student);
        studentMapper.insertSelective(student);
    }

    @Override
    public Student getById(Long id) {
        return studentMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Student> list(StudentQuery query) {
        return Collections.emptyList();
    }

    @Override
    public Page<Student> pagingList(Pageable pageable, StudentQuery query) {
        return null;
    }
}
