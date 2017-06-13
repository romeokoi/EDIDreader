package tko.edidreader;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class RawDataFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private String edid;
    public RawDataFragment() {
        // Required empty public constructor
    }

    public static RawDataFragment newInstance() {
        RawDataFragment fragment = new RawDataFragment();

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SecondActivity activity = (SecondActivity) getActivity();
        edid = activity.getdata();
        View view = inflater.inflate(R.layout.fragment_raw_data, container, false);
        TextView rawdatatextview = (TextView) view.findViewById(R.id.rawDataTextView);
        rawdatatextview.setText(edid);
        // Inflate the layout for this fragment
        return view;

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
        void onFragmentInteraction(String string);
    }
}
