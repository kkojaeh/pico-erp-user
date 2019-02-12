package pico.erp.user.group;

import java.util.Optional;
import java.util.stream.Stream;
import javax.validation.constraints.NotNull;

public interface GroupRepository {

  Group create(@NotNull Group group);

  void deleteBy(@NotNull GroupId id);

  boolean exists(@NotNull GroupId id);

  Optional<Group> findBy(@NotNull GroupId id);

  Stream<Group> findAll();

  void update(@NotNull Group group);

}
