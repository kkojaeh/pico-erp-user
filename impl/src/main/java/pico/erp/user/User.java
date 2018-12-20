package pico.erp.user;

import static org.springframework.util.StringUtils.isEmpty;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.StringUtils;
import pico.erp.audit.annotation.Audit;
import pico.erp.shared.data.Role;
import pico.erp.user.UserEvents.CreatedEvent;
import pico.erp.user.UserEvents.UpdatedEvent;
import pico.erp.user.UserExceptions.GroupAlreadyExistsException;
import pico.erp.user.UserExceptions.GroupNotFoundException;
import pico.erp.user.UserExceptions.PasswordInvalidException;
import pico.erp.user.department.Department;
import pico.erp.user.group.Group;
import pico.erp.user.role.RoleExceptions.AlreadyExistsException;
import pico.erp.user.role.RoleExceptions.NotFoundException;

@Getter
@ToString
@Builder(toBuilder = true)
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Audit(alias = "user")
public class User implements Serializable {

  private static final long serialVersionUID = 1L;

  boolean accountNonExpired;

  boolean accountNonLocked;

  boolean credentialsNonExpired;

  boolean enabled;

  @Id
  UserId id;

  String name;

  String email;

  String mobilePhoneNumber;

  String position;

  Set<Role> roles;

  Set<Group> groups;

  Department department;

  public User() {
    this.roles = new HashSet<>();
    this.groups = new HashSet<>();
    accountNonExpired = true;
    accountNonLocked = true;
    credentialsNonExpired = true;
    enabled = true;
  }

  public UserMessages.CreateResponse apply(UserMessages.CreateRequest request) {
    id = request.getId();
    name = request.getName();
    email = request.getEmail();

    mobilePhoneNumber = request.getMobilePhoneNumber();
    position = request.getPosition();
    department = request.getDepartment();

    String password = request.getPassword();

    if (isEmpty(password)) {
      password = request.getPasswordRandomGenerator().generate();
    }

    Collection<String> errors = request.getPasswordStrengthValidator()
      .validate(password);

    if (!errors.isEmpty()) {
      throw new PasswordInvalidException(StringUtils.collectionToDelimitedString(errors, "\n"));
    }
    return new UserMessages.CreateResponse(
      Arrays.asList(new CreatedEvent(this.id, password))
    );
  }

  public UserMessages.DeleteResponse apply(UserMessages.DeleteRequest request) {
    return new UserMessages.DeleteResponse(Collections.emptyList());
  }

  public UserMessages.GrantRoleResponse apply(UserMessages.GrantRoleRequest request) {
    Role role = request.getRole();
    if (roles.contains(role)) {
      throw new AlreadyExistsException();
    }
    roles.add(role);
    return new UserMessages.GrantRoleResponse(Collections.emptyList());
  }

  public UserMessages.RevokeRoleResponse apply(UserMessages.RevokeRoleRequest request) {
    Role role = request.getRole();
    if (!roles.contains(role)) {
      throw new NotFoundException();
    }
    roles.remove(role);
    return new UserMessages.RevokeRoleResponse(Collections.emptyList());
  }

  public UserMessages.UpdateResponse apply(UserMessages.UpdateRequest request) {
    name = request.getName();
    email = request.getEmail();
    mobilePhoneNumber = request.getMobilePhoneNumber();
    position = request.getPosition();
    department = request.getDepartment();
    enabled = request.isEnabled();
    return new UserMessages.UpdateResponse(
      Arrays.asList(new UpdatedEvent(this.id))
    );
  }

  public UserMessages.BelongToGroupResponse apply(UserMessages.BelongToGroupRequest request) {
    Group group = request.getGroup();
    if (groups.contains(group)) {
      throw new GroupAlreadyExistsException();
    }
    groups.add(group);
    return new UserMessages.BelongToGroupResponse(Collections.emptyList());
  }

  public UserMessages.WithdrawFromGroupResponse apply(
    UserMessages.WithdrawFromGroupRequest request) {
    Group group = request.getGroup();
    if (!groups.contains(group)) {
      throw new GroupNotFoundException();
    }
    groups.remove(group);
    return new UserMessages.WithdrawFromGroupResponse(Collections.emptyList());

  }

  public Set<Role> getWholeRoles() {
    return Stream.concat(
      roles.stream(),
      groups.stream()
        .flatMap(group -> group.getRoles().stream())
    ).collect(Collectors.toSet());
  }

  public UserMessages.PrepareImportResponse apply(UserMessages.PrepareImportRequest request) {

    return new UserMessages.PrepareImportResponse(Collections.emptyList());

  }


}
