package com.hp.docker_base.config.filter;

import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

/**
 * 自定义拦截器
 */

/**
 * @desc 自定义过滤器，可以在这里处理中文乱码等问题
 * @Author wangsh
 * @date 2018/5/6 15:44
 * @return
 */
public class CharacterFilter implements Filter {

    /**
     * 服务启动,调用初始化方法
     *
     * @param filterConfig
     * @throws ServletException
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        System.out.println("服务启动,调用过滤器Filter初始化方法init()..........");
    }


    /**
     * 请求时调用
     *
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        System.out.println("发送请求时,调用过滤器Filter的doFilter()方法..........");
        String userName = (String)((HttpServletRequest) servletRequest).getSession().getAttribute("userName");
        //放行通过
        HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper((HttpServletResponse) servletResponse);
        HttpServletRequest hrequest = (HttpServletRequest)servletRequest;
        if(StringUtils.isEmpty(userName)){
            if(hrequest.getRequestURI().indexOf("/deleteView") != -1 ||
                    hrequest.getRequestURI().indexOf("/asd") != -1 ||
                    hrequest.getRequestURI().indexOf("/download/downLoadList") != -1 ||
                    hrequest.getRequestURI().indexOf("/downList") != -1
            ) {//要拦截对象
                wrapper.sendRedirect("/login");
            }else {
                filterChain.doFilter(servletRequest, servletResponse);
            }
        }else{//登录后就不拦截
            filterChain.doFilter(servletRequest, servletResponse);
        }

       // filterChain.doFilter(servletRequest, servletResponse);
    }

    /**
     * 销毁调用
     */
    @Override
    public void destroy() {

        System.out.println("服务关闭，调用过滤器Filter的销毁方法destroy()..........");
    }
}
