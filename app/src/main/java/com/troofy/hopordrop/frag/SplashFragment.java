package com.troofy.hopordrop.frag;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.troofy.hopordrop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SplashFragment extends Fragment {

    private AlphaAnimation animation1;
    private AlphaAnimation animation2;


    public SplashFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_splash, container, false);


        final ImageView img = (ImageView)view.findViewById(R.id.splashLogo);

//        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.splashBar);
//        progressBar.setIndeterminate(true);

//        animation2 = new AlphaAnimation(1.0f, 0.0f);
//        animation2.setDuration(1000);
//        animation2.setStartOffset(3000);
//
//        //animation2 AnimationListener
//        animation2.setAnimationListener(new Animation.AnimationListener(){
//
//            @Override
//            public void onAnimationEnd(Animation arg0) {
//                // start animation1 when animation2 ends (repeat)
//                img.startAnimation(animation1);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation arg0) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void onAnimationStart(Animation arg0) {
//                // TODO Auto-generated method stub
//
//            }
//
//        });

//        animation1 = new AlphaAnimation(0.0f, 1.0f);
//        animation1.setDuration(1000);
//        animation1.setStartOffset(1000);
//
//        //animation1 AnimationListener
//        animation1.setAnimationListener(new Animation.AnimationListener(){
//
//            @Override
//            public void onAnimationEnd(Animation arg0) {
//                // start animation2 when animation1 ends (continue)
//                img.startAnimation(animation2);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation arg0) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void onAnimationStart(Animation arg0) {
//                // TODO Auto-generated method stub
//
//            }
//
//        });
//
//        img.startAnimation(animation1);

        return  view;
    }

}
