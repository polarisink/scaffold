package github.polarisink.scaffold.dao.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

import static github.polarisink.scaffold.common.constant.AuthConst.SUPER_ADMIN_ROLE_ID;

/**
 * Authentication
 *
 * @author aries
 * @since 2022-06-21
 */
@Data
@ApiModel("身份")
public class Authentication {
  @ApiModelProperty("用户id")
  private Long uid;
  @ApiModelProperty("角色id")
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
