package pico.erp.user.core;

import java.util.Collection;

public interface PasswordStrengthValidator {

  Collection<String> validate(String password);

}
