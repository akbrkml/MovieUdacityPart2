package com.akbar.dev.movieudacity.utils;

import android.content.Intent;
import android.content.res.Resources;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.akbar.dev.movieudacity.R;
import com.akbar.dev.movieudacity.view.FavoriteActivity;

/**
 * Created by akbar on 26/07/17.
 */

public class ViewUtils {

    public static void showSnackbar(String message, View view) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    public static void showSnackbarAction(String message, View view) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.setAction("VIEW ITEMS", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getContext().startActivity(new Intent(v.getContext(), FavoriteActivity.class));
            }
        });
        snackbar.setActionTextColor(view.getResources().getColor(R.color.colorAccent));
        snackbar.show();
    }
}
