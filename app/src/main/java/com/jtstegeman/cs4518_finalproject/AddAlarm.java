package com.jtstegeman.cs4518_finalproject;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jtstegeman.cs4518_finalproject.database.AlarmHelper;
import com.jtstegeman.cs4518_finalproject.database.AlarmObject;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddAlarm.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddAlarm#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddAlarm extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "name";

    private static final String DIALOG_DATE = "DialogDate";


    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_MAP  = 1;


    // TODO: Rename and change types of parameters

    private OnFragmentInteractionListener mListener;


    private AlarmObject alarm;

    private Button mEdit;
    private TextView mTime;
    private TextView mLocation;

    private String mName;

    public AddAlarm() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param mName Parameter 1.
     * @return A new instance of fragment AddAlarm.
     */
    // TODO: Rename and change types and number of parameters
    public static AddAlarm newInstance(String mName) {
        AddAlarm fragment = new AddAlarm();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, mName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mName = getArguments().getString(ARG_PARAM1);

        AlarmHelper helper = AlarmHelper.getInstance(this.getContext());
        alarm = helper.get(mName);
//        alarm.setTime(AlarmHelper.get(getActivity()).getTime());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_alarm, container, false);

        mEdit = (Button) v.findViewById(R.id.editButton);
        mTime = (TextView) v.findViewById(R.id.dataTime);
        mLocation = (TextView) v.findViewById(R.id.dataLocation);

        mTime.setText(alarm.getName());
        mLocation.setText(alarm.getLocation());

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

    public void editAlarm(View v){

        FragmentManager manager = getFragmentManager();


    }

    public void editTime(View v){
        FragmentManager manager = getFragmentManager();
        DatePickerFragmentNerdHerd dialog = DatePickerFragmentNerdHerd
                .newInstance(alarm.getTime());
        dialog.setTargetFragment(AddAlarm.this, REQUEST_DATE);
        dialog.show(manager, DIALOG_DATE);
    }
}
