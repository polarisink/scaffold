package github.polarisink.scaffold.domain.step;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * 向下插入请求
 *
 * @author lqs
 * @date 2021/12/1
 */
@Data
public class InsertRequest {

    /**
     * 被插入的id
     */
    @NotNull(message = "ID不能为空")
    private Long id;
    /**
     * 新步骤名字
     */
    @NotBlank(message = "新名字不能为空")
    private String newName;
    /**
     * 模板id
     */
    @NotNull(message = "模板不能为空")
    private Long templateId;
}
