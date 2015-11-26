package com.delarosa.portal.authentication;

import com.delarosa.portal.db.DB;
import com.delarosa.portal.db.entity.User;
import java.util.List;
import org.zkoss.essentials.services.UserInfoService;

/**
 *
 * @author Omar
 */
public class MyUserInfoService implements UserInfoService {

    @Override
    public User findUser(String account) {
        User user = null;

        List<User> list = DB.getUsersList();

        if (list.isEmpty()) {
            user = new User();
            user.setAlias("admin");
            user.setPassword("1234");
            user.setName("Administrador");
            user.setId(1);
            
            DB.insert(user, User.LIST_TYPE, DB.TABLE_USER);
        } else {
            for (User usr : list) {
                if (account.equals(usr.getAlias())) {
                    user = usr;
                    break;
                }
            }
        }

        return user;
    }

    @Override
    public User updateUser(org.zkoss.essentials.entity.User user) {
        //TODO
        return null;
    }
}
