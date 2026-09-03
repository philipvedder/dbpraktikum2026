package de.unileipzig.dbpraktikum.cli_interface.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.gui2.table.Table;

import de.unileipzig.dbpraktikum.cli_interface.db_interface.DBInterface;
import de.unileipzig.dbpraktikum.cli_interface.model.Category;
import de.unileipzig.dbpraktikum.cli_interface.model.Product;
import de.unileipzig.dbpraktikum.cli_interface.model.dto.ProductListEntry;
import de.unileipzig.dbpraktikum.cli_interface.ui.components.ProductListEntryTableComponent;

/** Displays the existing category tree without changing the database. */
public class CategoryTreeScreen {
    private final WindowBasedTextGUI gui;
    private final DBInterface db;
    private final Set<Long> expandedIds = new HashSet<>();
    private final List<Category> visibleCategories = new ArrayList<>();

    private List<Category> roots;
    private Table<String> categoryTable;

    public CategoryTreeScreen(WindowBasedTextGUI gui, DBInterface db) {
        this.gui = gui;
        this.db = db;
    }

    public void show() {
        roots = sortedCategories(db.getCategoryTree());

        BasicWindow window = new BasicWindow("Category Tree");
        Panel root = new Panel(new LinearLayout(Direction.VERTICAL));

        root.addComponent(new Label("Enter: expand/collapse a branch; open products for a leaf."));
        root.addComponent(new Label("[+] closed branch   [-] open branch   ID identifies the category"));

        categoryTable = new Table<>("Category", "ID", "Children");
        categoryTable.setPreferredSize(new TerminalSize(95, 23));
        categoryTable.setCellSelection(false);
        categoryTable.setSelectAction(this::activateSelectedCategory);
        root.addComponent(categoryTable);

        if (roots.isEmpty()) {
            root.addComponent(new Label("No categories found. Import category data first."));
        }

        Panel buttons = new Panel(new LinearLayout(Direction.HORIZONTAL));
        buttons.addComponent(new Button("Collapse all", () -> {
            expandedIds.clear();
            refreshTree(null);
        }));
        buttons.addComponent(new Button("Back", window::close));
        root.addComponent(buttons);

        refreshTree(null);
        window.setComponent(root);
        gui.addWindowAndWait(window);
    }

    private void activateSelectedCategory() {
        Category selected = selectedCategory();
        if (selected == null) {
            return;
        }

        if (childrenOf(selected).isEmpty()) {
            showSelectedProducts();
            return;
        }

        if (!expandedIds.add(selected.getId())) {
            expandedIds.remove(selected.getId());
        }
        refreshTree(selected.getId());
    }

    private void refreshTree(Long selectedId) {
        categoryTable.getTableModel().clear();
        visibleCategories.clear();
        for (Category category : roots) {
            appendCategory(category, 0);
        }

        if (!visibleCategories.isEmpty()) {
            int selectedRow = 0;
            for (int i = 0; i < visibleCategories.size(); i++) {
                if (visibleCategories.get(i).getId().equals(selectedId)) {
                    selectedRow = i;
                    break;
                }
            }
            categoryTable.setSelectedRow(selectedRow);
        }
    }

    private void appendCategory(Category category, int depth) {
        List<Category> children = childrenOf(category);
        boolean expanded = expandedIds.contains(category.getId());
        StringBuilder label = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            label.append("  ");
        }
        label.append(children.isEmpty() ? "    " : expanded ? "[-] " : "[+] ");
        label.append(category.getName());

        visibleCategories.add(category);
        categoryTable.getTableModel().addRow(
            label.toString(),
            category.getId().toString(),
            Integer.toString(children.size())
        );

        if (expanded) {
            for (Category child : sortedCategories(children)) {
                appendCategory(child, depth + 1);
            }
        }
    }

    private Category selectedCategory() {
        int row = categoryTable.getSelectedRow();
        return row >= 0 && row < visibleCategories.size() ? visibleCategories.get(row) : null;
    }

    private void showSelectedProducts() {
        Category selected = selectedCategory();
        if (selected == null) {
            return;
        }

        try {
            List<ProductListEntry> entries = new ArrayList<>();
            for (Product product : db.getProductsByCategory(selected)) {
                entries.add(new ProductListEntry(
                    product.getId(), product.getTitle(), product.getType(), product.getAvgRating(), product.getRatingQuantity()
                ));
            }
            entries.sort(Comparator.comparing(ProductListEntry::getTitle, String.CASE_INSENSITIVE_ORDER).thenComparing(ProductListEntry::getId));

            BasicWindow window = new BasicWindow("Products in category " + selected.getId());
            Panel root = new Panel(new LinearLayout(Direction.VERTICAL));
            root.addComponent(new Label(selected.getName()));
            root.addComponent(new Label("Products assigned directly to this category: " + entries.size()));

            ProductListEntryTableComponent products = new ProductListEntryTableComponent(gui, db);
            root.addComponent(products.getTable(120, 25));
            products.update(entries);

            root.addComponent(new Button("Back", window::close));
            window.setComponent(root);
            gui.addWindowAndWait(window);
        } catch (RuntimeException ex) {
            showLoadError("Could not load products for the selected category.", ex);
        }
    }

    private List<Category> childrenOf(Category category) {
        return category.getChilds() == null ? Collections.emptyList() : category.getChilds();
    }

    private List<Category> sortedCategories(List<Category> categories) {
        List<Category> sorted = new ArrayList<>(categories);
        sorted.sort(Comparator.comparing(Category::getName, String.CASE_INSENSITIVE_ORDER).thenComparing(Category::getId));
        return sorted;
    }

    private void showLoadError(String message, RuntimeException ex) {
        ex.printStackTrace();
        MessageDialog.showMessageDialog(gui, "Database error",
            message + "\nSee the terminal for details.", MessageDialogButton.OK);
    }
}
