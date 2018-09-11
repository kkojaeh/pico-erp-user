package pico.erp.user.group;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pico.erp.audit.annotation.Audit;
import pico.erp.shared.data.Role;
import pico.erp.user.data.GroupId;
import pico.erp.user.group.GroupMessages.CreateResponse;
import pico.erp.user.group.GroupMessages.DeleteResponse;
import pico.erp.user.group.GroupMessages.GrantRoleResponse;
import pico.erp.user.group.GroupMessages.RevokeRoleResponse;
import pico.erp.user.group.GroupMessages.UpdateResponse;
import pico.erp.user.role.RoleExceptions.AlreadyExistsException;
import pico.erp.user.role.RoleExceptions.NotFoundException;

@Getter
@ToString
@Builder(toBuilder = true)
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Audit(alias = "group")
public class Group implements Serializable {

  @Id
  GroupId id;

  String name;

  Set<Role> roles;

  public Group() {
    this.roles = new HashSet<>();
  }

  public GroupMessages.CreateResponse apply(GroupMessages.CreateRequest request) {
    id = request.getId();
    name = request.getName();
    return new CreateResponse(Collections.emptyList());
  }

  public GroupMessages.UpdateResponse apply(GroupMessages.UpdateRequest request) {
    name = request.getName();
    return new UpdateResponse(Collections.emptyList());
  }

  public GroupMessages.DeleteResponse apply(GroupMessages.DeleteRequest request) {
    return new DeleteResponse(Collections.emptyList());
  }

  public GroupMessages.GrantRoleResponse apply(GroupMessages.GrantRoleRequest request) {
    Role role = request.getRole();
    if (roles.contains(role)) {
      throw new AlreadyExistsException();
    }
    roles.add(role);
    return new GrantRoleResponse(Collections.emptyList());
  }

  public GroupMessages.RevokeRoleResponse apply(GroupMessages.RevokeRoleRequest request) {
    Role role = request.getRole();
    if (!roles.contains(role)) {
      throw new NotFoundException();
    }
    roles.remove(role);
    return new RevokeRoleResponse(Collections.emptyList());
  }

}
