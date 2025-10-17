package io.github.aicyi.example.service;

import io.github.aicyi.example.domain.StudentQuery;
import io.github.aicyi.example.domain.UserQuery;
import io.github.aicyi.example.domain.entity.base.Student;
import io.github.aicyi.example.domain.entity.base.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author Mr.Min
 * @description 用户服务
 * @date 14:51
 **/
public interface StudentService {

    void save(Student student);

    Student getById(Long id);

    List<Student> list(StudentQuery query);

    Page<Student> pagingList(Pageable pageable, StudentQuery query);
}
