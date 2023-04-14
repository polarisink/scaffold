//package github.polarisink.dao.mapper;
//
//import com.baomidou.mybatisplus.core.mapper.BaseMapper;
//import org.apache.ibatis.annotations.Param;
//
//import java.util.Collection;
//import java.util.List;
//
///**
// * 扩展批量插入/更新，必须是mysql的数据库，其它数据库会报错,建议所有mapper关联到此项
// *
// * @Author Administrator
// * @Date 2023/3/14 14:48
// */
//public interface ExtendMapper<T> extends BaseMapper<T> {
//
//    /**
//     * 批量插入 仅适用于mysql
//     *
//     * @param entityList 实体列表
//     * @return 影响行数
//     */
//    Integer insertBatchSomeColumn(Collection<T> entityList);
//
//    /**
//     * 自定义批量更新，条件为主键
//     * 如果要自动填充，@Param(xx) xx参数名必须是 list/collection/array 3个的其中之一
//     */
//    int updateBatchById(@Param("list") List<T> list);
//}
