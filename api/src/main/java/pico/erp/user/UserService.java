package pico.erp.user;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import pico.erp.shared.data.Auditor;
import pico.erp.user.UserRequests.CreateRequest;
import pico.erp.user.UserRequests.DeleteRequest;
import pico.erp.user.UserRequests.GrantRoleRequest;
import pico.erp.user.UserRequests.RevokeRoleRequest;
import pico.erp.user.UserRequests.UpdateRequest;
import pico.erp.user.role.RoleId;

public interface UserService {

  UserData create(@Valid CreateRequest request);

  void delete(@Valid DeleteRequest request);

  boolean exists(@NotNull UserId id);

  UserData get(@NotNull UserId id);

  UserData get(@NotNull String name);

  void grantRole(@Valid GrantRoleRequest request);

  boolean hasRole(@NotNull UserId userId, @NotNull RoleId roleId);

  void revokeRole(@Valid RevokeRoleRequest request);

  void update(@Valid UpdateRequest request);

  Auditor getAuditor(@NotNull UserId id);

}
