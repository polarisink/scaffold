package github.polarisink.scaffold.domain.step;

import com.fasterxml.jackson.annotation.JsonAlias;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * 步骤添加请求
 *
 * @author hzsk
 */
@Data
public class StepAddRequest {

    /**
     * 模板id
     */
    @JsonAlias("mouldId")
    @NotNull(message = "模板ID不能为空")
    private Long templateId;
    /**
     * 父id
     */
    @NotNull(message = "父ID不能为空,根节点父ID为0L")
    private Long id;
    /**
     * 新名字
     */
    @NotBlank(message = "新增节点名字不能为空")
    private String newName;
    /**
     * 是否是叶子节点
     */
    private Boolean isStep;

}
