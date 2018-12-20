package pico.erp.user;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import pico.erp.user.department.DepartmentId;
import pico.erp.user.group.GroupId;
import pico.erp.user.role.RoleId;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id")
public class UserData implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  UserId id;

  String name;

  String email;

  String mobilePhoneNumber;

  String position;

  boolean accountNonExpired;

  boolean accountNonLocked;

  boolean credentialsNonExpired;

  boolean enabled;

  DepartmentId departmentId;

  Set<RoleId> roles;

  Set<GroupId> groups;

  Set<RoleId> wholeRoles;

}
