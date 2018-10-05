package pico.erp.user;

import java.util.Optional;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import pico.erp.shared.data.Role;
import pico.erp.user.UserExceptions.NotFoundException;
import pico.erp.user.department.Department;
import pico.erp.user.department.DepartmentId;
import pico.erp.user.department.DepartmentMapper;
import pico.erp.user.group.Group;
import pico.erp.user.group.GroupEntity;
import pico.erp.user.group.GroupId;
import pico.erp.user.group.GroupMapper;
import pico.erp.user.password.PasswordRandomGenerator;
import pico.erp.user.password.PasswordStrengthValidator;
import pico.erp.user.role.RoleId;
import pico.erp.user.role.RoleMapper;

@Mapper
public abstract class UserMapper {

  @Autowired
  protected PasswordStrengthValidator passwordStrengthValidator;

  @Autowired
  protected PasswordRandomGenerator passwordRandomGenerator;

  /*@Autowired
  private DepartmentRepository departmentRepository;*/

  @Lazy
  @Autowired
  protected GroupMapper groupMapper;

  @Lazy
  @Autowired
  protected RoleMapper roleMapper;

  @Lazy
  @Autowired
  protected DepartmentMapper departmentMapper;

  @Lazy
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserEntityRepository userEntityRepository;

  /*protected Department map(DepartmentId departmentId) {
    return Optional.ofNullable(departmentId)
      .map(id -> departmentRepository.findBy(id)
        .orElseThrow(DepartmentExceptions.NotFoundException::new)
      )
      .orElse(null);
  }*/

  @AfterMapping
  protected void afterMapping(UserEntity from, @MappingTarget UserEntity to) {
    to.setDepartment(from.getDepartment());
  }

  @Mappings({
    @Mapping(target = "departmentId", source = "department.id")
  })
  public abstract UserData map(User user);

  @Mappings({
    @Mapping(target = "department", source = "departmentId"),
    @Mapping(target = "passwordStrengthValidator", expression = "java(passwordStrengthValidator)"),
    @Mapping(target = "passwordRandomGenerator", expression = "java(passwordRandomGenerator)")
  })
  public abstract UserMessages.CreateRequest map(UserRequests.CreateRequest request);

  public abstract UserMessages.DeleteRequest map(UserRequests.DeleteRequest request);

  @Mappings({
    @Mapping(target = "role", source = "roleId")
  })
  public abstract UserMessages.GrantRoleRequest map(UserRequests.GrantRoleRequest request);

  @Mappings({
    @Mapping(target = "role", source = "roleId")
  })
  public abstract UserMessages.RevokeRoleRequest map(UserRequests.RevokeRoleRequest request);

  @Mappings({
    @Mapping(target = "department", source = "departmentId")
  })
  public abstract UserMessages.UpdateRequest map(UserRequests.UpdateRequest request);

  public User domain(UserEntity entity) {
    return User.builder()
      .id(entity.getId())
      .name(entity.getName())
      .email(entity.getEmail())
      .mobilePhoneNumber(entity.getMobilePhoneNumber())
      .position(entity.getPosition())
      .accountNonExpired(entity.isAccountNonExpired())
      .accountNonLocked(entity.isAccountNonLocked())
      .credentialsNonExpired(entity.isCredentialsNonExpired())
      .enabled(entity.isEnabled())
      .roles(
        entity.getRoles()
          .stream()
          .map(roleMapper::map)
          .filter(role -> role != null)
          .collect(Collectors.toSet())
      )
      .groups(
        entity.getGroups()
          .stream()
          .map(groupMapper::domain)
          .collect(Collectors.toSet())
      )
      .department(
        Optional.ofNullable(entity.getDepartment())
          .map(departmentMapper::domain)
          .orElse(null)
      )
      .build();
  }

  @Mappings({
    @Mapping(target = "department", expression = "java(departmentMapper.entity(domain.getDepartment()))"),
    @Mapping(target = "createdBy", ignore = true),
    @Mapping(target = "createdDate", ignore = true),
    @Mapping(target = "lastModifiedBy", ignore = true),
    @Mapping(target = "lastModifiedDate", ignore = true)
  })
  public abstract UserEntity entity(User domain);

  protected GroupEntity entity(Group group) {
    return groupMapper.entity(group);
  }

  public User map(UserId userId) {
    return Optional.ofNullable(userId)
      .map(id -> userRepository.findBy(id)
        .orElseThrow(NotFoundException::new)
      )
      .orElse(null);
  }

  /*protected UserEntity map(UserId userId) {
    return userEntityRepository.findOne(userId);
  }*/

  protected RoleId map(Role role) {
    return roleMapper.map(role);
  }

  protected Role map(RoleId roleId) {
    return roleMapper.map(roleId);
  }

  protected Department map(DepartmentId departmentId) {
    return departmentMapper.domain(departmentId);
  }

  protected GroupId map(Group group) {
    return group != null ? group.getId() : null;
  }

  public abstract void pass(UserEntity from, @MappingTarget UserEntity to);


}
