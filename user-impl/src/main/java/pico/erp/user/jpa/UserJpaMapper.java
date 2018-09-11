package pico.erp.user.jpa;

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
import pico.erp.user.User;
import pico.erp.user.data.DepartmentId;
import pico.erp.user.data.GroupId;
import pico.erp.user.data.RoleId;
import pico.erp.user.data.UserId;
import pico.erp.user.department.Department;
import pico.erp.user.group.Group;
import pico.erp.user.role.RoleRepository;

@Mapper
public abstract class UserJpaMapper {

  @Autowired
  private GroupEntityRepository groupEntityRepository;

  @Autowired
  private DepartmentEntityRepository departmentEntityRepository;

  @Autowired
  private UserEntityRepository userEntityRepository;

  @Lazy
  @Autowired
  private RoleRepository roleRepository;

  protected RoleId map(Role role) {
    return RoleId.from(role.getId());
  }

  protected Role map(RoleId roleId) {
    return roleRepository.findBy(roleId).orElse(null);
  }

  protected UserEntity map(UserId userId) {
    return userEntityRepository.findOne(userId);
  }

  protected GroupEntity map(GroupId groupId) {
    return groupEntityRepository.findOne(groupId);
  }

  protected DepartmentEntity map(DepartmentId departmentId) {
    return departmentEntityRepository.findOne(departmentId);
  }


  @Mappings({
    @Mapping(target = "createdBy", ignore = true),
    @Mapping(target = "createdDate", ignore = true),
    @Mapping(target = "lastModifiedBy", ignore = true),
    @Mapping(target = "lastModifiedDate", ignore = true)
  })
  public abstract UserEntity map(User user);

  @Mappings({
    @Mapping(target = "createdBy", ignore = true),
    @Mapping(target = "createdDate", ignore = true),
    @Mapping(target = "lastModifiedBy", ignore = true),
    @Mapping(target = "lastModifiedDate", ignore = true)
  })
  public abstract GroupEntity map(Group group);

  @Mappings({
    @Mapping(target = "createdBy", ignore = true),
    @Mapping(target = "createdDate", ignore = true),
    @Mapping(target = "lastModifiedBy", ignore = true),
    @Mapping(target = "lastModifiedDate", ignore = true)
  })
  public abstract DepartmentEntity map(Department department);

  public User map(UserEntity entity) {
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
          .map(this::map)
          .filter(role -> role != null)
          .collect(Collectors.toSet())
      )
      .groups(
        entity.getGroups()
          .stream()
          .map(this::map)
          .collect(Collectors.toSet())
      )
      .department(
        Optional.ofNullable(entity.getDepartment())
          .map(this::map)
          .orElse(null)
      )
      .build();
  }

  public Group map(GroupEntity entity) {
    return Group.builder()
      .id(entity.getId())
      .name(entity.getName())
      .roles(
        entity.getRoles()
          .stream()
          .map(this::map)
          .filter(role -> role != null)
          .collect(Collectors.toSet())
      )
      .build();
  }

  protected Department map(DepartmentEntity entity) {
    return Department.builder()
      .id(entity.getId())
      .name(entity.getName())
      .manager(
        Optional.ofNullable(entity.getManager())
          .map(this::map)
          .orElse(null)
      )
      .build();
  }

  public abstract void pass(UserEntity from, @MappingTarget UserEntity to);

  public abstract void pass(GroupEntity from, @MappingTarget GroupEntity to);

  @AfterMapping
  protected void afterMapping(DepartmentEntity from, @MappingTarget DepartmentEntity to) {
    to.setManager(from.getManager());
  }

  @AfterMapping
  protected void afterMapping(UserEntity from, @MappingTarget UserEntity to) {
    to.setDepartment(from.getDepartment());
  }

  @Mappings({
    @Mapping(target = "manager", ignore = true)
  })
  public abstract void pass(DepartmentEntity from, @MappingTarget DepartmentEntity to);


}
