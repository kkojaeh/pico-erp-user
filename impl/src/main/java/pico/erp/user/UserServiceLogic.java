package pico.erp.user;

import kkojaeh.spring.boot.component.ComponentBean;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pico.erp.shared.data.Auditor;
import pico.erp.shared.event.EventPublisher;
import pico.erp.user.UserExceptions.AlreadyExistsException;
import pico.erp.user.UserExceptions.EmailAlreadyExistsException;
import pico.erp.user.UserExceptions.NotFoundException;
import pico.erp.user.UserRequests.CreateRequest;
import pico.erp.user.UserRequests.DeleteRequest;
import pico.erp.user.UserRequests.GrantRoleRequest;
import pico.erp.user.UserRequests.RevokeRoleRequest;
import pico.erp.user.UserRequests.UpdateRequest;
import pico.erp.user.role.RoleExceptions;
import pico.erp.user.role.RoleId;
import pico.erp.user.role.RoleRepository;

@SuppressWarnings("Duplicates")
@Service
@ComponentBean
@Transactional
@Validated
public class UserServiceLogic implements UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private EventPublisher eventPublisher;

  @Autowired
  private UserMapper userMapper;

  public UserData create(CreateRequest request) {
    if (userRepository.exists(request.getId())) {
      throw new AlreadyExistsException();
    }
    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
      throw new EmailAlreadyExistsException();
    }
    val user = new User();
    val response = user.apply(userMapper.map(request));

    val created = userRepository.create(user);
    eventPublisher.publishEvents(response.getEvents());
    return userMapper.map(created);
  }

  @Override
  public void delete(DeleteRequest request) {
    val user = userRepository.findBy(request.getId())
      .orElseThrow(NotFoundException::new);
    val response = user.apply(userMapper.map(request));
    userRepository.deleteBy(request.getId());
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public boolean exists(UserId id) {
    return userRepository.exists(id);
  }

  @Override
  public UserData get(UserId id) {
    return userRepository.findBy(id)
      .map(userMapper::map)
      .orElseThrow(UserExceptions.NotFoundException::new);
  }

  @Override
  public UserData get(String name) {
    return userRepository.findBy(name)
      .map(userMapper::map)
      .orElse(null);
  }

  @Override
  public Auditor getAuditor(UserId id) {
    return userRepository.findBy(id)
      .map(user -> new Auditor(user.getId().getValue(), user.getName()))
      .orElseThrow(UserExceptions.NotFoundException::new);
  }

  @Override
  public void grantRole(GrantRoleRequest request) {
    val user = userRepository.findBy(request.getId())
      .orElseThrow(UserExceptions.NotFoundException::new);
    val response = user.apply(userMapper.map(request));
    userRepository.update(user);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public boolean hasRole(UserId userId, RoleId roleId) {
    val user = userRepository.findBy(userId).orElseThrow(UserExceptions.NotFoundException::new);
    val role = roleRepository.findBy(roleId).orElseThrow(
      RoleExceptions.NotFoundException::new);
    return user.getRoles().contains(role);
  }

  @Override
  public void revokeRole(RevokeRoleRequest request) {
    val user = userRepository.findBy(request.getId())
      .orElseThrow(UserExceptions.NotFoundException::new);
    val response = user.apply(userMapper.map(request));
    userRepository.update(user);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public void update(UpdateRequest request) {
    val user = userRepository.findBy(request.getId())
      .orElseThrow(UserExceptions.NotFoundException::new);
    val response = user.apply(userMapper.map(request));
    userRepository.update(user);
    eventPublisher.publishEvents(response.getEvents());
  }

}
