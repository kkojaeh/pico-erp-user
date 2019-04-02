package pico.erp.user.department;

import kkojaeh.spring.boot.component.Give;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pico.erp.shared.event.EventPublisher;
import pico.erp.user.department.DepartmentRequests.CreateRequest;
import pico.erp.user.department.DepartmentRequests.DeleteRequest;
import pico.erp.user.department.DepartmentRequests.UpdateRequest;

@SuppressWarnings("Duplicates")
@Service
@Give
@Transactional
@Validated
public class DepartmentServiceLogic implements DepartmentService {

  @Autowired
  private DepartmentRepository departmentRepository;

  @Autowired
  private EventPublisher eventPublisher;

  @Autowired
  private DepartmentMapper mapper;

  @Override
  public DepartmentData create(CreateRequest request) {
    if (departmentRepository.exists(request.getId())) {
      throw new DepartmentExceptions.AlreadyExistsException();
    }
    val department = new Department();
    val response = department.apply(mapper.map(request));
    val created = departmentRepository.create(department);
    eventPublisher.publishEvents(response.getEvents());
    return mapper.map(created);
  }

  @Override
  public void delete(DeleteRequest request) {
    val department = departmentRepository.findBy(request.getId())
      .orElseThrow(DepartmentExceptions.NotFoundException::new);

    val response = department.apply(mapper.map(request));
    departmentRepository.deleteBy(department.getId());
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
      .orElseThrow(DepartmentExceptions.NotFoundException::new);
  }

  @Override
  public void update(UpdateRequest request) {
    val department = departmentRepository.findBy(request.getId())
      .orElseThrow(DepartmentExceptions.NotFoundException::new);
    val response = department.apply(mapper.map(request));
    departmentRepository.update(department);
    eventPublisher.publishEvents(response.getEvents());
  }
}
