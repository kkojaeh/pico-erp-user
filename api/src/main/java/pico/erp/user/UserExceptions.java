package pico.erp.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public interface UserExceptions {

  @ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE, reason = "email.already.exists.exception")
  class EmailAlreadyExistsException extends RuntimeException {

    private static final long serialVersionUID = 1L;

  }

  @ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE, reason = "password.equivalent.exception")
  class PasswordEquivalentException extends RuntimeException {

    private static final long serialVersionUID = 1L;

  }

  @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
  class PasswordInvalidException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PasswordInvalidException(String message) {
      super(message);
    }

  }

  @ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE, reason = "password.not.matched.exception")
  class PasswordNotMatchedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

  }

  @ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE, reason = "user.already.exists.exception")
  class AlreadyExistsException extends RuntimeException {

    private static final long serialVersionUID = 1L;
  }

  @ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE, reason = "user.group.already.exists.exception")
  class GroupAlreadyExistsException extends RuntimeException {

    private static final long serialVersionUID = 1L;
  }

  @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "user.group.not.found.exception")
  class GroupNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

  }

  @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "user.not.found.exception")
  class NotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

  }
}
