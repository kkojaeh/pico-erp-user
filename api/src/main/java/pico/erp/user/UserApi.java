package pico.erp.user;

import javax.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pico.erp.shared.data.Role;

public final class UserApi {

  @RequiredArgsConstructor
  public enum Roles implements Role {

    USER_MANAGER,
    USER_ACCESSOR;

    @Id
    @Getter
    private final String id = name();

  }
}
