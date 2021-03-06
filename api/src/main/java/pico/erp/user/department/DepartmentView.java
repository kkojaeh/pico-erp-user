package pico.erp.user.department;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pico.erp.shared.data.Auditor;
import pico.erp.user.UserId;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DepartmentView {

  DepartmentId id;

  String name;

  UserId managerId;

  String managerName;

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

    UserId managerId;

  }

}
