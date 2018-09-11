package pico.erp.user.group;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pico.erp.shared.TypeDefinitions;
import pico.erp.user.data.GroupId;
import pico.erp.user.data.RoleId;
import pico.erp.user.data.UserId;

public interface GroupRequests {

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class CreateRequest {

    @Valid
    @NotNull
    GroupId id;

    @Size(min = 2, max = TypeDefinitions.NAME_LENGTH)
    @NotNull
    String name;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  class GrantRoleRequest {

    @Valid
    @NotNull
    GroupId id;

    @Valid
    @NotNull
    RoleId roleId;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  class RevokeRoleRequest {

    @Valid
    @NotNull
    GroupId id;

    @Valid
    @NotNull
    RoleId roleId;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class UpdateRequest {

    @Valid
    @NotNull
    GroupId id;

    @Size(min = 2, max = TypeDefinitions.NAME_LENGTH)
    @NotNull
    String name;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  class DeleteRequest {

    @Valid
    @NotNull
    GroupId id;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  class AddUserRequest {

    @Valid
    @NotNull
    GroupId id;

    @Valid
    @NotNull
    UserId userId;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  class RemoveUserRequest {

    @Valid
    @NotNull
    GroupId id;

    @Valid
    @NotNull
    UserId userId;

  }
}
