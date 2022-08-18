package github.polarisink.dao.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

import static github.polarisink.common.constant.AuthConst.SUPER_ADMIN_ROLE_ID;

/**
 * Authentication
 *
 * @author aries
 * @since 2022-06-21
 */
@Data
@ApiModel("身份")
public class Authentication {
  private Long uid;
  private Long roleId;

  /**
   * 是不是超级管理员
   *
   * @return
   */
  public boolean isSuperAdmin() {
    return Objects.equals(this.roleId, SUPER_ADMIN_ROLE_ID);
  }
}
