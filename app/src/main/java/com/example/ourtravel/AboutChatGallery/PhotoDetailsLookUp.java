package com.example.ourtravel.AboutChatGallery;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

// ItemDetailsLookup 구현
// itemdetailslookup<T> 이때 T는 type의 t이다.
// 위 클래스는 사용자의 선택과 관련된 항목에 대해 정보를 제공한다.
// 리사이클러뷰에서 발생한 이벤트에 해당하는 아이템의 고유값과 어댑터상의 위치 및 기타 관련 정보를 제공하는 역할을 한다.

public class PhotoDetailsLookUp extends ItemDetailsLookup<Long> {
    public static final String TAG = PhotoDetailsLookUp.class.getSimpleName();
    private RecyclerView recyclerView;
    ArrayList selectPhoto = new ArrayList();
    String photo ;
    // 리사이클러뷰 찾기
    public PhotoDetailsLookUp(RecyclerView recyclerView){
        this.recyclerView = recyclerView;
    }

    // MotionEvent를 기반하여 선택된 내용을 ViewHolder에 매핑한다.
    @Nullable
    @Override
    public ItemDetails<Long> getItemDetails(@NonNull MotionEvent motionEvent) {
        Log.e(TAG, "getItemDetails: 들어옴" );
        // motionevent 클래스의 클릭 X축, Y축 좌표값을 얻을 수 있다.
        View view = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
        // 리사이클러뷰 전체를 기준으로 하는 모션 이벤트를 전달해주기 때문에 개별 아이템의 itemdetails를 생성하기 위해서는
        // 모션 이벤트에 전달된 좌표값을 기준으로 그 위치에 있는 아이템 정보를 얻을 수 있다.

        // 4번째 사진 선택하면 4번 돈다.
        if (view != null) {
            LookUpViewHolder viewHolder = (LookUpViewHolder) recyclerView.getChildViewHolder(view);
            Log.e(TAG, "getItemDetails: "+viewHolder.getItemId() );
            photo = "photo"+viewHolder.getItemId();
            return viewHolder.getItemDetails();
        }

        return null;
    }
}
