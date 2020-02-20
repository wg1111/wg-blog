package com.wg.blog.error;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.http.HttpStatus;

public class ErrorConfig implements ErrorPageRegistrar {
    @Override
    //前后端分离项目单页面应用合并时的跳转
    public void registerErrorPages(ErrorPageRegistry registry) {
        ErrorPage errorPage = new ErrorPage(HttpStatus.NOT_FOUND,"/index");
        registry.addErrorPages(errorPage);
    }
}
