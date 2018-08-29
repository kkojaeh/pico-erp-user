package pico.erp.user.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserGroupJoinedOrNotView {

  UserId userId;

  RoleId roleId;

  String roleName;

  String roleDescription;

  boolean granted;

}
