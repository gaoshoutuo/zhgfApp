package com.android.zhgf.zhgf.bean;

import java.io.Serializable;

public class OptionItem implements Serializable {
	private String answerOption;//答案选项
	private String answer;//答案
	public String getAnswerOption() {
		return answerOption;
	}
	public void setAnswerOption(String answerOption) {
		this.answerOption = answerOption;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String anwer) {
		this.answer = anwer;
	}
}
