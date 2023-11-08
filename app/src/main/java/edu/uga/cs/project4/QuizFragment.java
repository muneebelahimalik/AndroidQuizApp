package edu.uga.cs.project4;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizFragment extends Fragment {

    private ViewPager viewPager;
    private QuestionPagerAdapter questionPagerAdapter;
    private List<String> quizQuestions = new ArrayList<>();
    private List<String> quizCapitals = new ArrayList<>();
    private List<String> quizAdditionalCities = new ArrayList<>();
    private List<String> quizAdditionalCities2 = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int userScore = 0;
    private boolean isAnswerChecked = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);

        DBhelper dbHelper = new DBhelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBhelper.TABLE_QUESTIONS + " ORDER BY RANDOM() LIMIT 6", null);
        int count = 0;
        while (cursor.moveToNext() && count < 6) {
            String state = cursor.getString(cursor.getColumnIndex(DBhelper.KEY_STATE));
            String capital = cursor.getString(cursor.getColumnIndex(DBhelper.KEY_CAPITAL_CITY));
            String additionalCity = cursor.getString(cursor.getColumnIndex(DBhelper.KEY_ADDITIONAL_CITY2));
            String additionalCityy = cursor.getString(cursor.getColumnIndex(DBhelper.KEY_ADDITIONAL_CITY3));
            quizQuestions.add("Question: What is the capital of " + state + "?");
            quizCapitals.add(capital);
            quizAdditionalCities.add(additionalCity);
            quizAdditionalCities2.add(additionalCityy);
            Log.d("quizQuestion", "question: " + quizQuestions.get(count));
            count++;
        }

        cursor.close();
        db.close();

        // Shuffle the quiz questions
    //    Collections.shuffle(quizQuestions);

        // Initialize ViewPager and adapter
        viewPager = view.findViewById(R.id.viewPager);
        questionPagerAdapter = new QuestionPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(questionPagerAdapter);

        // Set up the question counter
        final TextView questionCounterTextView = view.findViewById(R.id.questionCounterTextView);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int questionNumber = position + 1;
                questionCounterTextView.setText("Question " + questionNumber + " of " + quizQuestions.size());

                // Access the selected answer from the currently displayed QuizQuestionFragment
                int selectedAnswer = ((QuizQuestionFragment) questionPagerAdapter.instantiateItem(viewPager, position)).getSelectedAnswer();

                // Check the selected answer and increment the score
                checkSelectedAnswer(selectedAnswer);
                Log.d("QuizFragment", "Question " + " - Selected Answer: " + selectedAnswer);
                Log.d("QuizFragment", "User Score: " + userScore);

                if (position == quizQuestions.size()) {
                    Log.d("QuizFragment", "FINAL User Score: " + userScore);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return view;
    }
    private void checkSelectedAnswer(int selectedAnswer) {
        if (selectedAnswer == 1) {
                userScore++; // Increment the score
            }
    }
    private class QuestionPagerAdapter extends FragmentPagerAdapter {

        public QuestionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int position) {
            String question = quizQuestions.get(position);
            String capital = quizCapitals.get(position); // Add a List<String> quizCapitals to store capital city names
            String additionalCity = quizAdditionalCities.get(position); // Add a List<String> quizAdditionalCities
            String additionalCityy = quizAdditionalCities2.get(position); // Add a List<String> quizAdditionalCities2

            return QuizQuestionFragment.newInstance(question, capital, additionalCity, additionalCityy);
        }


        @Override
        public int getCount() {
            return quizQuestions.size();
        }
    }
}