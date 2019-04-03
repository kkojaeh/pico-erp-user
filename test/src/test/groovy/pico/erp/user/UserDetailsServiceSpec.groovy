package pico.erp.user

import kkojaeh.spring.boot.component.SpringBootTestComponent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import pico.erp.shared.ComponentDefinitionServiceLoaderTestComponentSiblingsSupplier
import pico.erp.shared.TestParentApplication
import spock.lang.Specification

@SpringBootTest(classes = [UserApplication])
@SpringBootTestComponent(parent = TestParentApplication, siblingsSupplier = ComponentDefinitionServiceLoaderTestComponentSiblingsSupplier.class)
@Transactional
@Rollback
@ActiveProfiles("test")
class UserDetailsServiceSpec extends Specification {

  @Autowired
  UserDetailsService userDetailsService

  def "사용자 인증 - 생성한 사용자의 id 는 UserDetails 의 username 으로 매핑된다"() {
    expect:
    def userDetails = userDetailsService.loadUserByUsername(id)
    userDetails.username == id

    where:
    id    || name
    'kjh' || "고재훈"
    'ysh' || "임성환"
  }

  def "사용자 인증 - 존재하지 않는 사용자를 불러오면 오류가 발생한다"() {
    when:
    userDetailsService.loadUserByUsername("stranger")

    then:
    thrown(BadCredentialsException)
  }

}
