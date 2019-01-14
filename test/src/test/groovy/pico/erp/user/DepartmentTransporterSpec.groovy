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
import pico.erp.user.department.DepartmentId
import pico.erp.user.department.DepartmentService
import pico.erp.user.department.DepartmentTransporter
import spock.lang.Specification

@SpringBootTest(classes = [IntegrationConfiguration])
@Transactional
@Rollback
@ActiveProfiles("test")
@Configuration
@ComponentScan("pico.erp.config")
class DepartmentTransporterSpec extends Specification {

  @Autowired
  DepartmentTransporter departmentTransporter

  @Autowired
  DepartmentService departmentService

  @Value("classpath:department-import-data.xlsx")
  Resource importData

  def "export"() {
    when:
    def inputStream = departmentTransporter.exportExcel(
      new DepartmentTransporter.ExportRequest(
        empty: false
      )
    )

    then:
    //FileCopyUtils.copy(inputStream, new FileOutputStream("/Users/kojaehun/department.xlsx"))
    inputStream.contentLength > 0
  }

  def "import - 덮어쓴다"() {
    when:
    departmentTransporter.importExcel(
      new DepartmentTransporter.ImportRequest(
        inputStream: importData.getInputStream(),
        overwrite: true
      )
    )
    def previous = departmentService.get(DepartmentId.from("design"))
    def created = departmentService.get(DepartmentId.from("test"))
    then:
    previous.name == "기획2"
    created.id == DepartmentId.from("test")
    created.name == "테스트 부서"
  }

  def "import - 덮어쓰지 않는다"() {
    when:
    departmentTransporter.importExcel(
      new DepartmentTransporter.ImportRequest(
        inputStream: importData.getInputStream(),
        overwrite: false
      )
    )
    def previous = departmentService.get(DepartmentId.from("design"))
    then:
    previous.name != "기획2"
  }
}