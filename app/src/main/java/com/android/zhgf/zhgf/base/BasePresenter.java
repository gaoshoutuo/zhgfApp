package com.android.zhgf.zhgf.base;

/**
 * Created by mrtan on 9/27/16.
 */

public abstract class BasePresenter<V extends BaseView> {
    protected V mView;

    public void onAttach(V view){
        mView = view;
    }

    public void onDetach(){
        mView = null;
    }

}
