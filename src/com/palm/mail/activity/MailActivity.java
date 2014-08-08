package com.palm.mail.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.palm.mail.R;
import com.palm.mail.mail.send.MailSenderInfo;
import com.palm.mail.mail.send.SimpleMailSender;

/**
 * 发送邮件
 * 
 * @author weixiang.qin
 * 
 */
public class MailActivity extends Activity implements OnClickListener {
	private static final int MAIL_SENDING = 1;
	private static final int MAIL_FINISH = 2;
	private Activity mActivity;
	private EditText mailAddrEt;
	private EditText mailTitleEt;
	private EditText mailContentEt;
	private Button mailSendBtn;
	private Button mailAppBtn;
	private EmailHandler emailHandler;
	private boolean isSending = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mail);
		initView(savedInstanceState);
	}

	/**
	 * 
	 * @param savedInstanceState
	 */
	private void initView(Bundle savedInstanceState) {
		mActivity = this;
		emailHandler = new EmailHandler();
		mailAddrEt = (EditText) findViewById(R.id.mail_addr_et);
		mailTitleEt = (EditText) findViewById(R.id.mail_title_et);
		mailContentEt = (EditText) findViewById(R.id.mail_content_et);
		mailSendBtn = (Button) findViewById(R.id.mail_send_btn);
		mailAppBtn = (Button) findViewById(R.id.mail_app_btn);
		mailSendBtn.setOnClickListener(this);
		mailAppBtn.setOnClickListener(this);
	}

	@SuppressLint("HandlerLeak")
	class EmailHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MAIL_SENDING:
				Toast.makeText(mActivity, "邮件正在发送", Toast.LENGTH_SHORT).show();
				break;
			case MAIL_FINISH:
				boolean result = (Boolean) msg.obj;
				Toast.makeText(mActivity, result ? "邮件发送成功" : "邮件发送失败",
						Toast.LENGTH_SHORT).show();
				isSending = false;
				break;
			default:
				break;
			}
		}

	}

	class EmailThread implements Runnable {
		private MailSenderInfo mailInfo;

		public EmailThread(MailSenderInfo mailInfo) {
			this.mailInfo = mailInfo;
		}

		@Override
		public void run() {
			emailHandler.sendEmptyMessage(MAIL_SENDING);
			boolean result = SimpleMailSender.sendTextMail(mailInfo);
			Message msg = new Message();
			msg.what = MAIL_FINISH;
			msg.obj = result;
			emailHandler.sendMessage(msg);
		}

	}

	/**
	 * 使用系统邮件应用发送邮件
	 * 
	 * @param mailAddr
	 * @param mailTitle
	 * @param mailContent
	 */
	public void sendMail(String mailAddr, String mailTitle, String mailContent) {
		Intent email = new Intent(Intent.ACTION_SEND);
		email.setType("message/rfc822");
		email.putExtra(Intent.EXTRA_EMAIL, mailAddr);// 设置邮件发收人
		email.putExtra(Intent.EXTRA_SUBJECT, mailTitle);// 设置邮件标题
		email.putExtra(Intent.EXTRA_TEXT, mailContent);// 设置邮件内容
		startActivity(Intent.createChooser(email, "请选择邮件发送软件"));// 调用系统的邮件系统
	}

	@Override
	public void onClick(View v) {
		String mailAddr = mailAddrEt.getText().toString();
		String mailTitle = mailTitleEt.getText().toString();
		String mailContent = mailContentEt.getText().toString();
		if ("".equals(mailAddr) || "".equals(mailTitle)
				|| "".equals(mailContent)) {
			Toast.makeText(mActivity, R.string.mail_empty,
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (v.getId() == mailSendBtn.getId()) {
			if (isSending) {
				Toast.makeText(mActivity, R.string.mail_empty,
						Toast.LENGTH_SHORT).show();
				return;
			}
			MailSenderInfo mailInfo = new MailSenderInfo();
			mailInfo.setValidate(true);
			mailInfo.setToAddress(mailAddr);
			mailInfo.setSubject(mailTitle);
			mailInfo.setContent(mailContent);
			isSending = true;
			new Thread(new EmailThread(mailInfo)).start();
		} else if (v.getId() == mailAppBtn.getId()) {
			sendMail(mailAddr, mailTitle, mailContent);
		}
	}
}
