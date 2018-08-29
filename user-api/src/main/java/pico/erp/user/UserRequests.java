package pico.erp.user;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pico.erp.shared.TypeDefinitions;
import pico.erp.user.data.DepartmentId;
import pico.erp.user.data.RoleId;
import pico.erp.user.data.UserId;

public interface UserRequests {

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class CreateRequest {

    @Valid
    @NotNull
    UserId id;

    @Size(min = 1, max = TypeDefinitions.PASSWORD_LENGTH)
    String password;

    @Size(min = 1, max = TypeDefinitions.EMAIL_LENGTH)
    @NotNull
    String email;

    @Size(min = 2, max = TypeDefinitions.NAME_LENGTH)
    @NotNull
    String name;

    @Size(max = TypeDefinitions.PHONE_NUMBER_LENGTH)
    String mobilePhoneNumber;

    @Size(max = TypeDefinitions.TITLE_LENGTH)
    String position;

    @Valid
    DepartmentId departmentId;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  class DeleteRequest {

    @Valid
    @NotNull
    UserId id;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  class GrantRoleRequest {

    @Valid
    @NotNull
    UserId id;

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
    UserId id;

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
    UserId id;

    @NotNull
    @Size(min = 1, max = TypeDefinitions.NAME_LENGTH)
    String name;

    @Size(min = 1, max = TypeDefinitions.EMAIL_LENGTH)
    @NotNull
    String email;

    boolean enabled;

    @Size(max = TypeDefinitions.PHONE_NUMBER_LENGTH)
    String mobilePhoneNumber;

    @Size(max = TypeDefinitions.TITLE_LENGTH)
    String position;

    @Valid
    DepartmentId departmentId;

  }
}
