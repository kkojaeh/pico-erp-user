package pico.erp.user.group;

import kkojaeh.spring.boot.component.ComponentBean;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pico.erp.shared.event.EventPublisher;
import pico.erp.user.UserExceptions.NotFoundException;
import pico.erp.user.UserId;
import pico.erp.user.UserMessages.BelongToGroupRequest;
import pico.erp.user.UserMessages.WithdrawFromGroupRequest;
import pico.erp.user.UserRepository;
import pico.erp.user.group.GroupExceptions.AlreadyExistsException;
import pico.erp.user.group.GroupRequests.AddUserRequest;
import pico.erp.user.group.GroupRequests.CreateRequest;
import pico.erp.user.group.GroupRequests.DeleteRequest;
import pico.erp.user.group.GroupRequests.GrantRoleRequest;
import pico.erp.user.group.GroupRequests.RemoveUserRequest;
import pico.erp.user.group.GroupRequests.RevokeRoleRequest;
import pico.erp.user.group.GroupRequests.UpdateRequest;
import pico.erp.user.role.RoleExceptions;
import pico.erp.user.role.RoleId;
import pico.erp.user.role.RoleRepository;

@SuppressWarnings("Duplicates")
@Service
@ComponentBean
@Transactional
@Validated
public class GroupServiceLogic implements GroupService {

  @Autowired
  private GroupRepository groupRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private GroupMapper mapper;

  @Autowired
  private EventPublisher eventPublisher;

  @Override
  public void addUser(AddUserRequest request) {
    val group = groupRepository.findBy(request.getId())
      .orElseThrow(GroupExceptions.NotFoundException::new);
    val user = userRepository.findBy(request.getUserId())
      .orElseThrow(NotFoundException::new);
    val belongToGroupRequest = new BelongToGroupRequest();
    belongToGroupRequest.setGroup(group);
    val response = user.apply(belongToGroupRequest);
    userRepository.update(user);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public GroupData create(CreateRequest request) {
    val group = new Group();
    val response = group.apply(mapper.map(request));
    if (groupRepository.exists(group.getId())) {
      throw new AlreadyExistsException();
    }
    groupRepository.create(group);
    eventPublisher.publishEvents(response.getEvents());
    return mapper.map(group);
  }

  @Override
  public void delete(DeleteRequest request) {
    val group = groupRepository.findBy(request.getId())
      .orElseThrow(GroupExceptions.NotFoundException::new);
    val response = group.apply(mapper.map(request));
    groupRepository.deleteBy(request.getId());
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public boolean exists(GroupId id) {
    return groupRepository.exists(id);
  }

  @Override
  public GroupData get(GroupId id) {
    return groupRepository.findBy(id)
      .map(mapper::map)
      .orElseThrow(GroupExceptions.NotFoundException::new);
  }

  @Override
  public void grantRole(GrantRoleRequest request) {
    val group = groupRepository.findBy(request.getId())
      .orElseThrow(GroupExceptions.NotFoundException::new);
    val response = group.apply(mapper.map(request));
    groupRepository.update(group);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public boolean hasRole(GroupId groupId, RoleId roleId) {
    val group = groupRepository.findBy(groupId).orElseThrow(
      GroupExceptions.NotFoundException::new);
    val role = roleRepository.findBy(roleId).orElseThrow(
      RoleExceptions.NotFoundException::new);
    return group.getRoles().contains(role);
  }

  @Override
  public boolean hasUser(GroupId groupId, UserId userId) {
    val group = groupRepository.findBy(groupId).orElseThrow(
      GroupExceptions.NotFoundException::new);
    val user = userRepository.findBy(userId).orElseThrow(NotFoundException::new);
    return user.getGroups().contains(group);
  }

  @Override
  public void removeUser(RemoveUserRequest request) {
    val group = groupRepository.findBy(request.getId())
      .orElseThrow(GroupExceptions.NotFoundException::new);
    val user = userRepository.findBy(request.getUserId())
      .orElseThrow(NotFoundException::new);
    val withdrawFromGroupRequest = new WithdrawFromGroupRequest();
    withdrawFromGroupRequest.setGroup(group);
    val response = user.apply(withdrawFromGroupRequest);
    userRepository.update(user);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public void revokeRole(RevokeRoleRequest request) {
    val group = groupRepository.findBy(request.getId())
      .orElseThrow(GroupExceptions.NotFoundException::new);
    val response = group.apply(mapper.map(request));
    groupRepository.update(group);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public void update(UpdateRequest request) {
    val group = groupRepository.findBy(request.getId())
      .orElseThrow(GroupExceptions.NotFoundException::new);
    val response = group.apply(mapper.map(request));
    groupRepository.update(group);
    eventPublisher.publishEvents(response.getEvents());
  }
}
