package pico.erp.user.impl;

import java.util.ArrayList;
import java.util.List;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pico.erp.user.PasswordRandomGenerator;

@Component
public class PasswordRandomGeneratorPassay implements PasswordRandomGenerator {

  @Value("${password.generation.length}")
  private int length;

  public String generate() {

    PasswordGenerator generator = new PasswordGenerator();
    List<CharacterRule> rules = new ArrayList<>();

    rules.add(new CharacterRule(EnglishCharacterData.Digit, 1));
    rules.add(new CharacterRule(EnglishCharacterData.LowerCase, 1));
    rules.add(new CharacterRule(EnglishCharacterData.UpperCase, 1));
    rules.add(new CharacterRule(EnglishCharacterData.Special, 1));

    return generator.generatePassword(length, rules);
  }

}
