package pico.erp.user.department;

import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
interface DepartmentEntityRepository extends CrudRepository<DepartmentEntity, DepartmentId> {


}

@Repository
@Transactional
public class DepartmentRepositoryJpa implements DepartmentRepository {

  @Autowired
  private DepartmentEntityRepository repository;


  @Autowired
  private DepartmentMapper mapper;

  @Override
  public Department create(Department department) {
    val entity = mapper.jpa(department);
    val created = repository.save(entity);
    return mapper.jpa(created);
  }

  @Override
  public void deleteBy(DepartmentId id) {
    repository.delete(id);
  }

  @Override
  public boolean exists(DepartmentId id) {
    return repository.exists(id);
  }

  @Override
  public Optional<Department> findBy(DepartmentId id) {
    return Optional.ofNullable(repository.findOne(id))
      .map(mapper::jpa);
  }

  @Override
  public Stream<Department> getAll() {
    return StreamSupport.stream(
      repository.findAll().spliterator(), false
    ).map(mapper::jpa);
  }

  @Override
  public void update(Department department) {
    val entity = repository.findOne(department.getId());
    mapper.pass(mapper.jpa(department), entity);
    repository.save(entity);
  }
}
