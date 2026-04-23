package io.github.aicyi.example.web;

import io.github.aicyi.commons.lang.IResponse;
import io.github.aicyi.example.domain.StudentQuery;
import io.github.aicyi.midware.web.PageResponse;
import io.github.aicyi.midware.web.Response;
import io.github.aicyi.commons.util.mapper.FieldMapBuilder;
import io.github.aicyi.commons.util.mapper.MapperUtils;
import io.github.aicyi.example.domain.StudentBean;
import io.github.aicyi.example.web.dto.StudentReq;
import io.github.aicyi.example.web.dto.StudentResp;
import io.github.aicyi.example.service.StudentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public IResponse<StudentResp> getById(@RequestParam String id) {
        StudentBean bean = studentService.getById(Long.valueOf(id));
        StudentResp resp = MapperUtils.INSTANCE.map(bean, StudentResp.class, FieldMapBuilder.create()
                .add("username", "userName")
                .add("score", "score0")
                .build());
        return Response.success(resp);
    }

    @ApiOperation(value = "分页查询学生", notes = "分页查询学生")
    @RequestMapping(value = "/paged-list", method = RequestMethod.GET)
    public IResponse<PageResponse<StudentResp>> pagedList(@Validated @ModelAttribute StudentReq req) {
        StudentQuery query = MapperUtils.INSTANCE.map(req, StudentQuery.class);
        Page<StudentBean> page = studentService.pagedList(query);
        List<StudentResp> respList = MapperUtils.INSTANCE.mapAsList(page.getContent(), StudentResp.class, FieldMapBuilder.create()
                .add("username", "userName")
                .add("score", "score0")
                .build());
        return Response.success(PageResponse.build(respList, page));
    }
}
