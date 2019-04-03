package pico.erp.user.role;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import kkojaeh.spring.boot.component.ComponentAutowired;
import org.springframework.stereotype.Repository;
import pico.erp.shared.data.Role;

@Repository
public class RoleRepositoryImpl implements RoleRepository {

  @ComponentAutowired(required = false)
  List<Role> roles;

  @Override
  public Stream<Role> findAll() {
    return roles.stream();
  }

  @Override
  public Optional<Role> findBy(RoleId id) {
    return roles.stream()
      .filter(r -> r.getId().equals(id.getValue()))
      .findFirst();
  }

}
