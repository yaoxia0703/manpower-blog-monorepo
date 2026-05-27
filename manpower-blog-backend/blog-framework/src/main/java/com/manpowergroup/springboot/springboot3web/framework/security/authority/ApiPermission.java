package com.manpowergroup.springboot.springboot3web.framework.security.authority;

public class ApiPermission {

    private String code;
    private String path;
    private String method;

    public ApiPermission() {
    }

    public ApiPermission(String code, String path, String method) {
        this.code = code;
        this.path = path;
        this.method = method;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
