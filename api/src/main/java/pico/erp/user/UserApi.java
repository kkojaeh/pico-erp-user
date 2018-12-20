package pico.erp.user;

import javax.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pico.erp.shared.ApplicationId;
import pico.erp.shared.data.Role;

public final class UserApi {

  public static ApplicationId ID = ApplicationId.from("user");

  @RequiredArgsConstructor
  public enum Roles implements Role {

    USER_MANAGER;

    @Id
    @Getter
    private final String id = name();

  }
}
