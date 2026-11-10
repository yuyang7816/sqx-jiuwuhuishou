package com.sqx.controller;

import com.sqx.app.base.auth.auth.interceptor.AuthorizationInterceptor;
import com.sqx.app.base.auth.auth.resolver.LoginUserHandlerMethodArgumentResolver;
import com.sqx.app.base.auth.support.AppAuthTokenSupport;
import com.sqx.app.base.user.db.service.IUserService;
import com.sqx.common.shiro.utils.JwtUtil;
import org.springframework.boot.test.mock.mockito.MockBean;

/**
 * WebMvcTest 切片公共 Mock。
 */
abstract class ControllerSliceTestSupport {

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private AppAuthTokenSupport appAuthTokenSupport;

    @MockBean
    private IUserService iUserService;

    @MockBean
    private AuthorizationInterceptor authorizationInterceptor;

    @MockBean
    private LoginUserHandlerMethodArgumentResolver loginUserHandlerMethodArgumentResolver;
}
