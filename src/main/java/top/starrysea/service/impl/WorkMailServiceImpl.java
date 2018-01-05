package top.starrysea.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import top.starrysea.common.DaoResult;
import top.starrysea.dao.IOnlineDao;
import top.starrysea.kql.entity.Entity;
import top.starrysea.mail.Mail;
import top.starrysea.mail.MailCommon;
import top.starrysea.object.dto.Online;
import top.starrysea.object.dto.Work;
import top.starrysea.service.IMailService;

//这是用于作品新增时推送的邮件服务
@Service("mailService")
public class WorkMailServiceImpl implements IMailService {
	@Autowired
	private IOnlineDao onlineDao;
	@Autowired
	private MailCommon mailCommon;

	@Override
	public void sendMailService(Entity entity) {
		DaoResult daoResult = onlineDao.getAllOnlineDao();
		if (!daoResult.isSuccessed()) {
			return;
		}
		List<Online> receivers = daoResult.getResult(List.class);
		Work work = (Work) entity;
		receivers.parallelStream().forEach(
				receiver -> mailCommon.send(new Mail(receiver.getOnlineEmail(), "星之海志愿者公会", work.getWorkPdfpath())));
		// for (Online receiver : receivers) {
		// mailCommon.send(new Mail(receiver.getOnlineEmail(), "星之海志愿者公会",
		// work.getWorkPdfpath()));
		// }
	}

}
