package pico.erp.user;

import javax.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pico.erp.shared.data.Role;

@RequiredArgsConstructor
public enum UserRoles implements Role {

  USER_MANAGER;

  @Id
  @Getter
  private final String id = name();

}
