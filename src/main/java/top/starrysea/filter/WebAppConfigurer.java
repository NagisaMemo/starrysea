package top.starrysea.filter;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebAppConfigurer extends WebMvcConfigurerAdapter {

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new PreSessionInterceptor()).addPathPatterns("/work/add", "/work/remove",
				"/activity/add", "/activity/modify", "/activity/remove", "/activity/funding/add",
				"/activity/funding/remove", "/order", "/order/modify", "/order/remove", "/order/export");
		registry.addInterceptor(new AfterLoginInterceptor()).addPathPatterns("/user/login");
		registry.addInterceptor(new ErrorInterceptor()).addPathPatterns("/**");
		registry.addInterceptor(new AfterExitInterceptor()).addPathPatterns("/user/exit");
		super.addInterceptors(registry);
	}
}
