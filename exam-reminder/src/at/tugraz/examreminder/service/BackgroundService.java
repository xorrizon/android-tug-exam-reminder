package at.tugraz.examreminder.service;


import android.content.Intent;
import com.commonsware.cwac.wakeful.WakefulIntentService;

public class BackgroundService extends WakefulIntentService {

	public BackgroundService() {
		super(BackgroundService.class.getName());
	}

	@Override
	protected void doWakefulWork(Intent intent) {
		//@TODO
	}
}
