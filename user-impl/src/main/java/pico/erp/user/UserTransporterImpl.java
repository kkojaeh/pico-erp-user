package pico.erp.user;

import com.coreoz.windmill.Windmill;
import com.coreoz.windmill.exports.config.ExportHeaderMapping;
import com.coreoz.windmill.exports.exporters.excel.ExportExcelConfig;
import com.coreoz.windmill.files.FileSource;
import com.coreoz.windmill.imports.Parsers;
import java.io.ByteArrayInputStream;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import pico.erp.shared.Public;
import pico.erp.shared.data.ContentInputStream;
import pico.erp.shared.data.Role;
import pico.erp.shared.event.EventPublisher;
import pico.erp.user.department.DepartmentId;
import pico.erp.user.department.DepartmentMapper;
import pico.erp.user.group.Group;
import pico.erp.user.group.GroupId;
import pico.erp.user.group.GroupMapper;
import pico.erp.user.role.RoleId;
import pico.erp.user.role.RoleMapper;

@Component
@Public
@Validated
@Transactional
public class UserTransporterImpl implements UserTransporter {

  @Autowired
  private UserRepository userRepository;


  @Autowired
  private RoleMapper roleMapper;

  @Autowired
  private GroupMapper groupMapper;

  @Autowired
  private DepartmentMapper departmentMapper;

  @Autowired
  private EventPublisher eventPublisher;

  @SneakyThrows
  @Override
  public ContentInputStream exportExcel(ExportRequest request) {
    val locale = LocaleContextHolder.getLocale();
    Stream<User> users = request.isEmpty() ? Stream.empty() : userRepository.getAll();
    val workbook = new XSSFWorkbook();
    val bytes = Windmill
      .export(() -> users.iterator())
      .withHeaderMapping(
        new ExportHeaderMapping<User>()
          .add("id", e -> e.getId().getValue())
          .add("name", e -> e.getName())
          .add("email", e -> e.getEmail())
          .add("mobilePhoneNumber", e -> e.getMobilePhoneNumber())
          .add("position", e -> e.getPosition())
          .add("roles", e ->
            StringUtils.collectionToCommaDelimitedString(
              e.getRoles().stream()
                .map(Role::getId)
                .collect(Collectors.toList())
            )
          )
          .add("groups", e ->
            StringUtils.collectionToCommaDelimitedString(
              e.getGroups().stream()
                .map(Group::getId)
                .map(GroupId::getValue)
                .collect(Collectors.toList())
            )
          )
          .add("departmentId", e -> e.getDepartment() != null ? e.getDepartment().getId() : null)
          .add("accountNonExpired", e -> e.isAccountNonExpired() + "")
          .add("accountNonLocked", e -> e.isAccountNonLocked() + "")
          .add("credentialsNonExpired", e -> e.isCredentialsNonExpired() + "")
          .add("enabled", e -> e.isEnabled() + "")
      )
      .asExcel(
        ExportExcelConfig.fromWorkbook(workbook).build("users")
      )
      .toByteArray();
    return ContentInputStream.builder()
      .name(
        String.format("users-%s.%s",
          DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(OffsetDateTime.now()),
          ContentInputStream.XLSX_CONTENT_EXTENSION
        )
      )
      .contentType(ContentInputStream.XLSX_CONTENT_TYPE)
      .contentLength(bytes.length)
      .inputStream(new ByteArrayInputStream(bytes))
      .build();
  }

  @SneakyThrows
  @Override
  public void importExcel(ImportRequest request) {
    val users = Parsers.xlsx("users")
      .trimValues()
      .parse(FileSource.of(request.getInputStream()))
      .skip(1)
      .map(row -> User.builder()
        .id(UserId.from(row.cell("id").asString()))
        .name(row.cell("name").asString())
        .email(row.cell("email").asString())
        .mobilePhoneNumber(row.cell("mobilePhoneNumber").asString())
        .position(row.cell("position").asString())
        .roles(
          StringUtils.commaDelimitedListToSet(row.cell("roles").asString())
            .stream()
            .map(RoleId::from)
            .map(roleMapper::map)
            .collect(Collectors.toSet())
        )
        .groups(
          StringUtils.commaDelimitedListToSet(row.cell("groups").asString())
            .stream()
            .map(GroupId::from)
            .map(groupMapper::map)
            .collect(Collectors.toSet())
        )
        .department(
          Optional.ofNullable(row.cell("departmentId").asString())
            .map(id -> DepartmentId.from(id))
            .map(id -> departmentMapper.map(id))
            .orElse(null)
        )
        .accountNonExpired(
          Boolean.valueOf(row.cell("accountNonExpired").asString())
        )
        .accountNonLocked(
          Boolean.valueOf(row.cell("accountNonLocked").asString())
        )
        .credentialsNonExpired(
          Boolean.valueOf(row.cell("credentialsNonExpired").asString())
        )
        .enabled(
          Boolean.valueOf(row.cell("enabled").asString())
        )
        .build()
      );

    users.forEach(user -> {
      val previous = userRepository.findBy(user.getId()).orElse(null);
      val response = user.apply(new UserMessages.PrepareImportRequest(previous));
      if (previous == null) {
        userRepository.create(user);
      } else if (request.isOverwrite()) {
        userRepository.update(user);
      }
      eventPublisher.publishEvents(response.getEvents());
    });
  }

}
