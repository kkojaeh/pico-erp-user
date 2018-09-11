package pico.erp.user.department;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import pico.erp.user.data.DepartmentData;
import pico.erp.user.data.DepartmentId;
import pico.erp.user.department.DepartmentRequests.CreateRequest;
import pico.erp.user.department.DepartmentRequests.DeleteRequest;
import pico.erp.user.department.DepartmentRequests.UpdateRequest;

public interface DepartmentService {

  DepartmentData create(@Valid CreateRequest request);

  void delete(@Valid DeleteRequest request);

  boolean exists(@NotNull DepartmentId id);

  DepartmentData get(@NotNull DepartmentId id);

  void update(@Valid UpdateRequest request);

}
