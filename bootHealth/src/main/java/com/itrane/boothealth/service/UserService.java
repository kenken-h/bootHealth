package com.itrane.boothealth.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.itrane.boothealth.model.UserInfo;

/**
 * UserDetailsService を拡張したユーザー・サービス.
 */
public interface UserService {

    public List<UserInfo> findPage(int offset, int pageRows, String searchStr,
            String sortDir, String... sortCols);

    public UserInfo save(UserInfo e) throws DbAccessException;

    @Transactional
    public void deleteUser(Long id) throws EntityNotFoundException;
}
