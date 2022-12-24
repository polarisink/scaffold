package github.polarisink.mbpgen.utils;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;

/**
 * TODO test
 * MybatisPlusGenerator
 *
 * @author hzsk
 */
public class TableGenerator {

  static String path = "E:\\ideaProjects\\scaffold\\scaffold-api\\src\\main\\resources\\application-dev.yml";

  public static void main(String[] args) throws MalformedURLException {

    Resource resource = new FileUrlResource(path);
    YamlPropertiesFactoryBean yamlBean = new YamlPropertiesFactoryBean();
    yamlBean.setResources(resource);
    Properties props = yamlBean.getObject();
    // 通过key获取value
    // 使用了env进行配置,现在不知道如何解决
    String url = props.getProperty("spring.datasource.druid.primary.jdbc-url");
    String userName = props.getProperty("spring.datasource.druid.primary.username");
    String password = props.getProperty("spring.datasource.druid.primary.password");
    /*
    String url = props.getProperty("spring.datasource.druid.primary.jdbc-url");
     String userName = props.getProperty("spring.datasource.druid.primary.username");
     String password = props.getProperty("spring.datasource.druid.primary.password");
    */
    List<String> tables = List.of("archives");
    /*@formatter:off*/
    FastAutoGenerator.create(url, userName, password).globalConfig(builder -> {
          builder.author("Aries")
              .outputDir(System.getProperty("user.dir") + "\\src\\main\\java")    //输出路径(写到java目录)
              .enableSwagger()
              .commentDate("yyyy-MM-dd").fileOverride();                  //开启覆盖之前生成的文件
        }).packageConfig(builder ->
            builder.parent("github.polarisink")
                .moduleName("mbpgen")
                .entity("entity")
                .service("service")
                .serviceImpl("service.impl")
                .controller("handler")
                .mapper("dao")
                .xml("dao")
                .pathInfo(Collections.singletonMap(OutputFile.xml,
                    System.getProperty("user.dir") + "\\src\\main\\resources\\dao"))
        ).strategyConfig(builder ->
            builder.addInclude(tables)
                //.addTablePrefix("p_")
                .serviceBuilder()
                .formatServiceFileName("%sService")
                .formatServiceImplFileName("%sServiceImpl")
                .entityBuilder()
                .enableLombok()
                .logicDeleteColumnName("deleted")
                .enableTableFieldAnnotation()
                .controllerBuilder()
                // 映射路径使用连字符格式，而不是驼峰
                .enableHyphenStyle()
                .formatFileName("%sController")
                .enableRestStyle()
                .mapperBuilder()
                //生成通用的resultMap
                .enableBaseResultMap().superClass(BaseMapper.class).formatMapperFileName("%sMapper")
                .enableMapperAnnotation().formatXmlFileName("%sMapper")
        ).templateConfig(builder -> {
          // 实体类使用我们自定义模板
          builder.entity("templates/myentity.java");
        }).templateEngine(new FreemarkerTemplateEngine())
        // 使用Freemarker引擎模板，默认的是Velocity引擎模板
        .execute();
    /*@formatter:on*/
  }
}

