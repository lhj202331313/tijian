package boy.wang.userservice.config;

import boy.wang.userservice.common.CurrentUserHandlerMethodArgumentResolver;
import boy.wang.userservice.common.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;
    private final CurrentUserHandlerMethodArgumentResolver currentUserResolver;

    @Autowired
    public WebConfig(LoginInterceptor loginInterceptor,
                     CurrentUserHandlerMethodArgumentResolver currentUserResolver) {
        this.loginInterceptor = loginInterceptor;
        this.currentUserResolver = currentUserResolver;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/user/login",
                        "/api/user/register",
                        "/api/hospital/list",
                        "/api/hospital/**",
                        "/api/setmeal/list",
                        "/api/setmeal/**",
                        "/api/doctor/list"
                );
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentUserResolver);
    }
}
