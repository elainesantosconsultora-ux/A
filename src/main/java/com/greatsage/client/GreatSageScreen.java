package com.greatsage.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Tela "Grande Sabio" desenhada em codigo, seguindo a captura de referencia. */
public class GreatSageScreen extends Screen {
    // Proporcoes tiradas da captura (janela ~642x458 px)
    private static final int W = 321, H = 229;
    private static final int FRAME = 0xFFC9CBD3, FRAME_DARK = 0xFF9A9CA6;
    private static final int BLUE = 0xFF3F86E0, BLUE_DARK = 0xFF1F4E8C;
    private static final int TAB_OFF = 0xFF4A4C52, CONTENT = 0xFF1F1F1F;
    private static final int ROW = 0xFF4F4224, ROW_LINE = 0xFFC9A83A, GOLD = 0xFFFFE08A;
    private static final int ROW_H = 20;

    private enum Tab { TODOS("Todos"), FAVORITOS("Favoritos"), TECNICAS("Tecnicas");
        final String label; Tab(String l) { label = l; } }

    private final List<Entry> all = new ArrayList<>();
    private Tab tab = Tab.TODOS;
    private int page = 0;
    private String selectedId = null;
    private EditBox search;
    private int left, top;

    public GreatSageScreen() {
        super(Component.literal("Grande Sabio"));
        // TODO: preencher com as tecnicas absorvidas (dados do jogador, sincronizados pelo servidor)
        all.add(new Entry("rimuru_tempest", "Rimuru Tempest", Entry.Kind.CHARACTER, "Tensura"));
    }

    @Override protected void init() {
        left = (width - W) / 2;
        top = (height - H) / 2;
        search = new EditBox(font, left + 228, top + 14, 66, 14, Component.literal("Buscar"));
        search.setMaxLength(32);
        search.setResponder(s -> page = 0);
        addRenderableWidget(search);
    }

    private List<Entry> visible() {
        String q = search == null ? "" : search.getValue().toLowerCase(Locale.ROOT).trim();
        List<Entry> out = new ArrayList<>();
        for (Entry e : all) {
            if (tab == Tab.FAVORITOS && !FavoritesStore.isFav(e.id())) continue;
            if (tab == Tab.TECNICAS && e.kind() != Entry.Kind.TECHNIQUE) continue;
            if (!q.isEmpty() && !e.name().toLowerCase(Locale.ROOT).contains(q)) continue;
            out.add(e);
        }
        return out;
    }
    private int perPage() { return (contentH() - 12) / ROW_H; }
    private int contentX() { return left + 16; }
    private int contentY() { return top + 59; }
    private int contentW() { return W - 32; }
    private int contentH() { return 134; }
    private int tabX(int i) { return left + 16 + new int[]{0, 49, 111}[i]; }
    private int tabW(int i) { return new int[]{47, 60, 53}[i]; }

    @Override public void render(GuiGraphics g, int mx, int my, float pt) {
        renderBackground(g);
        // moldura
        g.fill(left, top, left + W, top + H, FRAME_DARK);
        g.fill(left + 1, top + 1, left + W - 1, top + H - 1, FRAME);
        // detalhes azuis nas bordas
        g.fill(left + 8, top + 52, left + 10, top + H - 40, BLUE);
        g.fill(left + W - 10, top + 52, left + W - 8, top + H - 40, BLUE);
        g.fill(left + 40, top + H - 14, left + W - 40, top + H - 13, BLUE);
        g.fill(left + 250, top + 50, left + W - 14, top + 51, BLUE);
        // icone de slime (placeholder desenhado)
        g.fill(left + 10, top + 14, left + 32, top + 30, BLUE);
        g.fill(left + 13, top + 11, left + 29, top + 14, BLUE);
        g.fill(left + 15, top + 19, left + 18, top + 22, 0xFF10306A);
        g.fill(left + 24, top + 19, left + 27, top + 22, 0xFF10306A);
        // titulo + subtitulo
        g.pose().pushPose();
        g.pose().scale(1.5f, 1.5f, 1f);
        g.drawString(font, "Grande Sabio", (int) ((left + 40) / 1.5f), (int) ((top + 10) / 1.5f), BLUE, false);
        g.pose().popPose();
        g.pose().pushPose();
        g.pose().scale(0.5f, 0.5f, 1f);
        g.drawString(font, "— REGISTRO DE TECNICAS ABSORVIDAS —", (left + 42) * 2, (top + 30) * 2, BLUE, false);
        g.pose().popPose();
        // abas
        for (Tab t : Tab.values()) {
            int i = t.ordinal();
            int x = tabX(i), y = top + 40;
            g.fill(x, y, x + tabW(i), y + 18, t == tab ? BLUE_DARK : TAB_OFF);
            String icon = i == 0 ? "\u2261 " : i == 1 ? "\u2605 " : "+ ";
            g.drawString(font, icon + t.label, x + 4, y + 5, 0xFFFFFFFF, true);
        }
        // area de conteudo
        int cx = contentX(), cy = contentY(), cw = contentW(), ch = contentH();
        g.fill(cx - 1, cy - 1, cx + cw + 1, cy + ch + 1, 0xFF0E0E0E);
        g.fill(cx, cy, cx + cw, cy + ch, CONTENT);
        // linhas
        List<Entry> list = visible();
        int pages = Math.max(1, (int) Math.ceil(list.size() / (double) perPage()));
        page = Math.min(page, pages - 1);
        for (int r = 0; r < perPage(); r++) {
            int idx = page * perPage() + r;
            if (idx >= list.size()) break;
            Entry e = list.get(idx);
            int ry = cy + 12 + r * ROW_H;
            boolean sel = e.id().equals(selectedId);
            boolean hover = mx >= cx + 8 && mx < cx + cw - 8 && my >= ry && my < ry + 16;
            g.fill(cx + 8, ry, cx + cw - 8, ry + 16, sel || hover ? ROW : 0xFF2A2A2A);
            g.fill(cx + 8, ry + 15, cx + cw - 8, ry + 16, sel || hover ? ROW_LINE : 0xFF3A3A3A);
            g.drawString(font, e.name(), cx + 14, ry + 4, sel || hover ? GOLD : 0xFFDDDDDD, false);
            boolean fav = FavoritesStore.isFav(e.id());
            g.drawString(font, "\u2605", cx + cw - 22, ry + 4, fav ? GOLD : 0xFF666666, false);
        }
        // info do selecionado
        Entry s = all.stream().filter(e -> e.id().equals(selectedId)).findFirst().orElse(null);
        if (s != null) g.drawString(font, s.source(), cx + 8, cy + ch - 12, 0xFF8899BB, false);
        // paginacao
        String p = (page + 1) + " / " + pages;
        int px = left + W / 2 - font.width(p) / 2;
        g.drawString(font, "<", px - 22, top + H - 20, page > 0 ? BLUE : 0xFF777777, false);
        g.drawString(font, p, px, top + H - 20, 0xFF2B4F8A, false);
        g.drawString(font, ">", px + font.width(p) + 14, top + H - 20, page < pages - 1 ? BLUE : 0xFF777777, false);
        super.render(g, mx, my, pt);
    }

    @Override public boolean mouseClicked(double mx, double my, int button) {
        if (super.mouseClicked(mx, my, button)) return true;
        for (Tab t : Tab.values()) {
            int i = t.ordinal();
            if (mx >= tabX(i) && mx < tabX(i) + tabW(i) && my >= top + 40 && my < top + 58) {
                tab = t; page = 0; return true;
            }
        }
        List<Entry> list = visible();
        int cx = contentX(), cy = contentY(), cw = contentW();
        for (int r = 0; r < perPage(); r++) {
            int idx = page * perPage() + r;
            if (idx >= list.size()) break;
            int ry = cy + 12 + r * ROW_H;
            if (mx >= cx + 8 && mx < cx + cw - 8 && my >= ry && my < ry + 16) {
                if (mx >= cx + cw - 26) FavoritesStore.toggle(list.get(idx).id());
                else selectedId = list.get(idx).id();
                return true;
            }
        }
        int pages = Math.max(1, (int) Math.ceil(list.size() / (double) perPage()));
        String p = (page + 1) + " / " + pages;
        int px = left + W / 2 - font.width(p) / 2;
        if (my >= top + H - 24 && my < top + H - 10) {
            if (mx >= px - 26 && mx < px - 12 && page > 0) { page--; return true; }
            if (mx >= px + font.width(p) + 10 && mx < px + font.width(p) + 24 && page < pages - 1) { page++; return true; }
        }
        return false;
    }

    @Override public boolean isPauseScreen() { return false; }
}
