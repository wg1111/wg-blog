package com.wg.blog.mail;

import javax.mail.PasswordAuthentication;

/**
 * 验证邮箱和授权码
 */

public class MyAuthenticator extends javax.mail.Authenticator {
    private String strUser;
    private String strPwd;
    public MyAuthenticator(String user, String password) {
        this.strUser = user;
        this.strPwd = password;
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(strUser, strPwd);
    }
}
