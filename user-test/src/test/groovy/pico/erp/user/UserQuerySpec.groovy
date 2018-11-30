package pico.erp.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import pico.erp.shared.IntegrationConfiguration
import spock.lang.Specification

@SpringBootTest(classes = [IntegrationConfiguration])
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
