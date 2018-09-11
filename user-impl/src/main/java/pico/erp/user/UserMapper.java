package pico.erp.user;

import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import pico.erp.shared.data.Role;
import pico.erp.user.UserExceptions.NotFoundException;
import pico.erp.user.data.DepartmentData;
import pico.erp.user.data.DepartmentId;
import pico.erp.user.data.GroupData;
import pico.erp.user.data.GroupId;
import pico.erp.user.data.RoleId;
import pico.erp.user.data.UserData;
import pico.erp.user.data.UserId;
import pico.erp.user.department.Department;
import pico.erp.user.department.DepartmentExceptions;
import pico.erp.user.department.DepartmentMessages.CreateRequest;
import pico.erp.user.department.DepartmentMessages.DeleteRequest;
import pico.erp.user.department.DepartmentMessages.UpdateRequest;
import pico.erp.user.department.DepartmentRepository;
import pico.erp.user.department.DepartmentRequests;
import pico.erp.user.group.Group;
import pico.erp.user.group.GroupMessages;
import pico.erp.user.group.GroupMessages.GrantRoleRequest;
import pico.erp.user.group.GroupMessages.RevokeRoleRequest;
import pico.erp.user.group.GroupRequests;
import pico.erp.user.role.RoleExceptions;
import pico.erp.user.role.RoleRepository;

@Mapper
public abstract class UserMapper {

  @Autowired
  protected PasswordStrengthValidator passwordStrengthValidator;

  @Autowired
  protected PasswordRandomGenerator passwordRandomGenerator;

  @Autowired
  private DepartmentRepository departmentRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;


  protected RoleId map(Role role) {
    return RoleId.from(role.getId());
  }

  protected Department map(DepartmentId departmentId) {
    return Optional.ofNullable(departmentId)
      .map(id -> departmentRepository.findBy(id)
        .orElseThrow(DepartmentExceptions.NotFoundException::new)
      )
      .orElse(null);
  }

  protected Role map(RoleId roleId) {
    return Optional.ofNullable(roleId)
      .map(id -> roleRepository.findBy(id)
        .orElseThrow(RoleExceptions.NotFoundException::new)
      )
      .orElse(null);
  }

  protected User map(UserId userId) {
    return Optional.ofNullable(userId)
      .map(id -> userRepository.findBy(id)
        .orElseThrow(NotFoundException::new)
      )
      .orElse(null);
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

  public abstract GroupMessages.CreateRequest map(GroupRequests.CreateRequest request);

  public abstract GroupMessages.UpdateRequest map(GroupRequests.UpdateRequest request);

  public abstract GroupMessages.DeleteRequest map(GroupRequests.DeleteRequest request);

  @Mappings({
    @Mapping(target = "role", source = "roleId")
  })
  public abstract GrantRoleRequest map(GroupRequests.GrantRoleRequest request);

  @Mappings({
    @Mapping(target = "role", source = "roleId")
  })
  public abstract RevokeRoleRequest map(GroupRequests.RevokeRoleRequest request);

  public abstract GroupData map(Group group);

  @Mappings({
    @Mapping(target = "manager", source = "managerId")
  })
  public abstract CreateRequest map(DepartmentRequests.CreateRequest request);

  @Mappings({
    @Mapping(target = "manager", source = "managerId")
  })
  public abstract UpdateRequest map(DepartmentRequests.UpdateRequest request);

  public abstract DeleteRequest map(DepartmentRequests.DeleteRequest request);

  @Mappings({
    @Mapping(target = "managerId", source = "manager.id")
  })
  public abstract DepartmentData map(Department department);

  protected GroupId mapId(Group group) {
    return group.getId();
  }


}
