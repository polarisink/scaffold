package github.polarisink.dao.bean.request;


import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 机床档案新增请求
 *
 * @author aries
 * @date 2022/5/11
 */
@Data
public class ArchivesAddRequest implements Serializable {

    private static final long serialVersionUID = 1784425732671451601L;

    private String handle;

    private String opticalMachNum;


    private LocalDateTime opticalMachLoadTime;

    private String machNum;

    private Long machModelId;


    private LocalDateTime factoryTime;

    private String machSn;

    private String prodName;

    private String prodStandard;

    private String serialNum;

    private Long bomId;

}
