package com.example.ian.mobile_oki.logic;

import com.example.ian.mobile_oki.contracts.CharacterSelectContract;
import com.example.ian.mobile_oki.data.DatabaseInterface;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

/**
 * Created by Ian on 7/5/2017.
 */
public class CharSelPresenterTest {
    @Mock
    CharacterSelectContract.View mCharacterSelectView;

    @Mock
    DatabaseInterface mDB;

    CharSelPresenter charSelPresenter;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        charSelPresenter = new CharSelPresenter(mCharacterSelectView, mDB);
    }

    @Test
    public void viewShouldCreateAndSetPresenter(){
       verify(mCharacterSelectView).setPresenter(charSelPresenter);
    }

    // Actually, gets names and their 3-letter character codes
    @Test
    public void shouldFetchListOfCharacterNamesFromDatabase(){
        // given an initialized Presenter
        // when character name list is requested from the database
        charSelPresenter.fetchListOfNames();

        verify(mDB).getCharacterNamesAndCodes(); // verifies that it ran, but that's it
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme