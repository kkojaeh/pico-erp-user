package pico.erp.user.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
