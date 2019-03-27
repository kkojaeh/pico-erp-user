package pico.erp.user;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pico.erp.shared.data.Auditor;
import pico.erp.user.department.DepartmentId;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserView {

  UserId id;

  String name;

  String email;

  String position;

  Boolean enabled;

  String mobilePhoneNumber;

  DepartmentId departmentId;

  String departmentName;

  Auditor createdBy;

  LocalDateTime createdDate;

  Auditor lastModifiedBy;

  LocalDateTime lastModifiedDate;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Filter {

    String name;

    DepartmentId departmentId;

    Boolean enabled;

  }


}
