package com.example.ian.mobile_oki.logic;

import com.example.ian.mobile_oki.contracts.OkiMoveSelectContract;
import com.example.ian.mobile_oki.data.DatabaseInterface;
import com.example.ian.mobile_oki.data.OkiMoveListItem;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by Ian on 8/5/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class OkiMoveSelectPresenterTest {
    @Mock
    OkiMoveSelectContract.View mView;

    @Mock
    DatabaseInterface mDB;

    @InjectMocks
    private OkiMoveSelectPresenter mPresenter;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        mPresenter = new OkiMoveSelectPresenter(mView, mDB);
    }

    @Test
    public void testStart() throws Exception {
        mPresenter.start();

        verify(mView).displayOkiMoveList();
    }

    /**
     * If I knew how to use Build Variants to make a mock version of the CharacterDatabase class,
     * I could better write this test.
     * @throws Exception possible exception
     */
    @Test
    public void testGetListOfOkiMoves() throws Exception {
        // Given a character code...
        // When getting the list of moves
        mPresenter.getListOfOkiMoves();

        // Then the db should return a list of OkiMoveListItems
        verify(mDB).getOkiMoves();
    }

    /**
     * Just testing that the method is being called.
     * @throws Exception possible exception
     */
    @Test
    public void testUpdateCurrentOkiMove() throws Exception {
        // Given a selected Oki Move and an Oki Number...
        OkiMoveListItem okiMove = new OkiMoveListItem(
                "","",0,0,0,0);
        int okiNumber = 0;

        // When the current oki move is updated...
        mPresenter.updateCurrentOkiMove(okiMove);

        // Then the database should update the current oki move...
        verify(mDB).setCurrentOkiMove(okiNumber, okiMove);
    }
}