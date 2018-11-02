package com.ali.jar2proxy.extend.model;

import java.util.List;

/**
 * @author coolme200
 */
public class UserConsultResult extends UccBaseResult {

    private static final long  serialVersionUID = 2301573028980930746L;
    private List<UserConsultInfo> users;

    public List<UserConsultInfo> getUsers() {
        return users;
    }

    public void setUsers(List<UserConsultInfo> users) {
        this.users = users;
    }
}
