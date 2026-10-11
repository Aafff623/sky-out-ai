package com.fortunate.config;

import com.fortunate.interceptor.JwtTokenClientInterceptor;
import com.fortunate.interceptor.JwtTokenStoreInterceptor;
import com.fortunate.json.JacksonObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.List;

/**
 * @description spring mvc配置类
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebMvcConfiguration extends WebMvcConfigurationSupport {
  
  private final JwtTokenStoreInterceptor jwtTokenStoreInterceptor;
  private final JwtTokenClientInterceptor jwtTokenClientInterceptor;
  
  /**
   * 注册自定义拦截器
   * @param registry 注册器
   */
  @Override
  protected void addInterceptors(InterceptorRegistry registry) {
    log.info("开始注册自定义拦截器...");
    // 管理端拦截器
    registry.addInterceptor(jwtTokenStoreInterceptor)
            .addPathPatterns("/store/**") // 拦截admin下的所有接口
            .excludePathPatterns("/store/employee/login") // 排除登录接口
            .excludePathPatterns("/client/shopInfo");

    // 微信小程序用户端拦截器
    registry.addInterceptor(jwtTokenClientInterceptor)
            .addPathPatterns("/client/**")
            .excludePathPatterns("/client/user/login") // 排除登录接口
            // 菜品/套餐/分类与店铺状态：匿名可浏览（点餐页未登录也能看菜单）
            .excludePathPatterns("/client/category/**", "/client/dish/**", "/client/setmeal/**")
            .excludePathPatterns("/client/shop/status", "/client/shopInfo");// 排除获取商铺状态接口
  }
  
  @Bean
  public Docket storeApiDocket() {
    log.info("开始生成【后台】项目接口文档...");
    
    ApiInfo apiInfo = new ApiInfoBuilder().title("后台API接口文档")
            .version("1.0.0")
            .build();
    return new Docket(DocumentationType.SWAGGER_2).groupName("管理端接口")
            .apiInfo(apiInfo)
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.fortunate.controller.store"))
            .paths(PathSelectors.any())
            .build();
  }
  
  @Bean
  public Docket clientApiDocket() {
    log.info("开始生成【用户端】项目接口文档...");
    ApiInfo apiInfo = new ApiInfoBuilder().title("小程序前台API接口文档")
            .version("2.0")
            .description("小程序前台API接口文档")
            .build(); // 构建API信息
    
    return new Docket(DocumentationType.SWAGGER_2).groupName("用户端接口")
            .apiInfo(apiInfo)
            .select()
            // 指定生成接口需要扫描的包路径
            .apis(RequestHandlerSelectors.basePackage("com.fortunate.controller.client"))
            .paths(PathSelectors.any())
            .build();
  }
  
  @Override
  protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
    log.info("开始扩展消息转换器...");
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    converter.setObjectMapper(new JacksonObjectMapper());
    converters.add(0, converter);
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
            .allowedOriginPatterns("*")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true);
  }
  
  @Override
  protected void addResourceHandlers(ResourceHandlerRegistry registry) {
    log.info("开始注册静态资源映射...");
    registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");
    registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
  }
}
