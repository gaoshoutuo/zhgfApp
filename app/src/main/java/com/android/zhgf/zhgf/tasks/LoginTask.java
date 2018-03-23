package com.android.zhgf.zhgf.tasks;

import android.app.ProgressDialog;
import android.content.Context;

import com.android.zhgf.zhgf.R;
import com.android.zhgf.zhgf.tasks.Response.Listener;


public class LoginTask extends BaseAsyncTask<Void, Void, Boolean> {
	private String username;
	private String password;

	private ProgressDialog dialog;
	
	public LoginTask(Listener<Boolean> listener, Context context, String username, String password) {
		super(listener, context);
		
		this.username = username;
		this.password = password;

		dialog = ProgressDialog.show(context, null, context.getResources().getString(R.string.login_login));
	}
	
	@Override
	public Response<Boolean> doInBackground(Void... params) {
		Context context = getContext();
		if (context != null) {
			try {

				//Boolean loginResult  = SmackManager.getInstance().login(username, password);
				return Response.success(true);
			} catch(Exception e) {

				return Response.error(e);
			}
		} else {
			return null;
		}
	}

	@Override
	protected void onPostExecute(Response<Boolean> response) {
		dialog.dismiss();

		super.onPostExecute(response);
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();

		dismissDialog();
	}

	public void dismissDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	public void dismissDialogAndCancel() {
		dismissDialog();
		cancel(false);
	}
}