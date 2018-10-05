package pico.erp.user.group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pico.erp.user.role.RoleId;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GroupRoleGrantedOrNotView {

  GroupId groupId;

  RoleId roleId;

  String roleName;

  String roleDescription;

  boolean granted;

}
