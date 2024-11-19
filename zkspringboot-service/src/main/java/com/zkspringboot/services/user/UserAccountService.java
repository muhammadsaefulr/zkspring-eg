package com.zkspringboot.services.user;

import com.zkspringboot.domain.maindb.basicsetup.UsrGlobalUsers;

public interface UserAccountService {
    public UsrGlobalUsers findUserByID(int userId);

    public int dbInsert(UsrGlobalUsers usrGlobalUsers);

    public int dbUpdate(UsrGlobalUsers usrGlobalUsers);

    public int dbDeleteById(int userId);
}
