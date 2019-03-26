package pico.erp.user.department;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import pico.erp.user.User;
import pico.erp.user.department.DepartmentEvents.CreatedEvent;
import pico.erp.user.department.DepartmentEvents.DeletedEvent;
import pico.erp.user.department.DepartmentEvents.UpdatedEvent;

@Getter
@ToString
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Department implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  DepartmentId id;

  String name;

  User manager;

  public Department() {
  }

  public DepartmentMessages.CreateResponse apply(DepartmentMessages.CreateRequest request) {
    id = request.getId();
    name = request.getName();
    manager = request.getManager();
    return new DepartmentMessages.CreateResponse(
      Arrays.asList(new CreatedEvent(this.id))
    );
  }

  public DepartmentMessages.UpdateResponse apply(DepartmentMessages.UpdateRequest request) {
    name = request.getName();
    manager = request.getManager();

    return new DepartmentMessages.UpdateResponse(
      Arrays.asList(new UpdatedEvent(this.id))
    );
  }

  public DepartmentMessages.DeleteResponse apply(DepartmentMessages.DeleteRequest request) {
    return new DepartmentMessages.DeleteResponse(
      Arrays.asList(new DeletedEvent(this.id))
    );
  }

  public DepartmentMessages.PrepareImportResponse apply(
    DepartmentMessages.PrepareImportRequest request) {

    return new DepartmentMessages.PrepareImportResponse(Collections.emptyList());

  }

}
