/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.cks.hiroyuki2.worksupport3.Fragments.AddFriendFragment;
import com.cks.hiroyuki2.worksupport3.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.cks.hiroyuki2.worksupprotlib.Util.QR_FILE_NAME;
import static com.cks.hiroyuki2.worksupprotlib.Util.toastNullable;

/**
 * PagerAdapterおじさん！
 * {@link AddFriendFragment}付属
 */
public class AddFriendVPAdapter extends PagerAdapter implements Callback{

    private static final String TAG = "MANUAL_TAG: " + AddFriendVPAdapter.class.getSimpleName();
    private static final int WIDTH = 500;//QRコードの解像度ってどうやって決めよう？？
    private static final String ENCODING_CHAR = "UTF-8";
    private Context context;
    private AddFriendFragment fragment;
    private LayoutInflater inflater;
    @BindView(R.id.camera_btn) Button btn;
    @BindView(R.id.my_qr_img) ImageView iv;
    private String pageName0;
    private String pageName1;

    public AddFriendVPAdapter(Context context, AddFriendFragment fragment){
        this.context = context;
        this.fragment = fragment;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        pageName0 = context.getString(R.string.vp_page_name0);
        pageName1 = context.getString(R.string.vp_page_name1);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return pageName0;
            case 1:
                return pageName1;
        }
        return null;
    }

    @Override @NonNull
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = null;
        switch (position){
            case 0:
                view = inflater.inflate(R.layout.addfriend_vp_item_qr, null);
                ButterKnife.bind(this, view);
                File file = new File(context.getFilesDir(), QR_FILE_NAME);
                Picasso.with(context).load(file).into(iv, this);

//                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                if (user == null){
//                    //todo エラー画像を表示し、エラー処理をしてください
//                    Toast.makeText(context, "エラー画像を表示し、エラー処理をしてください", Toast.LENGTH_LONG).show();
//                } else {
//                    String string = user.getUid() + Util.delimiter
//                            + user.getDisplayName() + Util.delimiter
//                            + user.getPhotoUrl();
//                    new AwesomeQRCode.Renderer()
//                            .contents(string)
//                            .dotScale(1f)
//                            .size(400).margin(20)
//                            .renderAsync(this);
//                    Bitmap bitmap = encodeAsBitmap(user.getUid() + Util.delimiter
//                            + user.getDisplayName() + Util.delimiter
//                            + user.getPhotoUrl() + Util.delimiter);
//                    if (bitmap != null){
//                        iv.setImageBitmap(bitmap);
//                    } else {
//                        //エラー画像を表示し、エラー処理をしてください
//                    }
//                }
                break;
            case 1:
                view = inflater.inflate(R.layout.social_vp_item_shake, null);
                //todo 整備してください
                break;
        }
        container.addView(view);
        return view;
    }

    @OnClick(R.id.camera_btn)
    void onClickCameraBtn(){
//        BarcodeDetector detector = new BarcodeDetector.Builder(getApplicationContext())
//                        .setBarcodeFormats(Barcode.QR_CODE)
//                        .build();
//        if(!detector.isOperational()){
//            Toast.makeText(getApplicationContext(), "Could not set up the detector!", Toast.LENGTH_LONG).show();
//            return;
//        }

//        Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
//        SparseArray<Barcode> barcodes = detector.detect(frame);

        IntentIntegrator integrator = new IntentIntegrator((Activity) context);
        integrator.setBeepEnabled(false);
        integrator.initiateScan();
    }

//    @Nullable
//    private Bitmap encodeAsBitmap(String str) {
//
//        BitMatrix result;
//        ConcurrentHashMap<EncodeHintType, Object> hints = new ConcurrentHashMap<>();
//        hints.put(EncodeHintType.CHARACTER_SET, ENCODING_CHAR);
//        //エラー訂正レベル指定
//        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
////        //マージン指定
////        hints.put(EncodeHintType.MARGIN, 0);
//        try {
//            result = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, WIDTH, WIDTH, hints);
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//            return null;
//        } catch (WriterException e){
//            e.printStackTrace();
//            return null;
//        }
//
//        int w = result.getWidth();
//        int h = result.getHeight();
//        int[] pixels = new int[w * h];
//        for (int y = 0; y < h; y++) {
//            int offset = y * w;
//            for (int x = 0; x < w; x++) {
//                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
//            }
//        }
//        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
//        bitmap.setPixels(pixels, 0, WIDTH, 0, 0, w, h);
//        return bitmap;
//    }

//    @Override
//    public void onRendered(final AwesomeQRCode.Renderer renderer, final Bitmap bitmap) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                // Tip: here we use runOnUiThread(...) to avoid the problems caused by operating UI elements from a non-UI thread.
//                iv.setImageBitmap(bitmap);
//            }
//        });
//    }
//
//    @Override
//    public void onError(AwesomeQRCode.Renderer renderer, Exception e) {
//        logStackTrace(e);
//        Toast.makeText(context, "QRコードを取得できませんでした", Toast.LENGTH_LONG).show();
//        //todo エラー画像を表示
//    }

    @Override
    public void onSuccess() {
        //do nothing
    }

    @Override
    public void onError() {
        toastNullable(context, R.string.qr_err_msg);
    }
}
