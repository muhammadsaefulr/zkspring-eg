package com.zkspringboot.services.impl.user;

import com.zkspringboot.dao.maindb.basicsetup.UsrGlobalUsersMapper;
import com.zkspringboot.domain.maindb.basicsetup.UsrGlobalUsers;
import com.zkspringboot.services.user.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("usrAccountService")
public class UserAccountServiceImpl implements UserAccountService {

    @Autowired
    private UsrGlobalUsersMapper usrGlobalUsersMapper;


    @Transactional(readOnly = true)
    @Override
    public UsrGlobalUsers findUserByID(int userId) {
        return usrGlobalUsersMapper.selectByPrimaryKey(userId);
    }

    @Transactional(readOnly = true)
    @Override
    public int dbInsert(UsrGlobalUsers usrGlobalUsers) {
        return usrGlobalUsersMapper.insertSelective(usrGlobalUsers);
    }

    @Transactional(readOnly = true)
    @Override
    public int dbUpdate(UsrGlobalUsers usrGlobalUsers) {
        return usrGlobalUsersMapper.updateByPrimaryKey(usrGlobalUsers);
    }
}
