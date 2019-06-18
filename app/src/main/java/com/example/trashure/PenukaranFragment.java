package com.example.trashure;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;


public class PenukaranFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        eventPenukaran();
    }

    private void eventPenukaran() {
        final ExpandableRelativeLayout expandableRelativeLayout = (ExpandableRelativeLayout) getActivity().findViewById(R.id.expandableLayout);
        final LinearLayout llPulsa = (LinearLayout) getActivity().findViewById(R.id.ll_pulsa);
        final ImageView arrow = (ImageView) getActivity().findViewById(R.id.iv_penukaran_pulsa);
        expandableRelativeLayout.toggle();
        llPulsa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(expandableRelativeLayout.isExpanded())
                {
                    expandableRelativeLayout.toggle();
                    arrow.setImageResource(R.drawable.ic_arrow_drop_down);
                }
                else
                {
                    expandableRelativeLayout.toggle();
                    arrow.setImageResource(R.drawable.ic_arrow_drop_up);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_penukaran, container, false);
    }

}