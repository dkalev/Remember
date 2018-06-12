package com.example.dkalev.remember.flashcard;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

public class CardTestUtils {

    private static final String[] frontText = {"front 1","front 2","front 3",
            "front 4","front 5","front 6","front 7"};
    private static final String[] backText = {"back 1","back 2","back 3",
            "back 4","back 5","back 6","back 7"};
    private final Random rand;

    public CardTestUtils(long seed) {
        rand = new Random(seed);
    }

    public ArrayList<Card> getNewCards(int count) {
        final ArrayList<Card> cards = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            cards.add(getNewProperty(i % frontText.length));
        }
        return cards;
    }


    private Card getNewProperty(int i) {
        final JSONObject json = new JSONObject();
        try {
            json.put(Card.JSON_FRONT_TEXT, frontText[i]);
            json.put(Card.JSON_BACK_TEXT, backText[i]);
            json.put(Card.JSON_FRONT_PIC, "");
            json.put(Card.JSON_BACK_PIC, "");
            return new Card(json);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    @NonNull
    private Card getNewProperty() {
        final JSONObject json = new JSONObject();

        int randomValue = rand.nextInt(frontText.length);
        try {
            json.put(Card.JSON_FRONT_TEXT, frontText[randomValue]);
            json.put(Card.JSON_BACK_TEXT, backText[randomValue]);
            json.put(Card.JSON_FRONT_PIC, "");
            json.put(Card.JSON_BACK_PIC, "");
            return new Card(json);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
