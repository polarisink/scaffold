package github.polarisink.scaffold.domain.step;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StepDto {
    private Long id;
    private Long parentId;
    private String name;
    private Long templateId;

    public StepDto(Long id, Long parentId, String name) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
    }
}
