package com.example.ian.mobile_oki.logic;

import android.content.Intent;

import com.example.ian.mobile_oki.contracts.MainMenuContract;
import com.example.ian.mobile_oki.data.CharacterDatabase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static android.app.Activity.RESULT_OK;
import static com.example.ian.mobile_oki.view.MainActivity.CHARACTER_EXTRA;
import static com.example.ian.mobile_oki.view.MainActivity.CHAR_SEL_REQUEST_CODE;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * What do we want to do from the Presenter's point of view?
 *
 * Initialize the program when Presenter is started.
 *  Open CharSel if no character is selected.
 *
 *
 * Created by Ian on 7/5/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class MainMenuPresenterTest {

    @Mock
    MainMenuContract.View mMainMenuView;

    @Mock
    CharacterDatabase mDB;

    MainMenuPresenter mainMenuPresenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mainMenuPresenter = new MainMenuPresenter(mMainMenuView, mDB);
    }

    @Test public void checkSetPresenterOnCreation(){
        verify(mMainMenuView).setPresenter(mainMenuPresenter);
    }

    @Test public void noCharacterSelectedShouldStartActivity(){
        when(mMainMenuView.hasSelectedCharacter()).thenReturn(false);

        mainMenuPresenter.start();

        // Verify that character select screen shows
        verify(mMainMenuView).showCharacterSelect();
    }

    @Test public void characterSelectedShouldNotStartActivity(){
        when(mMainMenuView.hasSelectedCharacter()).thenReturn(true);

        mainMenuPresenter.start();

        verify(mMainMenuView, never()).showCharacterSelect();
    }

    // character is returned by the activity
    @Test public void characterSelectFinished_successfullySetCharacter(){
        // i have no idea how to write this test...
    }

    // no character is returned
}