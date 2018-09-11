package pico.erp.user.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.EnglishSequenceData;
import org.passay.IllegalSequenceRule;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.PropertiesMessageResolver;
import org.passay.RepeatCharacterRegexRule;
import org.passay.Rule;
import org.passay.RuleResult;
import org.passay.WhitespaceRule;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import pico.erp.user.PasswordStrengthValidator;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@ConfigurationProperties("password.strength")
public class PasswordStrengthValidatorPassay implements PasswordStrengthValidator,
  InitializingBean {

  final Map<Locale, Properties> messageProperties = new HashMap<>();

  final List<Rule> rules = new ArrayList<>();

  @Setter
  int minLength = 8;

  @Setter
  int maxLength = 16;

  @Setter
  int minUpperCaseLength = 0;

  @Setter
  int minLowerCaseLnegth = 0;

  @Setter
  int minDigitLnegth = 1;

  @Setter
  int specialCharacterLength = 1;

  @Setter
  boolean whitespaceAllowed = false;

  @Setter
  int alphabeticalSequenceLength = 3;

  @Setter
  int numericalSequenceLength = 3;

  @Setter
  int qwertySequenceLength = 4;

  @Setter
  int repeatCharacterLength = 4;

  @Value("classpath:user/passay/messages_en_US.properties")
  Properties messages_en_US;

  @Value("classpath:user/passay/messages_ko_KR.properties")
  Properties messages_ko_KR;

  @Override
  public void afterPropertiesSet() throws Exception {

    messageProperties.put(Locale.KOREA, messages_ko_KR);
    messageProperties.put(Locale.KOREAN, messages_ko_KR);
    messageProperties.put(Locale.ENGLISH, messages_en_US);
    messageProperties.put(Locale.US, messages_en_US);

    rules.add(new LengthRule(minLength, maxLength));

    if (minUpperCaseLength > 0) {
      rules.add(new CharacterRule(EnglishCharacterData.UpperCase, minUpperCaseLength));
    }
    if (minLowerCaseLnegth > 0) {
      rules.add(new CharacterRule(EnglishCharacterData.LowerCase, minLowerCaseLnegth));
    }
    if (minDigitLnegth > 0) {
      rules.add(new CharacterRule(EnglishCharacterData.Digit, minDigitLnegth));
    }
    if (specialCharacterLength > 0) {
      rules.add(new CharacterRule(EnglishCharacterData.Special, specialCharacterLength));
    }
    if (!whitespaceAllowed) {
      rules.add(new WhitespaceRule());
    }
    if (alphabeticalSequenceLength > 0) {
      rules.add(
        new IllegalSequenceRule(EnglishSequenceData.Alphabetical, alphabeticalSequenceLength,
          false));
    }
    if (numericalSequenceLength > 0) {
      rules.add(
        new IllegalSequenceRule(EnglishSequenceData.Numerical, numericalSequenceLength, false));
    }
    if (qwertySequenceLength > 0) {
      rules.add(new IllegalSequenceRule(EnglishSequenceData.USQwerty, qwertySequenceLength, false));
    }
    if (repeatCharacterLength > 0) {
      rules.add(new RepeatCharacterRegexRule(repeatCharacterLength));
    }
  }

  @Override
  public Collection<String> validate(String password) {

    Locale locale = LocaleContextHolder.getLocale();

    PropertiesMessageResolver propertiesMessageResolver = null;

    if (messageProperties.containsKey(locale)) {
      propertiesMessageResolver = new PropertiesMessageResolver(messageProperties.get(locale));
    } else {
      propertiesMessageResolver = new PropertiesMessageResolver();
    }

    PasswordValidator validator = new PasswordValidator(propertiesMessageResolver, rules);

    RuleResult result = validator.validate(new PasswordData(password));

    if (result.isValid()) {
      return Collections.emptyList();
    } else {
      List<String> messages = new ArrayList<>();
      messages.addAll(validator.getMessages(result));
      return messages;
    }
  }

}
