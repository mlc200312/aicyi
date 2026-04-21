package io.github.aicyi.example.web;

import io.github.aicyi.commons.util.mapper.FieldMapBuilder;
import io.github.aicyi.commons.util.mapper.MapperUtils;
import io.github.aicyi.example.domain.StudentBean;
import io.github.aicyi.example.domain.entity.base.Student;
import io.github.aicyi.example.web.dto.StudentResp;
import io.github.aicyi.example.service.StudentService;
import io.github.aicyi.midware.web.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 15:45
 **/
@Api(value = "学生控制器", tags = {"学生控制器"})
@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @ApiOperation(value = "查询学生", notes = "查询学生")
    @RequestMapping(value = "/get-by-id", method = RequestMethod.GET)
    public Result<StudentResp> getById(@RequestParam String id) {
        StudentBean bean = studentService.getById(Long.valueOf(id));
        StudentResp resp = MapperUtils.INSTANCE.map(bean, StudentResp.class, FieldMapBuilder.create()
                .add("username", "userName")
                .add("score", "score0")
                .build());
        return Result.success(resp);
    }
}
