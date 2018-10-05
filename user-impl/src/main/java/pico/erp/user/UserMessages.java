package pico.erp.user;

import java.util.Collection;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.Value;
import pico.erp.shared.TypeDefinitions;
import pico.erp.shared.data.Role;
import pico.erp.shared.event.Event;
import pico.erp.user.department.Department;
import pico.erp.user.group.Group;
import pico.erp.user.password.PasswordRandomGenerator;
import pico.erp.user.password.PasswordStrengthValidator;

public interface UserMessages {

  @Data
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
    Department department;

    @NotNull
    PasswordRandomGenerator passwordRandomGenerator;

    @NotNull
    PasswordStrengthValidator passwordStrengthValidator;

  }

  @Data
  class GrantRoleRequest {

    @Valid
    @NotNull
    Role role;

  }

  @Data
  class RevokeRoleRequest {

    @Valid
    @NotNull
    Role role;

  }

  @Data
  class BelongToGroupRequest {

    @Valid
    @NotNull
    Group group;

  }

  @Data
  class WithdrawFromGroupRequest {

    @Valid
    @NotNull
    Group group;

  }

  @Data
  class UpdateRequest {

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
    Department department;

  }

  @Data
  class DeleteRequest {

  }

  @Value
  class CreateResponse {

    Collection<Event> events;

  }

  @Value
  class GrantRoleResponse {

    Collection<Event> events;

  }

  @Value
  class RevokeRoleResponse {

    Collection<Event> events;

  }

  @Value
  class BelongToGroupResponse {

    Collection<Event> events;

  }

  @Value
  class WithdrawFromGroupResponse {

    Collection<Event> events;

  }

  @Value
  class UpdateResponse {

    Collection<Event> events;

  }

  @Value
  class DeleteResponse {

    Collection<Event> events;

  }
}
