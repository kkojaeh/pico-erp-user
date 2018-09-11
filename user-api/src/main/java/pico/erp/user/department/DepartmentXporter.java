package pico.erp.user.department;

import java.io.InputStream;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pico.erp.shared.data.ContentInputStream;

public interface DepartmentXporter {

  ContentInputStream exportExcel(@NotNull @Valid ExportRequest request);

  void importExcel(@NotNull @Valid ImportRequest request);

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  class ImportRequest {

    @NotNull
    InputStream inputStream;

    boolean overwrite;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  class ExportRequest {

    boolean empty;

  }

}
