package pico.erp.user.group;

import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import pico.erp.shared.data.Role;
import pico.erp.user.group.GroupMessages.GrantRoleRequest;
import pico.erp.user.group.GroupMessages.RevokeRoleRequest;
import pico.erp.user.role.RoleId;
import pico.erp.user.role.RoleMapper;

@Mapper
public abstract class GroupMapper {

  @Lazy
  @Autowired
  protected RoleMapper roleMapper;

  @Autowired
  private GroupEntityRepository groupEntityRepository;

  public Group domain(GroupEntity entity) {
    return Group.builder()
      .id(entity.getId())
      .name(entity.getName())
      .roles(
        entity.getRoles()
          .stream()
          .map(roleMapper::map)
          .filter(role -> role != null)
          .collect(Collectors.toSet())
      )
      .build();
  }

  @Mappings({
    @Mapping(target = "createdBy", ignore = true),
    @Mapping(target = "createdDate", ignore = true),
    @Mapping(target = "lastModifiedBy", ignore = true),
    @Mapping(target = "lastModifiedDate", ignore = true)
  })
  public abstract GroupEntity entity(Group group);

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

  protected GroupEntity map(GroupId groupId) {
    return groupEntityRepository.findOne(groupId);
  }

  protected RoleId map(Role role) {
    return roleMapper.map(role);
  }

  protected Role map(RoleId roleId) {
    return roleMapper.map(roleId);
  }

  public abstract void pass(GroupEntity from, @MappingTarget GroupEntity to);

}
