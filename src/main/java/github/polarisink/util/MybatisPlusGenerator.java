package github.polarisink.util;

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
import java.util.Objects;
import java.util.Scanner;

/**
 * @author polaris
 * @description mybatis plus generator com.lqs.common.util
 * @date 2021/4/14 10:38 上午
 */
public class MybatisPlusGenerator {

	/**
	 * 读取控制台内容
	 *
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
		//全局配置
		String projectPath = System.getProperty("user.dir") + "/src/main/java";
		boolean open = false;//是否打开
		String author = System.getProperty("user.name");
		String controllerName = "%sController";
		String serviceName = "%sService";
		String serviceImpleName = "%sServiceImpl";
		boolean isBaseResultMap = true;
		boolean isColumnList = true;
		//数据源
		String url = "jdbc:mysql://10.1.20.185:3306/edonline?useUnicode=true&useSSL=false&characterEncoding=utf8";
		String driverName = "com.mysql.cj.jdbc.Driver";
		String userName = "root";
		String password = "654321";
		DbType dbType = DbType.MYSQL;
		//包配置
		String mouldName = scanner("模块名");
		String parent = "cn.hzncc";
		String controllerPack = "controller";
		String servicePack = "service";
		boolean isMybatis = true;
		String scanner = scanner("使用JPA还是Mybatis:(M/J,默认M)");
		//如果是M就是mybatis,否则就是jpa
		isMybatis = "m".equalsIgnoreCase(scanner);
		//模板路径
		String mapperLocation = "/src/main/resources/mapper/";
		boolean ifSwagger2 = true;
		String controller = "/templates/controller.java";
		String mapperXml = "/templates/mapper.xml";
		String entity = isMybatis ? "/templates/entity.java" : "/templates/entityJpa.java";
		String service = isMybatis ? "/templates/service.java" : "/templates/serviceJpa.java";
		String serviceImpl = isMybatis ? "/templates/serviceImpl.java" : "/templates/serviceImplJpa.java";
		String mapper = isMybatis ? "/templates/mapper.java" : "/templates/mapperJpa.java";
		String daoName = isMybatis ? "%sDao" : "%sRepository";
		String daoPackage = isMybatis ? "dao" : "repository";

		//自定义映射
		MySqlTypeConvertCustom typeConvertCustom = new MySqlTypeConvertCustom();
		//策略配置
		NamingStrategy namingStrategy = NamingStrategy.underline_to_camel;
		NamingStrategy columnStrategy = NamingStrategy.no_change;
		boolean isLombokEntity = true;
		boolean isRestController = true;
		String tableNames = scanner("表名,多个英文逗号分割(*代表生成所有)");
		String allTables = "*";
		boolean controllerMappingHyphenStyle = true;
		String tablePrefix = "_";


		// 代码生成器
		AutoGenerator mpg = new AutoGenerator();
		// 全局配置
		GlobalConfig gc = new GlobalConfig();
		gc.setOutputDir(projectPath);
		gc.setAuthor(author);
		gc.setOpen(open);
		gc.setSwagger2(ifSwagger2);
		gc.setControllerName(controllerName);
		gc.setServiceName(serviceName);
		gc.setServiceImplName(serviceImpleName);
		gc.setMapperName(daoName);
		gc.setBaseResultMap(isBaseResultMap);
		gc.setBaseColumnList(isColumnList);
		mpg.setGlobalConfig(gc);
		// 数据源配置
		DataSourceConfig dsc = new DataSourceConfig();
		dsc.setDbType(dbType);
		dsc.setUrl(url);
		dsc.setDriverName(driverName);
		dsc.setUsername(userName);
		dsc.setPassword(password);
		//使用自定义converter
		dsc.setTypeConvert(typeConvertCustom);
		mpg.setDataSource(dsc);
		// 包配置
		PackageConfig pc = new PackageConfig();
		pc.setModuleName(mouldName);
		pc.setParent(parent);
		pc.setController(controllerPack);
		pc.setService(servicePack);
		pc.setMapper(daoPackage);
		mpg.setPackageInfo(pc);
		//策略配置
		//自定义配置
		InjectionConfig cfg = new InjectionConfig() {
			@Override
			public void initMap() {
				// to do nothing
			}
		};
		// 如果模板引擎是 freemarker
		//如果是mybatis会生成mapper.xml
		if (isMybatis) {
			// 如果模板引擎是 velocity
			// String templatePath = "/templates/mapper.xml.vm";
			// 自定义输出配置
			List<FileOutConfig> focList = new ArrayList<>();
			// 自定义配置会被优先输出
			focList.add(new FileOutConfig(mapperXml + ".ftl") {
				@Override
				public String outputFile(TableInfo tableInfo) {
					// 自定义输出文件名，如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
					return projectPath + mapperLocation + pc.getModuleName()
						+ "/" + tableInfo.getEntityName() + "Dao" + StringPool.DOT_XML;
				}
			});
			cfg.setFileOutConfigList(focList);
		}
		mpg.setCfg(cfg);
		// 配置模板
		TemplateConfig templateConfig = new TemplateConfig();
		// 设置生成xml的模板
		templateConfig.setXml(mapperXml)
			.setEntity(entity)
			.setMapper(mapper)
			.setController(controller)
			.setService(service)
			.setServiceImpl(serviceImpl);

		templateConfig.setXml(null);
		mpg.setTemplateEngine(new FreemarkerTemplateEngine());
		mpg.setTemplate(templateConfig);
		// 策略配置
		StrategyConfig strategy = new StrategyConfig();
		strategy.setNaming(namingStrategy);
		strategy.setColumnNaming(columnStrategy);
		strategy.setEntityLombokModel(isLombokEntity);
		strategy.setRestControllerStyle(isRestController);
		//*就生成所有表的数据
		if (!Objects.equals(tableNames, "*")) {
			String[] split = tableNames.split(",");
			strategy.setInclude(split);
		}
		strategy.setControllerMappingHyphenStyle(controllerMappingHyphenStyle);
		strategy.setTablePrefix(pc.getModuleName() + tablePrefix);
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
