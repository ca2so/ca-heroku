package de.logicline.herokutemplate.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.logicline.herokutemplate.dao.ContactDao;
import de.logicline.herokutemplate.dao.UserDao;
import de.logicline.herokutemplate.dto.ContactDto;
import de.logicline.herokutemplate.model.ContactEntity;
import de.logicline.herokutemplate.model.UserEntity;
import de.logicline.herokutemplate.utils.Enums;
import de.logicline.herokutemplate.utils.PasswordGenerator;

@Service
public class UserServiceImpl implements UserService {

	private Logger log = Logger.getLogger(this.getClass().getName());

	@PersistenceContext
	EntityManager em;

	@Autowired
	UserDao userDao;

	@Autowired
	ContactDao contactDao;

	@Transactional
	public Integer createUser(ContactDto contactDto) {

		UserEntity ue = new UserEntity();
		ue.setUsername(contactDto.getCustomerId());
		String password = new PasswordGenerator().generatePswd(10, 10, 2, 2, 2);
		ue.setPassword(password);
		String token = DigestUtils
				.md5Hex(password + contactDto.getCustomerId());
		ue.setToken(token);
		ue.setRole(Enums.UserRole.ROLE_CUSTOMER.name());
		userDao.create(ue);
		contactDto.setUserIdFk(ue.getUserId());
		ContactEntity ce = contactDto.toEntity(new ContactEntity());
		ce.setUserIdFk(ue.getUserId());

		return ue.getUserId();
	};

	public UserEntity getUserByNameAndPassword(String username, String password) {
		return userDao.getUserByNameAndPassword(username, password);

	}

	public List<ContactEntity> getContactList() {
		List<ContactEntity> resultList = contactDao.findAll();
		return resultList;
	}

	public Map<Integer, String> getCustomerIdMap() {

		List<ContactEntity> resultList = contactDao.findAll();

		Map<Integer, String> customerIdMap = new HashMap<Integer, String>();

		for (ContactEntity uie : resultList) {
			customerIdMap.put(uie.getUserIdFk(), uie.getCustomerId());
		}

		return customerIdMap;

	}

	@Deprecated
	public ContactEntity getContact(String token) {

		Integer userId = userDao.getUserId(token);
		ContactEntity result = contactDao.getContactByUserId(userId);

		return result;
	}

	@Deprecated
	@Transactional
	public void updateUserInfo(String token, ContactDto contactDto) {
		ContactEntity contactOld = getContact(token);
		if (contactOld.getUserIdFk().equals(contactDto.getUserIdFk())) {
			ContactEntity contactUpdated = contactDto.toEntity(contactOld);
			contactDao.edit(contactUpdated);
		}
	}

	public ContactEntity getContactByUserId(Integer userId) {
		ContactEntity result = contactDao.getContactByUserId(userId);

		return result;
	};

	@Transactional
	public void updateUserInfoByUserId(Integer userId, ContactDto contactDto) {
		ContactEntity contactOld = contactDao.getContactByUserId(userId);
		if (contactOld.getUserIdFk().equals(contactDto.getUserIdFk())) {
			ContactEntity contactUpdated = contactDto.toEntity(contactOld);
			contactDao.edit(contactUpdated);
		}
	}

	public Map<Integer, String> searchUserByCustomerId(String customerId) {

		List<ContactEntity> resultList = contactDao
				.findByCustomerId(customerId);

		Map<Integer, String> customerIdMap = new HashMap<Integer, String>();

		for (ContactEntity contact : resultList) {
			customerIdMap.put(contact.getUserIdFk(), contact.getCustomerId());
		}

		return customerIdMap;

	}

	@Transactional
	public String updatePassword(Integer userId) {
		String password = new PasswordGenerator().generatePswd(10, 10, 2, 2, 2);
		UserEntity userForUpdate = userDao.find(userId);
		userForUpdate.setPassword(password);
		userDao.edit(userForUpdate);
		return password;

	}
}
