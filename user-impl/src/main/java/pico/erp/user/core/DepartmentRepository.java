package pico.erp.user.core;

import java.util.Optional;
import java.util.stream.Stream;
import javax.validation.constraints.NotNull;
import pico.erp.user.data.DepartmentId;
import pico.erp.user.domain.Department;

public interface DepartmentRepository {

  Department create(@NotNull Department department);

  void deleteBy(@NotNull DepartmentId id);

  boolean exists(@NotNull DepartmentId id);

  Optional<Department> findBy(@NotNull DepartmentId id);

  Stream<Department> getAll();

  void update(@NotNull Department department);

}
