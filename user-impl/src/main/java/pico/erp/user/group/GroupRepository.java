package pico.erp.user.group;

import java.util.Optional;
import java.util.stream.Stream;
import javax.validation.constraints.NotNull;
import pico.erp.user.data.GroupId;

public interface GroupRepository {

  Group create(@NotNull Group group);

  void deleteBy(@NotNull GroupId id);

  boolean exists(@NotNull GroupId id);

  Optional<Group> findBy(@NotNull GroupId id);

  Stream<Group> getAll();

  void update(@NotNull Group group);

}
