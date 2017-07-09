package com.example.ian.mobile_oki.contracts;

import android.content.Context;
import android.content.Intent;

import com.example.ian.mobile_oki.BasePresenter;
import com.example.ian.mobile_oki.BaseView;
import com.example.ian.mobile_oki.data.CharacterListItem;

import java.util.List;

/**
 * <p>Interface for Main Menu and MainMenuPresenter
 * <p>
 * Created by Ian on 7/2/2017.
 */

public interface MainMenuContract {

    interface View extends BaseView<Presenter>{

        boolean hasSelectedCharacter();

        void showCharacterSelect();

        void setCharacter(String stringExtra);
    }

    interface Presenter extends BasePresenter {

        void result(int requestCode, int resultCode, Intent intent);

    }
}
