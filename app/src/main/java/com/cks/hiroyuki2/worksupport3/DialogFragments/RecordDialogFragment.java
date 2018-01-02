/*
 * Copyright 2017 Hiroyuki Tamura
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cks.hiroyuki2.worksupport3.DialogFragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cks.hiroyuki2.worksupport3.Fragments.GroupSettingFragment;
import com.cks.hiroyuki2.worksupport3.Fragments.RecordFragment;
import com.cks.hiroyuki2.worksupport3.R;
import com.cks.hiroyuki2.worksupprotlib.Entity.Group;
import com.cks.hiroyuki2.worksupprotlib.Entity.GroupInUserDataNode;
import com.cks.hiroyuki2.worksupprotlib.Entity.RecordData;
import com.cks.hiroyuki2.worksupprotlib.Entity.User;
import com.cks.hiroyuki2.worksupprotlib.TemplateEditor;
import com.cks.hiroyuki2.worksupprotlib.Util;
import com.cks.hiroyuki2.worksupprotlib.UtilDialog;
import com.cks.hiroyuki2.worksupprotlib.UtilSpec;
import com.cks.hiroyuki2.worksupprotlib.RecordDataUtil;
import com.example.hiroyuki3.worksupportlibw.Adapters.GroupSettingRVAdapter;
import com.example.hiroyuki3.worksupportlibw.Adapters.RecordRVAdapter;
import com.example.hiroyuki3.worksupportlibw.Adapters.RecordVPAdapter;
import com.example.hiroyuki3.worksupportlibw.Adapters.SocialGroupListRVAdapter;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.refactor.library.SmoothCheckBox;

import static android.view.View.GONE;
import static com.cks.hiroyuki2.worksupport3.Fragments.GroupSettingFragment.GROUP;
import static com.cks.hiroyuki2.worksupprotlib.Util.LIST_MAP_VALUE;
import static com.cks.hiroyuki2.worksupprotlib.Util.delimiter;
import static com.cks.hiroyuki2.worksupprotlib.Util.setImgFromStorage;
import static com.cks.hiroyuki2.worksupprotlib.UtilDialog.editBuilder;
import static com.cks.hiroyuki2.worksupprotlib.UtilDialog.sendIntent;

/**
 * Dialog表示を一手に引き受けるおじさん！ハード！
 */

public class RecordDialogFragment extends DialogFragment implements DialogInterface.OnClickListener, View.OnClickListener {

    private static final String TAG = "MANUAL_TAG: " + RecordDialogFragment.class.getSimpleName();
    public static final float CHOOSEN_TAG_ELEVATION = 10;
    private String from;
    private TextInputEditText editText;
    private AutoCompleteTextView autv;
    private Bundle bundle;
    private List<RecordData> list;
    private AlertDialog.Builder builder;
    public static final String DIALOG_BUTTON = "DIALOG_BUTTON";
    public static final String DIALOG_TIME_VALUE = "DIALOG_TIME_VALUE";
    public static final int CALLBACK_TIME_VALUE = 101;
//    public static final int CALLBACK_TIME_VALUE_2 = 102;
    public static final String ADD_NEW_TAG = "ADD_NEW_TAG";
    private SharedPreferences pref;
    private int selectedCircleNum = 0;
    private View root;//CalenderFragment.EDIT_TAGで使用
//    private CheckBox checkBox;
    private SmoothCheckBox checkBoxS;
    private String[] strings;
    private CheckBox checkBoxDisplay;
    private com.shawnlin.numberpicker.NumberPicker picker;
    private RadioGroup radio;
    private Dialog dialog;
    private GroupMemberParams groupMemberParams;
    private Unbinder unbinder;

    public static RecordDialogFragment newInstance(Bundle bundle){
        Log.d(TAG, "newInstance: fire");
        RecordDialogFragment frag = new RecordDialogFragment();
        frag.setArguments(bundle);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (dialog != null)
            return dialog;

        builder = new AlertDialog.Builder(getContext());
        bundle = getArguments();
        if (bundle == null)
            return dialog = builder.create();

        from = bundle.getString("from", "");
        Log.d(TAG, "onCreateDialog: " + from);

        switch (from){
            case DIALOG_TIME_VALUE:{
                String string = bundle.getString(LIST_MAP_VALUE);
                if (string != null){
                    string = string.substring(0, string.indexOf(delimiter));
                }
                View view = makeDialogContent(5, true, false, string);
                UtilDialog.editBuilder(builder, "イベント名", R.string.ok, R.string.cancel, view, this, null);
                break;}

            case RecordRVAdapter.DIALOG_LONGTAP:{
                editBuilder(builder, null, R.string.ok, R.string.cancel, null, this, null).setMessage("イベントを削除しますか？");
                break;}

//            case Util.TEMPLATE_PARAMS_NAME:
            case Util.PARAMS_NAME:
//            case RecordVPAdapter.COMMENT_NAME:
//            case RecordVPAdapter.TAGVIEW_NAME:
                {
                View view1 = makeDialogContent(1, false, false, null);
                editBuilder(builder, getContext().getString(R.string.edit_name), R.string.ok, R.string.cancel, view1, this, null);
                break;}

            case RecordVPAdapter.TAG_ADD:
                setTagDialog();
                break;
//            case Util.TEMPLATE_TAG_ADD:
//                setTemplateTagDialog();
//                break;

            case RecordVPAdapter.TAG_ITEM:
            case RecordVPAdapter.HEADER_TAG_ADD:
//            case CalenderFragment.CAL_TAG_ADD:
                newTagDialog(from);
                break;

            case Util.TEMPLATE_TAG_EDIT:
//            case CalenderFragment.EDIT_TAG:
            case RecordVPAdapter.HEADER_TAG_EDIT:{
                root = newEditDialog(from);
                editBuilder(builder, "タグを編集", R.string.ok, R.string.cancel, root, this, null);
                break;}

            case Util.TEMPLATE_PARAMS_SLIDER_MAX:{
                LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.template_params_slider_dialog, null);
                picker = view.findViewById(R.id.picker);
                String[] strings = bundle.getStringArray(Util.PARAMS_VALUES);
                if (strings == null) break;
                picker.setMaxValue(Util.PARAMS_SLIDER_MAX_MAX);
                picker.setMinValue(3);
                picker.setValue(Integer.parseInt(strings[3]));
                editBuilder(builder, getResources().getString(R.string.slider_max), R.string.ok, R.string.cancel, view, this, null);
                break;}

            case Util.TEMPLATE_PARAMS_ADD:{
                LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.template_params_add_dialog, null);
                editText = view.findViewById(R.id.edit_text);
                radio = view.findViewById(R.id.radio);
                pref = getContext().getSharedPreferences(Util.PREF_NAME, Context.MODE_PRIVATE);
                @IdRes int id = pref.getInt(Util.PREF_KEY_PARAM_RADIO_ID, R.id.radio0);//デフォルトでどのラジオボタンにチェックを入れるかを表す。
                RadioButton radio = view.findViewById(id);
                radio.setChecked(true);
                AlertDialog dialog = editBuilder(builder, getString(R.string.add_item), R.string.ok, R.string.cancel, view, this, null).create();
                List<String> params = bundle.getStringArrayList(Util.PARAMS_VALUES);
                editText.addTextChangedListener(createTw(dialog, view, params));
                return this.dialog = dialog;}

            case SocialGroupListRVAdapter.TAG_GROUP_NON_ADDED:{
                GroupInUserDataNode groupNode = (GroupInUserDataNode)getArguments().getSerializable(SocialGroupListRVAdapter.GROUP);
                editBuilder(builder, null, R.string.dialog_btn_add_group, R.string.dialog_btn_reject_invitation, null, this, this)
                        .setMessage("グループ「"+groupNode.name+"」に招待されています。参加しますか？");
                break;}
            case GroupSettingFragment.TAG_EXIT_GROUP:{
                Group group = (Group) getArguments().getSerializable(GROUP);
                editBuilder(builder, null, R.string.ok, R.string.cancel, null, this, null)
                        .setMessage("本当にグループ「"+ group.groupName +"」から退会しますか？");
                break;}

            case GroupSettingRVAdapter.CLICK_GROUP_MEMBER:{
                User user = (User) getArguments().getSerializable(GroupSettingRVAdapter.USER);
                if (user == null)
                    break;
                View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_content_person, null);
                groupMemberParams = new GroupMemberParams();
                unbinder = ButterKnife.bind(groupMemberParams, view);
                groupMemberParams.registerItem.setVisibility(GONE);/*今は自動追加は許さない*/
                groupMemberParams.name.setText(user.getName());
                setImgFromStorage(user, groupMemberParams.icon, R.drawable.ic_face_origin_48dp);
                editBuilder(builder, null, 0, 0, view, null, null);
//                editBuilder(builder, null, R.string.ok, R.string.cancel, null, this, null)
//                        .setMessage("「"+ user.getName() +"」さんをグループから退会させますか？");
                break;}
        }
        return dialog = builder.create();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null)
            unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dialog = null;
    }

    private TextWatcher createTw(AlertDialog dialog, View rootView, List<String> multiList){
        UtilDialog util = new UtilDialog(dialog);
        util.initView(rootView);
        util.setRestriction(R.string.comment_max_restriction);
        util.setSecondRst(R.string.restriction_multi_plot);
        util.setHintDef(null);
        return util.createTwMaxAndMulti(R.integer.tag_max_len, null, multiList);
    }

    /**
     * 色をクリックした時点でpreferenceに書き込んじゃう！べんり
     */
    private void setColorCircle(final LinearLayout root, final int num){
        Log.d(TAG, "setColorCircle: fire");
        int id = UtilSpec.circleId.get(num);
        final FrameLayout fm = root.findViewById(id);
        ImageView iv = (ImageView) fm.getChildAt(0);
        iv.setColorFilter(ContextCompat.getColor(getContext(), UtilSpec.colorId.get(num)));
        iv.setOnClickListener(view -> {
            Log.d(TAG, "onClick: fire");

            SharedPreferences.Editor editor = pref.edit();
            editor.putInt(Util.PREF_KEY_COLOR, num);
            editor.apply();

            for (int id1 : UtilSpec.colorId) {
                FrameLayout fm1 = root.findViewById(id1);
                ImageView iv1 = (ImageView) fm1.getChildAt(1);
                if (fm1.getChildAt(1).getVisibility() == View.VISIBLE){
                    iv1.setVisibility(View.INVISIBLE);
                }
            }
            if (from.equals(Util.TEMPLATE_TIME_COLOR))
                checkBoxS.setChecked(false);
            fm.getChildAt(1).setVisibility(View.VISIBLE);
        });
    }

    private void setTagDialog(){
        if (!(getTargetFragment() instanceof RecordFragment))//これfromで分岐させた方がいいでしょ
            return;

        LayoutInflater inflater = getActivity().getLayoutInflater();
        FlowLayout tagAdd = (FlowLayout) inflater.inflate(R.layout.record_vp_item_tagadd_dialog, null);

        int date = Integer.parseInt(bundle.getString(RecordVPAdapter.PAGE_TAG));
        RecordFragment fragment = (RecordFragment) getTargetFragment();
        RecordDataUtil dataUtil = RecordDataUtil.getInstance();
        if (dataUtil.dataMap == null || dataUtil.dataMap.isEmpty() || !dataUtil.dataMap.containsKey(date)) {
            Log.w(TAG, "onCreateDialog: fragment.adapter.dataMap == null || fragment.adapter.dataMap.isEmpty() || !fragment.adapter.dataMap.containsKey(date)");
            return;
        }

        list = dataUtil.dataMap.get(date);

        if (list == null || list.isEmpty()) {
            Log.d(TAG, "onCreateDialog: fakfoau");
            list = TemplateEditor.deSerialize(getContext());
            if (list == null) return;
        }

        RecordData data = list.get(bundle.getInt(RecordVPAdapter.DATA_NUM));
        for (String key : data.data.keySet()) {
            String value2 = (String) data.data.get(key);
            Log.d(TAG, "onCreateDialog: value2:" + value2);
            String[] strings = value2.split(delimiter);

            final FrameLayout item = (FrameLayout) inflater.inflate(R.layout.record_vp_item_tagitem, null);
//                LinearLayout container = item.findViewById(R.id.container);
//                container.removeViewAt(1);

            final int color = UtilSpec.colorId.get(Integer.parseInt(strings[1]));
//                container.setBackgroundColor(ContextCompat.getColor(getContext(), color));

            TextView tv = item.findViewById(R.id.tv);
            tv.setText(strings[0]);
//                tv.setBackgroundColor(ContextCompat.getColor(getContext(), color));

            final CardView cv = item.findViewById(R.id.card_container);
            cv.setCardBackgroundColor(ContextCompat.getColor(getContext(), color));
            if (Boolean.parseBoolean(strings[2])){
                cv.setCardElevation(CHOOSEN_TAG_ELEVATION);
            } else {
                cv.setCardElevation(0);
            }

            cv.setOnClickListener(view -> {
                Log.d(TAG, "onClick: fire");
                if (cv.getCardElevation() == 0) {
                    cv.setCardElevation(CHOOSEN_TAG_ELEVATION);
                } else {
                    cv.setCardElevation(0);
                }
            });

            tagAdd.addView(item);
        }
        editBuilder(builder, getContext().getResources().getString(R.string.choose_tag), R.string.ok, R.string.cancel, tagAdd, this, null);
    }

//    private void setTemplateTagDialog(){
//        if (!from.equals(Util.TEMPLATE_TAG_ADD))
//            return;
//
//        List<RecordData> list = TemplateEditor.deSerialize(getContext());
//        if(list == null) throw new IllegalArgumentException("おーい！");
//        RecordData data = list.get(bundle.getInt(RecordVPAdapter.DATA_NUM));
//        if (data == null) throw new IllegalArgumentException("おーい！");
//
//        LayoutInflater inflater = getActivity().getLayoutInflater();
//        FlowLayout fl = (FlowLayout) inflater.inflate(R.layout.record_vp_item_tagadd_dialog, null);
//        if (data.data != null){
//            for (String key: data.data.keySet()) {
//                String value = (String) data.data.get(key);
//                String[] strings = value.split(Util.delimiter);
//                final FrameLayout item = (FrameLayout) inflater.inflate(R.layout.record_vp_item_tagitem, null);
//                ImageView iv = (ImageView) item.findViewById(R.id.remove);
//            }
//        }
//
//
//        editBuilder(builder, getContext().getResources().getString(R.string.choose_tag_display), R.string.ok, R.string.cancel, new View(getContext()), this);
//    }

    private void newTagDialog(@NonNull String command){
        LinearLayout view2 = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.record_vp_item_tagitem_dialog, null);
        autv = view2.findViewById(R.id.edit_text);
        autv.setSingleLine(true);
//        if (command.equals(RecordVPAdapter.HEADER_TAG_ADD) || command.equals(CalenderFragment.CAL_TAG_ADD)){
//            //検索候補を表示するadapterをセットする
//        }
        if (from.equals(RecordVPAdapter.TAG_ITEM)){
            View ll = view2.findViewById(R.id.display_check);
            ll.setVisibility(View.VISIBLE);
            checkBoxDisplay = view2.findViewById(R.id.checkbox);
        }
        pref = getContext().getSharedPreferences(Util.PREF_NAME, Context.MODE_PRIVATE);
        int num = pref.getInt(Util.PREF_KEY_COLOR, 0);
        int defId = UtilSpec.colorId.get(num);
        FrameLayout fm = view2.findViewById(defId);
        fm.getChildAt(1).setVisibility(View.VISIBLE);
        for (int i=0; i<UtilSpec.colorId.size(); i++) {
            setColorCircle(view2, i);
        }

        editBuilder(builder, "タグを追加", R.string.ok, R.string.cancel, view2, this, null);
    }

    private View newEditDialog(@NonNull String command){//todo newTagDialogと色々と一緒にできそう。fragmentがメンバ変数もてないのかもう一度確かめるべき
        LinearLayout view2 = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.record_vp_item_tagitem_dialog, null);

        autv = view2.findViewById(R.id.edit_text);
        String dataTxt = bundle.getString(Integer.toString(R.id.data_txt));
        if (from.equals(Util.TEMPLATE_TAG_EDIT)){
            strings = dataTxt.split(delimiter);
            autv.setText(strings[0]);
            View ll = view2.findViewById(R.id.display_check);
            ll.setVisibility(View.VISIBLE);
            checkBoxDisplay = view2.findViewById(R.id.checkbox);
        } else {
            selectedCircleNum = Integer.parseInt(dataTxt.substring(dataTxt.length()-1));
            autv.setText(dataTxt.substring(0, dataTxt.length()-1));
        }
        int defId = UtilSpec.colorId.get(selectedCircleNum);
        FrameLayout fm = view2.findViewById(defId);
        fm.getChildAt(1).setVisibility(View.VISIBLE);

        for (int i=0; i<UtilSpec.colorId.size(); i++) {
            FrameLayout ffm = view2.findViewById(UtilSpec.colorId.get(i));
            ImageView iv = (ImageView) ffm.getChildAt(0);
            iv.setTag(i);
            iv.setColorFilter(ContextCompat.getColor(getContext(), UtilSpec.colorId.get(i)));
            iv.setOnClickListener(this);
        }
        return view2;
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: fire");

        if (!(/*from.equals(CalenderFragment.EDIT_TAG) || from.equals(RecordVPAdapter.HEADER_TAG_EDIT) ||*/ from.equals(Util.TEMPLATE_TAG_EDIT)))
            return;

        selectedCircleNum = (Integer) view.getTag();
        for (int id: UtilSpec.colorId) {
            FrameLayout fm = root.findViewById(id);
            ImageView iv = (ImageView) fm.getChildAt(1);
            if (fm.getChildAt(1).getVisibility() == View.VISIBLE){
                iv.setVisibility(View.INVISIBLE);
            }
        }
        ((FrameLayout)view.getParent()).getChildAt(1).setVisibility(View.VISIBLE);
    }

    private View makeDialogContent(int maxLine, boolean hint, boolean isAutv, @Nullable String defaultText){
        TextInputLayout view1 = (TextInputLayout) getActivity().getLayoutInflater().inflate(R.layout.dialog_content, null);
        view1.setHintEnabled(hint);
        if (isAutv){
            autv = view1.findViewById(R.id.edit_text);
            autv.setMaxLines(maxLine);
            autv.setText(defaultText);
        } else {
            editText = view1.findViewById(R.id.edit_text);
            editText.setMaxLines(maxLine);
            editText.setText(defaultText);
        }
        String value1 = bundle.getString(RecordVPAdapter.NAME, null);
        if (value1 != null)
            if (isAutv){
                autv.setText(value1);
            } else {
                editText.setText(value1);
            }

        return view1;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        Log.d(TAG, "onClick: fire");

        switch (from){
            case DIALOG_TIME_VALUE:
                String oldValue = bundle.getString(LIST_MAP_VALUE, null);
                String pairNum = "0";
                if (oldValue != null){
                    pairNum = oldValue.split(delimiter)[1];
                }
                String newValue = editText.getText().toString() + delimiter + pairNum;
                bundle.putString(LIST_MAP_VALUE, newValue);//todo イベント名の重複を許さないように設定/nullCheck
                sendIntent(CALLBACK_TIME_VALUE, this);
                break;

            case RecordRVAdapter.DIALOG_LONGTAP:
                sendIntent(RecordRVAdapter.CALLBACK_LONGTAP, this);
                break;

            case Util.PARAMS_NAME:
//                String s = editText.getText().toString();
//                if (s.isEmpty()){
//                    Toast.makeText(getContext(), "項目名を入力してください", Toast.LENGTH_SHORT).show();
//                    return;//todo dialogを消さないように変更
//                }
//                int date = Integer.parseInt(bundle.getString(RecordVPAdapter.PAGE_TAG));//PAGE_TAGとかcurrentPageのtagで良くねえか問題。というかメンバ変数で持った方がよくないか問題。
//                Log.d(TAG, "onClick: " + date);
//                List<RecordData> dataList = ((RecordFragment)getTargetFragment()).adapter.dataMap.get(date);//todo dataListはnullでありうるので要修正
//                for (RecordData data: dataList) {
//                    if (data.dataName.equals(s)){
//                        Toast.makeText(getContext(), "タグ名が重複しています", Toast.LENGTH_SHORT).show();
//                        return;//todo dialogを消さないように変更;
//                    }
//                }

                //todo タグ名重複をチェック
                bundle.putString(RecordVPAdapter.NAME, editText.getText().toString());
                sendIntent(RecordVPAdapter.CALLBACK_TAGVIEW_NAME, this);
                break;

            case RecordVPAdapter.TAG_ADD:
                FlowLayout fl = ((AlertDialog) dialogInterface).findViewById(R.id.flow);
                ArrayList<Integer> list = new ArrayList<>();
                for (int n=0; n<fl.getChildCount(); n++) {
                    CardView cv = fl.getChildAt(n).findViewById(R.id.card_container);
                    list.add((int)cv.getCardElevation());
//                    if (cv.getCardElevation() == 0){
//                        list.add(n);
//                    }
                }
                bundle.putIntegerArrayList(ADD_NEW_TAG, list);
                sendIntent(RecordVPAdapter.CALLBACK_TAG_ADD, this);
                break;

            case RecordVPAdapter.TAG_ITEM://!checkInputTxt()しないとだめじゃないの？
                bundle.putString(from, autv.getText().toString() + delimiter + pref.getInt(Util.PREF_KEY_COLOR, 0) + delimiter + Boolean.toString(checkBoxDisplay.isChecked()));
                sendIntent(getTargetRequestCode(), this);
                break;

            case RecordVPAdapter.HEADER_TAG_ADD:
                if (!checkInputTxt()) return;
                bundle.putString(RecordVPAdapter.HEADER_TAG_VALUE, autv.getText().toString() + pref.getInt(Util.PREF_KEY_COLOR, 0));
                sendIntent(getTargetRequestCode(), this);
                break;

//            case CalenderFragment.CAL_TAG_ADD:
//                if (!checkInputTxt()) return;
//                bundle.putString(from, autv.getText().toString() + pref.getInt(Util.PREF_KEY_COLOR, 0));
//                sendIntent(getTargetRequestCode(), this);
//                break;
//            case CalenderFragment.EDIT_TAG:
            case RecordVPAdapter.HEADER_TAG_EDIT:{
                if (!checkInputTxt()) return;
                bundle.putString(from, autv.getText().toString() + selectedCircleNum);
                sendIntent(getTargetRequestCode(), this);
                break;}
            case Util.TEMPLATE_TAG_EDIT:{
                strings[0] = autv.getText().toString();
                strings[2] = Boolean.toString(checkBoxDisplay.isChecked());
                bundle.putString(from, Util.joinArr(strings, delimiter));
                sendIntent(getTargetRequestCode() ,this);
                break;}
            
//            case Util.TEMPLATE_TIME_PAIR_DES:
//                if (checkBox.isChecked()){
//                    SharedPreferences pref = getContext().getSharedPreferences(Util.PREF_NAME, Context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor = pref.edit();
//                    editor.putBoolean(Util.PREF_KEY_TEMPLATE_DES, true);
//                    editor.apply();
//                }
//                dialogInterface.dismiss();
//                builder = new AlertDialog.Builder(getContext());//builderを作り直す
//                from = Util.TEMPLATE_TIME_PAIR;
//                makeDialogTimeGroup();
//                builder.create().show();
//                break;
//            case Util.TEMPLATE_TIME_PAIR:
//                Log.d(TAG, "onClick: Util.TEMPLATE_TIME_PAIR");//これ未完成です
//                break;
//            case Util.TEMPLATE_TIME_COLOR:{
//                int colorNum;
//                if (checkBoxS.isChecked()){
//                    colorNum = 100;
//                } else {
//                    colorNum = pref.getInt(Util.PREF_KEY_COLOR, 0);
//                }
//                String value = bundle.getString(RecordRVAdapter.LIST_MAP_VALUE).split(Util.delimiter)[0];
//                value = value + Util.delimiter + Integer.toString(colorNum);
//                bundle.putString(RecordRVAdapter.LIST_MAP_VALUE, value);
//                sendIntent(getTargetRequestCode(), this);
//                break;}

            case Util.TEMPLATE_PARAMS_SLIDER_MAX:{
                String[] strings = bundle.getStringArray(Util.PARAMS_VALUES);
                if (strings == null) break;
                int max = picker.getValue();
                if (Integer.parseInt(strings[2]) > max)
                    strings[2] = Integer.toString(max);
                strings[3] = Integer.toString(max);
                sendIntent(getTargetRequestCode(), this);
                break;}

            case Util.TEMPLATE_PARAMS_ADD:{
                String title = editText.getText().toString();
                int id = radio.getCheckedRadioButtonId();
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt(Util.PREF_KEY_PARAM_RADIO_ID, id);
                editor.apply();

                List<String> strList = new ArrayList<>();
                switch (id){
                    case R.id.radio0:{
                        strList.add("0");
                        strList.add(title);
                        strList.add(Boolean.toString(false));
                        break;}
                    case R.id.radio1:{
                        strList.add("1");
                        strList.add(title);
                        strList.add("3");
                        strList.add("5");
                        break;}
                }

                String[] strings = new String[strList.size()];
                strList.toArray(strings);
                bundle.putStringArray(Util.PARAMS_VALUES, strings);//え？putStringArrayListにすればいいって？残念、ParamsRVAで持ってるlist<Bundle>の中身はarrayで持ってるんだ。今更直すのめんどい。
                sendIntent(getTargetRequestCode(), this);
                break;}
            case SocialGroupListRVAdapter.TAG_GROUP_NON_ADDED:{
                getArguments().putInt(DIALOG_BUTTON, i);
                sendIntent(getTargetRequestCode(), this);
                break;}
            case GroupSettingFragment.TAG_EXIT_GROUP:
                sendIntent(getTargetRequestCode(), this);
                break;
            case GroupSettingRVAdapter.CLICK_GROUP_MEMBER:
                sendIntent(getTargetRequestCode(), this);
                break;
        }
    }

    //todo これのちのち削除
    private boolean checkInputTxt(){
        String newString1 = autv.getText().toString();
        if (newString1.isEmpty()){
            Toast.makeText(getContext(), "タグ名を入力してください", Toast.LENGTH_SHORT).show();
            return false;
        }

        String string1 = bundle.getString(RecordVPAdapter.HEADER_TAG_VALUE);
        if (string1 == null)
            return true;
        List<String> tagList1 = new ArrayList<>();
        if (string1.contains(delimiter)){
            String[] strings = string1.split(delimiter);
            Collections.addAll(tagList1, strings);
        } else {
            tagList1.add(string1);
        }

        for (String string: tagList1) {
            String value = string.substring(0, string.length()-1);
            if (value.equals(newString1)){
                Toast.makeText(getContext(), "タグ名が重複しています", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    public static String WITCH_CLICKED = "WITCH_CLICKED";

    public class GroupMemberParams{
        @BindView(R.id.name) TextView name;
        @BindView(R.id.icon) ImageView icon;
        @BindView(R.id.register_user) View registerItem;
        @BindView(R.id.item_remove) View itemRmove;

        @OnClick({R.id.register_user, R.id.item_remove})
        void onClickRegisterUser(View v){
            Log.d(TAG, "onClickRegisterUser() called");
            getArguments().putInt(WITCH_CLICKED, v.getId());
            sendIntent(getTargetRequestCode(), RecordDialogFragment.this);
            dismiss();
        }
    }
}
