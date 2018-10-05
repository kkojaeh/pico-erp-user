package pico.erp.user.password;

import java.util.Collection;

public interface PasswordStrengthValidator {

  Collection<String> validate(String password);

}
