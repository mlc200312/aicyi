package io.github.aicyi.example.service;

import io.github.aicyi.example.domain.StudentBean;
import io.github.aicyi.example.domain.StudentQuery;
import io.github.aicyi.example.domain.entity.base.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author Mr.Min
 * @description 用户服务
 * @date 14:51
 **/
public interface StudentService {

    void register(StudentBean studentBean);

    void delete(Long id);

    void update(Student student);

    StudentBean getById(Long id);

    Student getByMobile(String mobile);

    List<Student> list(StudentQuery query);

    Page<Student> pagedList(Pageable pageable, StudentQuery query);
}
