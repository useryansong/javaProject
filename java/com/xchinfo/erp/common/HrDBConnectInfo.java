package com.xchinfo.erp.common;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.yecat.mybatis.utils.jdbc.DBConnectInfo;
@Component
@ConfigurationProperties(prefix="hrDBConnectInfo")
public class HrDBConnectInfo extends DBConnectInfo {
    @Override
    public String getUsername() {
        return super.username;
    }
    @Override
    public void setUsername(String username) {
        super.username = username;
    }
    @Override
    public String getPassword() {
        return super.password;
    }
    @Override
    public void setPassword(String password) {
        super.password = password;
    }
    @Override
    public String getUrl() {
        return super.url;
    }
    @Override
    public void setUrl(String url) {
        this.url = url;
    }
    @Override
    public String getDriverName() {
        return driverName;
    }
    @Override
    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }
}
