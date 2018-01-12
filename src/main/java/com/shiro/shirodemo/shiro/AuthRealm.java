package com.shiro.shirodemo.shiro;

import com.shiro.shirodemo.Enum.EnumCode;
import com.shiro.shirodemo.entity.Permission;
import com.shiro.shirodemo.entity.Role;
import com.shiro.shirodemo.entity.User;
import com.shiro.shirodemo.exception.MyException;
import com.shiro.shirodemo.service.UserService;
import com.shiro.shirodemo.utils.JsonResult;
import com.shiro.shirodemo.utils.ResultUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AuthRealm extends AuthorizingRealm {

    /**
     * 日志打印
     */
    private final static Logger log = LoggerFactory.getLogger(AuthRealm.class);

    /**
     * 用户
     */
    @Autowired
    private UserService userService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals){
        return null;
    }

    /**
     * @desc: 授权
     * 
     * @date: 2017/12/18
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        log.info("======================= 认证登陆 ======================");
        // 获取用户输入的token
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String name = upToken.getUsername();
        String pass = String.valueOf(upToken.getPassword());
        List<User> list = userService.checkUser(name,pass);
        User user = null;
        if (null == list || list.isEmpty()) {
            throw new AuthenticationException("账号或密码错误");
        }else if(list.get(0).getStatus() == 0){
            /**
             * 账号被禁用
             */
            throw new AuthenticationException("账号已被禁止登陆");
        }else{
            user = list.get(0);
            //登录成功
            //更新登录时间 last login time
            user.setLastLoginTime(new Date());
            userService.updateById(user);
        }
        log.info("======================= 登陆成功 ======================");
        return new SimpleAuthenticationInfo(user, user.getPswd(), getName());
    }
}
