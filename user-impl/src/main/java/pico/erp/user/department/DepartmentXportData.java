package pico.erp.user.department;

import com.ebay.xcelite.annotations.Column;
import com.ebay.xcelite.annotations.Row;
import com.ebay.xcelite.converters.ColumnValueConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pico.erp.user.UserXportData.UserIdColumnValueConverter;
import pico.erp.user.data.DepartmentId;
import pico.erp.user.data.UserId;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Row(colsOrder = {"id", "name", "managerId"})
public class DepartmentXportData {

  @Column(converter = DepartmentIdColumnValueConverter.class)
  DepartmentId id;

  @Column
  String name;

  @Column(converter = UserIdColumnValueConverter.class)
  UserId managerId;

  public static class DepartmentIdColumnValueConverter implements
    ColumnValueConverter<String, DepartmentId> {

    @Override
    public DepartmentId deserialize(String value) {
      return DepartmentId.from(value);
    }

    @Override
    public String serialize(DepartmentId value) {
      return value.getValue();
    }

  }
}
