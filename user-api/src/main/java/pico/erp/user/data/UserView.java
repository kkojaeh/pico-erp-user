package pico.erp.user.data;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pico.erp.shared.data.Auditor;

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

  OffsetDateTime createdDate;

  Auditor lastModifiedBy;

  OffsetDateTime lastModifiedDate;

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
