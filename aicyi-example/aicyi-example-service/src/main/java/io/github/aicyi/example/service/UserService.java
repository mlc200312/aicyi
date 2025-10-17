package io.github.aicyi.example.service;

import io.github.aicyi.example.domain.UserQuery;
import io.github.aicyi.example.domain.entity.base.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author Mr.Min
 * @description 用户服务
 * @date 14:51
 **/
public interface UserService {

    void save(User user);

    User getById(Long id);

    List<User> list(UserQuery query);

    Page<User> pagingList(Pageable pageable, UserQuery query);
}
