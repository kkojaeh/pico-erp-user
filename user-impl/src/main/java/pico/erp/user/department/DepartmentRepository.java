package pico.erp.user.department;

import java.util.Optional;
import java.util.stream.Stream;
import javax.validation.constraints.NotNull;
import pico.erp.user.data.DepartmentId;

public interface DepartmentRepository {

  Department create(@NotNull Department department);

  void deleteBy(@NotNull DepartmentId id);

  boolean exists(@NotNull DepartmentId id);

  Optional<Department> findBy(@NotNull DepartmentId id);

  Stream<Department> getAll();

  void update(@NotNull Department department);

}
