package github.polarisink.api.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.TimeZone;

import static github.polarisink.common.utils.TimeUtils.*;

/**
 * @author lqs mvc配置
 */
@Slf4j
@Configuration
public class MvcConfig implements WebMvcConfigurer {


    /**
     * 跨域设置 使用gateway代理的时候不需要配置跨域
     */
    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        /*@formatter:off*/
        corsRegistry
                .addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true)
                .maxAge(3600)
                .allowedHeaders("*");
        /*@formatter:on*/
    }

    /**
     * ResponseBodyAdvice中遇到String会报错. 因为在所有的HttpMessageConverter实例集合中,StringHttpMessageConverter要比其它的Converter排得靠前一些.
     * 我们需要将处理Object类型的HttpMessageConverter放得靠前一些
     *
     * @param converters
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        // 忽略json字符串中不识别的属性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 忽略无法转换的对象
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // PrettyPrinter 格式化输出
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        // NULL参与序列化
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        // 指定时区
        objectMapper.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));
        // 日期类型字符串处理
        objectMapper.setDateFormat(new SimpleDateFormat(S_F_STR));
        // java8日期日期处理
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(SF));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DAY_F));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(TIME_F));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(SF));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DAY_F));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(TIME_F));
        objectMapper.registerModule(javaTimeModule);
        converter.setObjectMapper(objectMapper);
        converters.add(0, converter);
    }
}
