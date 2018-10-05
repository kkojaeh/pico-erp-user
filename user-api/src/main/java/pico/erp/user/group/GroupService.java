package pico.erp.user.group;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import pico.erp.user.UserId;
import pico.erp.user.group.GroupRequests.AddUserRequest;
import pico.erp.user.group.GroupRequests.CreateRequest;
import pico.erp.user.group.GroupRequests.DeleteRequest;
import pico.erp.user.group.GroupRequests.GrantRoleRequest;
import pico.erp.user.group.GroupRequests.RemoveUserRequest;
import pico.erp.user.group.GroupRequests.RevokeRoleRequest;
import pico.erp.user.group.GroupRequests.UpdateRequest;
import pico.erp.user.role.RoleId;

public interface GroupService {

  void addUser(@Valid AddUserRequest request);

  GroupData create(@Valid CreateRequest request);

  void delete(@Valid DeleteRequest request);

  boolean exists(@NotNull GroupId id);

  GroupData get(@NotNull GroupId id);

  void grantRole(@Valid GrantRoleRequest request);

  boolean hasRole(@NotNull GroupId groupId, @NotNull RoleId roleId);

  boolean hasUser(@NotNull GroupId groupId, @NotNull UserId userId);

  void removeUser(@Valid RemoveUserRequest request);

  void revokeRole(@Valid RevokeRoleRequest request);

  void update(@Valid UpdateRequest request);


}
