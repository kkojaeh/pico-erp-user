package pico.erp.user.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import pico.erp.shared.data.Role;
import pico.erp.user.core.RoleRepository;
import pico.erp.user.data.RoleId;

@Repository
public class RoleRepositoryImpl implements RoleRepository {

  @Autowired(required = false)
  @Lazy
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
