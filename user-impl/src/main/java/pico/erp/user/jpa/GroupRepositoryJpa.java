package pico.erp.user.jpa;

import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pico.erp.user.data.GroupId;
import pico.erp.user.group.Group;
import pico.erp.user.group.GroupRepository;

@Repository
interface GroupEntityRepository extends CrudRepository<GroupEntity, GroupId> {

}

@Repository
@Transactional
public class GroupRepositoryJpa implements GroupRepository {

  @Autowired
  private GroupEntityRepository repository;

  @Autowired
  private UserJpaMapper mapper;


  @Override
  public Group create(Group group) {
    GroupEntity entity = mapper.map(group);
    entity = repository.save(entity);
    return mapper.map(entity);
  }

  @Override
  public void deleteBy(GroupId id) {
    repository.delete(id);
  }

  @Override
  public boolean exists(GroupId id) {
    return repository.exists(id);
  }

  @Override
  public Optional<Group> findBy(GroupId id) {
    return Optional.ofNullable(repository.findOne(id)).map(mapper::map);
  }

  @Override
  public Stream<Group> getAll() {
    return StreamSupport.stream(
      repository.findAll().spliterator(), false
    ).map(mapper::map);
  }

  @Override
  public void update(Group group) {
    GroupEntity entity = repository.findOne(group.getId());
    mapper.pass(mapper.map(group), entity);
    repository.save(entity);
  }


}
