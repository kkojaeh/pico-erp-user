package pico.erp.user.department;

import java.util.Collection;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import pico.erp.shared.TypeDefinitions;
import pico.erp.shared.event.Event;
import pico.erp.user.User;

public interface DepartmentMessages {

  @Data
  final class CreateRequest {

    @Valid
    @NotNull
    DepartmentId id;

    @Size(min = 1, max = TypeDefinitions.NAME_LENGTH)
    @NotNull
    String name;

    @Valid
    User manager;

  }

  @Data
  final class UpdateRequest {

    @Size(min = 1, max = TypeDefinitions.NAME_LENGTH)
    @NotNull
    String name;

    @Valid
    User manager;

  }

  @Data
  final class DeleteRequest {

  }

  @NoArgsConstructor
  @AllArgsConstructor
  @Data
  class PrepareImportRequest {

    Department previous;

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
  class PrepareImportResponse {

    Collection<Event> events;

  }

}
