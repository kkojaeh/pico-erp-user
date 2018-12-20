package pico.erp.user.role;

import java.util.Optional;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import pico.erp.shared.data.Role;

@Mapper
public abstract class RoleMapper {

  @Lazy
  @Autowired
  private RoleRepository roleRepository;

  public RoleId map(Role role) {
    return RoleId.from(role.getId());
  }

  public Role map(RoleId roleId) {
    return Optional.ofNullable(roleId)
      .map(id -> roleRepository.findBy(id)
        .orElseThrow(RoleExceptions.NotFoundException::new)
      )
      .orElse(null);
  }

}
