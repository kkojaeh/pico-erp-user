package pico.erp.user;

import java.util.Collection;

public interface PasswordStrengthValidator {

  Collection<String> validate(String password);

}
