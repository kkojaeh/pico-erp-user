package pico.erp.user;

import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
interface UserEntityRepository extends CrudRepository<UserEntity, UserId> {

  @Query("SELECT u FROM User u WHERE u.email = :email")
  UserEntity findByEmail(@Param("email") String email);

  @Query("SELECT u FROM User u WHERE u.name = :name")
  UserEntity findByName(@Param("name") String name);

  /*
  @Modifying
  @Query("UPDATE User u set u.password = :encodedPassword where u.id = :id")
  int updateEncodedPasswordByUsername(@Param("encodedPassword") String encodedPassword,
    @Param("id") UserId id);
    */

}

@Repository
@Transactional
public class UserRepositoryJpa implements UserRepository {

  @Autowired
  private UserEntityRepository repository;

  @Autowired
  private UserMapper mapper;

  @Override
  public User create(User user) {
    val entity = mapper.entity(user);
    val created = repository.save(entity);
    return mapper.domain(created);
  }

  @Override
  public void deleteBy(UserId id) {
    repository.deleteById(id);
  }

  @Override
  public boolean exists(UserId id) {
    return repository.existsById(id);
  }

  @Override
  public Optional<User> findBy(UserId id) {
    return repository.findById(id)
      .map(mapper::domain);
  }

  @Override
  public Optional<User> findBy(String name) {
    return Optional.ofNullable(repository.findByName(name))
      .map(mapper::domain);
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return Optional.ofNullable(repository.findByEmail(email))
      .map(mapper::domain);
  }

  @Override
  public Stream<User> getAll() {
    return StreamSupport.stream(
      repository.findAll().spliterator(), false
    ).map(mapper::domain);
  }

  @Override
  public void update(User user) {
    val entity = repository.findById(user.getId()).get();
    mapper.pass(mapper.entity(user), entity);
    repository.save(entity);
  }


}
