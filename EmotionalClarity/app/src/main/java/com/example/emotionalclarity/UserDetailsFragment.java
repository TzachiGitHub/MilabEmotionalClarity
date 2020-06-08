package com.example.emotionalclarity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class UserDetailsFragment extends Fragment {
    private String gender;
    private String name;
    public UserDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserDetailsFragment.
     */
    public static UserDetailsFragment newInstance(String param1, String param2) {
        UserDetailsFragment fragment = new UserDetailsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private FragmentCommunication communicator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_details, container, false);
        final TextInputEditText nameEditText = (TextInputEditText) view.findViewById(R.id.nameOfUser);

        // Gender:
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.genderRadioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected

                switch (checkedId){
                    case R.id.Male: {
                        gender = "Male";
                        break;
                    }
                    case R.id.Female: {
                        gender = "Female";
                        break;
                    }
                    default: {
                        gender = "Other";
                        break;
                    }
                }
            }
        });

        Button beginButton = (Button) view.findViewById(R.id.beginButton);
        beginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try{
                    name = nameEditText.getText().toString();
                    // Check if no radio-button chosen or no name was provided:
                    if (gender == null || name.equals("")) throw new NullPointerException();

                    // Send user info to Main Activity:
                    communicator = (FragmentCommunication) container.getContext();
                    communicator.getInformation(name, gender);

                    // Return to main activity:
                    getActivity().getSupportFragmentManager().beginTransaction().
                            remove(UserDetailsFragment.this).commit();

                } catch (NullPointerException e){
                    // Details were not provided:
                    Toast.makeText(getContext(), "Please fill name and gender",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        return view;
    }

}
