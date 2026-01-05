import cn.hutool.poi.excel.ExcelUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExcelTest {
    public static void main(String[] args) {
        String f1 = "/Users/aries/Downloads/深圳佳禾上架编码.xls";
        String f2 = "/Users/aries/Downloads/价格表.xls";
        String f3 = "/Users/aries/Downloads/深圳佳禾上架编码-修改.xls";
        List<List<Object>> prices = ExcelUtil.getReader(f2).read();
        prices.removeFirst();
        Map<Object, Object> pricesMap = prices.stream().collect(Collectors.toMap(List::getFirst, l -> l.get(1)));
        List<List<Object>> read = ExcelUtil.getReader(f1).read();
        for (int i = 1; i < read.size(); i++) {
            List<Object> objects = read.get(i);
            Object o = pricesMap.getOrDefault(objects.getFirst(), 0);
            if (objects.size() == 2) {
                objects.add(o);
            } else {
                objects.set(2, o);
            }
        }
       /* ExcelWriter writer = ExcelUtil.getWriter(f3);
        writer.write(read);
        writer.flush();*/

    }
}
