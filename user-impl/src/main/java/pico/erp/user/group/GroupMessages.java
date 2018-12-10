package pico.erp.user.group;

import java.util.Collection;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import pico.erp.shared.TypeDefinitions;
import pico.erp.shared.data.Role;
import pico.erp.shared.event.Event;

public interface GroupMessages {

  @Data
  final class CreateRequest {

    @Valid
    @NotNull
    GroupId id;

    @Size(min = 2, max = TypeDefinitions.NAME_LENGTH)
    @NotNull
    String name;

  }

  @Data
  final class GrantRoleRequest {

    @Valid
    @NotNull
    Role role;

  }

  @Data
  final class RevokeRoleRequest {

    @Valid
    @NotNull
    Role role;

  }

  @Data
  final class UpdateRequest {

    @Size(min = 2, max = TypeDefinitions.NAME_LENGTH)
    @NotNull
    String name;

  }

  @Data
  final class DeleteRequest {

  }

  @NoArgsConstructor
  @AllArgsConstructor
  @Data
  class PrepareImportRequest {

    Group previous;

  }

  @Value
  class CreateResponse {

    Collection<Event> events;

  }

  @Value
  class DeleteResponse {

    Collection<Event> events;

  }

  @Value
  class UpdateResponse {

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
  class PrepareImportResponse {

    Collection<Event> events;

  }
}
