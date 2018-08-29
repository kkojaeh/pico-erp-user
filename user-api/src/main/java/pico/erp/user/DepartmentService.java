package pico.erp.user;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import pico.erp.user.DepartmentRequests.CreateRequest;
import pico.erp.user.DepartmentRequests.DeleteRequest;
import pico.erp.user.DepartmentRequests.UpdateRequest;
import pico.erp.user.data.DepartmentData;
import pico.erp.user.data.DepartmentId;

public interface DepartmentService {

  DepartmentData create(@Valid CreateRequest request);

  void delete(@Valid DeleteRequest request);

  boolean exists(@NotNull DepartmentId id);

  DepartmentData get(@NotNull DepartmentId id);

  void update(@Valid UpdateRequest request);

}
