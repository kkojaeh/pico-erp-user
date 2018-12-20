package pico.erp.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import pico.erp.shared.IntegrationConfiguration
import pico.erp.user.group.GroupId
import pico.erp.user.group.GroupService
import pico.erp.user.group.GroupTransporter
import spock.lang.Specification

@SpringBootTest(classes = [IntegrationConfiguration])
@Transactional
@Rollback
@ActiveProfiles("test")
@Configuration
@ComponentScan("pico.erp.config")
class GroupTransporterSpec extends Specification {

  @Autowired
  GroupTransporter groupTransporter

  @Autowired
  GroupService groupService

  @Value("classpath:group-import-data.xlsx")
  Resource importData

  def "export"() {
    when:
    def inputStream = groupTransporter.exportExcel(
      new GroupTransporter.ExportRequest(
        empty: false
      )
    )

    then:
    //FileCopyUtils.copy(inputStream, new FileOutputStream("/Users/kojaehun/group.xlsx"))
    inputStream.contentLength > 0
  }

  def "import - 덮어쓴다"() {
    when:
    groupTransporter.importExcel(
      new GroupTransporter.ImportRequest(
        inputStream: importData.getInputStream(),
        overwrite: true
      )
    )
    def previous = groupService.get(GroupId.from("finance"))
    def created = groupService.get(GroupId.from("test"))
    then:
    previous.name == "재경2"
    created.id == GroupId.from("test")
    created.name == "테스트 그룹"
  }

  def "import - 덮어쓰지 않는다"() {
    when:
    groupTransporter.importExcel(
      new GroupTransporter.ImportRequest(
        inputStream: importData.getInputStream(),
        overwrite: false
      )
    )
    def previous = groupService.get(GroupId.from("finance"))
    then:
    previous.name != "재경2"
  }
}
