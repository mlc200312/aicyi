package io.github.aicyi.example.web;

import io.github.aicyi.example.web.dto.StudentResp;
import io.github.aicyi.example.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 15:45
 **/
@RestController
@RequestMapping("/user")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @RequestMapping("/list")
    public StudentResp list() {
        return null;
    }
}
