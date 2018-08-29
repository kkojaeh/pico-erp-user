/*
패스워드는 firebase 에서 관리하기 때문에
패스워드가 중복 관리 되고 있음
그로인해 패스워드 변경에 대한 처리가 복잡해 지기 때문에
패스워드 컬럼을 삭제
 */
ALTER TABLE usr_user DROP password;
