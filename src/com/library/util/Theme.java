package com.library.util;

import java.awt.Color;

public class Theme {
    public final Color background;
    public final Color cardBackground;
    public final Color textPrimary;
    public final Color textSecondary;
    public final Color accentBlue;
    public final Color accentGreen;
    public final Color accentPurple;
    public final Color accentOrange;

    public Theme(Color background, Color cardBackground, Color textPrimary, 
                Color textSecondary, Color accentBlue, Color accentGreen, 
                Color accentPurple, Color accentOrange) {
        this.background = background;
        this.cardBackground = cardBackground;
        this.textPrimary = textPrimary;
        this.textSecondary = textSecondary;
        this.accentBlue = accentBlue;
        this.accentGreen = accentGreen;
        this.accentPurple = accentPurple;
        this.accentOrange = accentOrange;
    }
} 