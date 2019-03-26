package pico.erp.user.group;

import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
interface GroupEntityRepository extends CrudRepository<GroupEntity, GroupId> {

}

@Repository
@Transactional
public class GroupRepositoryJpa implements GroupRepository {

  @Autowired
  private GroupEntityRepository repository;

  @Autowired
  private GroupMapper mapper;


  @Override
  public Group create(Group group) {
    val entity = mapper.entity(group);
    val created = repository.save(entity);
    return mapper.domain(created);
  }

  @Override
  public void deleteBy(GroupId id) {
    repository.deleteById(id);
  }

  @Override
  public boolean exists(GroupId id) {
    return repository.existsById(id);
  }

  @Override
  public Optional<Group> findBy(GroupId id) {
    return repository.findById(id).map(mapper::domain);
  }

  @Override
  public Stream<Group> findAll() {
    return StreamSupport.stream(
      repository.findAll().spliterator(), false
    ).map(mapper::domain);
  }

  @Override
  public void update(Group group) {
    GroupEntity entity = repository.findById(group.getId()).get();
    mapper.pass(mapper.entity(group), entity);
    repository.save(entity);
  }


}
