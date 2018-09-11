package pico.erp.user.role;

import java.util.Optional;
import java.util.stream.Stream;
import javax.validation.constraints.NotNull;
import pico.erp.shared.data.Role;
import pico.erp.user.data.RoleId;

public interface RoleRepository {

  Stream<Role> findAll();

  Optional<Role> findBy(@NotNull RoleId id);

}
