//package ventureindustries.altimeter;
//
//import android.content.Context;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.provider.ContactsContract;
//import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//
//public class StartFragment extends Fragment {
//
//    private Button startButton;
//    private Button stopButton;
//    private View view;
//
//    @Override
//    public void onCreate( Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//    }
//
//
//    public void loadData(String str){
//        getFragmentManager().beginTransaction();
//        //updated this line
//        ((Button) getActivity().findViewById(R.id.start_button)).setEnabled(false);
//        ((Button) getActivity().findViewById(R.id.stop_button)).setEnabled(false);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
//        view = inflater.inflate(R.layout.fragment_start, container, false);
//
//
//        return view;
//
//    }
//}
