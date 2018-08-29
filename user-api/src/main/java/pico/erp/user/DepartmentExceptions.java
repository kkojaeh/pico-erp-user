package pico.erp.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public interface DepartmentExceptions {

  @ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE, reason = "department.already.exists.exception")
  class AlreadyExistsException extends RuntimeException {

    private static final long serialVersionUID = 1L;
  }

  @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "department.not.found.exception")
  class NotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

  }
}
