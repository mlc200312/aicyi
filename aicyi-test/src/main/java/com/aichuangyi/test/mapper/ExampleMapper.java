//package com.aichuangyi.test.mapper;
//
//import com.aichuangyi.test.domain.Example;
//import com.aichuangyi.test.dto.ExampleResp;
//import org.mapstruct.EnumMapping;
//import org.mapstruct.Mapper;
//import org.mapstruct.MappingConstants;
//import org.mapstruct.factory.Mappers;
//
//import java.util.List;
//import java.util.Set;
//
///**
// * @author Mr.Min
// * @description 业务描述
// * @date 18:38
// **/
//@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
//public interface ExampleMapper {
//    ExampleMapper INSTANCE = Mappers.getMapper(ExampleMapper.class);
//
//    @EnumMapping()
//    ExampleResp exampleToResp(Example example);
//
//    List<ExampleResp> exampleToRespList(List<Example> exampleList);
//
//    Set<ExampleResp> exampleToRespSet(Set<Example> exampleSet);
//}
