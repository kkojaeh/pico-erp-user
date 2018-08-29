package pico.erp.user;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import pico.erp.user.GroupRequests.AddUserRequest;
import pico.erp.user.GroupRequests.CreateRequest;
import pico.erp.user.GroupRequests.DeleteRequest;
import pico.erp.user.GroupRequests.GrantRoleRequest;
import pico.erp.user.GroupRequests.RemoveUserRequest;
import pico.erp.user.GroupRequests.RevokeRoleRequest;
import pico.erp.user.GroupRequests.UpdateRequest;
import pico.erp.user.data.GroupData;
import pico.erp.user.data.GroupId;
import pico.erp.user.data.RoleId;
import pico.erp.user.data.UserId;

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
