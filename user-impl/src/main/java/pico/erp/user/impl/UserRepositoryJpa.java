package pico.erp.user.impl;

import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pico.erp.user.core.UserRepository;
import pico.erp.user.data.UserId;
import pico.erp.user.domain.User;
import pico.erp.user.impl.jpa.UserEntity;

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
  private UserJpaMapper mapper;

  @Override
  public User create(User user) {
    UserEntity entity = mapper.map(user);
    entity = repository.save(entity);
    return mapper.map(entity);
  }

  @Override
  public void deleteBy(UserId id) {
    repository.delete(id);
  }

  @Override
  public boolean exists(UserId id) {
    return repository.exists(id);
  }

  @Override
  public Optional<User> findBy(UserId id) {
    return Optional.ofNullable(repository.findOne(id))
      .map(mapper::map);
  }

  @Override
  public Optional<User> findBy(String name) {
    return Optional.ofNullable(repository.findByName(name))
      .map(mapper::map);
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return Optional.ofNullable(repository.findByEmail(email))
      .map(mapper::map);
  }

  @Override
  public Stream<User> getAll() {
    return StreamSupport.stream(
      repository.findAll().spliterator(), false
    ).map(mapper::map);
  }

  @Override
  public void update(User user) {
    UserEntity entity = repository.findOne(user.getId());
    mapper.pass(mapper.map(user), entity);
    repository.save(entity);
  }


}
