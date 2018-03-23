package com.android.zhgf.zhgf.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.android.zhgf.zhgf.R;

import com.android.zhgf.zhgf.bean.TopicItem;

public class TopicAdapter extends BaseAdapter implements View.OnClickListener {
	private static final String TAG = "TopicAdapter" ;
	String KEY = "list_topic_item";

	private LayoutInflater mInflater;
	private Context context;
	private InnerItemOnclickListener mListener;
	private List<Map<String, TopicItem>> mData;// �洢��EditTextֵ
   
    
	
	public TopicAdapter(Context context, List<Map<String, TopicItem>> data) {
		mData = data;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
		init();
	}
	public void setOnInnerItemOnclickListener(InnerItemOnclickListener listener){
        this.mListener = listener;
    }

    private void init() {
    	mData.clear();
    }
	
	@Override
	public int getCount() {
		int count = mData == null ? 0 : mData.size();
		return count ;
	}

	@Override
	public Object getItem(int position) {
		/*Object obj =  records != null && records.size() > position ? records.get(position) : null;
		return  obj;*/
		return null;
	}

	@Override
	public long getItemId(int position) {
		//return position;
		return 0;
	}

	private Integer index = -1;
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if( convertView == null ){
			convertView = mInflater.inflate(R.layout.topic_item, null);
			
			holder = new Holder() ;
            holder.questionNo = (TextView)convertView.findViewById(R.id.topic_item_no);
			holder.question = (TextView)convertView.findViewById(R.id.topic_item_question);
			holder.option = (RadioGroup) convertView.findViewById(R.id.topic_item_option);
			holder.option1 = (RadioButton) convertView.findViewById(R.id.topic_item_option1);
			holder.option2 = (RadioButton) convertView.findViewById(R.id.topic_item_option2);
			holder.option3 = (RadioButton) convertView.findViewById(R.id.topic_item_option3);
			holder.option4 = (RadioButton) convertView.findViewById(R.id.topic_item_option4);
			holder.option.setTag(position);
			
			holder.option.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        index = (Integer) v.getTag();
                    }
                    return false;
                }
            });
			
			class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
                public MyOnCheckedChangeListener(Holder holder) {
                    mHolder = holder;
                }

                private Holder mHolder;

				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					// TODO Auto-generated method stub
					int position = (Integer) mHolder.option.getTag();
					TopicItem item = mData.get(position).get(KEY);
					
					int RadioButtonId = mHolder.option.getCheckedRadioButtonId();
					group.check(RadioButtonId);
					
					RadioButton rb = (RadioButton)mHolder.option.findViewById(RadioButtonId);
					if(RadioButtonId==mHolder.option1.getId()){
						item.setUserAnswerId(item.getOptionList().get(0).getAnswerOption().trim());
					}else if(RadioButtonId==mHolder.option2.getId()){
						item.setUserAnswerId(item.getOptionList().get(1).getAnswerOption().trim());
					}else if(RadioButtonId==mHolder.option3.getId()){
						item.setUserAnswerId(item.getOptionList().get(2).getAnswerOption().trim());
					}else if(RadioButtonId==mHolder.option4.getId()){
						item.setUserAnswerId(item.getOptionList().get(3).getAnswerOption().trim());
					}
					mData.get(position).put(KEY, item);
				}
                
            }
			holder.option.setOnCheckedChangeListener(new MyOnCheckedChangeListener(holder));
			
			convertView.setTag(holder);
		}else{
			holder = (Holder)convertView.getTag();
			holder.option.setTag(position);
		}
		TopicItem item = (TopicItem)mData.get(position).get(KEY);
		if( item != null ){
            holder.questionNo.setText("题" + item.getQuestionId());
			holder.question.setText(item.getQuestion());
			holder.option1.setText(item.getOptionList().get(0).getAnswer().toString());
			holder.option2.setText(item.getOptionList().get(1).getAnswer().toString());
			holder.option3.setText(item.getOptionList().get(2).getAnswer().toString());
			holder.option4.setText(item.getOptionList().get(3).getAnswer().toString());
			if(item.getUserAnswerId().trim().equalsIgnoreCase(item.getOptionList().get(0).getAnswerOption().trim())){
				holder.option.check(holder.option1.getId());
			}
			else if(item.getUserAnswerId().trim().equalsIgnoreCase(item.getOptionList().get(1).getAnswerOption().trim())){
				holder.option.check(holder.option2.getId());
			}else if(item.getUserAnswerId().trim().equalsIgnoreCase(item.getOptionList().get(2).getAnswerOption().trim())){
				holder.option.check(holder.option3.getId());
			}else if(item.getUserAnswerId().trim().equalsIgnoreCase(item.getOptionList().get(3).getAnswerOption().trim())){
				holder.option.check(holder.option4.getId());
			}
			
		}
		holder.option.clearFocus();
		if (index != -1 && index == position) {
            holder.option.requestFocus();
        }
		return convertView;
	}

    @Override
    public void onClick(View view) {
        mListener.click(view);

    }
    public interface InnerItemOnclickListener {
        public void click(View v);
    }

    private class Holder{
        private TextView questionNo;
		private TextView question;
		private RadioGroup option;
		private RadioButton option1;
		private RadioButton option2;
		private RadioButton option3;
		private RadioButton option4;
	}
}
