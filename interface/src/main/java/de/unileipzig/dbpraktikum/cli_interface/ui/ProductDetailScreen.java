package de.unileipzig.dbpraktikum.cli_interface.ui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Borders;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.table.Table;

import de.unileipzig.dbpraktikum.cli_interface.db_interface.DBInterface;
import de.unileipzig.dbpraktikum.cli_interface.model.Book;
import de.unileipzig.dbpraktikum.cli_interface.model.CD;
import de.unileipzig.dbpraktikum.cli_interface.model.Category;
import de.unileipzig.dbpraktikum.cli_interface.model.DVD;
import de.unileipzig.dbpraktikum.cli_interface.model.Format;
import de.unileipzig.dbpraktikum.cli_interface.model.Offer;
import de.unileipzig.dbpraktikum.cli_interface.model.Person;
import de.unileipzig.dbpraktikum.cli_interface.model.Product;
import de.unileipzig.dbpraktikum.cli_interface.model.Review;
import de.unileipzig.dbpraktikum.cli_interface.model.Track;
import de.unileipzig.dbpraktikum.cli_interface.util.FormatUtil;

public class ProductDetailScreen {
    /**
     * Lanterna TUI Screen for Product detail view
     */

    // Lanterna TUI and DB Interface
    private final WindowBasedTextGUI gui;
    private final DBInterface db;

    // Current product
    private final String productId;
    private Product product = null;

    // Component storage
    private BasicWindow window = null;

    /**
     * Constructor with only PID. Will trigger load from DB. 
     * @param gui Lanterna WindowBasedTextGUI
     * @param db DBInterface
     * @param productId Current productId
     */
    public ProductDetailScreen(WindowBasedTextGUI gui, DBInterface db, String productId) {
        this.gui = gui;
        this.db = db;
        this.productId = productId;
    }

    /**
     * Constructor with Product. Skip loading.
     * @param gui Lanterna WindowBasedTextGUI
     * @param db DBInterface
     * @param p Current Product
     */
    public ProductDetailScreen(WindowBasedTextGUI gui, DBInterface db, Product p) {
        this.gui = gui;
        this.db = db;
        this.productId = p.getId();
        this.product = p;
    }

    /**
     * Constructs and shows the TUI window (the first time)
     */
    public void show() {
        this.show(false);
    }

    /**
     * Constructs and shows the TUI window
     * @param update If show is triggered as part of an update, set this true. 
     * The window will be updated instead of freshly build.
     */
    public void show(boolean update) {
        // Get Product
        if (product == null) 
            product = db.getProduct(productId);

        // Setup terminal and screen layers
        if (window == null)
            window = new BasicWindow("Product " + productId);

        // Create panel to hold components
        Panel root = new Panel();
        root.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        // --- Top row for product data
        Panel top = new Panel(new GridLayout(2));
        top.addComponent(
            getGeneralProductData()
            .withBorder(Borders.singleLine("Product details"))
            .setPreferredSize(new TerminalSize(50, 20)));
        top.addComponent(
            getSpecificProductData()
            .withBorder(Borders.singleLine(product.getType().name()))
            .setPreferredSize(new TerminalSize(80, 20)));

        root.addComponent(top);
        root.addComponent(new EmptySpace(new TerminalSize(1, 1)));

        // --- Middle row for Offers and reviews
        Panel middle = new Panel(new GridLayout(2));

        middle.addComponent(
            getOfferTable()
            .withBorder(Borders.singleLine("Offers"))
            .setPreferredSize(new TerminalSize(50, 20)));

        middle.addComponent(
            getReviewTable()
            .withBorder(Borders.singleLine("Reviews"))
            .setPreferredSize(new TerminalSize(80, 20)));

        root.addComponent(middle);
        root.addComponent(new EmptySpace(new TerminalSize(1, 1)));

        // --- Add bottom buttons
        Panel buttons = new Panel(new LinearLayout(Direction.HORIZONTAL));
        buttons.addComponent(new Button("Back", window::close));
        buttons.addComponent(new Button("Cheaper Similars", () -> showCheaperSimilars()));
        buttons.addComponent(new Button("Add Review", () -> showAddReviewScreen()));

        root.addComponent(buttons);

        if (update) {
            try {
                // update window
                window.setComponent(null);
                window.setComponent(root);
                gui.updateScreen();
            } catch (Exception e) {System.out.println(e);}
        } else {
            // Show window
            window.setComponent(root);
            gui.addWindowAndWait(window);
        }
    }

    /**
     * Show cheaper Similars screen for product
     */
    private void showCheaperSimilars() {
        // Open Screen
        new CheaperSimilarProductsScreen(gui, db, product).show();
    }

    /**
     * Show add Review screen for product, update view afterwards.
     */
    private void showAddReviewScreen() {
        // Open Screen
        new AddReviewScreen(gui, db, product).show();
        product = null;
        show(true);

    }

    /**
     * Open the review screen for the review at the given table row 
     * @param selectedRow selected table row
     */
    private void openSelectedReview(int selectedRow) {
        // Ensure list is not empty
        if (product.getReviews().isEmpty()) {
            return;
        }

        // Get PID
        Review selected = product.getReviews().get(selectedRow);

        // Open Screen
        new ShowReviewScreen(gui, db, selected).show();
        
    }

    /**
     * Get the Offer Table Component
     * @return Table<String> String Table Component
     */
    private Table<String> getOfferTable() {
        Table<String> t = new Table<>("Shop", "Condition", "Price", "Currency");
        t.setCellSelection(false); //No independent cell selection

        for (Offer o: db.getOffers(product)) {
            t.getTableModel().addRow(
                o.getShop().getName(),
                o.getCondition(),
                FormatUtil.formatDecimal(o.getPrice()),
                o.getCurrency()
            );
        }

        return t;
    }

    /**
     * Get the Review Table Component
     * @return Table<String> String Table Component
     */
    private Table<String> getReviewTable() {
        Table<String> t = new Table<>("Customer", "Date", "Points", "Text");
        t.setCellSelection(false); //No independent cell selection
        t.setSelectAction(() -> openSelectedReview(t.getSelectedRow()));

        for (Review r: product.getReviews()) {
            t.getTableModel().addRow(
                r.getCustomer().getName(),
                FormatUtil.formatDate(r.getDate()),
                FormatUtil.formatInt(r.getPoints()),
                FormatUtil.trunc(r.getText(), 40)
            );
        }

        return t;
    }

    /**
     * Build a Panel for general Product Data
     * @return Laterna Panel
     */
    private Panel getGeneralProductData() {
        Panel generalDetails = new Panel(new GridLayout(2));

        generalDetails.addComponent(new Label("ID"));
        generalDetails.addComponent(new Label(product.getId()));

        generalDetails.addComponent(new Label("Type"));
        generalDetails.addComponent(new Label(product.getType().name()));

        generalDetails.addComponent(new Label("Title"));
        generalDetails.addComponent(new Label(product.getTitle()));

        generalDetails.addComponent(new Label("Salesrank"));
        generalDetails.addComponent(new Label(FormatUtil.formatInt(product.getSalesrank())));

        generalDetails.addComponent(new Label("Rating Quantity"));
        generalDetails.addComponent( new Label(FormatUtil.formatInt(product.getRatingQuantity())));

        generalDetails.addComponent(new Label("Average Rating"));
        generalDetails.addComponent(new Label(FormatUtil.formatDecimal(product.getAvgRating())));

        Table<String> categories = new Table<>("Categories");
        categories.setPreferredSize(new TerminalSize(50, 12));
        categories.setCellSelection(false); //No independet cell selection
        for (Category c : product.getCategories()) {
            categories.getTableModel().addRow(FormatUtil.trunc(c.getName(), 45));
        }

        Panel genData = new Panel(new LinearLayout(Direction.VERTICAL));
        genData.addComponent(generalDetails);
        genData.addComponent(categories);

        return genData;
    }

    /**
     * Build a Panel for CD specific Data
     * @return Lanterna Panel
     */
    private Panel getCdData() {
        CD c = (CD) product;

        // Specific single row data
        Panel details = new Panel(new GridLayout(2));

        details.addComponent(new Label("Label"));
        details.addComponent(new Label(c.getLabel().getName()));

        details.addComponent(new Label("Publication"));
        details.addComponent(new Label(c.getPublication().toString()));

        // Specific list data
        Table<String> artists = new Table<>("Artists");
        artists.setPreferredSize(new TerminalSize(80, 4));
        artists.setCellSelection(false); //No independet cell selection
        for (Person p : c.getArtists()) {
            artists.getTableModel().addRow(FormatUtil.trunc(p.getName(), 45));
        }

        Table<String> tracks = new Table<>("Tracks");
        tracks.setPreferredSize(new TerminalSize(80, 12));
        tracks.setCellSelection(false); //No independet cell selection
        for (Track t : c.getTracks()) {
            tracks.getTableModel().addRow(FormatUtil.trunc(t.getName(), 45));
        }

        Panel cdData = new Panel(new LinearLayout(Direction.VERTICAL));
        cdData.addComponent(details);
        cdData.addComponent(artists);
        cdData.addComponent(tracks);

        return cdData;
    }

    /**
     * Build a Panel for DVD specific Data
     * @return Lanterna Panel
     */
    private Panel getDvdData() {
        DVD d = (DVD) product;

        // Specific single row data
        Panel details = new Panel(new GridLayout(2));

        details.addComponent(new Label("Runtime"));
        details.addComponent(new Label(FormatUtil.formatInt(d.getRuntime())));

        details.addComponent(new Label("Region Code"));
        details.addComponent(new Label(FormatUtil.formatInt(d.getRegionCode())));

        // Specific list data
        Table<String> formats = new Table<>("Formats");
        formats.setPreferredSize(new TerminalSize(80, 4));
        formats.setCellSelection(false); //No independet cell selection
        for (Format t : d.getFormats()) {
            formats.getTableModel().addRow(t.getName());
        }

        Table<String> actors = new Table<>("Actors");
        actors.setPreferredSize(new TerminalSize(80, 4));
        actors.setCellSelection(false); //No independet cell selection
        for (Person t : d.getActors()) {
            actors.getTableModel().addRow(t.getName());
        }

        Table<String> directors = new Table<>("Directors");
        directors.setPreferredSize(new TerminalSize(80, 4));
        directors.setCellSelection(false); //No independet cell selection
        for (Person t : d.getDirectors()) {
            directors.getTableModel().addRow(t.getName());
        }

        Table<String> creators = new Table<>("Actors");
        creators.setPreferredSize(new TerminalSize(80, 4));
        creators.setCellSelection(false); //No independet cell selection
        for (Person t : d.getCreators()) {
            creators.getTableModel().addRow(t.getName());
        }

        Panel dvdData = new Panel(new LinearLayout(Direction.VERTICAL));
        dvdData.addComponent(details);
        dvdData.addComponent(formats);
        dvdData.addComponent(actors);
        dvdData.addComponent(directors);
        dvdData.addComponent(creators);

        return dvdData;
    }

    /**
     * Build a Panel for Book specific Data
     * @return Lanterna Panel
     */
    private Panel getBookData() {
        Book b = (Book) product;

        // Specific single row data
        Panel details = new Panel(new GridLayout(2));        

        details.addComponent(new Label("Publisher"));
        details.addComponent(new Label(b.getPublisher().getName()));

        details.addComponent(new Label("ISBN"));
        details.addComponent(new Label(b.getIsbn()));

        details.addComponent(new Label("Pages"));
        details.addComponent(new Label(FormatUtil.formatInt(b.getPages())));

        details.addComponent(new Label("Publication"));
        details.addComponent(new Label(b.getPublication().toString())); //TODO: format

        // Specific list data
        Table<String> authors = new Table<>("Authors");
        authors.setPreferredSize(new TerminalSize(80, 4));
        authors.setCellSelection(false); //No independet cell selection
        for (Person t : b.getAuthors()) {
            authors.getTableModel().addRow(t.getName());
        }

        Panel bookData = new Panel(new LinearLayout(Direction.VERTICAL));
        bookData.addComponent(details);
        bookData.addComponent(authors);
        return bookData;
    }

    /**
     * Get Specific Data Panel, according to Product type.
     * @return Lanterna Panel
     */
    private Panel getSpecificProductData() {
        switch (product.getType()) {
            case BOOK:
                return getBookData();
            case MUSIC_CD:
                return getCdData();
            case DVD: 
                return getDvdData();
            default:
                return new Panel();
        }
    }
}
