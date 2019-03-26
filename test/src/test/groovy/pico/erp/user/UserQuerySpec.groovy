package pico.erp.user

import kkojaeh.spring.boot.component.SpringBootTestComponent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.domain.PageRequest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import pico.erp.shared.TestParentApplication
import spock.lang.Specification

@SpringBootTest(classes = [UserApplication])
@SpringBootTestComponent(parent = TestParentApplication, siblings = [])
@ComponentScan(useDefaultFilters = false)
@Transactional
@Rollback
@ActiveProfiles("test")
class UserQuerySpec extends Specification {

  @Autowired
  UserQuery userQuery

  def "사용자 조회 - 조회 조건에 맞게 조회"() {
    expect:
    def page = userQuery.retrieve(condition, pageable)
    page.totalElements == totalElements


    where:
    condition                        | pageable               || totalElements
    new UserView.Filter(name: "고재훈") | new PageRequest(0, 10) || 1
    new UserView.Filter(name: "임성환") | new PageRequest(0, 10) || 1
  }

}
