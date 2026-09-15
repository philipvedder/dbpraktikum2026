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

public class CategoryTreeScreen {
    /**
     * Lanterna TUI Screen for viewing the Category tree
     */

    // Lanterna TUI and DB Interface
    private final WindowBasedTextGUI gui;
    private final DBInterface db;

    // Mirror the table rows to retain both the entity and its full path.
    private final List<Category> visibleCategories = new ArrayList<>();
    private final List<List<String>> visibleCategoryPaths = new ArrayList<>();
    private final Set<Long> expandedIds = new HashSet<>();

    // Current Category and Table
    private List<Category> roots;
    private Table<String> categoryTable;

    public CategoryTreeScreen(WindowBasedTextGUI gui, DBInterface db) {
        this.gui = gui;
        this.db = db;
    }

    /**
     * Constructs and shows the TUI window
     */
    public void show() {
        Category treeRoot = db.getCategoryTree();
        roots = treeRoot == null
            ? Collections.emptyList()
            : sortedCategories(childrenOf(treeRoot));

        BasicWindow window = new BasicWindow("Category Tree");
        Panel root = new Panel(new LinearLayout(Direction.VERTICAL));

        // Instructions
        root.addComponent(new Label("Enter: expand/collapse a branch; open products for a leaf."));
        root.addComponent(new Label("[+] closed branch   [-] open branch   ID identifies the category"));

        // Category Table
        categoryTable = new Table<>("Category", "ID", "Children");
        categoryTable.setPreferredSize(new TerminalSize(95, 23));
        categoryTable.setCellSelection(false);
        categoryTable.setSelectAction(this::activateSelectedCategory);
        root.addComponent(categoryTable);

        // Empty fallback
        if (roots.isEmpty()) {
            root.addComponent(new Label("No categories found. Import category data first."));
        }

        //Bottom Buttons
        Panel buttons = new Panel(new LinearLayout(Direction.HORIZONTAL));
        buttons.addComponent(new Button("Collapse all", () -> {
            expandedIds.clear();
            refreshTree(null);
        }));
        buttons.addComponent(new Button("Show Products", () -> showSelectedProducts()));
        buttons.addComponent(new Button("Back", window::close));
        root.addComponent(buttons);

        // Fill tree, build window
        refreshTree(null);
        window.setComponent(root);
        gui.addWindowAndWait(window);
    }

    /**
     * Open Product list for selected Category if leaf, otherwise expand
     */
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

    /**
     * Update tree by expanding one category, or collapsing all
     * @param selectedId Category to expand
     */
    private void refreshTree(Long selectedId) {
        //Clear current Table
        categoryTable.getTableModel().clear();
        visibleCategories.clear();
        visibleCategoryPaths.clear();
        for (Category category : roots) {
            appendCategory(category, 0, Collections.emptyList());
        }

        // Expand single category
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

    /**
     *  Adds a category and its expanded descendants to the visible table rows
     * @param category Category to add
     * @param depth current visual depth
     * @param parentPath Categpry path of parent
     */
    private void appendCategory(Category category, int depth, List<String> parentPath) {
        // Get all children
        List<Category> children = childrenOf(category);

        // Get current status and path
        boolean expanded = expandedIds.contains(category.getId());
        List<String> categoryPath = new ArrayList<>(parentPath);
        categoryPath.add(category.getName());

        // Build label for Category
        StringBuilder label = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            label.append("  ");
        }
        label.append(children.isEmpty() ? "    " : expanded ? "[-] " : "[+] ");
        label.append(category.getName());

        // Add to visible categories
        visibleCategories.add(category);
        visibleCategoryPaths.add(categoryPath);
        categoryTable.getTableModel().addRow(
            label.toString(),
            category.getId().toString(),
            Integer.toString(children.size())
        );

        // Add childs recursively if expanded.
        if (expanded) {
            for (Category child : sortedCategories(children)) {
                appendCategory(child, depth + 1, categoryPath);
            }
        }
    }

    /**
     * Get currenty selected category
     */
    private Category selectedCategory() {
        int row = categoryTable.getSelectedRow();
        return row >= 0 && row < visibleCategories.size() ? visibleCategories.get(row) : null;
    }

    /**
     * Open product list for currently selceted category
     */
    private void showSelectedProducts() {
        // Get Selected
        Category selected = selectedCategory();
        if (selected == null) {
            return;
        }

        try {
            // Get Products
            List<String> categoryPath = selectedCategoryPath();
            List<ProductListEntry> entries = new ArrayList<>();

            // Build Table
            for (Product product : db.getProductsByCategoryPath(categoryPath)) {
                entries.add(new ProductListEntry(
                    product.getId(), product.getTitle(), product.getType(), product.getAvgRating(), product.getRatingQuantity()
                ));
            }
            entries.sort(Comparator.comparing(ProductListEntry::getTitle, String.CASE_INSENSITIVE_ORDER).thenComparing(ProductListEntry::getId));

            // Construct new window
            BasicWindow window = new BasicWindow("Products in category " + selected.getId());
            Panel root = new Panel(new LinearLayout(Direction.VERTICAL));
            root.addComponent(new Label(String.join(" > ", categoryPath)));
            root.addComponent(new Label("Products assigned directly to this category: " + entries.size()));

            // Add Table to window
            ProductListEntryTableComponent products = new ProductListEntryTableComponent(gui, db);
            root.addComponent(products.getTable(120, 25));
            products.update(entries);

            // Add buttons
            root.addComponent(new Button("Back", window::close));

            // show
            window.setComponent(root);
            gui.addWindowAndWait(window);
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            MessageDialog.showMessageDialog(gui, "Database error", "Could not load products for the selected category.\nSee the terminal for details.", MessageDialogButton.OK);
        }
    }

    /**
     * Get Path of currently selected Category
     * @return Category path
     */
    private List<String> selectedCategoryPath() {
        int row = categoryTable.getSelectedRow();
        return row >= 0 && row < visibleCategoryPaths.size()
            ? visibleCategoryPaths.get(row)
            : Collections.emptyList();
    }

    /**
     * Get Children of given Category, or null if do not exist
     * @param category Category to search
     * @return List of Category children
     */
    private List<Category> childrenOf(Category category) {
        return category.getChilds() == null ? Collections.emptyList() : category.getChilds();
    }

    /**
     * Sort Categories by name
     * @param categories Categories to sort
     * @return Sorted List
     */
    private List<Category> sortedCategories(List<Category> categories) {
        List<Category> sorted = new ArrayList<>(categories);
        sorted.sort(Comparator.comparing(Category::getName, String.CASE_INSENSITIVE_ORDER).thenComparing(Category::getId));
        return sorted;
    }
}
