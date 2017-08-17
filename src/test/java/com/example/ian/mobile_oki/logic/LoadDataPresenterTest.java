package com.example.ian.mobile_oki.logic;

import com.example.ian.mobile_oki.contracts.LoadDataContract;
import com.example.ian.mobile_oki.data.DatabaseInterface;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test
 * Created by Ian on 8/17/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class LoadDataPresenterTest {
    @Mock
    LoadDataContract.View view;

    @Mock
    DatabaseInterface db;

    @InjectMocks
    LoadDataPresenter presenter;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        presenter = new LoadDataPresenter(view, db);
    }

    @Test
    public void initialize_shouldSetPresenter() throws Exception {
        // Given a new view
        // When view starts
        // Then set view's presenter
        verify(view).setPresenter(presenter);
    }

    @Test
    public void start_shouldPopulateCharacterSpinner() throws Exception {
        // Given an initialized view
        // When presenter starts,
        presenter.start();
        // Then populate the Character Spinner (widget)
        verify(view).populateCharacterSpinner();
    }
}