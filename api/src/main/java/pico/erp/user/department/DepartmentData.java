package pico.erp.user.department;

import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import pico.erp.user.UserId;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id")
public class DepartmentData implements Serializable {

  private static final long serialVersionUID = 1L;

  DepartmentId id;

  String name;

  UserId managerId;

}
