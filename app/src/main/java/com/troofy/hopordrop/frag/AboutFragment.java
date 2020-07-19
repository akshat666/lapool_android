package com.troofy.hopordrop.frag;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.troofy.hopordrop.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AboutFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AboutFragment#AboutFragment()} newInstance} factory method to
 * create an instance of this fragment.
 */
public class AboutFragment extends android.support.v4.app.Fragment {


    public AboutFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_about, container,
                false);

        Button rateBtn = (Button)view.findViewById(R.id.rateBtn);
        rateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getContext().getPackageName())));
                }catch (Exception e){

                }

            }
        });

        Button fbBtn = (Button)view.findViewById(R.id.fbFollowBtn);
        fbBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href=http://www.facebook.com/hopordrop"));
                    startActivity(intent);
                } catch(Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com/hopordrop")));
                }

            }
        });

        Button twitterBtn = (Button)view.findViewById(R.id.twitterFollBtn);
        twitterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + "hopordropapp")));
                }catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/#!/" + "hopordropapp")));
                }

            }
        });

        TextView url = (TextView) view.findViewById(R.id.siteURL);
        url.setClickable(true);
        url.setMovementMethod(LinkMovementMethod.getInstance());
        String siteURLtxt = getString(R.string.mainLink);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            url.setText(Html.fromHtml(siteURLtxt, Html.FROM_HTML_MODE_LEGACY));
        } else {
            url.setText(Html.fromHtml(siteURLtxt));
        }

        TextView faqURL = (TextView) view.findViewById(R.id.faqUrl);
        faqURL.setClickable(true);
        faqURL.setMovementMethod(LinkMovementMethod.getInstance());
        String faqURLtxt = getString(R.string.faqLink);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            faqURL.setText(Html.fromHtml(faqURLtxt, Html.FROM_HTML_MODE_LEGACY));
        } else {
            faqURL.setText(Html.fromHtml(faqURLtxt));
        }

        //teerms and policy hyperlink
        TextView terms =(TextView)view.findViewById(R.id.termsUrl);
        terms.setClickable(true);
        terms.setMovementMethod(LinkMovementMethod.getInstance());
        String termsTxt = getString(R.string.termsLink);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            terms.setText(Html.fromHtml(termsTxt,Html.FROM_HTML_MODE_LEGACY));
        } else {
            terms.setText(Html.fromHtml(termsTxt));
        }

        TextView policy =(TextView)view.findViewById(R.id.policyUrl);
        policy.setClickable(true);
        policy.setMovementMethod(LinkMovementMethod.getInstance());
        String policyTxt = getString(R.string.policyLink);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            policy.setText(Html.fromHtml(policyTxt,Html.FROM_HTML_MODE_LEGACY));
        } else {
            policy.setText(Html.fromHtml(policyTxt));
        }

        return view;


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
