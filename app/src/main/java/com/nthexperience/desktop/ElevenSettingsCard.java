package com.nthexperience.desktop;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/** Shared card anatomy for links, grouped controls and expandable Settings rows. */
final class ElevenSettingsCard extends LinearLayout {
    final LinearLayout header, labels, children;
    final TextView title, summary;
    private TextView arrow;
    ElevenSettingsCard(Context c, String name, String hint, String kind, boolean icon) {
        super(c); setOrientation(VERTICAL);
        setBackground(ElevenSurface.round(c, TenAppearance.dark(c) ? 0xff2b2c3d : 0xfffdfdff, 5));
        header = new LinearLayout(c); header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(14), dp(12), dp(12), dp(12)); header.setMinimumHeight(dp(64));
        if (icon) {
            View glyph = new TenStyle.Glyph(c, kind, TenAppearance.ink(c));
            LayoutParams gp = new LayoutParams(dp(24), dp(24)); gp.rightMargin = dp(14); header.addView(glyph, gp);
        }
        labels = Aero.column(c);
        title = Aero.text(c, name, 14, TenAppearance.ink(c)); labels.addView(title);
        summary = Aero.text(c, hint, 11, TenAppearance.dark(c) ? 0xffc2c3cf : 0xff646471);
        summary.setPadding(0, dp(3), 0, 0); summary.setVisibility(hint.isEmpty() ? GONE : VISIBLE); labels.addView(summary);
        header.addView(labels, new LayoutParams(0, -2, 1)); addView(header, new LayoutParams(-1, -2));
        children = Aero.column(c); children.setVisibility(GONE); addView(children, new LayoutParams(-1, -2));
        setContentDescription(name);
    }
    void link(Runnable action) {
        arrow("›"); header.setFocusable(true); header.setContentDescription(title.getText());
        header.setBackground(TenStyle.appHover(getContext(), Color.TRANSPARENT));
        header.setOnClickListener(v -> action.run());
        setFocusable(true); setOnClickListener(v -> action.run());
    }
    void expandable(boolean expanded, java.util.function.Consumer<Boolean> changed) {
        arrow(expanded ? "⌃" : "⌄"); children.setVisibility(expanded ? VISIBLE : GONE);
        header.setFocusable(true); header.setContentDescription(title.getText() + (expanded ? ", expanded" : ", collapsed"));
        header.setBackground(TenStyle.appHover(getContext(), Color.TRANSPARENT));
        header.setOnClickListener(v -> {
            boolean next = children.getVisibility() != VISIBLE;
            children.setVisibility(next ? VISIBLE : GONE); arrow.setText(next ? "⌃" : "⌄");
            header.setContentDescription(title.getText() + (next ? ", expanded" : ", collapsed")); changed.accept(next);
        });
    }
    void child(View child) {
        View divider = new View(getContext()); divider.setBackgroundColor(TenAppearance.dark(getContext()) ? 0xff393a49 : 0xffececf1);
        children.addView(divider, new LayoutParams(-1, dp(1)));
        children.addView(child, new LayoutParams(-1, -2));
    }
    private void arrow(String text) {
        if (arrow != null) header.removeView(arrow);
        arrow = Aero.text(getContext(), text, 18, TenAppearance.ink(getContext())); arrow.setGravity(Gravity.CENTER);
        header.addView(arrow, new LayoutParams(dp(28), dp(36)));
    }
    private int dp(int value) { return Aero.dp(getContext(), value); }
}
