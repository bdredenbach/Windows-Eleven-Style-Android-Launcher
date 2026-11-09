package com.nthexperience.desktop;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/** Bounded Settings history, including the search and scroll state of each visit. */
final class ElevenSettingsHistory {
    static final int LIMIT = 32;
    static final class Entry {
        final String page, query;
        final int scroll, sideScroll;
        Entry(String page, String query, int scroll, int sideScroll) {
            this.page = valid(page) ? page : "home";
            this.query = query == null ? "" : query.substring(0, Math.min(256, query.length()));
            this.scroll = Math.max(0, Math.min(1_000_000, scroll));
            this.sideScroll = Math.max(0, Math.min(1_000_000, sideScroll));
        }
        JSONObject json() {
            JSONObject o = new JSONObject();
            try { o.put("page", page); o.put("query", query); o.put("scroll", scroll); o.put("side", sideScroll); }
            catch (org.json.JSONException ignored) { }
            return o;
        }
        static Entry read(JSONObject o) {
            return o == null ? new Entry("home", "", 0, 0) :
                    new Entry(o.optString("page"), o.optString("query"), o.optInt("scroll"), o.optInt("side"));
        }
    }
    final List<Entry> back = new ArrayList<>(), forward = new ArrayList<>();
    Entry current = new Entry("home", "", 0, 0);
    static boolean valid(String id) {
        if ("home".equals(id) || TenSettingsCatalog.page(id) != null) return true;
        for (String[] c : TenSettingsCatalog.CATEGORIES) if (c[0].equals(id)) return true;
        return false;
    }
    void remember(String page, String query, int y, int sideY) { current = new Entry(page, query, y, sideY); }
    void visit(String page) {
        if (!valid(page)) page = "home";
        if (!current.page.equals(page) || !current.query.isEmpty()) {
            push(back, current); forward.clear(); current = new Entry(page, "", 0, current.sideScroll);
        }
    }
    Entry back() {
        if (back.isEmpty()) return null;
        push(forward, current); current = back.remove(back.size() - 1); return current;
    }
    Entry forward() {
        if (forward.isEmpty()) return null;
        push(back, current); current = forward.remove(forward.size() - 1); return current;
    }
    private static void push(List<Entry> list, Entry e) {
        list.add(e); while (list.size() > LIMIT) list.remove(0);
    }
    JSONObject json() {
        JSONObject o = new JSONObject();
        try {
            o.put("current", current.json());
            JSONArray b = new JSONArray(), f = new JSONArray();
            for (Entry e : back) b.put(e.json()); for (Entry e : forward) f.put(e.json());
            o.put("back", b); o.put("forward", f);
        } catch (org.json.JSONException ignored) { }
        return o;
    }
    void restore(JSONObject o) {
        back.clear(); forward.clear();
        current = Entry.read(o == null ? null : o.optJSONObject("current"));
        if (o != null) { readList(o.optJSONArray("back"), back); readList(o.optJSONArray("forward"), forward); }
    }
    private static void readList(JSONArray a, List<Entry> out) {
        if (a == null) return;
        for (int i = Math.max(0, a.length() - LIMIT); i < a.length(); i++) {
            JSONObject o = a.optJSONObject(i);
            if (o != null && valid(o.optString("page"))) push(out, Entry.read(o));
        }
    }
}
