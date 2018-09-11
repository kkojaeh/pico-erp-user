package pico.erp.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import pico.erp.shared.IntegrationConfiguration
import pico.erp.user.data.GroupId
import pico.erp.user.data.GroupView
import pico.erp.user.group.GroupQuery
import pico.erp.user.group.GroupRequests
import pico.erp.user.group.GroupService
import spock.lang.Specification

@SpringBootTest(classes = [IntegrationConfiguration])
@Transactional
@Rollback
@ActiveProfiles("test")
class GroupQuerySpec extends Specification {

  def setup() {
    groupService.create(new GroupRequests.CreateRequest(id: GroupId.from("sa"), name: "슈퍼어드민"))
    groupService.create(new GroupRequests.CreateRequest(id: GroupId.from("user"), name: "일반사용자"))
  }

  @Autowired
  GroupQuery groupQuery

  @Autowired
  GroupService groupService

  def "그룹 조회 - 조회 조건에 맞게 조회"() {
    expect:
    def page = groupQuery.retrieve(condition, pageable)
    page.totalElements == totalElements

    where:
    condition                           | pageable               || totalElements
    new GroupView.Filter(name: "슈퍼어드민") | new PageRequest(0, 10) || 1
    new GroupView.Filter(name: "일반사용자") | new PageRequest(0, 10) || 1
  }

}
