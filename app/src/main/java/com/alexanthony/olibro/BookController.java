package com.alexanthony.olibro;

import android.widget.MediaController;
import android.content.Context;

public class BookController extends MediaController {

    public BookController(Context c) {
        super(c);
    }

    public void hide() {
        // Override hide() to stop automatic hiding of MediaController for 3s
    }
}
