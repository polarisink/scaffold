package com.scaffold.spi;

import cn.hutool.core.util.ServiceLoaderUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileConverterSystem {
    
    // 文件转换器接口
    public interface FileConverter {
        String getSourceFormat();
        String getTargetFormat();
        String getConverterName();
        boolean convert(String sourceContent);
    }
    
    // Markdown to HTML转换器
    public static class MarkdownToHtmlConverter implements FileConverter {
        @Override
        public String getSourceFormat() {
            return "md";
        }
        
        @Override
        public String getTargetFormat() {
            return "html";
        }
        
        @Override
        public String getConverterName() {
            return "Markdown转HTML";
        }
        
        @Override
        public boolean convert(String sourceContent) {
            System.out.println("正在转换 Markdown -> HTML");
            System.out.println("源内容：" + sourceContent);
            String html = "<html><body><p>" + sourceContent + "</p></body></html>";
            System.out.println("结果：" + html);
            return true;
        }
    }
    
    // JSON to XML转换器
    public static class JsonToXmlConverter implements FileConverter {
        @Override
        public String getSourceFormat() {
            return "json";
        }
        
        @Override
        public String getTargetFormat() {
            return "xml";
        }
        
        @Override
        public String getConverterName() {
            return "JSON转XML";
        }
        
        @Override
        public boolean convert(String sourceContent) {
            System.out.println("正在转换 JSON -> XML");
            System.out.println("源内容：" + sourceContent);
            String xml = "<root><data>" + sourceContent + "</data></root>";
            System.out.println("结果：" + xml);
            return true;
        }
    }
    
    // CSV to Excel转换器
    public static class CsvToExcelConverter implements FileConverter {
        @Override
        public String getSourceFormat() {
            return "csv";
        }
        
        @Override
        public String getTargetFormat() {
            return "xlsx";
        }
        
        @Override
        public String getConverterName() {
            return "CSV转Excel";
        }
        
        @Override
        public boolean convert(String sourceContent) {
            System.out.println("正在转换 CSV -> Excel");
            System.out.println("源内容：" + sourceContent);
            System.out.println("结果：已生成Excel文件");
            return true;
        }
    }
    
    // 转换器管理器
    static class ConverterManager {
        private Map<String, FileConverter> converters;
        
        public ConverterManager() {
            this.converters = new HashMap<>();
            loadConverters();
        }
        
        private void loadConverters() {
            List<FileConverter> converterList = ServiceLoaderUtil.loadList(FileConverter.class);
            
            System.out.println("正在加载文件转换器...");
            for (FileConverter converter : converterList) {
                String key = converter.getSourceFormat() + "->" + converter.getTargetFormat();
                converters.put(key, converter);
                System.out.println("  已加载：" + converter.getConverterName() + 
                                 " (" + key + ")");
            }
            System.out.println("共加载 " + converterList.size() + " 个转换器\n");
        }
        
        public boolean convert(String sourceFormat, String targetFormat, String content) {
            String key = sourceFormat + "->" + targetFormat;
            FileConverter converter = converters.get(key);
            
            if (converter == null) {
                System.err.println("不支持的转换：" + key);
                return false;
            }
            
            return converter.convert(content);
        }
        
        public void listSupportedConversions() {
            System.out.println("支持的转换类型：");
            converters.forEach((key, converter) -> 
                System.out.println("  - " + key + " (" + converter.getConverterName() + ")"));
        }
    }
    
    public static void main(String[] args) {
        System.out.println("========== 文件格式转换系统 ==========\n");
        
        ConverterManager manager = new ConverterManager();
        
        // 显示支持的转换
        manager.listSupportedConversions();
        
        // 场景1：Markdown转HTML
        System.out.println("\n场景1：Markdown转HTML");
        manager.convert("md", "html", "# Hello World\n这是一个Markdown文档");
        
        // 场景2：JSON转XML
        System.out.println("\n场景2：JSON转XML");
        manager.convert("json", "xml", "{\"name\":\"张三\",\"age\":25}");
        
        // 场景3：CSV转Excel
        System.out.println("\n场景3：CSV转Excel");
        manager.convert("csv", "xlsx", "姓名,年龄,城市\n张三,25,北京");
        
        // 场景4：不支持的转换
        System.out.println("\n场景4：尝试不支持的转换");
        manager.convert("pdf", "word", "test content");
    }
}