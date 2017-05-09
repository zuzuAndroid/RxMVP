package rxfamily.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trello.rxlifecycle.components.support.RxFragment;

import rxfamily.R;


public abstract class BaseFragment extends RxFragment {

    protected BaseActivity mActivity;
    private View view;

    private Toolbar toolbar;
    private TextView title_tv;
    private Boolean hasMenu = false;
    private int menu_layout;

    public BaseFragment() {

    }

    protected abstract void initView(View view, Bundle savedInstanceState);

    //获取布局文件ID
    protected abstract int getLayoutId();

    //获取宿主Activity
    protected BaseActivity getHoldingActivity() {
        return mActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*view = inflater.inflate(getLayoutId(), container, false);
        initView(view, savedInstanceState);
        return view;*/
        if(view == null){
            view = inflater.inflate(getLayoutId(), null,false);
            initView(view, savedInstanceState);
        }
        //缓存的rootView需要判断是否已经被加过parent， 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (BaseActivity)context;
    }

    public void initToolbar(boolean has_title) {
        toolbar = (Toolbar)view.findViewById(R.id.toolbar);
        mActivity.setSupportActionBar(toolbar);

        ActionBar actionBar = mActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);//使能app bar的导航功能
            actionBar.setDisplayShowTitleEnabled(false);//必须设置
        }
        toolbar.setTitle("");

        if(has_title){
            title_tv = (TextView)view.findViewById(R.id.toolbar_title);
        }
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    protected void setToolbarTitle(String title) {
        if(title_tv != null){
            title_tv.setText(title);
        }
    }

    protected void setHasMenu(Boolean show) {
        this.hasMenu = show;
        setHasOptionsMenu(true);
    }

    protected Boolean getHasMenu() {
        return this.hasMenu;
    }

    /**
     * 设置右侧菜单layout
     */
    protected void setMenuLayout(int layout_id){
        this.menu_layout = layout_id;
    }

    protected int getMenuLayout(){
        return this.menu_layout;
    }

    /**
     *  右侧菜单(可自定义菜单layout)
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(getHasMenu()) {
            menu.clear();
            inflater.inflate(getMenuLayout(), menu);
            super.onCreateOptionsMenu(menu, inflater);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    protected void startActivity(Class<?> cls) {
        startActivity(new Intent(getHoldingActivity(), cls));
    }

    protected void startActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }
}
