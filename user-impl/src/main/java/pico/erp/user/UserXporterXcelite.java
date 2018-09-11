package pico.erp.user;

import com.ebay.xcelite.reader.SheetReader;
import com.ebay.xcelite.sheet.XceliteSheetImpl;
import com.ebay.xcelite.writer.SheetWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pico.erp.shared.Public;
import pico.erp.shared.data.ContentInputStream;
import pico.erp.user.UserRequests.CreateRequest;
import pico.erp.user.UserRequests.UpdateRequest;
import pico.erp.user.data.UserId;
import pico.erp.user.department.DepartmentRepository;

@Component
@Public
@Validated
@Transactional
public class UserXporterXcelite implements UserXporter {

  @Autowired
  private UserService userService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private DepartmentRepository departmentRepository;

  @SneakyThrows
  @Override
  public ContentInputStream exportExcel(ExportRequest request) {
    XSSFWorkbook workbook = new XSSFWorkbook();
    SheetWriter<UserXportData> userWriter = new XceliteSheetImpl(workbook.createSheet("users"))
      .getBeanWriter(UserXportData.class);
    userWriter.generateHeaderRow(true);
    if (request.isEmpty()) {
      userWriter.write(Collections.emptyList());
    } else {
      userWriter.write(
        userRepository.getAll()
          .map(this::translate)
          .collect(Collectors.toList())
      );
    }
    @Cleanup
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    workbook.write(baos);

    return ContentInputStream.builder()
      .name(
        String.format("user-%s.%s",
          DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(OffsetDateTime.now()),
          "xlsx"
        )
      )
      .contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
      .contentLength(baos.size())
      .inputStream(new ByteArrayInputStream(baos.toByteArray()))
      .build();
  }

  @SneakyThrows
  @Override
  public void importExcel(ImportRequest request) {
    @Cleanup
    InputStream xlsxInputStream = request.getInputStream();
    XSSFWorkbook workbook = new XSSFWorkbook(xlsxInputStream);
    SheetReader<UserXportData> userReader = new XceliteSheetImpl(workbook.getSheet("users"))
      .getBeanReader(UserXportData.class);
    userReader.skipHeaderRow(true);
    Collection<UserXportData> users = userReader.read();

    Map<UserId, Boolean> exists = new HashMap<>();

    users.stream()
      .forEach(data -> {
        exists.put(data.getId(), userService.exists(data.getId()));
      });

    // 존재하지 않는 것만 생성
    users.stream()
      .filter(data -> !exists.get(data.getId()))
      .map(this::toCreate)
      .forEach(userService::create);

    if (request.isOverwrite()) {
      users.stream()
        .filter(data -> exists.get(data.getId()))
        .map(this::toUpdate)
        .forEach(userService::update);
    }
  }

  CreateRequest toCreate(UserXportData data) {
    return CreateRequest.builder()
      .id(data.getId())
      .name(data.getName())
      .email(data.getEmail())
      .password(data.getPassword())
      .mobilePhoneNumber(data.getMobilePhoneNumber())
      .position(data.getPosition())
      .departmentId(
        Optional.ofNullable(data.getDepartmentId())
          .map(id -> departmentRepository.exists(id) ? id : null)
          .orElse(null)
      )
      .build();
  }

  UpdateRequest toUpdate(UserXportData data) {
    return UpdateRequest.builder()
      .id(data.getId())
      .name(data.getName())
      .email(data.getEmail())
      .mobilePhoneNumber(data.getMobilePhoneNumber())
      .position(data.getPosition())
      .departmentId(
        Optional.ofNullable(data.getDepartmentId())
          .map(id -> departmentRepository.exists(id) ? id : null)
          .orElse(null)
      )
      .build();
  }

  UserXportData translate(User user) {
    return UserXportData.builder()
      .id(user.getId())
      .name(user.getName())
      .email(user.getEmail())
      .mobilePhoneNumber(user.getMobilePhoneNumber())
      .position(user.getPosition())
      .departmentId(
        Optional.ofNullable(user.getDepartment())
          .map(department -> department.getId())
          .orElse(null)
      )
      .build();
  }

}
