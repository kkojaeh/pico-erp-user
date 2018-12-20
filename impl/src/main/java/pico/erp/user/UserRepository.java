package pico.erp.user;

import java.util.Optional;
import java.util.stream.Stream;
import javax.validation.constraints.NotNull;

public interface UserRepository {

  User create(@NotNull User user);

  void deleteBy(@NotNull UserId id);

  boolean exists(@NotNull UserId id);

  Optional<User> findBy(@NotNull UserId id);

  Optional<User> findBy(@NotNull String name);

  Optional<User> findByEmail(@NotNull String email);

  Stream<User> getAll();

  void update(@NotNull User user);

}
