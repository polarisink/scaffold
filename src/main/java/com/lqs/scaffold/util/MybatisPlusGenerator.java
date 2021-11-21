package com.lqs.scaffold.util;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.FileOutConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.ITypeConvert;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.TemplateConfig;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author polaris
 * @description mybatis plus generator util
 * @date 2021/4/14 10:38 上午
 */
public class MybatisPlusGenerator {

	/**
	 * 读取控制台内容
	 * @param tip
	 * @return
	 */
	static String scanner(String tip) {
		Scanner scanner = new Scanner(System.in);
		StringBuilder help = new StringBuilder();
		help.append("请输入" + tip + ":");
		System.out.println(help);
		if (scanner.hasNext()) {
			String ipt = scanner.next();
			if (StringUtils.isNotBlank(ipt)) {
				return ipt;
			}
		}
		throw new MybatisPlusException("请输入正确的" + tip + "！");
	}

	public static void main(String[] args) {
		// 代码生成器
		AutoGenerator mpg = new AutoGenerator();

		// 全局配置
		GlobalConfig gc = new GlobalConfig();
		String projectPath = System.getProperty("user.dir");
		gc.setOutputDir(projectPath + "/src/main/java");
		gc.setAuthor("polaris");
		gc.setOpen(false);
		//实体属性 Swagger2 注解
		gc.setSwagger2(true);
		gc.setControllerName("%sController");
		gc.setServiceName("%sService");
		gc.setServiceImplName("%sServiceImpl");
		gc.setMapperName("%sDao");
		gc.setBaseResultMap(true);
		gc.setBaseColumnList(true);
		mpg.setGlobalConfig(gc);

		// 数据源配置
		DataSourceConfig dsc = new DataSourceConfig();
		dsc.setDbType(DbType.MYSQL);
		dsc.setUrl("jdbc:mysql://polaris.ink:3306/scaffold?useUnicode=true&useSSL=false&characterEncoding=utf8");
		dsc.setDriverName("com.mysql.cj.jdbc.Driver");
		dsc.setUsername("root");
		dsc.setPassword("123456");
		//使用自定义converter
		dsc.setTypeConvert(new MySqlTypeConvertCustom());
		mpg.setDataSource(dsc);

		// 包配置
		PackageConfig pc = new PackageConfig();
		pc.setModuleName(scanner("模块名"));
		pc.setParent("com.lqs");
		pc.setController("controller");
		pc.setService("service");
		pc.setMapper("dao");
		mpg.setPackageInfo(pc);

		//自定义配置
		InjectionConfig cfg = new InjectionConfig() {
			@Override
			public void initMap() {
				// to do nothing
			}
		};

		// 如果模板引擎是 freemarker
		String templatePath = "/templates/mapper.xml.ftl";
		// 如果模板引擎是 velocity
		// String templatePath = "/templates/mapper.xml.vm";

		// 自定义输出配置
		List<FileOutConfig> focList = new ArrayList<>();
		// 自定义配置会被优先输出
		focList.add(new FileOutConfig(templatePath) {
			@Override
			public String outputFile(TableInfo tableInfo) {
				// 自定义输出文件名，如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
				return projectPath + "/src/main/resources/mapper/" + pc.getModuleName()
					+ "/" + tableInfo.getEntityName() + "Dao" + StringPool.DOT_XML;
			}
		});
		/*
		cfg.setFileCreate(new IFileCreate() {
				@Override
				public boolean isCreate(ConfigBuilder configBuilder, FileType fileType, String filePath) {
						// 判断自定义文件夹是否需要创建
						checkDir("调用默认方法创建的目录，自定义目录用");
						if (fileType == FileType.MAPPER) {
								// 已经生成 mapper 文件判断存在，不想重新生成返回 false
								return !new File(filePath).exists();
						}
						// 允许生成模板文件
						return true;
				}
		});
		*/
		cfg.setFileOutConfigList(focList);
		mpg.setCfg(cfg);

		// 配置模板
		TemplateConfig templateConfig = new TemplateConfig();
		templateConfig.setXml("/templates/mapper.xml")                                                        // 设置生成xml的模板
			// 设置生成entity的模板
			.setEntity("/templates/entity.java")
			// 设置生成mapper的模板
			.setMapper("/templates/mapper.java")
			// 设置生成service的模板
			.setController("/templates/controller.java")
			// 设置生成serviceImpl的模板
			.setService("/templates/service.java")
			.setServiceImpl("/templates/serviceImpl.java");

		// 配置自定义输出模板
		//指定自定义模板路径，注意不要带上.ftl/.vm, 会根据使用的模板引擎自动识别
		//templateConfig.setEntity("templates/entity2.java");
		//templateConfig.setService();
		//templateConfig.setController();

		templateConfig.setXml(null);
		mpg.setTemplateEngine(new FreemarkerTemplateEngine());
		mpg.setTemplate(templateConfig);

		// 策略配置
		StrategyConfig strategy = new StrategyConfig();
		strategy.setNaming(NamingStrategy.underline_to_camel);
		strategy.setColumnNaming(NamingStrategy.underline_to_camel);
		strategy.setEntityLombokModel(true);
		strategy.setRestControllerStyle(true);
		strategy.setInclude(scanner("表名，多个英文逗号分割").split(","));
		strategy.setControllerMappingHyphenStyle(true);
		strategy.setTablePrefix(pc.getModuleName() + "_");
		mpg.setStrategy(strategy);
		mpg.execute();
	}
}

class MySqlTypeConvertCustom extends MySqlTypeConvert implements ITypeConvert {
	@Override
	public IColumnType processTypeConvert(GlobalConfig globalConfig, String fieldType) {
		String t = fieldType.toLowerCase();
		if (t.contains("tinyint")) {
			return DbColumnType.BOOLEAN;
		}
		return super.processTypeConvert(globalConfig, fieldType);
	}
}
