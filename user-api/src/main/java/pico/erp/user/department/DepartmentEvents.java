package pico.erp.user.department;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pico.erp.shared.event.Event;
import pico.erp.user.data.DepartmentId;

public interface DepartmentEvents {

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  final class CreatedEvent implements Event {

    public final static String CHANNEL = "event.user-department.created";

    private DepartmentId departmentId;

    public String channel() {
      return CHANNEL;
    }

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  final class DeletedEvent implements Event {

    public final static String CHANNEL = "event.user-department.deleted";

    private DepartmentId departmentId;

    public String channel() {
      return CHANNEL;
    }

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  final class UpdatedEvent implements Event {

    public final static String CHANNEL = "event.user-department.updated";

    private DepartmentId departmentId;

    public String channel() {
      return CHANNEL;
    }

  }
}
