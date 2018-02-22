package com.jtstegeman.cs4518_finalproject;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewAlarm.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewAlarm#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewAlarm extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    // TODO: Rename and change types of parameters
    private EditText mNewLocation;
    private EditText mNewName;
    private Button mNewGPSLocation;
    private Button mNewTime;
    private Button mNewConfirm;
    private Button mNewCancel;

    private OnFragmentInteractionListener mListener;

    public NewAlarm() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NewAlarm.
     */
    // TODO: Rename and change types and number of parameters
    public static NewAlarm newInstance() {
        NewAlarm fragment = new NewAlarm();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_new_alarm, container, false);

        mNewName = (EditText) v.findViewById(R.id.newName);
        mNewLocation = (EditText) v.findViewById(R.id.newLocation);
        mNewGPSLocation = (Button) v.findViewById(R.id.newGPSLocation);
        mNewTime = (Button) v.findViewById(R.id.newTime);
        mNewConfirm = (Button) v.findViewById(R.id.newConfirm);
        mNewCancel = (Button) v.findViewById(R.id.newCancel);




        return v;
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
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void confirmNewAlarm(View view) {

    }

    public void cancelNewAlarm(View view) {

    }

    public void selectNewLocation(View v){

    }

    public void selectNewDate(View v){

    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
