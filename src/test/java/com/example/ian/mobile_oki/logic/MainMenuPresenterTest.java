package com.example.ian.mobile_oki.logic;

import android.content.Intent;

import com.example.ian.mobile_oki.contracts.MainMenuContract;
import com.example.ian.mobile_oki.data.CharacterDatabase;
import com.example.ian.mobile_oki.data.DatabaseInterface;

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
 *
 * <ol>
 * <li>Program starts.
 * <li>MainActivity creates a presenter, and tells the presenter to initialize the program. (TODO: Use dependency injection instead)
 * <li>MainMenuPresenter tells MainActivity to start the character select activity.
 * <li>CharacterSelectActivity requests a list of character names and 3-letter character codes from the database,
 * then displays the names.
 * <li>User selects a character name, and character select activity sends the character code to MainActivity.
 * <li>MainActivity sets the character and notifies the presenter.
 * <li>Presenter checks if the Timeline is Ready to be displayed.
 * <li>It still needs a KD Move, so it tells the MainActivity to start the KDMoveSelectActivity.
 * <li>User selects a KD Move, and KDMoveSelectActivity sends it back to MainActivity.
 * <ul><li>User can choose to view Move Details first.</li></ul>
 * <li>MainActivity sets the KD Move and notifies the presenter.
 * <li>Presenter checks if the Timeline is Ready to be displayed.
 * </ol>
 *
 *
 * Created by Ian on 7/5/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class MainMenuPresenterTest {

    @Mock
    private MainMenuContract.View mMainMenuView;

    @Mock
    private DatabaseInterface mDB;

    private MainMenuContract.Presenter mainMenuPresenter;

    @Before
    public void setUp() {
        mainMenuPresenter = new MainMenuPresenter(mMainMenuView, mDB);
    }

    @Test public void checkSetPresenterOnCreation(){
        verify(mMainMenuView).setPresenter(mainMenuPresenter);
    }

    @Test public void noCharacterSelected_shouldStartActivity(){
        when(mMainMenuView.hasSelectedCharacter()).thenReturn(false);

        mainMenuPresenter.start();

        // Verify that character select screen shows
        verify(mMainMenuView).showCharacterSelect();
    }

    @Test public void characterAlreadySelected_shouldNotStartActivity(){
        when(mMainMenuView.hasSelectedCharacter()).thenReturn(true);

        mainMenuPresenter.start();

        verify(mMainMenuView, never()).showCharacterSelect();
    }

    @Test public void characterSelectFinished_shouldShowKDMoveSelect(){
        // given a character has been selected, and kd move has not been selected...
//        when(mMainMenuView).isTimelineReady().thenReturn(false);
        when(mMainMenuView.hasSelectedCharacter()).thenReturn(true);
        when(mMainMenuView.hasSelectedKDMove()).thenReturn(false);

        // when
        mainMenuPresenter.start();

        // then
        verify(mMainMenuView).showKDMoveSelect();
    }

    // no character is returned
}