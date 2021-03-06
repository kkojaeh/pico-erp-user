package pico.erp.user.security;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import kkojaeh.spring.boot.component.ComponentBean;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pico.erp.shared.data.AuthorizedUser;
import pico.erp.user.User;
import pico.erp.user.UserId;
import pico.erp.user.UserRepository;

@ComponentBean
@Service
@Transactional(readOnly = true)
public class UserDetailsServiceImpl implements UserDetailsService {

  private final MessageSource messageSource;

  private final UserRepository userRepository;

  public UserDetailsServiceImpl(@Autowired MessageSource messageSource,
    @Autowired UserRepository userRepository) {
    this.messageSource = messageSource;
    this.userRepository = userRepository;
  }

  @Override
  @Cacheable("load-user-by-username")
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findBy(UserId.from(username))
      .orElseGet(() -> userRepository.findByEmail(username)
        .orElseThrow(() -> new BadCredentialsException(messageSource.getMessage(
          "id.or.password.not.matched.exception", null, LocaleContextHolder.getLocale())))
      );

    return new AuthorizedUserImpl(user);
  }

  @NoArgsConstructor
  @Getter
  @ToString
  @EqualsAndHashCode(of = "username")
  private class AuthorizedUserImpl implements AuthorizedUser {

    private static final long serialVersionUID = 1L;

    private static final String ROLE_PREFIX = "ROLE_";

    /**
     * 시스템에서 생성한 유일 키값 사용자가 아이디로 입력한 값
     */
    String username;

    /**
     * 사용자가 표시(인지)되는 이름
     */
    String name;

    String email;

    String mobilePhoneNumber;

    String position;

    boolean accountNonExpired = true;

    boolean accountNonLocked = true;

    boolean credentialsNonExpired = true;

    boolean enabled;

    Collection<? extends GrantedAuthority> authorities;

    String departmentName;

    public AuthorizedUserImpl(User user) {
      username = user.getId().getValue();
      name = user.getName();
      email = user.getEmail();
      mobilePhoneNumber = user.getMobilePhoneNumber();
      position = user.getPosition();
      accountNonExpired = user.isAccountNonExpired();
      accountNonLocked = user.isAccountNonLocked();
      credentialsNonExpired = user.isCredentialsNonExpired();
      enabled = user.isEnabled();
      departmentName = Optional.ofNullable(user.getDepartment())
        .map(department -> department.getName())
        .orElse(null);
      this.authorities = user.getWholeRoles().stream()
        .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role.getId()))
        .collect(Collectors.toSet());
    }

    public String getPassword() {
      return "N/A";
    }

    @Override
    public boolean hasAnyRole(String... roles) {
      val roleAuthorities = Stream.of(roles)
        .map(role -> ROLE_PREFIX + role)
        .collect(Collectors.toSet());
      return getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .anyMatch(authority -> roleAuthorities.contains(authority));
    }

    @Override
    public boolean hasRole(String role) {
      val roleAuthority = ROLE_PREFIX + role;
      return getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .anyMatch(authority -> roleAuthority.equals(authority));
    }
  }

}
