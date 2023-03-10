package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否已经登陆,过滤器
 */
@WebFilter(filterName = "loginCheckFiler",urlPatterns = "/*")
@Slf4j
public class LoginCheckFiler implements Filter {
    //路径匹配器
    public  static  final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String requestURI = request.getRequestURI();
        log.info("拦截到请求路径：{}",requestURI);
        //定义不需要处理的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"

        };

        //判断是否请求处理
        boolean check = check(urls, requestURI);
        if (check){
            //放行
            log.info("放行{}请求路径",requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        //如果已经登陆，则直接放行
        if(request.getSession().getAttribute("employee")!=null){
            log.info("放行{}请求路径,用户{}已经登陆",requestURI,request.getSession().getAttribute("employee"));

            //取出用户id,放入线程变量中
            Long employeeId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(employeeId);

            filterChain.doFilter(request,response);
            return;
        }
        if(request.getSession().getAttribute("user")!=null){
            log.info("放行{}请求路径,用户{}已经登陆",requestURI,request.getSession().getAttribute("user"));

            //取出用户id,放入线程变量中
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request,response);
            return;
        }


        log.info("拦截{}请求路径,用户未登陆",requestURI);
        //如果没有登陆，则返回未登录结果,通过输出流方式返回数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    /**
     * 路径匹配，检查本次请求是否需要处理
     * @param urls 路径
     * @param requestURI URI
     * @return
     */
    public boolean check(String[] urls, String requestURI){
        for (String url:urls){
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match){
                return true;
            }
        }
        return false;
    }
}
