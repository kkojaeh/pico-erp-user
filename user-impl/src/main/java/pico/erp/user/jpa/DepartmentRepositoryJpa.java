package pico.erp.user.jpa;

import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pico.erp.user.data.DepartmentId;
import pico.erp.user.department.Department;
import pico.erp.user.department.DepartmentRepository;

@Repository
interface DepartmentEntityRepository extends CrudRepository<DepartmentEntity, DepartmentId> {


}

@Repository
@Transactional
public class DepartmentRepositoryJpa implements DepartmentRepository {

  @Autowired
  private DepartmentEntityRepository repository;


  @Autowired
  private UserJpaMapper mapper;

  @Override
  public Department create(Department department) {
    DepartmentEntity entity = mapper.map(department);
    entity = repository.save(entity);
    return mapper.map(entity);
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
      .map(mapper::map);
  }

  @Override
  public Stream<Department> getAll() {
    return StreamSupport.stream(
      repository.findAll().spliterator(), false
    ).map(mapper::map);
  }

  @Override
  public void update(Department department) {
    DepartmentEntity entity = repository.findOne(department.getId());
    mapper.pass(mapper.map(department), entity);
    repository.save(entity);
  }
}
