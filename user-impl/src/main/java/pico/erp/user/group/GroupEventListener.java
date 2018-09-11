package pico.erp.user.group;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pico.erp.shared.event.EventPublisher;
import pico.erp.user.UserMapper;

@SuppressWarnings("unused")
@Component
@Transactional
public class GroupEventListener {

  @Autowired
  private EventPublisher eventPublisher;

  @Autowired
  private UserMapper userMapper;

}
