package com.petcare;

import com.petcare.model.User;
import com.petcare.view.LoginView;
import com.petcare.view.MainMenuView;

public class Main {
    public static void main(String[] args) {
        LoginView loginView = new LoginView();
        User loggedUser = loginView.show();

        // If the user closed the login dialog instead of logging in, just end the program quietly
        if (loggedUser != null) {
            new MainMenuView(loggedUser).show();
        }
    }
}