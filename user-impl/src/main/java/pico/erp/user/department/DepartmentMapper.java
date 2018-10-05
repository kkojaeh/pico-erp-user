package pico.erp.user.department;

import java.util.Optional;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import pico.erp.user.User;
import pico.erp.user.UserId;
import pico.erp.user.UserMapper;

@Mapper
public abstract class DepartmentMapper {

  @Lazy
  @Autowired
  protected UserMapper userMapper;

  @Autowired
  private DepartmentEntityRepository departmentEntityRepository;

  @Lazy
  @Autowired
  private DepartmentRepository departmentRepository;

  @AfterMapping
  protected void afterMapping(DepartmentEntity from, @MappingTarget DepartmentEntity to) {
    to.setManager(from.getManager());
  }

  public Department domain(DepartmentEntity entity) {
    return Department.builder()
      .id(entity.getId())
      .name(entity.getName())
      .manager(
        Optional.ofNullable(entity.getManager())
          .map(userMapper::domain)
          .orElse(null)
      )
      .build();
  }

  public Department domain(DepartmentId departmentId) {
    return Optional.ofNullable(departmentId)
      .map(id -> departmentRepository.findBy(id)
        .orElseThrow(DepartmentExceptions.NotFoundException::new)
      ).orElse(null);
  }

  @Mappings({
    @Mapping(target = "manager", expression = "java(userMapper.entity(domain.getManager()))"),
    @Mapping(target = "createdBy", ignore = true),
    @Mapping(target = "createdDate", ignore = true),
    @Mapping(target = "lastModifiedBy", ignore = true),
    @Mapping(target = "lastModifiedDate", ignore = true)
  })
  public abstract DepartmentEntity entity(Department domain);

  public DepartmentEntity entity(DepartmentId departmentId) {
    return departmentEntityRepository.findOne(departmentId);
  }

  protected User map(UserId userId) {
    return userMapper.map(userId);
  }

  @Mappings({
    @Mapping(target = "manager", source = "managerId")
  })
  public abstract DepartmentMessages.CreateRequest map(DepartmentRequests.CreateRequest request);

  @Mappings({
    @Mapping(target = "manager", source = "managerId")
  })
  public abstract DepartmentMessages.UpdateRequest map(DepartmentRequests.UpdateRequest request);

  public abstract DepartmentMessages.DeleteRequest map(DepartmentRequests.DeleteRequest request);

  @Mappings({
    @Mapping(target = "managerId", source = "manager.id")
  })
  public abstract DepartmentData map(Department department);

  @Mappings({
    @Mapping(target = "manager", ignore = true)
  })
  public abstract void pass(DepartmentEntity from, @MappingTarget DepartmentEntity to);

}
