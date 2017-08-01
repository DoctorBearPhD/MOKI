package com.example.ian.mobile_oki.logic;

import com.example.ian.mobile_oki.contracts.MainMenuContract;
import com.example.ian.mobile_oki.data.DatabaseInterface;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Captor
    private ArgumentCaptor<MainMenuContract.Presenter> capturedPresenter;

    @Before
    public void setUp() {
        mainMenuPresenter = new MainMenuPresenter(mMainMenuView, mDB);
    }

    @Test public void instantiationShouldSetPresenter(){
        verify(mMainMenuView).setPresenter(mainMenuPresenter);
        //assert that an extra MainMenuPresenter has not been created (requires Context and View: can't)
        //assertEquals(mainMenuPresenter, capturedPresenter.getValue());
    }

    @Test public void onInitialization_shouldNotShowTimeline(){
        mainMenuPresenter.start();

        assertTrue(!mainMenuPresenter.isTimelineReady());
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

    @Test public void characterSelectFinished_shouldStartKDMoveSelect(){
        // given a character has been selected, and kd move has not been selected...
        when(mMainMenuView.hasSelectedCharacter()).thenReturn(true);
        when(mMainMenuView.hasSelectedKDMove()).thenReturn(false);

        // when
        mainMenuPresenter.start();

        // then
        verify(mMainMenuView).showKDMoveSelect();
    }

    @Test public void characterAndKDMoveSelected_shouldShowTimeline(){
        // given a character has been selected, and kd move has not been selected...
        when(mMainMenuView.hasSelectedCharacter()).thenReturn(true);
        when(mMainMenuView.hasSelectedKDMove()).thenReturn(true);

        // when
        mainMenuPresenter.start();

        // then
        verify(mMainMenuView).showTimeline();
    }

    // when no character is returned from Select activity

    // ---
}