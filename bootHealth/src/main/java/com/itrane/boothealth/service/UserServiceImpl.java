package com.itrane.boothealth.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itrane.boothealth.model.UserInfo;
import com.itrane.boothealth.repo.UserRepository;
import com.itrane.boothealth.repo.VitalMstRepository;

/**
 * ログイン・ユーザー(UserDetails)とユーザー情報(UserInfo)のサービス実装.
 */
@Service
public class UserServiceImpl implements UserService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private UserRepository repo;
    private VitalMstRepository vmRepo;

    @Autowired
    public UserServiceImpl(UserRepository repo, VitalMstRepository vmRepo) {
        this.repo = repo;
        this.vmRepo = vmRepo;
    }

    // #############
    // Page 取得
    // #############
    public List<UserInfo> findPage(int offset, int pageRows, String searchStr,
            String sortDir, String... sortCols) {
        Sort.Direction dir = Sort.Direction.ASC;
        if (sortDir.equals("desc")) {
            dir = Sort.Direction.DESC;
        }
        int pageNum = offset / pageRows;
        PageRequest request = new PageRequest(pageNum, pageRows, dir, sortCols);

        if (searchStr.equals("")) {
            return repo.findPage(request);
        } else {
            return repo.findPage(searchStr, request);
        }
    }

    // #############
    // CRUD
    // #############
    @Transactional
    public UserInfo save(UserInfo e) throws DbAccessException {
        UserInfo p = null;
        try {
            p = repo.save(e);
        } catch (Exception ex) {
            throw new DbAccessException("DBエラー", DbAccessException.OTHER_ERROR);
        }
        return p;
    }

    @Transactional
    public void deleteUser(Long id) throws EntityNotFoundException {
        UserInfo p = repo.findOne(id);
        if (p == null) {
            throw new EntityNotFoundException();
        }
        // 関連する Vod を削除
        // 関連する VitalMst を削除
        // vmRepo.removeByUser(p);
        repo.delete(p);
    }

}
