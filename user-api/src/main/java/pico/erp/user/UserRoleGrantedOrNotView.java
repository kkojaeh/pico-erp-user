package pico.erp.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pico.erp.user.role.RoleId;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserRoleGrantedOrNotView {

  UserId userId;

  RoleId roleId;

  String roleName;

  String roleDescription;

  boolean granted;

}
