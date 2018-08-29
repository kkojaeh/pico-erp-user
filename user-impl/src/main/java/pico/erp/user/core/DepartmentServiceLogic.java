package pico.erp.user.core;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pico.erp.audit.AuditService;
import pico.erp.shared.Public;
import pico.erp.shared.event.EventPublisher;
import pico.erp.user.DepartmentExceptions.AlreadyExistsException;
import pico.erp.user.DepartmentExceptions.NotFoundException;
import pico.erp.user.DepartmentRequests.CreateRequest;
import pico.erp.user.DepartmentRequests.DeleteRequest;
import pico.erp.user.DepartmentRequests.UpdateRequest;
import pico.erp.user.DepartmentService;
import pico.erp.user.data.DepartmentData;
import pico.erp.user.data.DepartmentId;
import pico.erp.user.domain.Department;

@SuppressWarnings("Duplicates")
@Service
@Public
@Transactional
@Validated
public class DepartmentServiceLogic implements DepartmentService {

  @Autowired
  private DepartmentRepository departmentRepository;

  @Autowired
  private EventPublisher eventPublisher;

  @Lazy
  @Autowired
  private AuditService auditService;

  @Autowired
  private UserMapper mapper;

  @Override
  public DepartmentData create(CreateRequest request) {
    if (departmentRepository.exists(request.getId())) {
      throw new AlreadyExistsException();
    }
    val department = new Department();
    val response = department.apply(mapper.map(request));
    val created = departmentRepository.create(department);
    auditService.commit(created);
    eventPublisher.publishEvents(response.getEvents());
    return mapper.map(created);
  }

  @Override
  public void delete(DeleteRequest request) {
    val department = departmentRepository.findBy(request.getId())
      .orElseThrow(NotFoundException::new);

    val response = department.apply(mapper.map(request));
    departmentRepository.deleteBy(department.getId());
    auditService.delete(department);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public boolean exists(DepartmentId id) {
    return departmentRepository.exists(id);
  }

  @Override
  public DepartmentData get(DepartmentId id) {
    return departmentRepository.findBy(id)
      .map(mapper::map)
      .orElseThrow(NotFoundException::new);
  }

  @Override
  public void update(UpdateRequest request) {
    val department = departmentRepository.findBy(request.getId())
      .orElseThrow(NotFoundException::new);
    val response = department.apply(mapper.map(request));
    departmentRepository.update(department);
    auditService.commit(department);
    eventPublisher.publishEvents(response.getEvents());
  }
}
