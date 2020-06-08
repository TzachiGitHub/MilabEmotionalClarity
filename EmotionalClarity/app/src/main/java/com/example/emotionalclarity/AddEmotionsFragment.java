package com.example.emotionalclarity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddEmotionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddEmotionsFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";

    private ArrayList<String> emotionNames;

    public AddEmotionsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param unChosen: list of emotions to display
     * @return A new instance of fragment AddEmotionsFragment.
     */
    public static AddEmotionsFragment newInstance(ArrayList<String> unChosen) {
        AddEmotionsFragment fragment = new AddEmotionsFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_PARAM1, unChosen);
        fragment.setArguments(args);
        return fragment;
    }

    private FragmentCommunication communicator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            emotionNames = getArguments().getStringArrayList(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_emotions, container, false);
        communicator = (FragmentCommunication) container.getContext();

        // Set recycler view:
        final RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.emotionCheckList);
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        recyclerView.setAdapter(new UnCheckedAdapter(emotionNames));

        // Done - add selected emotions (if any):
        Button doneAddingButton = (Button) view.findViewById(R.id.doneAddingButton);
        doneAddingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                communicator.getInformation(((UnCheckedAdapter)
                        Objects.requireNonNull(recyclerView.getAdapter())).getCheckedList());
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        // Exit fragment without adding:
        ImageButton xButton = (ImageButton) view.findViewById(R.id.xButton);
        xButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                communicator.getInformation();
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
        return view;
    }
}
