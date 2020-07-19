package com.troofy.hopordrop.frag;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.troofy.hopordrop.R;
import com.troofy.hopordrop.activity.AskRideActivity;
import com.troofy.hopordrop.activity.GiveDropActivity;

import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PoolHomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PoolHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PoolHomeFragment extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private LinearLayout askRideLayout;
    private LinearLayout giveRideLayout;
    private ImageView hopLogo;
    private ImageView dropLogo;
    private Vibrator vibrator;
    private Animation in;
    private Animation out;
    private TextView tipsTxt;
    private Handler handler;
    private Random random = new Random();
    private int currentTip = 0;
    private Runnable mFadeOut =new Runnable(){

        @Override
        public void run() {

            tipsTxt.startAnimation(out);
        }
    };



    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PoolHomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PoolHomeFragment newInstance(String param1, String param2) {
        PoolHomeFragment fragment = new PoolHomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public PoolHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pool_home, container, false);
        vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);

        this.askRideLayout = (LinearLayout)view.findViewById(R.id.askRideBtn);
         askRideLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AskRideActivity.class);
                vibrator.vibrate(30);
                startActivity(i);
            }
        });
        this.hopLogo = (ImageView)view.findViewById(R.id.hopLogo);
        hopLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AskRideActivity.class);
                vibrator.vibrate(30);
                startActivity(i);
            }
        });
        this.giveRideLayout = (LinearLayout)view.findViewById(R.id.giveRideBtn);
        giveRideLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), GiveDropActivity.class);
                vibrator.vibrate(30);
                startActivity(i);
            }
        });
        this.dropLogo = (ImageView) view.findViewById(R.id.dropLogo);
        dropLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), GiveDropActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                vibrator.vibrate(30);
                startActivity(i);
            }
        });

        tipsTxt = (TextView) view.findViewById(R.id.tipsTxt);
        tipsTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tipsTxt.setSelected(true);
            }
        });
        final String tipsArray[] = getResources().getStringArray(R.array.main_tips);

        handler = new Handler();

        in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(3000);
        in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(currentTip == tipsArray.length)
                {
                    currentTip = 0;
                }
                tipsTxt.startAnimation(out);
                tipsTxt.setText(tipsArray[currentTip++]);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        out = new AlphaAnimation(1.0f, 0.0f);
        out.setDuration(3000);
        out.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationEnd(Animation animation) {
                if(currentTip == tipsArray.length)
                {
                    currentTip = 0;
                }
                tipsTxt.startAnimation(in);
                tipsTxt.setText(tipsArray[currentTip++]);

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationStart(Animation arg0) {
                // TODO Auto-generated method stub

            }
        });

        //mSwitcher.startAnimation(out);
        tipsTxt.setText(tipsArray[0]);
        currentTip = 0;
        tipsTxt.startAnimation(in);

       /*
        mSwitcher.startAnimation(out);
        mSwitcher.setText("Text 2.");
        mSwitcher.startAnimation(in);
        */

        handler.postDelayed(mFadeOut, 5000);



        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
