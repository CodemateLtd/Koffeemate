package com.codemate.brewflop.data.network.model;

import java.util.List;

public class UserListResponse {
    public boolean ok;
    public List<User> members;
    public long cache_ts;
}