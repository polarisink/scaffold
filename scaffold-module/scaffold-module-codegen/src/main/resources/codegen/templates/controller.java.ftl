package ${table.packageName}.${javaBusinessName};

import com.scaffold.base.util.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${apiPrefix}")
@RequiredArgsConstructor
public class ${className}Controller {
    private final ${className}Service ${lowerClassName}Service;

    @PostMapping("/page")
    public PageResponse<${className}> page(@RequestBody ${className}QueryDTO query) {
        return ${lowerClassName}Service.page(query);
    }

    @GetMapping("/{id}")
    public ${className} get(@PathVariable Long id) {
        return ${lowerClassName}Service.getById(id);
    }

    @PostMapping
    public void create(@RequestBody ${className}CreateDTO createDTO) {
        ${lowerClassName}Service.save(createDTO);
    }

    @PutMapping
    public void update(@RequestBody ${className} entity) {
        ${lowerClassName}Service.updateById(entity);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        ${lowerClassName}Service.deleteById(id);
    }
}
